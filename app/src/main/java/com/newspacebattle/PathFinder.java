package com.newspacebattle;

import android.os.Looper;
import android.widget.Space;

import java.util.ArrayList;

/**
 * Created by Dylan on 2018-09-16. Defines the brain behind how a ship moves.
 */
class PathFinder {

    private int attackStage;
    float destX, destY;
    private boolean pointOrObj;
    private ArrayList<PointObject> path;
    private Ship ship;
    private GameObject targetObj;
    private ArrayList<Ship> enemies;

    //Constructor method
    PathFinder(Ship ship) {
        this.ship = ship;
        enemies = new ArrayList<>();
        path = new ArrayList<>();
    }

    //Go to these coordinates
    void run(float moveToX, float moveToY) {
        pointOrObj = false;
        destX = moveToX;
        destY = moveToY;
        targetObj = null;
        path.clear();
        path.add(new PointObject(destX, destY));
        startFinder();
    }

    //Follow this object
    void run(GameObject target) {
        pointOrObj = true;
        targetObj = target;
        destX = targetObj.centerPosX;
        destY = targetObj.centerPosY;
        path.clear();
        path.add(new PointObject(destX, destY));
        startFinder();
    }

    //Attack this group of enemies
    void runAttack(ArrayList<Ship> enemies) {
        stopFinder();
        this.enemies = enemies;
        if (enemies.size() == 0) {
            return;
        }
        ship.attacking = true;
        if (ship.type.equals("BattleShip") || ship.type.equals("FlagShip") || ship.type.equals("LaserCruiser")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    encircle();
                }
            }).start();
        } else if (ship.type.equals("Bomber") || ship.type.equals("Fighter")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    strafeRun();
                }
            }).start();
        }
    }

    //Stop going to destination or attacking
    void stopFinder() {
        targetObj = null;
        ship.destination = false;
        ship.attacking = false;
        enemies.clear();
        path.clear();
    }

    //Starts the pathfinding process
    private void startFinder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                path = getPath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        while (ship.destination && ship.exists && path.size() > 0) {
                            checkDestination();
                            Utilities.delay(1);
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        while (ship.destination && ship.exists && path.size() > 0) {
                            driveShip();
                            Utilities.delay(500);
                        }
                    }
                }).start();
                while (ship.destination && ship.exists && path.size() > 0) {
                    if (!GameScreen.paused) {
                        ArrayList<PointObject> possiblePath = getPath();
                        if (possiblePath != path) {
                            path = possiblePath;
                        }
                        Utilities.delay(250);
                    }
                }
            }
        }).start();
    }

    //Finds the shortest path to the destination
    private ArrayList<PointObject> getPath() {
        ArrayList<PointObject> possiblePath = new ArrayList<>();
        possiblePath.add(new PointObject(destX, destY));

        ArrayList<PointObject> possibleInts = new ArrayList<>();

        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            if (GameScreen.objects.get(i) != ship) {
                PointObject intersect = Utilities.lineCircleIntersect(ship, ship.centerPosX, ship.centerPosY, possiblePath.get(0).x, possiblePath.get(0).y, GameScreen.objects.get(i));
                if (intersect != null) {
                    possibleInts.add(intersect);
                }
            }
        }

        if (possibleInts.size() == 0) {
            return path;
        }
        PointObject closestInt = new PointObject(0, 0);

        if (possibleInts.size() == 1) {
            closestInt = possibleInts.get(0);
        } else if (possibleInts.size() > 1) {
            long distance = 999999999999999999L;
            for (PointObject i : possibleInts) {
                if (Utilities.distanceFormula(i.x, i.y, ship.centerPosX, ship.centerPosY) < distance) {
                    closestInt = i;
                    distance = (long) Utilities.distanceFormula(i.x, i.y, ship.centerPosX, ship.centerPosY);
                }
            }
        }

        if (closestInt.object != null && !(Utilities.distanceFormula(possiblePath.get(0).x, possiblePath.get(0).y, closestInt.object.centerPosX, closestInt.object.centerPosY) <= ship.radius + closestInt.object.radius)) {
            for (double i = closestInt.object.centerPosX - (closestInt.object.radius - ship.radius) * 1.5; i <= closestInt.object.centerPosX + (closestInt.object.radius + ship.radius) * 1.5; i++) {
                double y1 = Utilities.circleEquation1(i, 1.5 * (closestInt.object.radius + ship.radius), closestInt.object.centerPosX, closestInt.object.centerPosY);
                double y2 = Utilities.circleEquation2(i, 1.5 * (closestInt.object.radius + ship.radius), closestInt.object.centerPosX, closestInt.object.centerPosY);

                if (Utilities.lineCircleIntersect(ship, ship.centerPosX, ship.centerPosY, i, y1, closestInt.object) == null) {
                    if (Utilities.lineCircleIntersect(ship, i, y1, possiblePath.get(0).x, possiblePath.get(0).y, closestInt.object) == null) {
                        possiblePath.add(new PointObject(i, y1));
                        break;
                    }
                }

                if (Utilities.lineCircleIntersect(ship, ship.centerPosX, ship.centerPosY, i, y2, closestInt.object) == null) {
                    if (Utilities.lineCircleIntersect(ship, i, y2, possiblePath.get(0).x, possiblePath.get(0).y, closestInt.object) == null) {
                        possiblePath.add(new PointObject(i, y2));
                        break;
                    }
                }
            }
        }
        return possiblePath;
    }

    //Checks how close ship is to the destination
    private void checkDestination() {
        double stopDistance = 50;
        if (pointOrObj) {
            destX = targetObj.centerPosX;
            destY = targetObj.centerPosY;
            stopDistance = (ship.radius + targetObj.radius) * 2;
            if (ship instanceof ResourceCollector && (((ResourceCollector) ship).harvesting || ((ResourceCollector) ship).unloading)) {
                stopDistance = (ship.radius + targetObj.radius) * 1.1;
            } else if (ship instanceof ResourceCollector && ((ResourceCollector) ship).unloading) {
                stopDistance = (ship.radius + targetObj.radius) * 1.1;
            }
        }
        if (path.size() > 0) {
            if (Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y) <= stopDistance) {
                if (path.size() >= 1) {
                    path.remove(path.size() - 1);
                    if (path.size() == 0) {
                        if(ship.docking && targetObj instanceof SpaceStation){
                           if(((SpaceStation) targetObj).dockedShips.size() < ((SpaceStation) targetObj).maxDockedNum) {

                               ((SpaceStation) targetObj).dockedShips.add(ship);
                               ship.health = ship.MAX_HEALTH;

                               if (ship instanceof Fighter) {
                                   GameScreen.fighters.remove(ship);
                               } else if (ship instanceof Bomber) {
                                   GameScreen.bombers.remove(ship);
                               } else if (ship instanceof ResourceCollector) {
                                   GameScreen.resourceCollectors.remove(ship);
                               } else if (ship instanceof Scout) {
                                   GameScreen.scouts.remove(ship);
                               }
                               GameScreen.ships.remove(ship);
                               GameScreen.objects.remove(ship);
                               ship.docked = true;
                               ship.docking = false;
                           }
                        }

                        if (ship instanceof ResourceCollector){
                            if(((ResourceCollector) ship).harvesting && targetObj instanceof Asteroid){
                                ((ResourceCollector) ship).mineAsteroid();
                            }else if(((ResourceCollector) ship).unloading && targetObj instanceof FlagShip){
                                ((ResourceCollector) ship).transferRes();
                            }
                        }
                        ship.stop();
                        return;
                    }
                } else {
                    ship.destination = false;
                    if (ship instanceof ResourceCollector) {
                        ((ResourceCollector) ship).harvesting = false;
                        ((ResourceCollector) ship).unloading = false;
                    }
                }
            }
            if (path.size() > 0) {
                double requiredAngle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y);
                if (Math.abs(requiredAngle - ship.degrees) <= 5) {
                    ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y) * Math.PI / 180);
                    ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y) * Math.PI / 180);
                }
            }
        }
    }

    //Sets the acceleration of the ship
    private void driveShip() {
        if (path.size() > 0) {
            if (Math.abs(Utilities.angleDim(ship.accelerationX, ship.accelerationY) - Utilities.angleDim(ship.velocityX, ship.velocityY)) > 0.25) {
                int turnAngle = 150;
                double requiredAngle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y);
                if (ship.degrees - requiredAngle <= 0) {
                    turnAngle *= -1;
                }

                ship.accelerationX = ship.accelerate / 2 * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
                ship.accelerationY = ship.accelerate / 2 * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
            } else {
                ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y) * Math.PI / 180);
                ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, path.get(path.size() - 1).x, path.get(path.size() - 1).y) * Math.PI / 180);
            }
        }
    }

    //Attacks by doing strafe runs
    private void strafeRun() {
        while (ship.attacking) {
            Ship closest = null;
            double distance = 9999999999999999f;
            for (int i = 0; i <= enemies.size() - 1; i++) {
                if (Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, enemies.get(i).centerPosX, enemies.get(i).centerPosY) < distance) {
                    closest = enemies.get(i);
                    distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, enemies.get(i).centerPosX, enemies.get(i).centerPosY);
                }
            }
            if (closest == null) {
                return;
            }

            if (distance > (ship.radius + closest.radius) * 3) {
                attackStage = 1;
            } else if (distance <= (ship.radius + closest.radius) * 3) {
                attackStage = 2;
            } else {
                attackStage = 3;
            }

            if (attackStage == 1) {
                if (path.size() == 0) {
                    ship.setDestination(closest.centerPosX, closest.centerPosY, true);
                }
            } else if (attackStage == 2) {
                ship.accelerationX = ship.accelerate / 2 * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - 150, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - 150, ship.centerPosY, ship.radius)) * Math.PI / 180);
                ship.accelerationY = ship.accelerate / 2 * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - 150, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - 150, ship.centerPosY, ship.radius)) * Math.PI / 180);
                Utilities.delay(500);
                ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees, ship.centerPosY, ship.radius)) * Math.PI / 180);
                ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees, ship.centerPosY, ship.radius)) * Math.PI / 180);
            } else if (attackStage == 3) {

            }
        }
    }

    //Encircles target and fires
    private void encircle() {

    }
}