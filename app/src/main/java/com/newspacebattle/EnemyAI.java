package com.newspacebattle;

import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

import kotlin.Triple;

class EnemyAI {
    int team;
    int numResourceCollectors, numScouts;
    float resCentroidX = 0, resCentroidY = 0;
    double buildFleetWeight;
    boolean buildingFleet;

    FlagShip flagShip = null;
    Blackboard blackboard;

    ArrayList<Ship> freeShips = new ArrayList<>();
    ArrayList<Triple<ArrayList<Ship>, Formation, Boolean>> fleets = new ArrayList<>();
    ArrayList<Formation> formations = new ArrayList<>();

    double[][] threats = new double[GameScreen.grid_size][GameScreen.grid_size];

    static final double[] defaultFleet = {0.375, 0.375, 0.1875, 0.0625};

    EnemyAI(int team, Blackboard blackboard) {
        this.team = team;
        this.blackboard = blackboard;
        if (teamSize() == 0) {
            return;
        }

        if (team == 1) {
            formations = GameScreen.formationsTeam1;
        } else if (team == 2) {
            formations = GameScreen.formationsTeam2;
        } else if (team == 3){
            formations = GameScreen.formationsTeam3;
        } else if (team == 4) {
            formations = GameScreen.formationsTeam4;
        }

        new Thread(() -> {
            Looper.prepare();
            while (teamSize() > 0) {
                if (!GameScreen.paused) {
                    update();
                }
                Utilities.delay(3000);
            }
        }).start();
    }

    void update() {
        shipLoop();
        updateThreats();
        flagShip();
        assessFreeShips();
        handleFleets();
    }

    private void shipLoop() {
        int numFlagShip = 0, numResourceCollectors = 0, numScouts = 0;
        float resCentroidX = 0, resCentroidY = 0;

        for (int i = 0; i < GameScreen.ships.size(); i++) {
            if (GameScreen.ships.get(i).team != team) {
                continue;
            }

            Ship ship = GameScreen.ships.get(i);
            ship.autoAttack = true;

            if (ship instanceof FlagShip) {
                numFlagShip++;
                flagShip = (FlagShip) ship;
                flagShip.enemyAI = this;
            } else if (ship instanceof ResourceCollector) {
                numResourceCollectors++;
                if (!((ResourceCollector) ship).harvesting && !((ResourceCollector) ship).unloading) {
                    ((ResourceCollector) ship).goToAsteroid();
                }
                resCentroidX += ship.centerPosX;
                resCentroidY += ship.centerPosY;
            } else if (ship instanceof Scout) {
                numScouts++;
                if (!((Scout) ship).scouting) {
                    ((Scout) ship).scout();
                }
            }
        }
        if (numFlagShip > 1) {
            throw new RuntimeException("There must be exactly one flagship per team");
        }
        this.numResourceCollectors = numResourceCollectors;
        this.numScouts = numScouts;

        resCentroidX /= numResourceCollectors;
        resCentroidY /= numResourceCollectors;
        this.resCentroidX = resCentroidX;
        this.resCentroidY = resCentroidY;
    }

    private void flagShip() {
        if (flagShip == null) {
            return;
        }

        flagShip.countResourceCollector = 5 - numResourceCollectors;
        flagShip.countScout = 1 - numScouts;

        if (flagShip.countResourceCollector == 0) {
            flagShip.buildingSpaceStation = true;
            flagShip.buildingBattleShip = true;
            flagShip.buildingLaserCruiser = true;
            flagShip.buildingBomber = true;
            flagShip.buildingFighter = true;
            flagShip.buildingScout = true;

            flagShip.buildingResourceCollector = false;
        } else {
            flagShip.buildingSpaceStation = false;
            flagShip.buildingBattleShip = false;
            flagShip.buildingLaserCruiser = false;
            flagShip.buildingBomber = false;
            flagShip.buildingFighter = false;
            flagShip.buildingScout = false;

            flagShip.buildingResourceCollector = true;
        }

        boolean inAsteroidCluster = false;
        for (int i = 0; i < GameScreen.asteroidClusters.size(); i++) {
            if (Utilities.distanceFormula(resCentroidX, resCentroidY, GameScreen.asteroidClusters.get(i).positionX, GameScreen.asteroidClusters.get(i).positionY) < GameScreen.asteroidClusters.get(i).radius + flagShip.radius) {
                inAsteroidCluster = true;
                break;
            }
        }

        if (!inAsteroidCluster && !flagShip.attacking && !flagShip.destination && Utilities.distanceFormula(flagShip.centerPosX, flagShip.centerPosY, resCentroidX, resCentroidY) > flagShip.radius * 3) {
            flagShip.stop();
            flagShip.setDestination(resCentroidX, resCentroidY);
        }
    }

