package com.newspacebattle;

import android.graphics.Matrix;

/**
 * Created by Dylan on 2019-01-13. Defines the explosion animation.
 */
class Explosion {

    int frame, timeActive, degrees;
    float positionX, positionY;
    float centerPosX, centerPosY;
    float width, height;
    float midX, midY;
    float radius, scale;
    boolean active;
    Matrix appearance = new Matrix();
    GameObject explodingObj;

    //Creates explosion object
    Explosion() {
        active = false;
        degrees = (int) ((Math.random() * 360) + 1);
    }

    //Spawns the explosion animation
    void createExplosion(GameObject object) {
        explodingObj = object;
        frame = 1;
        active = true;
        width = object.width;
        height = object.height;
        midX = width / 2;
        midY = height / 2;
        positionX = object.centerPosX;
        positionY = object.centerPosY;
        centerPosX = positionX - midX;
        centerPosY = positionY - midY;
        radius = midY;
        timeActive = 0;
        scale = object.height / Main.screenY * 8;
    }

    //Updates the object's properties
    void update() {
        matrix();
        updateFrame();
    }

    //Draws the object properly
    private void matrix() {
        appearance.setRotate(0, midX, midY);
        appearance.preScale(scale, scale);
        appearance.postTranslate(centerPosX, centerPosY);
    }

    //Determines which explosion frame to draw
    private void updateFrame() {
        if (timeActive > 2000) {
            active = false;
            return;
        }
        timeActive += 16;
        if (timeActive >= 0 && timeActive < 80) {
            frame = 1;
        } else if (timeActive >= 80 && timeActive < 160) {
            frame = 2;
        } else if (timeActive >= 160 && timeActive < 240) {
            frame = 3;
        } else if (timeActive >= 240 && timeActive < 320) {
            frame = 4;
        } else if (timeActive >= 320 && timeActive < 400) {
            frame = 5;
        } else if (timeActive >= 400 && timeActive < 480) {
            frame = 6;
        } else if (timeActive >= 480 && timeActive < 560) {
            frame = 7;
        } else if (timeActive >= 560 && timeActive < 640) {
            frame = 8;
        } else if (timeActive >= 640 && timeActive < 720) {
            frame = 9;
        } else if (timeActive >= 720 && timeActive < 800) {
            frame = 10;
        } else if (timeActive >= 800 && timeActive < 880) {
            frame = 11;
        } else if (timeActive >= 880 && timeActive < 960) {
            frame = 12;
        } else if (timeActive >= 960 && timeActive < 1040) {
            frame = 13;
        } else if (timeActive >= 1040 && timeActive < 1120) {
            frame = 14;
        } else if (timeActive >= 1120 && timeActive < 1200) {
            frame = 15;
        } else if (timeActive >= 1200 && timeActive < 1280) {
            frame = 16;
        } else if (timeActive >= 1280 && timeActive < 1360) {
            frame = 17;
        } else if (timeActive >= 1360 && timeActive < 1440) {
            frame = 18;
        } else if (timeActive >= 1440 && timeActive < 1520) {
            frame = 19;
        } else if (timeActive >= 1520 && timeActive < 1600) {
            frame = 20;
        } else if (timeActive >= 1600 && timeActive < 1680) {
            frame = 21;
        } else if (timeActive >= 1680 && timeActive < 1760) {
            frame = 22;
        } else if (timeActive >= 1760 && timeActive < 1840) {
            frame = 23;
        } else if (timeActive >= 1840 && timeActive < 1940) {
            frame = 24;
        } else if (timeActive >= 1940 && timeActive < 2000) {
            frame = 25;
        }
    }
}