package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-06. Defines a scout ship.
 */
class Scout extends Ship {

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
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 500;
        health = 1000;
        accelerate = 0.35f;
        maxSpeed = accelerate * 75;
        preScaleX = 1;
        preScaleY = 1;
        dockable = true;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }
}