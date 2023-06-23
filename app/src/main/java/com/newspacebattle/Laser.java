package com.newspacebattle;

/**
 * Created by Dylan on 2019-01-15. Defines a laser object
 */
class Laser extends GameObject {

    final static float SIZE = Main.screenY / GameScreen.circleRatio / 3f / 2;
    private float scale, damage;

    //Constructor method
    Laser() {
        type = "Laser";
        exists = false;
        scale = 1;

    }

    //Spawns a laser from a ship
    void createLaser(float x, float y, int team, float angle, float damage) {
        exists = true;
        this.team = team;
        this.damage = damage;
        degrees = angle;
        positionX = x - midX;
        positionY = y - midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
    }

    //Updates the object's properties
    void update() {
        matrix();
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale, scale);
        appearance.postTranslate(positionX, positionY);
    }

    //When laser hits another object(incomplete)
    void impact(GameObject object) {

    }
}