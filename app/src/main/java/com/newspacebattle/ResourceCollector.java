package com.newspacebattle;

import android.os.Looper;

/**
 * Created by Dylan on 2018-06-30. Defines a resource collector ship.
 */
class ResourceCollector extends Ship {

    private final double RESOURCE_CAPACITY = 2500;
    boolean harvesting, unloading;
    double resources;
    private Asteroid asteroidSelected;
    private FlagShip flagShipSelected = null;

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
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 1250;
        health = 1000;
        resources = 0;
        accelerate = 0.35f;
        maxSpeed = accelerate * 75;
        preScaleX = 1;
        preScaleY = 1;
        harvesting = false;
        unloading = false;
        dockable = true;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
        if (resources > 0) {
            System.out.println(resources);
        }
    }

    //Finds closest asteroid with resources
    void goToAsteroid() {
        if (resources * 0.8 >= 2000) {
            goToCollector();
            return;
        }
        unloading = false;
        harvesting = true;
        double nearest = 99999999999999999.0;
        asteroidSelected = null;
        for (int i = 0; i <= GameScreen.asteroids.size() - 1; i++) {
            if (Math.sqrt(Math.abs(Math.pow(GameScreen.asteroids.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.asteroids.get(i).centerPosY - centerPosY, 2))) < nearest && GameScreen.asteroids.get(i).resources > 0) {
                asteroidSelected = GameScreen.asteroids.get(i);
                nearest = Math.sqrt(Math.abs(Math.pow(GameScreen.asteroids.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.asteroids.get(i).centerPosY - centerPosY, 2)));
            }
        }
        if (asteroidSelected != null) {
            setDestination(asteroidSelected.centerPosX, asteroidSelected.centerPosY, false);
        }
    }

    //Mines the asteroid
    void mineAsteroid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (resources < RESOURCE_CAPACITY && harvesting && asteroidSelected.resources > 0) {
                    velocityX = asteroidSelected.velocityX;
                    velocityY = asteroidSelected.velocityY;
                    if (resources <= 2500) {
                        resources += 4;
                        asteroidSelected.resources -= 4;
                    }
                    Utilities.delay(16);
                }
                goToCollector();
            }
        }).start();
    }

    //Finds nearest flagship
    private void goToCollector() {
        unloading = true;
        harvesting = false;
        double nearest = 99999999999999999.0;
        flagShipSelected = null;
        for (int i = 0; i <= GameScreen.flagShips.size() - 1; i++) {
            if (Math.sqrt(Math.abs(Math.pow(GameScreen.flagShips.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.flagShips.get(i).centerPosY - centerPosY, 2))) < nearest) {
                flagShipSelected = GameScreen.flagShips.get(i);
                nearest = Math.sqrt(Math.abs(Math.pow(GameScreen.flagShips.get(i).centerPosX - centerPosX, 2) + Math.pow(GameScreen.flagShips.get(i).centerPosY - centerPosY, 2)));
            }
        }
        if (flagShipSelected != null) {
            setDestination(flagShipSelected.centerPosX, flagShipSelected.centerPosY, false);
        }
    }

    //Transfers resources to flagship
    void transferRes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (!(resources < 0) && unloading) {
                    velocityX = flagShipSelected.velocityX;
                    velocityY = flagShipSelected.velocityY;
                    resources -= 4;
                    flagShipSelected.resources += 4;
                    Utilities.delay(16);
                }
                goToAsteroid();
            }
        }).start();
    }
}