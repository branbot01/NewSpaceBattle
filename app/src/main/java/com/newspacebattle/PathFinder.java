package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dylan on 2018-09-16. Defines the brain behind how a ship moves.
 */
class PathFinder {

    int driveTime, shootTime;
    float destX, destY, tempX, tempY;
    private boolean pointOrObj;
    private Ship ship;
    private GameObject targetObj;
    ArrayList<Ship> enemies;

    //Constructor method
    PathFinder(Ship ship) {
        this.ship = ship;
        enemies = new ArrayList<>();
    }

    //Go to these coordinates
    void run(float moveToX, float moveToY) {
        ship.attacking = false;
        pointOrObj = false;
        destX = moveToX;
        destY = moveToY;
        targetObj = null;
        startFinder();
    }

    //Follow this object
    void run(GameObject target) {
        if (target == null) {
            System.out.println("Error: Target is null");
        }
        ship.attacking = false;
        if (ship.formation != null) {
            run(target.centerPosX, target.centerPosY);
            return;
        }
        pointOrObj = true;
        targetObj = target;
        destX = targetObj.centerPosX;
        destY = targetObj.centerPosY;
        startFinder();
    }

    //Attack this group of enemies
    void runAttack(ArrayList<Ship> enemies) {
        if (ship instanceof SpaceStation || ship instanceof ResourceCollector || ship instanceof Scout) {
            ship.attacking = false;
            return;
        }
        ship.formation = null;
        stopFinder();
        this.enemies.addAll(enemies);
        this.enemies.removeIf(enemy -> enemy.team == ship.team);
        if (this.enemies.size() == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                startAttacker();
            }
        }).start();
    }

    void autoAttack() {
        if (ship instanceof SpaceStation || ship instanceof ResourceCollector || ship instanceof Scout) {
            ship.attacking = false;
            return;
        }
        new Thread(() -> {
            Looper.prepare();
            while (ship.exists && !ship.destination) {
                if (!ship.attacking) {
                    double closestDistance = 1000000000;
                    int closestIndex = -1;
                    boolean isShip = false;
                    for (int i = 0; i < GameScreen.ships.size(); i++) {
                        try {
                            if (GameScreen.ships.get(i).team != ship.team && GameScreen.ships.get(i) != ship && GameScreen.ships.get(i).exists) {
                                isShip = true;
                                double distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY);
                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closestIndex = i;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    }
                    if (!isShip) {
                        ship.stop();
                        break;
                    }
                    if (closestIndex != -1) {
                        stopFinder();
                        enemies.add(GameScreen.ships.get(closestIndex));
                        startAttacker();
                    }
                }
                Utilities.delay(1000);
            }
        }).start();
    }

    //Stop going to destination or attacking
    void stopFinder() {
        targetObj = null;
        enemies.clear();
    }

    private void startAttacker() {
        ship.attacking = true;
        ship.formation = null;
        while (ship.exists && ship.attacking) {
            try {
                if (!enemies.get(0).exists) {// || Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, enemies.get(0).centerPosX, enemies.get(0).centerPosY) > SpaceStation.constRadius * 20) {
                    ship.attacking = false;
                    return;
                }
                destX = enemies.get(0).centerPosX;
                destY = enemies.get(0).centerPosY;
                PointObject direction = pathFind();
                if (driveTime >= ship.driveTime) {
                    driveTime = 0;
                    tempX = (float) direction.x;
                    tempY = (float) direction.y;
                    driveShip(direction.x, direction.y);
                }
                driveTime += 5;
                shootTime += 5;
                if (Math.abs(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) - ship.degrees) <= 5) {
                    ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) * Math.PI / 180);
                    ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) * Math.PI / 180);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            Utilities.delay(5);
        }
    }

    //Starts the pathfinding process
    private void startFinder() {
        new Thread(() -> {
            Looper.prepare();
            PointObject direction = pathFind();
            tempX = (float) direction.x;
            tempY = (float) direction.y;
            ship.degrees = (float) Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY);
            driveShip(direction.x, direction.y);
            checkDestination();
            Utilities.delay(500);
            int time = 0;
            while (ship.exists && ship.destination) {
                if (time >= 500) {
                    time = 0;
                    direction = pathFind();
                    tempX = (float) direction.x;
                    tempY = (float) direction.y;
                    driveShip(direction.x, direction.y);
                }
                checkDestination();
                Utilities.delay(1);
                time++;
            }
        }).start();
    }

    private PointObject pathFind() {
        PointObject direction = new PointObject(destX, destY);
        ArrayList<GameObject> nearbyObjects = new ArrayList<>();

        for (int i = 0; i < GameScreen.objects.size(); i++) {
            GameObject obj = GameScreen.objects.get(i);
            if (obj == ship) {
                continue;
            }
            double distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, obj.centerPosX, obj.centerPosY);
            double angle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, obj.centerPosX, obj.centerPosY);
            double addedDistance = 0;

            if (ship.attacking && obj.team != ship.team && !(obj instanceof Asteroid)) {
                double shootDegree = 10;
                if (obj instanceof SpaceStation || obj instanceof BattleShip || obj instanceof FlagShip) {
                    shootDegree = 20;
                }
                if (ship instanceof BattleShip || ship instanceof Bomber) {
                    if (shootTime >= ship.shootTime && distance <= ship.avoidanceRadius * 3) {
                        ship.shoot();
                        shootTime = 0;
                    }
                } else {
                    if (Math.abs(angle - ship.degrees) <= shootDegree && shootTime >= ship.shootTime && distance <= ship.avoidanceRadius * 3) {
                        ship.shoot();
                        shootTime = 0;
                    }
                }
            }


            if (obj instanceof SpaceStation) {
                addedDistance = obj.radius * 1.5f;
            }
            if (distance <= ship.avoidanceRadius + obj.radius + addedDistance) {
                nearbyObjects.add(obj);
                if (obj instanceof ResourceCollector && ((ResourceCollector) obj).flagShipSelected == ship) {
                    nearbyObjects.remove(obj);
                    continue;
                }

                if (Utilities.distanceFormula(destX, destY, obj.centerPosX, obj.centerPosY) <= obj.radius && !ship.docking) {
                    if (ship instanceof ResourceCollector) {
                        if (((ResourceCollector) ship).harvesting || ((ResourceCollector) ship).unloading) {
                            nearbyObjects.remove(obj);
                        }
                    }
                }
            }
        }

        if (nearbyObjects.size() == 0) {
            return direction;
        }

        final int MAX_POINTS = 16;
        boolean[] possiblePoints = new boolean[MAX_POINTS];
        Arrays.fill(possiblePoints, true);
        double[] distances = new double[MAX_POINTS];
        Arrays.fill(distances, Double.MAX_VALUE);
        for (int i = 0; i < MAX_POINTS; i++) {
            float angle = ship.degrees + (float) (i * (360 / MAX_POINTS));
            double newX = Utilities.circleAngleX(angle, ship.centerPosX, ship.avoidanceRadius);
            double newY = Utilities.circleAngleY(angle, ship.centerPosY, ship.avoidanceRadius);
            distances[i] = Utilities.distanceFormula(newX, newY, destX, destY);
            for (int ii = 0; ii < nearbyObjects.size(); ii++) {
                GameObject obj = nearbyObjects.get(ii);
                double distance = Utilities.distanceFormula(newX, newY, obj.centerPosX, obj.centerPosY);
                double addedDistance = 0;
                if (obj instanceof SpaceStation) {
                    addedDistance = obj.radius / 2;
                }
                if (distance <= ship.avoidanceRadius + addedDistance) {
                    possiblePoints[i] = false;
                }
            }
        }

        int index;
        if (!ship.attacking) {
            double min = Double.MAX_VALUE;
            index = -1;
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] < min && possiblePoints[i]) {
                    min = distances[i];
                    index = i;
                }
            }
            if (index == -1) {
                return direction;
            }
        } else {
            boolean allFalse = true;
            for (boolean possiblePoint : possiblePoints) {
                if (possiblePoint) {
                    allFalse = false;
                    break;
                }
            }
            if (allFalse) {
                return direction;
            }
            do {
                index = (int) (Math.random() * MAX_POINTS);
            } while (!possiblePoints[index]);
        }
        float angle = ship.degrees + (float) (index * (360 / MAX_POINTS));
        direction.x = Utilities.circleAngleX(angle, ship.centerPosX, ship.avoidanceRadius);
        direction.y = Utilities.circleAngleY(angle, ship.centerPosY, ship.avoidanceRadius);
        return direction;
    }

    //Checks how close ship is to the destination
    private void checkDestination() {
        double stopDistance = ship.radius / 2;
        if (pointOrObj) {
            destX = targetObj.centerPosX;
            destY = targetObj.centerPosY;
            stopDistance = (ship.radius + targetObj.radius) * 2;
            if (ship instanceof ResourceCollector && (((ResourceCollector) ship).harvesting)) {
                stopDistance = (ship.radius + targetObj.radius) * 1.1;
            } else if (ship instanceof ResourceCollector && ((ResourceCollector) ship).unloading) {
                stopDistance = (ship.radius + targetObj.radius) * 1.3;
            }
        }
        if (Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, destX, destY) <= stopDistance) {
            if (ship.docking && targetObj instanceof SpaceStation) {
                if (((SpaceStation) targetObj).dockedShips.size() < ((SpaceStation) targetObj).maxDockedNum) {
                    ((SpaceStation) targetObj).dockedShips.add(ship);
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

            if (ship instanceof ResourceCollector) {
                if (((ResourceCollector) ship).harvesting && targetObj instanceof Asteroid) {
                    ((ResourceCollector) ship).mineAsteroid();
                } else if (((ResourceCollector) ship).unloading && targetObj instanceof FlagShip) {
                    ((ResourceCollector) ship).transferRes();
                }
            }
            ship.stop();
            return;
        }
        if (Math.abs(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) - ship.degrees) <= 5) {
            ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) * Math.PI / 180);
            ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY) * Math.PI / 180);
        }
    }

    //Sets the acceleration of the ship
    private void driveShip(double x, double y) {
        double requiredAngle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, x, y);

        if (Math.abs(ship.degrees - requiredAngle) > 5) {
            int turnAngle = 160;
            if (ship.attacking && !(ship instanceof FlagShip || ship instanceof BattleShip || ship instanceof LaserCruiser)) {
                turnAngle = 110;
            }

            double requiredPointX = Utilities.circleAngleX(requiredAngle, ship.centerPosX, ship.radius);
            double requiredPointY = Utilities.circleAngleY(requiredAngle, ship.centerPosY, ship.radius);
            double point1X = Utilities.circleAngleX(ship.degrees + 100, ship.centerPosX, ship.radius);
            double point1Y = Utilities.circleAngleY(ship.degrees + 100, ship.centerPosY, ship.radius);
            double point2X = Utilities.circleAngleX(ship.degrees - 100, ship.centerPosX, ship.radius);
            double point2Y = Utilities.circleAngleY(ship.degrees - 100, ship.centerPosY, ship.radius);

            if (Utilities.distanceFormula(point1X, point1Y, requiredPointX, requiredPointY) < Utilities.distanceFormula(point2X, point2Y, requiredPointX, requiredPointY)) {
                turnAngle *= -1;
            }

            float turnConstant = 1.5f;
            if (ship.attacking) {
                turnConstant = 1;
            }

            ship.accelerationX = ship.accelerate / turnConstant * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
            ship.accelerationY = ship.accelerate / turnConstant * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
        }
    }
}