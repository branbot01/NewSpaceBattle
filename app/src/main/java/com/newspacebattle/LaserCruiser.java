package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-06. Defines a laser cruiser ship
 */
class LaserCruiser extends Ship {

    private PointObject laser1, laser2;

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
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 3000;
        health = 7500;
        accelerate = 0.35f;
        maxSpeed = accelerate * 75;
        preScaleX = 1.75f;
        preScaleY = 1.75f;
        dockable = false;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    //Shoots two lasers forward
    void shoot() {
        laser1.x = Utilities.circleAngleX(degrees - 25, centerPosX, (radius + Laser.SIZE) * 1.2);
        laser1.y = Utilities.circleAngleY(degrees - 25, centerPosY, (radius + Laser.SIZE) * 1.2);

        laser2.x = Utilities.circleAngleX(degrees + 25, centerPosX, (radius + Laser.SIZE) * 1.2);
        laser2.y = Utilities.circleAngleY(degrees + 25, centerPosY, (radius + Laser.SIZE) * 1.2);
    }
}