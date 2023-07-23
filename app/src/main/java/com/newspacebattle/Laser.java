package com.newspacebattle;

/**
 * Created by Dylan on 2019-01-15. Defines a laser object
 * Continued by Brandon on 2023-06-23.
 */
class Laser extends GameObject {

    final static float SIZE = Main.screenY / GameScreen.circleRatio / 3f, MAX_SPEED = 700; //may change later
    private float scale, damage;

    private int timeLeft;
    private Ship ownShip;

    //Constructor method
    Laser() {
        type = "Laser";
        width = Main.screenX / 2f;
        height = Main.screenY / GameScreen.circleRatio / 2f;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        mass = 1;  //may change later
        exists = false;
        maxSpeed = MAX_SPEED;
        scale = 4;

    }

    //Spawns a laser from a ship
    void createLaser(float x, float y, int team, float xVel, float yVel, float angle, float damage, Ship ownShip) {
        exists = true;
        this.team = team;
        this.damage = damage;
        this.ownShip = ownShip;
        degrees = angle;
        velocityX = xVel;
        velocityY = yVel;
        positionX = x - midX;
        positionY = y - midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        timeLeft = 5000; //to be changed
    }

    //Updates the object's properties
    void update() {
        move();
        matrix();
        countDown();
    }

    //Counts down the laser's lifetime
    private void countDown() {
        timeLeft -= 16;
        if (timeLeft <= 0) {
            impact(null);
        }
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale, 5 * scale);
        appearance.postTranslate(positionX, positionY);
    }

    //When laser hits another object(incomplete)
    void impact(GameObject object) {
        if (object instanceof Ship) {
            ((Ship) object).health -= damage;
        }
        for (int i = 0; i <= GameScreen.explosions.size() - 1; i++) {
            if (!GameScreen.explosions.get(i).active) {
                GameScreen.explosions.get(i).createExplosion(this);
                break;
            }
        }
        exists = false;
    }
}