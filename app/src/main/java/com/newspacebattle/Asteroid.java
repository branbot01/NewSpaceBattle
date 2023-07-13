package com.newspacebattle;

import java.util.Arrays;

/**
 * Created by Dylan on 2018-06-30. Asteroid class defines the asteroid object.
 */
class Asteroid extends GameObject {

    private int scale;
    double resources;
    boolean hasResLeft;
    ResourceCollector incomingResourceCollector;

    boolean[] availableDockSpots = new boolean[4];

    //Constructor method
    Asteroid(float x, float y, int size) {
        type = "Asteroid";
        scale = size;
        resources = 10000 * scale;
        mass = 1000L * scale + (int) (0.1 * resources);
        width = Main.screenX / 9 * scale;
        height = Main.screenY / GameScreen.circleRatio / 9 * scale;
        midX = width / 2;
        midY = height / 2;
        radius = midX;
        positionX = x - midX;
        positionY = y - midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        velocityX = 0;
        velocityY = 0;
        hasResLeft = true;
        degrees = (int) (Math.random() * 360 + 1);
        incomingResourceCollector = null;
    }

    //Updates the object's properties
    public void update() {
        updateResourceCollector();
        hasResLeft = checkResources();
        move();
        matrix();
    }

    private void updateResourceCollector() {
        if (incomingResourceCollector != null) {
            if (!incomingResourceCollector.exists){
                incomingResourceCollector = null;
            }
        }
    }

    //Checks whether asteroid still has resources, if not, asteroid blows up
    private boolean checkResources() {
        if (resources <= 0) {
            updateResourceCollector();
            return false;
        }
        mass = 1000L * scale + (int) (0.1 * resources);
        return true;
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale / 2, scale / 2);
        appearance.postTranslate(positionX, positionY);
    }
}