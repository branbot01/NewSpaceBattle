package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

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
        if (GameScreen.generation == 0) {
            attacker = new NeuralNetwork(14, 12, 3);
        } else {
            Random random = new Random();
            double crossoverRate = 0.9;
            double mutationRate = 0.001;

            if (random.nextDouble() < crossoverRate) {
                Ship parent1 = Utilities.rouletteWheelSelection(GameScreen.population);
                Ship parent2 = Utilities.rouletteWheelSelection(GameScreen.population);
                if (parent1 == null || parent2 == null) {
                    System.out.println("Null parent");
                }
                attacker = NeuralNetwork.merge(parent1.destinationFinder.attacker, parent2.destinationFinder.attacker);
                attacker.applyMutation(mutationRate);
            } else {
                attacker = Utilities.rouletteWheelSelection(GameScreen.population).destinationFinder.attacker;
                attacker.applyMutation(mutationRate);
            }
        }
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
        startAttacker();
    }

    void autoAttack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (ship.exists) {
                    if (!ship.attacking) {
                        for (int i = 0; i < GameScreen.ships.size(); i++) {
                            if (GameScreen.ships.get(i).team != ship.team) {
                                double distance = Utilities.distanceFormula(ship.centerPosX, ship.centerPosY, GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY);
                                if (distance < ship.radius * 200) {
                                    runAttack(new ArrayList<Ship>(Collections.singletonList(GameScreen.ships.get(i))));
                                    break;
                                }
                            }
                        }
                    }
                    Utilities.delay(500);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (ship.attacking && ship.exists) {
                    if (enemies.size() == 0) {
                        ship.attacking = false;
                        return;
                    }
                    double[] inputs;
                    try {
                        inputs = new double[]{ship.health, ship.centerPosX, ship.centerPosY, ship.velocityX, ship.velocityY, ship.accelerationX, ship.accelerationY, enemies.get(0).centerPosX, enemies.get(0).centerPosY, enemies.get(0).velocityX, enemies.get(0).velocityY, enemies.get(0).accelerationX, enemies.get(0).accelerationY, enemies.get(0).health};
                    } catch (Exception e) {
                        continue;
                    }
                    double[] reaction = attacker.forwardPropagation(inputs);
                    if (reaction[0] >= 0) {
                        ship.shoot();
                    }
                    double angle = Utilities.angleDim((float) reaction[1], (float) reaction[2]);
                    driveShip(Utilities.circleAngleX(angle, ship.centerPosX, ship.radius), Utilities.circleAngleY(angle, ship.centerPosY, ship.radius));
                    Utilities.delay(500);
                }
            }
        }).start();
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