package com.newspacebattle;

import java.util.ArrayList;
import java.util.Arrays;

import kotlin.Triple;

class Blackboard {
    int team, messageCount;
    boolean newMessage;
    ArrayList<String> log = new ArrayList<>();
    ArrayList<Ship> discoveredEnemyShips = new ArrayList<>();
    ArrayList<Ship> visibleEnemyShips = new ArrayList<>();
    ArrayList<Triple<Ship, Float, Float>> possibleEnemyShips = new ArrayList<>();

    double[][] friendlyGrid = new double[GameScreen.grid_size][GameScreen.grid_size];
    double[][] enemyGrid = new double[GameScreen.grid_size][GameScreen.grid_size];

    Blackboard(int team) {
        this.team = team;
        if (teamSize() == 0) {
            return;
        }
        addToLog("Welcome to SpaceBattle!");

        new Thread(() -> {
            while (teamSize() > 0) {
                if (!GameScreen.paused) {
                    update();
                }
                Utilities.delay(16);
            }
        }).start();
    }

    void addToLog(String log) {
        if (this.log.size() >= 100) {
            this.log.remove(0);
        }
        messageCount++;
        this.log.add(messageCount + ": " + log);
        newMessage = true;
    }

    void update() {
        scan();
        populateGrid();
    }

    private void scan() {
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            try {
                if (GameScreen.ships.get(i).team != this.team) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == this.team && !visibleEnemyShips.contains(GameScreen.ships.get(i)) && Utilities.distanceFormula(GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.ships.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                            if (!discoveredEnemyShips.contains(GameScreen.ships.get(i))) {
                                discoveredEnemyShips.add(GameScreen.ships.get(i));
                                addToLog("Enemy " + GameScreen.ships.get(i).type + " spotted!");
                            }
                            visibleEnemyShips.add(GameScreen.ships.get(i));
                            break;
                        }
                    }
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error: " + e);
            }
        }

        for (int i = 0; i < visibleEnemyShips.size(); i++) {
            try {
                boolean found = false;
                for (int ii = 0; ii < GameScreen.ships.size(); ii++) {
                    if (GameScreen.ships.get(ii).team == this.team && Utilities.distanceFormula(GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY, visibleEnemyShips.get(i).centerPosX, visibleEnemyShips.get(i).centerPosY) <= GameScreen.ships.get(ii).radius + GameScreen.ships.get(ii).sensorRadius + visibleEnemyShips.get(i).radius) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    possibleEnemyShips.add(new Triple<>(visibleEnemyShips.get(i), visibleEnemyShips.get(i).centerPosX, visibleEnemyShips.get(i).centerPosY));
                    visibleEnemyShips.remove(i);
                    continue;
                }
                if (visibleEnemyShips.get(i).health <= 0) {
                    visibleEnemyShips.remove(i);
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error: " + e);
            }
        }

        for (int i = 0; i < possibleEnemyShips.size(); i++) {
            try {
                for (int ii = 0; ii < visibleEnemyShips.size(); ii++) {
                    if (visibleEnemyShips.contains(possibleEnemyShips.get(i).getFirst())) {
                        possibleEnemyShips.remove(i);
                        break;
                    }
                }
                if (!possibleEnemyShips.get(i).getFirst().exists) {
                    possibleEnemyShips.remove(i);
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error: " + e);
            }
        }

        for (int i = 0; i < discoveredEnemyShips.size(); i++) {
            try {
                if (!discoveredEnemyShips.get(i).exists) {
                    discoveredEnemyShips.remove(i);
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error: " + e);
            }
        }
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

    String getLog() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < log.size() - 1; i++) {
            sb.append(log.get(i));
            sb.append("\n");
        }
        sb.append(log.get(log.size() - 1));
        return sb.toString();
    }

    private void populateGrid() {
        Arrays.stream(friendlyGrid).forEach(a -> Arrays.fill(a, 0));
        Arrays.stream(enemyGrid).forEach(a -> Arrays.fill(a, 0));
        for (int i = 0; i < friendlyGrid.length; i++) {
            for (int j = 0; j < friendlyGrid[0].length; j++) {
                try {
                    for (int ship = 0; ship < GameScreen.ships.size(); ship++) {
                        if (GameScreen.ships.get(ship).team == this.team) {
                            if (GameScreen.ships.get(ship).centerPosX >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosX <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                                friendlyGrid[j][i] += GameScreen.ships.get(ship).shipWeight;
                            }
                        }
                        if (visibleEnemyShips.contains(GameScreen.ships.get(ship))) {
                            if (GameScreen.ships.get(ship).centerPosX >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosX <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && GameScreen.ships.get(ship).centerPosY <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                                enemyGrid[j][i] -= GameScreen.ships.get(ship).shipWeight;
                            }
                        }
                    }

                    for (int possibleShip = 0; possibleShip < possibleEnemyShips.size(); possibleShip++) {
                        if (visibleEnemyShips.contains(possibleEnemyShips.get(possibleShip).getFirst())) {
                            continue;
                        }
                        if (possibleEnemyShips.get(possibleShip).getSecond() >= -GameScreen.mapSizeX / 2 + i * GameScreen.mapSizeX / GameScreen.grid_size && possibleEnemyShips.get(possibleShip).getSecond() <= -GameScreen.mapSizeX / 2 + (i + 1) * GameScreen.mapSizeX / GameScreen.grid_size && possibleEnemyShips.get(possibleShip).getThird() >= -GameScreen.mapSizeY / 2 + j * GameScreen.mapSizeY / GameScreen.grid_size && possibleEnemyShips.get(possibleShip).getThird() <= -GameScreen.mapSizeY / 2 + (j + 1) * GameScreen.mapSizeY / GameScreen.grid_size) {
                            enemyGrid[j][i] -= possibleEnemyShips.get(possibleShip).getFirst().shipWeight;
                        }
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    System.out.println("Error in populateGrid: " + e);
                }
            }
        }
    }

    void printGrid(double[][] grid) {
        for (double[] doubles : grid) {
            System.out.print("[");
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(doubles[j] + ", ");
            }
            System.out.println("]");
        }
    }
}