    private void updateThreats() {
        for (int i = 0; i < GameScreen.grid_size; i++) {
            for (int j = 0; j < GameScreen.grid_size; j++) {
                float posX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                float posY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                threats[j][i] = blackboard.enemyGrid[j][i] * 10000 / Utilities.distanceFormula(posX, posY, flagShip.centerPosX, flagShip.centerPosY);
            }
        }
    }

    private void assessFreeShips() {
        if (freeShips.size() == 0) {
            int fleetWeight = (int) Math.log(GameScreen.game_tick);
            if (fleetWeight > 20) {
                fleetWeight = 20;
            } else if (fleetWeight < 8) {
                fleetWeight = 8;
            }
            buildFleet(fleetWeight, defaultFleet);
            return;
        }

        double fleetWeight = 0;
        for (int i = 0; i < freeShips.size(); i++) {
            fleetWeight += freeShips.get(i).shipWeight;
            if (!freeShips.get(i).attacking && !freeShips.get(i).destination) {
                freeShips.get(i).setDestination(flagShip.centerPosX, flagShip.centerPosY);
            }
        }

        if (fleetWeight >= buildFleetWeight) {
            buildingFleet = false;
            Formation formation = new Formation(freeShips, Formation.RECTANGLE_FORMATION);
            ArrayList<Ship> newShips = new ArrayList<>();
            newShips.addAll(freeShips);
            fleets.add(new Triple<>(newShips, formation, false));
            formations.add(formation);
            freeShips.clear();

            int flagShipX = 0, flagShipY = 0;
            outer:
            for (int i = 0; i < GameScreen.grid_size; i++) {
                for (int j = 0; j < GameScreen.grid_size; j++) {
                    for (int ship = 0; ship < GameScreen.ships.size(); ship++) {
                        if (GameScreen.ships.get(ship) == this.flagShip) {
                            if (GameScreen.ships.get(ship).centerPosX >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosX <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                                flagShipX = i;
                                flagShipY = j;
                                break outer;
                            }
                        }
                    }
                }
            }

            outer:
            for (int i = flagShipX - 1; i <= flagShipX + 1; i++) {
                for (int j = flagShipY - 1; j <= flagShipY + 1; j++) {
                    try {
                        if (blackboard.friendlyGrid[j][i] == 0) {
                            float posX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                            float posY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                            formation.setDestination(posX, posY);
                            break outer;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
    }

    private void handleFleets() {
        for (int i = 0; i < fleets.size(); i++) {
            ArrayList<Ship> ships = fleets.get(i).getFirst();
            boolean noneAttacking = true;
            for (int j = 0; j < ships.size(); j++) {
                if (!ships.get(j).exists) {
                    ships.remove(j);
                    j--;
                    continue;
                }
                if (ships.get(j).attacking) {
                    noneAttacking = false;
                }
            }

            if (noneAttacking && ships.size() != fleets.get(i).getSecond().ships.size()){
                Formation formation = new Formation(ships, Formation.RECTANGLE_FORMATION);
                formations.add(formation);
                fleets.set(i, new Triple<>(ships, formation, false));
            }
        }
    }

    private void buildFleet(double minWeight, double[] probabilities) {
        if (probabilities.length != 4) {
            throw new RuntimeException("Probabilities must be of length 4");
        }
        if (buildingFleet) {
            return;
        }

        buildingFleet = true;
        buildFleetWeight = minWeight;
        double weight = 0;
        do {
            int index = Utilities.pickIndex(probabilities);
            if (index == 0) {
                flagShip.countFighter++;
                weight += 1;
            } else if (index == 1) {
                flagShip.countBomber++;
                weight += 1.4;
            } else if (index == 2) {
                flagShip.countLaserCruiser++;
                weight += 3.8;
            } else if (index == 3) {
                flagShip.countBattleShip++;
                weight += 7;
            }
        } while (weight < minWeight);
    }

    private int teamSize() {
        int teamSize = 0;
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            if (GameScreen.ships.get(i).team == team) {
                teamSize++;
            }
        }
        return teamSize;
    }

    public String toString() {
        String teamNumber = "Team: " + team;
        String teamResources = "Team Resources: " + GameScreen.resources[team - 1];
        String buildingStatus = "buildingSpaceStation: " + flagShip.buildingSpaceStation + ", " + flagShip.countSpaceStation + " \n" +
                "buildingResourceCollector: " + flagShip.buildingResourceCollector + ", " + flagShip.countResourceCollector + " \n" +
                "buildingScout: " + flagShip.buildingScout + ", " + flagShip.countScout + " \n" +
                "buildingFighter: " + flagShip.buildingFighter + ", " + flagShip.countFighter + " \n" +
                "buildingBomber: " + flagShip.buildingBomber + ", " + flagShip.countBomber + " \n" +
                "buildingLaserCruiser: " + flagShip.buildingLaserCruiser + ", " + flagShip.countLaserCruiser + " \n" +
                "buildingBattleShip: " + flagShip.buildingBattleShip + ", " + flagShip.countBattleShip + " \n";
        return teamNumber + "\n" + teamResources + "\n" + buildingStatus;
    }
}
