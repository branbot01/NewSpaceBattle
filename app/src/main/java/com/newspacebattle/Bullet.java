package com.newspacebattle;

/**
 * Created by Dylan on 2019-01-09. Defines a bullet object
 */
class Bullet extends GameObject {

    final static float SIZE = Main.screenY / GameScreen.circleRatio / 6f / 2, MAX_SPEED = 600;
    private float scale, damage;
    private int timeLeft;
    boolean visible;
    private Ship ownShip;

    //Constructor method
    Bullet() {
        type = "Bullet";
        width = Main.screenX / 6f;
        height = Main.screenY / GameScreen.circleRatio / 6f;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        mass = 20;
        maxSpeed = MAX_SPEED;
        scale = 1;
        exists = false;

    }

    //Spawns a bullet where a ship has fired, doesn't create a new bullet object
    void createBullet(float x, float y, int team, float xVel, float yVel, float angle, float damage, Ship ownShip) {
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
        timeLeft = 3000;
    }

    //Updates the object's properties
    void update() {
        move();
        matrix();
        countDown();
    }

    //Counts down the bullet's lifetime
    private void countDown() {
        timeLeft -= 16;
        if (timeLeft <= 0) {
            impact(null);
        }
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale, scale);
        appearance.postTranslate(positionX, positionY);
    }

    //When bullet hits another object, spawn explosion, to damage to other ship, destroy itself
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
