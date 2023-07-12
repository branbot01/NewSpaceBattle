package com.newspacebattle;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Dylan on 2019-01-14. Defines a missile object.
 */
class Missile extends GameObject {

    final static float SIZE = Main.screenY / GameScreen.circleRatio / 3f / 2, MAX_SPEED = 100;
    private float scale, damage;
    private int timeLeft;
    private boolean followingShip;
    private Handler delay = new Handler(Looper.getMainLooper());
    private Ship target, ownShip;

    //Constuctor method
    Missile() {
        type = "Missile";
        width = Main.screenX / 3f;
        height = Main.screenY / GameScreen.circleRatio / 3f;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        mass = 50;
        maxSpeed = MAX_SPEED;
        scale = 1;
        accelerate = 1;
        exists = false;
    }

    //Spawns a missile from a ship
    void createMissile(float x, float y, int team, float xVel, float yVel, float angle, float damage, Ship ownShip) {
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
        timeLeft = 7500;
        followingShip = false;
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                followingShip = true;
            }
        }, 500);
    }

    //Updates the object's properties
    void update() {
        move();
        matrix();
        countDown();
        if (followingShip) {
            trackShip();
        }
    }

    //How the missiles finds its target
    private void trackShip() {
        Ship closest = null;
        double distance = 9999999999999999f;
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            if (GameScreen.ships.get(i) != ownShip && GameScreen.ships.get(i).team != team) {
                if (Utilities.distanceFormula(centerPosX, centerPosY, GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY) < distance) {
                    closest = GameScreen.ships.get(i);
                    distance = Utilities.distanceFormula(centerPosX, centerPosY, GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY);
                }
            }
        }
        if (closest != null) {
            target = closest;
        } else {
            return;
        }
        if (Math.abs(Utilities.angleDim(accelerationX, accelerationY) - Utilities.angleDim(velocityX, velocityY)) > 0.25) {
            int turnAngle = 120;
            double requiredAngle = Utilities.anglePoints(centerPosX, centerPosY, target.centerPosX, target.centerPosY);
            if (Math.abs(requiredAngle - degrees) < 5) {
                accelerationX = accelerate * (float) Math.sin(Utilities.anglePoints(centerPosX, centerPosY, target.centerPosX, target.centerPosY) * Math.PI / 180);
                accelerationY = accelerate * (float) Math.cos(Utilities.anglePoints(centerPosX, centerPosY, target.centerPosX, target.centerPosY) * Math.PI / 180);
                return;
            }
            if (degrees - requiredAngle <= 0) {
                turnAngle *= -1;
            }
            //System.out.println(degrees + ", " + requiredAngle);
            accelerationX = accelerate * (float) Math.sin(Utilities.anglePoints(centerPosX, centerPosY, Utilities.circleAngleX(degrees - turnAngle, centerPosX, radius), Utilities.circleAngleY(degrees - turnAngle, centerPosY, radius)) * Math.PI / 180);
            accelerationY = accelerate * (float) Math.cos(Utilities.anglePoints(centerPosX, centerPosY, Utilities.circleAngleX(degrees - turnAngle, centerPosX, radius), Utilities.circleAngleY(degrees - turnAngle, centerPosY, radius)) * Math.PI / 180);
        } else {
            accelerationX = accelerate * (float) Math.sin(Utilities.anglePoints(centerPosX, centerPosY, target.centerPosX, target.centerPosY) * Math.PI / 180);
            accelerationY = accelerate * (float) Math.cos(Utilities.anglePoints(centerPosX, centerPosY, target.centerPosX, target.centerPosY) * Math.PI / 180);
        }

        if (accelerationX != 0 && accelerationY != 0) {
            degrees = (float) Utilities.angleDim(velocityX, velocityY);
        }
    }

    //Counts down the missiles lifetime
    private void countDown() {
        timeLeft -= 16;
        if (timeLeft <= 0) {
            impact(null);
        }
    }

    //Draws the object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale, scale);
        appearance.postTranslate(positionX, positionY);
    }

    //When missiles hit another object they explode, do damage and die
    void impact(GameObject object) {
        if (object instanceof Ship) {
            ((Ship) object).health -= damage;
            this.ownShip.dmgDone += damage;
        }else{
            this.ownShip.missedShots++;
        }
        for (int i = 0; i <= GameScreen.explosions.size() - 1; i++) {
            if (!GameScreen.explosions.get(i).active) {
                GameScreen.explosions.get(i).createExplosion(this);
                break;
            }
        }
        followingShip = false;
        exists = false;
    }
}
