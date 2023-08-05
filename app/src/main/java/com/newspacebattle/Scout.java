package com.newspacebattle;

import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;

import kotlin.Triple;

/**
 * Created by Dylan on 2018-07-06. Defines a scout ship.
 */
class Scout extends Ship {

    static float constRadius;
    static float MAX_SPEED;
    boolean scouting, lastSeenMode;
    ArrayList<Pair<AsteroidCluster, Boolean>> asteroidsVisited = new ArrayList<>();
    AsteroidCluster currentAsteroidCluster;

    static int cost;

    //Constructor method
    Scout(float x, float y, int team) {
        type = "Scout";
        this.team = team;
        width = Main.screenX / 1.9f;
        height = Main.screenY / GameScreen.circleRatio / 1.9f;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 9.5f;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 500;
        health = 1000;
        MAX_HEALTH = 1000;
        accelerate = 0.6f;
        maxSpeed = accelerate * 150;
        MAX_SPEED = maxSpeed;
        preScaleX = 1;
        preScaleY = 1;
        dockable = true;
        sensorRadius = radius * 84;
        setAllAsteroidsUnvisited();
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    boolean checkIfAlive() {
        setSelectColor();
        return !(health <= 0);
    }

    void setAllAsteroidsUnvisited() {
        asteroidsVisited.clear();
        for (int i = 0; i < GameScreen.asteroidClusters.size(); i++) {
            asteroidsVisited.add(new Pair<>(GameScreen.asteroidClusters.get(i), false));
        }
    }

    void scout() {
        if (scouting) {
            return;
        } else {
            scouting = true;
        }
        new Thread(() -> {
            Looper.prepare();
            while (scouting) {
                Utilities.delay(500);
                if (!scouting) {
                    break;
                }
                try {
                    ArrayList<Triple<Ship, Float, Float>> possibleEnemyShips = GameScreen.blackboards[team - 1].possibleEnemyShips;
                    if (possibleEnemyShips.size() == 0) {
                        lastSeenMode = false;
                    }
                    boolean allScouts = true;
                    for (int i = 0; i < possibleEnemyShips.size(); i++) {
                        if (!(possibleEnemyShips.get(i).getFirst() instanceof Scout)) {
                            allScouts = false;
                            break;
                        }
                    }
                    if (!allScouts && !lastSeenMode) {
                        lastSeenMode = true;
                        stop();
                    }

                    if (lastSeenMode) {
                        double closestDistance = Double.MAX_VALUE;
                        int closestIndex = -1;
                        if (possibleEnemyShips.size() > 0) {
                            outer:
                            for (int i = 0; i <= possibleEnemyShips.size() - 1; i++) {
                                if (possibleEnemyShips.get(i).getFirst() instanceof Scout) {
                                    continue;
                                }
                                for (int j = 0; j < GameScreen.blackHole.size(); j++) {
                                    if (Utilities.distanceFormula(possibleEnemyShips.get(i).getSecond(), possibleEnemyShips.get(i).getThird(), GameScreen.blackHole.get(j).positionX, GameScreen.blackHole.get(j).positionY) <= GameScreen.blackHole.get(j).radius * GameScreen.blackHole.get(j).pullDistance + radius) {
                                        continue outer;
                                    }
                                }
                                double distance = Utilities.distanceFormula(centerPosX, centerPosY, possibleEnemyShips.get(i).getSecond(), possibleEnemyShips.get(i).getThird());
                                if (distance <= sensorRadius) {
                                    if (exists) {
                                        possibleEnemyShips.remove(i);
                                    }
                                    continue;
                                }
                                if (distance < closestDistance) {
                                    closestIndex = i;
                                    closestDistance = distance;
                                }
                            }
                            if (!exists) {
                                return;
                            }
                            if (!destination && closestIndex != -1) {
                                float closerRadius = sensorRadius / 1.2f;
                                double angle = Utilities.anglePoints(possibleEnemyShips.get(closestIndex).getSecond(), possibleEnemyShips.get(closestIndex).getThird(), centerPosX, centerPosY);
                                setDestination((float) Utilities.circleAngleX(angle, possibleEnemyShips.get(closestIndex).getSecond(), closerRadius), (float) Utilities.circleAngleY(angle, possibleEnemyShips.get(closestIndex).getThird(), closerRadius));
                            }
                        }
                    } else {
                        if (!exists) {
                            return;
                        }
                        boolean allAsteroidsVisited = true;
                        for (int i = 0; i < asteroidsVisited.size(); i++) {
                            if (!asteroidsVisited.get(i).second) {
                                allAsteroidsVisited = false;
                            }
                        }
                        if (allAsteroidsVisited) {
                            setAllAsteroidsUnvisited();
                        }

                        if (!destination) {
                            while (true) {
                                int randomIndex = Math.round((float) Math.random() * (asteroidsVisited.size() - 1));
                                if (!asteroidsVisited.get(randomIndex).second) {
                                    float clusterRadius = asteroidsVisited.get(randomIndex).first.radius / 1.5f;
                                    double angle = Utilities.anglePoints(asteroidsVisited.get(randomIndex).first.positionX, asteroidsVisited.get(randomIndex).first.positionY, centerPosX, centerPosY);
                                    setDestination((float) Utilities.circleAngleX(angle, asteroidsVisited.get(randomIndex).first.positionX, clusterRadius), (float) Utilities.circleAngleY(angle, asteroidsVisited.get(randomIndex).first.positionY, clusterRadius));
                                    currentAsteroidCluster = asteroidsVisited.get(randomIndex).first;
                                    break;
                                }
                            }
                        } else {
                            if (Utilities.distanceFormula(centerPosX, centerPosY, currentAsteroidCluster.positionX, currentAsteroidCluster.positionY) <= currentAsteroidCluster.radius + radius) {
                                for (int i = 0; i < asteroidsVisited.size(); i++) {
                                    if (asteroidsVisited.get(i).first == currentAsteroidCluster) {
                                        asteroidsVisited.set(i, new Pair<>(currentAsteroidCluster, true));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    System.out.println("Error: " + e);
                }
            }
        }).start();
    }
}