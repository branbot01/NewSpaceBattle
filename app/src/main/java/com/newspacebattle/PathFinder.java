package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dylan on 2018-09-16. Defines the brain behind how a ship moves.
 */
class PathFinder {

    float destX, destY, tempX, tempY;
    private boolean pointOrObj;
    private Ship ship;
    private GameObject targetObj;
    ArrayList<Ship> enemies;
    NeuralNetwork attacker;

    //Constructor method
    PathFinder(Ship ship) {
        this.ship = ship;
        enemies = new ArrayList<>();
    }

    //Go to these coordinates
    void run(float moveToX, float moveToY) {
        pointOrObj = false;
        destX = moveToX;
        destY = moveToY;
        targetObj = null;
        startFinder();
    }

    //Follow this object
    void run(GameObject target) {
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
        stopFinder();
        this.enemies = enemies;
        if (enemies.size() == 0) {
            return;
        }
        ship.attacking = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startAttacker();
            }
        }).start();
    }

    void autoAttack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (ship.exists) {
                    if (!ship.attacking) {
                        double closestDistance = 1000000000;
                        int closestIndex = -1;
                        boolean isShip = false;
                        for (int i = 0; i < GameScreen.ships.size(); i++) {
                            try {
                                if (GameScreen.ships.get(i).team != ship.team) {
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
                            ship.attacking = true;
                            enemies.add(GameScreen.ships.get(closestIndex));
                            startAttacker();
                        }
                    }
                }
            }
        }).start();
    }

    //Stop going to destination or attacking
    void stopFinder() {
        targetObj = null;
        enemies.clear();
    }

    private void startAttacker() {
        int driveTime = 0;
        int shootTime = 0;

        while (ship.attacking && ship.exists) {
            Utilities.delay(10);

            try {
                if (!enemies.get(0).exists) {
                    ship.attacking = false;
                    return;
                }
                double enemyAngle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, enemies.get(0).centerPosX, enemies.get(0).centerPosY);
                boolean turn = false;
                for (int i = 0; i < GameScreen.objects.size(); i++) {
                    GameObject obj = GameScreen.objects.get(i);
                    if (obj != ship && obj.exists) {
                        double distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, obj.centerPosX, obj.centerPosY);
                        double angle = Utilities.anglePoints(ship.centerPosX, ship.centerPosY, obj.centerPosX, obj.centerPosY);
                        double extraRadius = 0;
                        if (obj.team != ship.team) {
                            if (ship instanceof BattleShip || ship instanceof Bomber) {
                                if (shootTime >= ship.shootTime && distance <= ship.avoidanceRadius * 3) {
                                    ship.shoot();
                                    shootTime = 0;
                                }
                            } else {
                                if (Math.abs(angle - ship.degrees) <= 10 && shootTime >= ship.shootTime && distance <= ship.avoidanceRadius * 3) {
                                    ship.shoot();
                                    shootTime = 0;
                                }
                            }

                            if (ship instanceof FlagShip || ship instanceof BattleShip || ship instanceof LaserCruiser){
                                if (!(obj instanceof FlagShip || obj instanceof BattleShip || obj instanceof LaserCruiser)){
                                    extraRadius = -ship.avoidanceRadius;
                                }
                            }
                        } else {
                            if (!(obj instanceof FlagShip || obj instanceof BattleShip || obj instanceof LaserCruiser)){
                                extraRadius = -ship.avoidanceRadius / 3;
                            }
                        }
                        if (distance < ship.avoidanceRadius + extraRadius) {
                            if (angle - ship.degrees >= 180)
                                enemyAngle -= 160;
                            else if (angle - ship.degrees < 180)
                                enemyAngle += 160;
                        }
                    }
                }

                if (Math.abs(ship.degrees - enemyAngle) > 5) {
                    if (driveTime >= ship.driveTime) {
                        driveTime = 0;
                        int turnAngle = 100;
                        if (ship instanceof FlagShip || ship instanceof BattleShip || ship instanceof LaserCruiser) {
                            turnAngle = 160;
                        }
                        if (ship.degrees <= enemyAngle) {
                            turnAngle = -turnAngle;
                        }

                        ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
                        ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
                    }
                } else {
                    ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees, ship.centerPosY, ship.radius)) * Math.PI / 180);
                    ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees, ship.centerPosY, ship.radius)) * Math.PI / 180);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            driveTime += 10;
            shootTime += 10;
        }
    }

    //Starts the pathfinding process
    private void startFinder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                PointObject direction = pathFind();
                tempX = (float) direction.x;
                tempY = (float) direction.y;
                ship.degrees = (float) Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY);
                driveShip(direction.x, direction.y);
                Utilities.delay(500);
                while (ship.exists && ship.destination) {
                    direction = pathFind();
                    tempX = (float) direction.x;
                    tempY = (float) direction.y;
                    driveShip(direction.x, direction.y);
                    Utilities.delay(500);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (ship.exists && ship.destination) {
                    checkDestination();
                    Utilities.delay(1);
                }
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
            double addedDistance = 0;
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

        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < min && possiblePoints[i]) {
                min = distances[i];
                minIndex = i;
            }
        }
        if (minIndex == -1) {
            return direction;
        }
        float angle = ship.degrees + (float) (minIndex * (360 / MAX_POINTS));
        direction.x = Utilities.circleAngleX(angle, ship.centerPosX, ship.avoidanceRadius);
        direction.y = Utilities.circleAngleY(angle, ship.centerPosY, ship.avoidanceRadius);
        return direction;
    }

    //Checks how close ship is to the destination
    private void checkDestination() {
        double stopDistance = ship.radius / 4;
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
            ship.destination = false;
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
            if (ship.degrees < requiredAngle) {
                turnAngle = -turnAngle;
            }

            ship.accelerationX = ship.accelerate / 1.5f * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
            ship.accelerationY = ship.accelerate / 1.5f * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
        } else {
            ship.accelerationX = ship.accelerate * (float) Math.sin(requiredAngle * Math.PI / 180);
            ship.accelerationY = ship.accelerate * (float) Math.cos(requiredAngle * Math.PI / 180);
        }
    }
}