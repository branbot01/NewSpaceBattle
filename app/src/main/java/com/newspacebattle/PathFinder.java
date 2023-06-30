package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Dylan on 2018-09-16. Defines the brain behind how a ship moves.
 */
class PathFinder {

    float destX, destY, tempX, tempY, avoidanceRadius;
    private boolean pointOrObj;
    private Ship ship;
    private GameObject targetObj;
    private ArrayList<Ship> enemies;

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
    }

    //Stop going to destination or attacking
    void stopFinder() {
        targetObj = null;
        enemies.clear();
    }

    //Starts the pathfinding process
    private void startFinder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                PointObject direction = pathFind();
                if (direction == null) {
                    return;
                }
                tempX = (float) direction.x;
                tempY = (float) direction.y;
                ship.degrees = (float) Utilities.anglePoints(ship.centerPosX, ship.centerPosY, tempX, tempY);
                driveShip(direction.x, direction.y);
                Utilities.delay(500);
                while (ship.exists && ship.destination) {
                    direction = pathFind();
                    if (direction == null) {
                        return;
                    }
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

    private PointObject pathFind(){
        PointObject direction = new PointObject(destX, destY);
        ArrayList<GameObject> nearbyObjects = new ArrayList<>();
        if (Objects.equals(ship.type, "ResourceCollector")) {
            avoidanceRadius = ship.radius * 9;
        } else if (Objects.equals(ship.type, "Scout")) {
            avoidanceRadius = ship.radius * 9.5f;
        } else if (Objects.equals(ship.type, "Fighter")) {
            avoidanceRadius = ship.radius * 10;
        } else if (Objects.equals(ship.type, "Bomber")) {
            avoidanceRadius = ship.radius * 8;
        } else if (Objects.equals(ship.type, "LaserCruiser")) {
            avoidanceRadius = ship.radius * 3.5f;
        } else if (Objects.equals(ship.type, "BattleShip")) {
            avoidanceRadius = ship.radius * 2;
        } else if (Objects.equals(ship.type, "FlagShip")) {
            avoidanceRadius = ship.radius * 2.25f;
        }

        for (int i = 0; i < GameScreen.objects.size(); i++) {
            GameObject obj = GameScreen.objects.get(i);
            if (obj == ship) {
                continue;
            }
            double distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, obj.centerPosX, obj.centerPosY);
            if (distance <= avoidanceRadius + obj.radius) {
                nearbyObjects.add(obj);

                if (Utilities.distanceFormula(destX, destY, obj.centerPosX, obj.centerPosY) <= obj.radius) {
                    ship.stop();
                    ship.destination = false;
                    return null;
                }
            }
        }

        if (nearbyObjects.size() == 0){
            return direction;
        }

        final int MAX_POINTS = 16;
        boolean[] possiblePoints = new boolean[MAX_POINTS];
        Arrays.fill(possiblePoints, true);
        double[] distances = new double[MAX_POINTS];
        Arrays.fill(distances, Double.MAX_VALUE);
        for (int i = 0; i < MAX_POINTS; i++){
            float angle = ship.degrees + (float) (i * (360 / MAX_POINTS));
            double newX = Utilities.circleAngleX(angle, ship.centerPosX, avoidanceRadius);
            double newY = Utilities.circleAngleY(angle, ship.centerPosY, avoidanceRadius);
            distances[i] = Utilities.distanceFormula(newX, newY, destX, destY);
            for (int ii = 0; ii < nearbyObjects.size(); ii++) {
                GameObject obj = nearbyObjects.get(ii);
                double distance = Utilities.distanceFormula(newX, newY, obj.centerPosX, obj.centerPosY);
                if (distance <= avoidanceRadius) {
                    possiblePoints[i] = false;
                }
            }
        }

        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < distances.length; i++){
            if (distances[i] < min && possiblePoints[i]){
                min = distances[i];
                minIndex = i;
            }
        }
        if (minIndex == -1){
            return direction;
        }
        float angle = ship.degrees + (float) (minIndex * (360 / MAX_POINTS));
        direction.x = Utilities.circleAngleX(angle, ship.centerPosX, avoidanceRadius);
        direction.y = Utilities.circleAngleY(angle, ship.centerPosY, avoidanceRadius);
        return direction;
    }

    //Checks how close ship is to the destination
    private void checkDestination() {
        double stopDistance = ship.radius / 6;
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
            if (ship instanceof ResourceCollector) {
                ((ResourceCollector) ship).harvesting = false;
                ((ResourceCollector) ship).unloading = false;
            }
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
            if (ship.degrees - requiredAngle <= 0) {
                turnAngle *= -1;
            }

            ship.accelerationX = ship.accelerate / 1.5f * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
            ship.accelerationY = ship.accelerate / 1.5f * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, Utilities.circleAngleX(ship.degrees - turnAngle, ship.centerPosX, ship.radius), Utilities.circleAngleY(ship.degrees - turnAngle, ship.centerPosY, ship.radius)) * Math.PI / 180);
        } else {
            ship.accelerationX = ship.accelerate * (float) Math.sin(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, x, y) * Math.PI / 180);
            ship.accelerationY = ship.accelerate * (float) Math.cos(Utilities.anglePoints(ship.centerPosX, ship.centerPosY, x, y) * Math.PI / 180);
        }
    }
}