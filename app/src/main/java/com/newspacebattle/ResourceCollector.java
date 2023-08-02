package com.newspacebattle;

import android.os.Looper;

/**
 * Created by Dylan on 2018-06-30. Defines a resource collector ship.
 */
class ResourceCollector extends Ship {

    final double RESOURCE_CAPACITY = 2500;
    boolean harvesting, unloading;
    double resources;
    private Asteroid asteroidSelected;

    static float constRadius;
    static float MAX_SPEED;

    static int cost;
    FlagShip flagShipSelected = null;

    //Constructor method
    ResourceCollector(float x, float y, int team) {
        type = "ResourceCollector";
        this.team = team;
        width = Main.screenX / 2;
        height = Main.screenY / GameScreen.circleRatio / 2;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 9;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 1250;
        health = 1000;
        MAX_HEALTH = 1000;
        resources = 0;
        accelerate = 0.35f;
        maxSpeed = accelerate * 75;
        MAX_SPEED = maxSpeed;
        preScaleX = 1;
        preScaleY = 1;
        harvesting = false;
        unloading = false;
        dockable = true;
        sensorRadius = radius * 12;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
        if (selected){
            System.out.println("harvesting: " + harvesting + ", unloading: " + unloading + ", destination: " + destination + ", asteroidSelected: " + (asteroidSelected == null));
        }
    }

    //Finds closest asteroid with resources
    void goToAsteroid() {
        if (resources < 0) {
            resources = 0;
        }
        if (resources >= 2000 || GameScreen.asteroids.size() == 0) {
            goToCollector();
            return;
        }
        unloading = false;
        harvesting = true;
        docking = false;
        double nearest = 99999999999999999.0;
        if (asteroidSelected != null) {
            asteroidSelected.incomingResourceCollector = null;
        }
        asteroidSelected = null;
        for (int i = 0; i <= GameScreen.asteroids.size() - 1; i++) {
            if (Math.sqrt(Math.abs(Math.pow(GameScreen.asteroids.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.asteroids.get(i).centerPosY - centerPosY, 2))) < nearest && GameScreen.asteroids.get(i).resources > 0) {
                if (GameScreen.asteroids.get(i).incomingResourceCollector == null) {
                    asteroidSelected = GameScreen.asteroids.get(i);
                    nearest = Math.sqrt(Math.abs(Math.pow(GameScreen.asteroids.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.asteroids.get(i).centerPosY - centerPosY, 2)));
                }
            }
        }
        if (asteroidSelected != null) {
            asteroidSelected.incomingResourceCollector = this;
            formation = null;
            setDestination(asteroidSelected.centerPosX, asteroidSelected.centerPosY);
        }
    }

    //Mines the asteroid
    void mineAsteroid() {
        new Thread(() -> {
            Looper.prepare();
            while (resources < RESOURCE_CAPACITY && harvesting && asteroidSelected.resources > 0) {
                velocityX = asteroidSelected.velocityX;
                velocityY = asteroidSelected.velocityY;
                resources += 4;
                asteroidSelected.resources -= 4;
                Utilities.delay(16);
            }
            goToCollector();
        }).start();
    }

    //Finds nearest flagship
    private void goToCollector() {
        if (asteroidSelected != null) {
            asteroidSelected.incomingResourceCollector = null;
            asteroidSelected = null;
        }
        unloading = true;
        harvesting = false;
        double nearest = 99999999999999999.0;
        flagShipSelected = null;
        for (int i = 0; i <= GameScreen.flagShips.size() - 1; i++) {
            if (GameScreen.flagShips.get(i).team == team) {
                if (Math.sqrt(Math.abs(Math.pow(GameScreen.flagShips.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.flagShips.get(i).centerPosY - centerPosY, 2))) < nearest) {
                    flagShipSelected = GameScreen.flagShips.get(i);
                    nearest = Math.sqrt(Math.abs(Math.pow(GameScreen.flagShips.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.flagShips.get(i).centerPosY - centerPosY, 2)));
                }
            }
        }
        if (flagShipSelected != null) {
            setDestination(flagShipSelected.centerPosX, flagShipSelected.centerPosY);
        }
    }

    //Transfers resources to flagship
    void transferRes() {
        new Thread(() -> {
            Looper.prepare();
            while (!(resources < 4) && unloading) {
                velocityX = flagShipSelected.velocityX;
                velocityY = flagShipSelected.velocityY;
                resources -= 4;
                GameScreen.resources[team - 1] += 4;
                if (resources > 0) {
                    Utilities.delay(16);
                }
            }
            goToAsteroid();
        }).start();
    }
}