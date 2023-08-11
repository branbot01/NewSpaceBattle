package com.newspacebattle;

import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Objects;

import kotlin.Triple;

class EnemyAI {
    int team, numResourceCollectors, numScouts;
    int flagShipX = 0, flagShipY = 0;
    float resCentroidX, resCentroidY;
    double buildFleetWeight, aggression;
    boolean buildingFleet;

    FlagShip flagShip = null;
    Blackboard blackboard;

    ArrayList<Ship> freeShips = new ArrayList<>();
    ArrayList<Triple<ArrayList<Ship>, Formation, Boolean>> fleets = new ArrayList<>();
    ArrayList<Formation> formations = new ArrayList<>();
    ArrayList<Pair<Integer, Integer>> blackholeCoords = new ArrayList<>();

    double[][] threats = new double[GameScreen.grid_size][GameScreen.grid_size];

    static final double[] defaultFleet = {0.375, 0.375, 0.1875, 0.0625};

    EnemyAI(int team, Blackboard blackboard) {
        this.team = team;
        this.blackboard = blackboard;
        if (teamSize() == 0) {
            return;
        }

        this.aggression = (Math.random() + 1);

        if (team == 1) {
            formations = GameScreen.formationsTeam1;
        } else if (team == 2) {
            formations = GameScreen.formationsTeam2;
        } else if (team == 3) {
            formations = GameScreen.formationsTeam3;
        } else if (team == 4) {
            formations = GameScreen.formationsTeam4;
        }

        new Thread(() -> {
            Looper.prepare();

            if (GameScreen.blackHole.size() > 0) {
                for (int i = 0; i < GameScreen.grid_size; i++) {
                    for (int j = 0; j < GameScreen.grid_size; j++) {
                        for (int k = 0; k < GameScreen.blackHole.size(); k++) {
                            if (GameScreen.blackHole.get(k).centerPosX >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.blackHole.get(k).centerPosX <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.blackHole.get(k).centerPosY >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && GameScreen.blackHole.get(k).centerPosY <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                                blackholeCoords.add(new Pair<>(i, j));
                                if (i + 1 < GameScreen.grid_size)
                                    blackholeCoords.add(new Pair<>(i + 1, j));
                                if (i - 1 >= 0)
                                    blackholeCoords.add(new Pair<>(i - 1, j));
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < blackholeCoords.size(); i++) {
                for (int j = i + 1; j < blackholeCoords.size(); j++) {
                    if (Objects.equals(blackholeCoords.get(i).first, blackholeCoords.get(j).first) && Objects.equals(blackholeCoords.get(i).second, blackholeCoords.get(j).second)) {
                        blackholeCoords.remove(j);
                        j--;
                    }
                }
            }

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
        if (flagShip != null) {
            updateThreats();
            flagShip();
            assessFreeShips();
            handleFleets();
        }
    }

    private void shipLoop() {
        int numFlagShip = 0, numResourceCollectors = 0, numScouts = 0;
        float resCentroidX = 0, resCentroidY = 0;

        for (int i = 0; i < GameScreen.ships.size(); i++) {
            try {
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
            } catch (Exception e) {
                System.out.println("Error in shipLoop: " + e);
                return;
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
        try {
            if (!flagShip.exists) {
                String teamColor = "";
                if (team == 1) {
                    teamColor = "Blue";
                } else if (team == 2) {
                    teamColor = "Red";
                } else if (team == 3) {
                    teamColor = "Yellow";
                } else if (team == 4) {
                    teamColor = "Green";
                }
                for (int i = 0; i < GameScreen.blackboards.length; i++) {
                    GameScreen.blackboards[i].addToLog(teamColor + "'s flagship has been destroyed!");
                }
                flagShip = null;
                return;
            }

            if (GameScreen.difficulty == 1) {
                flagShip.countResourceCollector = 3 - numResourceCollectors;
            } else if (GameScreen.difficulty == 2) {
                flagShip.countResourceCollector = 4 - numResourceCollectors;
            } else {
                if (GameScreen.resources[team - 1] < 50000) {
                    flagShip.countResourceCollector = 5 - numResourceCollectors;
                } else {
                    flagShip.countResourceCollector = 0;
                }
            }
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

            boolean closeToBlackHole = false;
            for (int i = 0; i < GameScreen.blackHole.size(); i++) {
                if (Utilities.distanceFormula(resCentroidX, resCentroidY, GameScreen.blackHole.get(i).centerPosX, GameScreen.blackHole.get(i).centerPosY) < GameScreen.blackHole.get(i).radius * GameScreen.blackHole.get(i).pullDistance) {
                    closeToBlackHole = true;
                    double angle = Utilities.anglePoints(flagShip.centerPosX, flagShip.centerPosY, GameScreen.blackHole.get(i).centerPosX, GameScreen.blackHole.get(i).centerPosY) + 180;
                    if (angle > 360) {
                        angle -= 360;
                    }
                    float x = (float) Utilities.circleAngleX(angle, flagShip.centerPosX, GameScreen.blackHole.get(i).radius * GameScreen.blackHole.get(i).pullDistance * 2);
                    float y = (float) Utilities.circleAngleY(angle, flagShip.centerPosY, GameScreen.blackHole.get(i).radius * GameScreen.blackHole.get(i).pullDistance * 2);
                    if (!flagShip.destination) {
                        flagShip.stop();
                        flagShip.setDestination(x, y);
                    } else {
                        if (!(x + flagShip.radius >= GameScreen.mapSizeX / 2f || x - flagShip.radius <= -GameScreen.mapSizeX / 2f || y + flagShip.radius >= GameScreen.mapSizeY / 2f || y - flagShip.radius <= -GameScreen.mapSizeY / 2f)) {
                            flagShip.destinationFinder.destX = x;
                            flagShip.destinationFinder.destY = y;
                        }
                    }
                    break;
                }
            }

            if (!closeToBlackHole && !inAsteroidCluster && !flagShip.attacking && Utilities.distanceFormula(flagShip.centerPosX, flagShip.centerPosY, resCentroidX, resCentroidY) > flagShip.radius * 3) {
                if (!flagShip.destination) {
                    flagShip.stop();
                    flagShip.setDestination(resCentroidX, resCentroidY);
                } else {
                    flagShip.destinationFinder.destX = resCentroidX;
                    flagShip.destinationFinder.destY = resCentroidY;
                }
            }

            double highestThreatPosX = 0, highestThreatPosY = 0;
            double highestThreat = 0;
            for (int i = 0; i < GameScreen.grid_size; i++) {
                for (int j = 0; j < GameScreen.grid_size; j++) {
                    if (threats[j][i] < highestThreat) {
                        highestThreat = threats[j][i];
                        highestThreatPosX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                        highestThreatPosY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                    }
                }
            }

            if (highestThreat < -2 && Utilities.distanceFormula(flagShip.centerPosX, flagShip.centerPosY, highestThreatPosX, highestThreatPosY) < flagShip.radius * 20) {
                for (int i = 0; i < fleets.size(); i++) {
                    if (fleets.get(i).getThird() || fleets.get(i).getSecond().destination) {
                        continue;
                    }

                    fleets.get(i).getSecond().setDestination((float) highestThreatPosX, (float) highestThreatPosY);
                }

                if (flagShip.attacking){
                    flagShip.stop();
                }

                if (GameScreen.difficulty >= 2) {
                    float angle = (float) Utilities.anglePoints(flagShip.centerPosX, flagShip.centerPosY, highestThreatPosX, highestThreatPosY) + 180;
                    if (angle > 360) {
                        angle -= 360;
                    }
                    float x = (float) Utilities.circleAngleX(angle, flagShip.centerPosX, flagShip.radius * 4);
                    float y = (float) Utilities.circleAngleY(angle, flagShip.centerPosY, flagShip.radius * 4);
                    if (!flagShip.destination) {
                        flagShip.stop();
                        flagShip.setDestination(x, y);
                    } else {
                        if (!(x + flagShip.radius >= GameScreen.mapSizeX / 2f || x - flagShip.radius <= -GameScreen.mapSizeX / 2f || y + flagShip.radius >= GameScreen.mapSizeY / 2f || y - flagShip.radius <= -GameScreen.mapSizeY / 2f)) {
                            flagShip.destinationFinder.destX = x;
                            flagShip.destinationFinder.destY = y;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in flagShip: " + e);
        }
    }

    private void updateThreats() {
        try {
            for (int i = 0; i < GameScreen.grid_size; i++) {
                for (int j = 0; j < GameScreen.grid_size; j++) {
                    float posX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                    float posY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                    threats[j][i] = blackboard.enemyGrid[j][i] * 10000 / Utilities.distanceFormula(posX, posY, flagShip.centerPosX, flagShip.centerPosY);

                    for (int ship = 0; ship < GameScreen.flagShips.size(); ship++) {
                        if (GameScreen.flagShips.get(ship) == this.flagShip) {
                            if (GameScreen.flagShips.get(ship).centerPosX >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.flagShips.get(ship).centerPosX <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.flagShips.get(ship).centerPosY >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && GameScreen.flagShips.get(ship).centerPosY <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                                flagShipX = i;
                                flagShipY = j;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in updateThreats: " + e);
        }
    }

    private void assessFreeShips() {
        if (freeShips.size() == 0) {
            int newFleetWeight = (int) Math.log(GameScreen.game_tick);
            if (newFleetWeight > 20) {
                newFleetWeight = 20;
            } else if (newFleetWeight < 8) {
                newFleetWeight = 8;
            }
            double chance = Math.random();
            if (GameScreen.difficulty == 1){
                if (chance < 0.1)
                    buildFleet(newFleetWeight, defaultFleet);
            } else if (GameScreen.difficulty == 2) {
                if (chance < 0.4)
                    buildFleet(newFleetWeight, defaultFleet);
            } else {
                buildFleetWeight = 20;
                try {
                    flagShip.countFighter = 20;
                    flagShip.countBomber = 20;
                    flagShip.countLaserCruiser = 20;
                    flagShip.countBattleShip = 20;
                } catch (Exception e) {
                    System.out.println("Error in assessFreeShips1: " + e);
                }
            }
            return;
        }

        try {
            double fleetWeight = 0;
            for (int i = 0; i < freeShips.size(); i++) {
                fleetWeight += freeShips.get(i).shipWeight;
                if (!freeShips.get(i).attacking && !freeShips.get(i).destination) {
                    freeShips.get(i).setDestination(flagShip.centerPosX, flagShip.centerPosY);
                }
            }

            if (fleetWeight >= buildFleetWeight) {
                buildingFleet = false;
                int randomType = (int) (Math.random() * 3);
                Formation formation = new Formation(freeShips, randomType);
                ArrayList<Ship> newShips = new ArrayList<>();
                newShips.addAll(freeShips);
                fleets.add(new Triple<>(newShips, formation, false));
                formations.add(formation);
                freeShips.clear();
            } else if (flagShip.countFighter + flagShip.countBomber + flagShip.countLaserCruiser + flagShip.countBattleShip == 0) {
                int newFleetWeight = (int) Math.log(GameScreen.game_tick);
                if (newFleetWeight > 20) {
                    newFleetWeight = 20;
                } else if (newFleetWeight < 8) {
                    newFleetWeight = 8;
                }
                double chance = Math.random();
                if (GameScreen.difficulty == 1){
                    if (chance < 0.1)
                        buildFleet(newFleetWeight, defaultFleet);
                } else if (GameScreen.difficulty == 2) {
                    if (chance < 0.4)
                        buildFleet(newFleetWeight, defaultFleet);
                } else {
                    buildFleet(newFleetWeight, defaultFleet);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in assessFreeShips: " + e);
        }
    }

    private void handleFleets() {
        try {
            double enemyFleetWeight = 0;
            for (int i = 0; i < blackboard.discoveredEnemyShips.size(); i++) {
                enemyFleetWeight += blackboard.discoveredEnemyShips.get(i).shipWeight;
            }

            double defensiveFleetWeight = 0;
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

                if (noneAttacking && ships.size() != fleets.get(i).getSecond().ships.size()) {
                    if (ships.size() == 1) {
                        freeShips.add(ships.get(0));
                        fleets.remove(i);
                        i--;
                        continue;
                    } else {
                        Formation formation = new Formation(ships, fleets.get(i).getSecond().type);
                        formations.add(formation);
                        fleets.set(i, new Triple<>(ships, formation, false));

                        boolean closeToBlackHole = false;
                        for (int j = 0; j < GameScreen.blackHole.size(); j++) {
                            if (Utilities.distanceFormula(formation.centerX, formation.centerY, GameScreen.blackHole.get(j).centerPosX, GameScreen.blackHole.get(j).centerPosY) < GameScreen.blackHole.get(j).radius * GameScreen.blackHole.get(j).pullDistance) {
                                closeToBlackHole = true;
                                break;
                            }
                        }

                        if (closeToBlackHole) {
                            formation.disbandFormation();
                            formations.remove(formation);
                            fleets.remove(i);
                            freeShips.addAll(ships);
                            i--;
                            continue;
                        }
                    }
                }

                if (!fleets.get(i).getThird()) {
                    for (int j = 0; j < ships.size(); j++) {
                        defensiveFleetWeight += ships.get(j).shipWeight;
                    }
                } else {
                    if (!fleets.get(i).getSecond().destination) {
                        fleets.set(i, new Triple<>(ships, fleets.get(i).getSecond(), false));
                    }
                }
            }

            float highestThreatPosX = 0, highestThreatPosY = 0;
            double highestThreat = 0;
            double threatWeight = 0;
            for (int i = 0; i < GameScreen.grid_size; i++) {
                for (int j = 0; j < GameScreen.grid_size; j++) {
                    if (threats[j][i] < highestThreat) {
                        highestThreat = threats[j][i];
                        highestThreatPosX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                        highestThreatPosY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                        threatWeight = blackboard.enemyGrid[j][i];
                    }
                }
            }

            if (defensiveFleetWeight > enemyFleetWeight * aggression) {
                if (!(threatWeight == 0 || highestThreat == 0)) {
                    for (int i = 0; i < fleets.size(); i++) {
                        if (fleets.get(i).getThird()) {
                            continue;
                        }

                        double individualFleetWeight = 0;
                        ArrayList<Ship> ships = fleets.get(i).getFirst();
                        for (int j = 0; j < ships.size(); j++) {
                            individualFleetWeight += ships.get(j).shipWeight;
                        }

                        if (individualFleetWeight >= threatWeight) {
                            fleets.get(i).getSecond().setDestination(highestThreatPosX, highestThreatPosY);
                            fleets.set(i, new Triple<>(fleets.get(i).getFirst(), fleets.get(i).getSecond(), true));
                            break;
                        }
                    }
                }
            }

            for (int l = 0; l < fleets.size(); l++){
                if (fleets.get(l).getThird() || fleets.get(l).getSecond().destination){
                    continue;
                }

                double minDistance = Double.MAX_VALUE;
                float minPosX = 0, minPosY = 0;
                outer:
                for (int i = flagShipX - 1; i <= flagShipX + 1; i++) {
                    inner:
                    for (int j = flagShipY - 1; j <= flagShipY + 1; j++) {
                        for (int k = 0; k < blackholeCoords.size(); k++) {
                            if (blackholeCoords.get(k).first == i && blackholeCoords.get(k).second == j) {
                                break inner;
                            }
                        }
                        try {
                            float posX = (-GameScreen.mapSizeX / 2f + i * GameScreen.mapSizeX / GameScreen.grid_size + -GameScreen.mapSizeX / 2f + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size) / 2f;
                            float posY = (-GameScreen.mapSizeY / 2f + j * GameScreen.mapSizeY / GameScreen.grid_size + -GameScreen.mapSizeY / 2f + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) / 2f;
                            if (threatWeight == 0 || highestThreat == 0) {
                                double chance = Math.random();
                                if (blackboard.friendlyGrid[j][i] == 0 && chance < 0.1) {
                                    fleets.get(l).getSecond().setDestination(posX, posY);
                                    break outer;
                                }
                            } else {
                                double distance = Utilities.distanceFormula(posX, posY, highestThreatPosX, highestThreatPosY);
                                if (distance < minDistance && blackboard.friendlyGrid[j][i] == 0) {
                                    minDistance = distance;
                                    minPosX = posX;
                                    minPosY = posY;
                                }
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                    }
                }

                if (minDistance != Double.MAX_VALUE && minPosX != fleets.get(l).getSecond().destX && minPosY != fleets.get(l).getSecond().destY) {
                    fleets.get(l).getSecond().setDestination(minPosX, minPosY);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in handleFleets: " + e);
        }
    }

    private void buildFleet(double minWeight, double[] probabilities) {
        if (probabilities.length != 4) {
            throw new RuntimeException("Probabilities must be of length 4");
        }
        if (buildingFleet) {
            return;
        }

        try {
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
        } catch (Exception e) {
            System.out.println("Error in buildFleet: " + e);
        }
    }

    private int teamSize() {
        int teamSize;
        try {
            teamSize = 0;
            for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
                if (GameScreen.ships.get(i).team == team) {
                    teamSize++;
                }
            }
        } catch (Exception e) {
            teamSize = 1;
            System.out.println("Error in teamSize: " + e);
        }
        return teamSize;
    }
}