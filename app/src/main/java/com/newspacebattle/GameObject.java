package com.newspacebattle;

import android.animation.ValueAnimator;
import android.graphics.Matrix;

/**
 * Created by Dylan on 2018-07-07. Defines the basic template for any object in the game.
 */
class GameObject {

    long mass;
    int team;
    float positionX, positionY;
    float centerPosX, centerPosY;
    float width, height;
    float midX, midY;
    float velocityX, velocityY;
    float accelerationX, accelerationY;
    float radius, maxSpeed, degrees;
    float gravVelX, gravVelY;
    float accelerate;
    boolean exists, destination, colliding;
    PathFinder destinationFinder;
    String type;
    Matrix appearance = new Matrix();

    //Constructor method
    GameObject() {
        exists = true;
        degrees = (int) ((Math.random() * 360) + 1);
    }

    //Method used to update position if the object has velocity
    void move() {
        if (Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)) <= maxSpeed){
            velocityX += accelerationX;
            velocityY += accelerationY;
        } else {
            velocityX -= velocityX / 150;
            velocityY -= velocityY / 150;
        }

        positionY -= velocityY;
        positionX += velocityX;

        positionY += gravVelY;
        positionX += gravVelX;

        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
    }

    //Testing purposes only
    public String toString() {
        System.out.println("Object Type: " + type);
        System.out.println("Position: " + centerPosX + ", " + centerPosY);
        System.out.println("Velocity: " + velocityX + ", " + velocityY);
        //System.out.println("grav: " + gravVelX + ", " + gravVelY);
        System.out.println("Acceleration: " + accelerationX + ", " + accelerationY);
        System.out.println("Degrees/Radius: " + degrees + ", " + radius);
        System.out.println("Velocity Angle: " + Utilities.angleDim(velocityX, velocityY));
        //System.out.println("Rotated point + 90: " + Utilities.circleAngleX(degrees + 90, centerPosX, radius) + ", " + Utilities.circleAngleY(degrees + 90, centerPosY, radius));
        return "";
    }
}