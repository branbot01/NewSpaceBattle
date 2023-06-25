package com.newspacebattle;

import java.util.Arrays;

/**
 * Created by Dylan on 2018-06-30. Asteroid class defines the asteroid object.
 */
class Asteroid extends GameObject {

    private int scale;
    double resources;
    boolean hasResLeft;
    boolean incomingResourceCollector;

    boolean[] availableDockSpots = new boolean[4];

    //Constructor method
    Asteroid(float x, float y, int size) {
        type = "Asteroid";
        scale = size;
        resources = 10000 * scale;
        mass = 1000 * scale + (int) (0.1 * resources);
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
        incomingResourceCollector = false;
    }

    //Updates the object's properties
    public void update() {
        hasResLeft = checkResources();
        move();
        matrix();
    }

    //Checks whether asteroid still has resources, if not, asteroid blows up
    private boolean checkResources() {
        if (resources <= 0) {
            return false;
        }
        mass = 1000 * scale + (int) (0.1 * resources);
        return true;
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale / 2, scale / 2);
        appearance.postTranslate(positionX, positionY);
    }
}