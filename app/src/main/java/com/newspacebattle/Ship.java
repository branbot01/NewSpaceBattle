package com.newspacebattle;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;

/**
 * Created by Dylan on 2018-07-04. Defines a ship object, that is subclassed by the GameObject class.
 */
class Ship extends GameObject {

    int shootTime, driveTime;
    float health, MAX_HEALTH;
    float bulletPower, missilePower, laserPower;
    float preScaleX, preScaleY;
    float avoidanceRadius, sensorRadius;
    double shipWeight;
    boolean movable, autoAttack, dockable, docking, docked, canWarp, selected, attSelected, canAttack, attacking, visible;
    Formation formation;
    Matrix arrow = new Matrix();
    Paint selector = new Paint();
    private final ValueAnimator stopperX, stopperY;

    //Constructor method
    Ship() {
        destinationFinder = new PathFinder(this);
        autoAttack = true;

        stopperX = ValueAnimator.ofFloat(velocityX, 0);
        stopperX.setInterpolator(new LinearInterpolator());
        stopperX.setDuration(1000);
        stopperX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                velocityX = (float) animation.getAnimatedValue();
            }
        });

        stopperY = ValueAnimator.ofFloat(velocityY, 0);
        stopperY.setInterpolator(new LinearInterpolator());
        stopperY.setDuration(1000);
        stopperY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                velocityY = (float) animation.getAnimatedValue();
            }
        });
    }

    //Determines ship highlight color
    void setSelectColor() {
        if (selected) {
            selector.setColor(Color.GREEN);
        } else if (attSelected) {
            selector.setColor(Color.RED);
        } else {
            selector.setColor(Color.GREEN);
        }
    }

    //Checks if ship still has health left
    boolean checkIfAlive() {
        setSelectColor();
        return health > 0;
    }

    //Stops the ship
    void stop() {
        stopperX.start();
        stopperY.start();

        accelerationX = 0;
        accelerationY = 0;

        destination = false;
        attacking = false;
        destinationFinder.searchingForEnemy = false;
        destinationFinder.stopFinder();
    }

    //Tells ship to move to this position
    void setDestination(float posX, float posY) {
        if (posX + radius >= GameScreen.mapSizeX / 2f || posX - radius <= -GameScreen.mapSizeX / 2f || posY + radius >= GameScreen.mapSizeY / 2f || posY - radius <= -GameScreen.mapSizeY / 2f) {
            return;
        }
        if (Utilities.distanceFormula(centerPosX, centerPosY, posX, posY) >= radius) {
            movable = false;
            destination = true;

            for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
                if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, posX, posY) <= GameScreen.objects.get(i).radius) {
                    destinationFinder.run(GameScreen.objects.get(i));
                    return;
                }
            }
            destinationFinder.run(posX, posY);
        }
    }

    //Sets the ship rotation based on its velocity direction
    void rotate() {
        if (accelerationX != 0 && accelerationY != 0) {
            degrees = (float) Utilities.angleDim(velocityX, velocityY);
        }

        appearance.setRotate(degrees, midX, midY);
        appearance.postTranslate(positionX, positionY);
        appearance.preScale(preScaleX, preScaleY);

        if (destination) {
            arrow.setRotate((float) Utilities.anglePoints(centerPosX, centerPosY, destinationFinder.destX, destinationFinder.destY), Main.screenX / 6f / 2, Main.screenY / 9f / 2);
            arrow.postTranslate(destinationFinder.destX - Main.screenX / 6f / 2, destinationFinder.destY - Main.screenY / 9f / 2);
        }
    }

    //Tells the ship to run their own shoot method
    void shoot() {
        switch (type) {
            case "BattleShip":
                for (int i = 0; i <= GameScreen.battleShips.size() - 1; i++) {
                    if (GameScreen.battleShips.get(i) == this) {
                        GameScreen.battleShips.get(i).shoot();
                    }
                }
                break;
            case "Bomber":
                for (int i = 0; i <= GameScreen.bombers.size() - 1; i++) {
                    if (GameScreen.bombers.get(i) == this) {
                        GameScreen.bombers.get(i).shoot();
                    }
                }
                break;
            case "Fighter":
                for (int i = 0; i <= GameScreen.fighters.size() - 1; i++) {
                    if (GameScreen.fighters.get(i) == this) {
                        GameScreen.fighters.get(i).shoot();
                    }
                }
                break;
            case "FlagShip":
                for (int i = 0; i <= GameScreen.flagShips.size() - 1; i++) {
                    if (GameScreen.flagShips.get(i) == this) {
                        GameScreen.flagShips.get(i).shoot();
                    }
                }
                break;
            case "LaserCruiser":
                for (int i = 0; i <= GameScreen.laserCruisers.size() - 1; i++) {
                    if (GameScreen.laserCruisers.get(i) == this) {
                        GameScreen.laserCruisers.get(i).shoot();
                    }
                }
                break;
        }
    }

    //tell ship to dock
    void dock() {
        float distanceToClosestSS = Float.MAX_VALUE, distanceToSS;
        SpaceStation closestSS = null;

        for (int i = 0; i <= GameScreen.spaceStations.size() - 1; i++) {
            if (GameScreen.spaceStations.get(i).team == team) {
                distanceToSS = (float) Utilities.distanceFormula(GameScreen.spaceStations.get(i).centerPosX, GameScreen.spaceStations.get(i).centerPosY, centerPosX, centerPosY);
                if (distanceToSS < distanceToClosestSS && GameScreen.spaceStations.get(i).dockedShips.size() < GameScreen.spaceStations.get(i).maxDockedNum) {
                    distanceToClosestSS = distanceToSS;
                    closestSS = GameScreen.spaceStations.get(i);
                }
            }
        }
        if (closestSS == null) {
            return;
        }
        docking = true;
        setDestination(closestSS.centerPosX, closestSS.centerPosY);
    }
}