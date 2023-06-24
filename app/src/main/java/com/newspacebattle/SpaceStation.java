package com.newspacebattle;

import android.graphics.Matrix;

import java.util.ArrayList;

/**
 * Created by Dylan on 2018-08-06. Defines a space station object.
 */
class SpaceStation extends Ship {

    Matrix ringSpiral1 = new Matrix(), ringSpiral2 = new Matrix(), ringSpiral3 = new Matrix();
    private int ring1Degrees, ring2Degrees, ring3Degrees;

    int maxDockedNum = 2; //to be changed

    ArrayList<Ship> dockedShips = new ArrayList<>();

    //Constructor method
    SpaceStation(float x, float y, int team) {
        type = "SpaceStation";
        this.team = team;
        width = Main.screenX * 7;
        height = Main.screenY / GameScreen.circleRatio * 7;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 25000;
        health = 40000;
        accelerate = 0;
        maxSpeed = 0;
        preScaleX = 7;
        preScaleY = 7;
        dockable = false;
        canWarp = true;
        degrees = 0;
        ring1Degrees = 90;
        ring2Degrees = 180;
        ring3Degrees = 270;
    }

    //Updates the object's properties
    public void update() {
        exists = checkIfAlive();
        move();
        setRotation();
        rotateRings();
    }

    //Handles the spinning of the station
    private void setRotation() {
        if (!GameScreen.paused) {
            degrees += 2;
            ring2Degrees += 2;
            ring1Degrees -= 2;
            ring3Degrees -= 2;
            if (degrees == 360) {
                degrees = 0;
                ring1Degrees = 90;
                ring2Degrees = 180;
                ring3Degrees = 0;
            }
        }
    }

    //Draws the ship properly
    private void rotateRings() {
        appearance.setRotate(degrees, midX, midY);
        appearance.postTranslate(positionX, positionY);
        appearance.preScale(preScaleX, preScaleY);

        ringSpiral1.setRotate(ring1Degrees, midX, midY);
        ringSpiral1.postTranslate(positionX, positionY);
        ringSpiral1.preScale(preScaleX * 2, preScaleY * 2);

        ringSpiral2.setRotate(ring2Degrees, midX, midY);
        ringSpiral2.postTranslate(positionX, positionY);
        ringSpiral2.preScale(preScaleX * 2, preScaleY * 2);

        ringSpiral3.setRotate(ring3Degrees, midX, midY);
        ringSpiral3.postTranslate(positionX, positionY);
        ringSpiral3.preScale(preScaleX * 2, preScaleY * 2);
    }
}
