package com.newspacebattle;

import android.os.Looper;

/**
 * Created by Dylan on 2018-07-06. Defines a laser cruiser ship
 *
 * Continued by Brandon on 2023-06-23.
 */
class LaserCruiser extends Ship {

    private PointObject laser1, laser2;

    static float constRadius;
    static float MAX_SPEED;

    static int buildTime, cost;

    //Constructor method
    LaserCruiser(float x, float y, int team) {
        type = "LaserCruiser";
        this.team = team;
        canAttack = true;
        width = Main.screenX * 1.75f;
        height = Main.screenY / GameScreen.circleRatio * 1.75f;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 3.5f;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 3000;
        health = 7500;
        MAX_HEALTH = 7500;
        accelerate = 0.35f;
        maxSpeed = accelerate * 75;
        MAX_SPEED = maxSpeed;
        preScaleX = 1.75f;
        preScaleY = 1.75f;
        dockable = false;
        laserPower = 400; //to be changed
        laser1 = new PointObject(0, 0);
        laser2 = new PointObject(0, 0);
        shootTime = 2500;
        driveTime = 1000;
        sensorRadius = radius * 20;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        if (autoAttack){
            destinationFinder.autoAttack();
        }
        move();
        rotate();
    }

    //Shoots two lasers forward
    void shoot() {
        laser1.x = Utilities.circleAngleX(degrees - 12, centerPosX, (radius + Laser.SIZE) * 1.2 * 3);
        laser1.y = Utilities.circleAngleY(degrees - 12, centerPosY, (radius + Laser.SIZE) * 1.2 * 3);

        laser2.x = Utilities.circleAngleX(degrees + 5, centerPosX, (radius + Laser.SIZE) * 0.95 * 3.7);
        laser2.y = Utilities.circleAngleY(degrees + 5, centerPosY, (radius + Laser.SIZE) * 0.95 * 3.7);

        for (int i = 0; i <= GameScreen.lasers.size() - 1; i++) {
            if (!GameScreen.lasers.get(i).exists) {
                GameScreen.lasers.get(i).createLaser(
                        (float) laser1.x
                        , (float) laser1.y
                        , team
                        , (float) (Laser.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Laser.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , laserPower
                        , this);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.lasers.size() - 1; i++) {
            if (!GameScreen.lasers.get(i).exists) {
                GameScreen.lasers.get(i).createLaser(
                        (float) laser2.x
                        , (float) laser2.y
                        , team
                        , (float) (Laser.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Laser.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , laserPower
                        , this);
                break;
            }
        }
    }
}
