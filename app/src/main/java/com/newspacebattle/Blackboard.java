package com.newspacebattle;

import java.util.ArrayList;

import kotlin.Triple;

class Blackboard {
    int team, messageCount;
    boolean newMessage;
    ArrayList<String> log = new ArrayList<>();
    ArrayList<Ship> discoveredEnemyShips = new ArrayList<>();
    ArrayList<Ship> visibleEnemyShips = new ArrayList<>();
    ArrayList<Triple<Ship, Float, Float>> possibleEnemyShips = new ArrayList<>();

    Blackboard(int team) {
        this.team = team;
        if (teamSize() == 0) {
            return;
        }
        addToLog("Welcome to SpaceBattle!");

        new Thread(() -> {
            while (true) {
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
}