package com.newspacebattle;

/**
 * Created by Dylan on 2018-06-30. Defines a black hole object.
 */
class BlackHole extends GameObject {

    private final int scaleFactor = 20;
    final float pullDistance = scaleFactor * 0.2083f;

    //Constructor method
    BlackHole(int x, int y) {
        type = "BlackHole";
        mass = 350000;
        width = Main.screenX * scaleFactor;
        height = Main.screenY / GameScreen.circleRatio * scaleFactor;
        midX = width / 2;
        midY = height / 2;
        positionX = x - midX;
        positionY = y - midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        radius = width / 2;
        degrees = 0;
    }

    //Updates the object's properties
    void update() {
        pullShips();
        setRotation();
        rotate();
    }

    //Determines the black hole's angle
    private void setRotation() {
        if (!GameScreen.paused) {
            degrees += 4;
            if (degrees == 360) {
                degrees = 0;
            }
        }
    }

    //Draws object properly
    private void rotate() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scaleFactor, scaleFactor);
        appearance.postTranslate(positionX, positionY);
    }

    //Pulls all nearby ships in and destroys them
    private void pullShips() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            if (Math.sqrt(Math.abs(Math.pow(centerPosX - GameScreen.objects.get(i).centerPosX, 2) + Math.pow(centerPosY - GameScreen.objects.get(i).centerPosY, 2))) <= radius * pullDistance) {
                final double gravForce = mass * GameScreen.objects.get(i).mass / (Math.pow(Math.sqrt(Math.abs(Math.pow(centerPosX - GameScreen.objects.get(i).centerPosX, 2) + Math.pow(centerPosY - GameScreen.objects.get(i).centerPosY, 2))), 1.8));
                GameScreen.objects.get(i).gravVelX = (float) (-gravForce * Math.sin(Utilities.anglePoints(centerPosX, centerPosY, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) * Math.PI / 180));
                GameScreen.objects.get(i).gravVelY = (float) (gravForce * Math.cos(Utilities.anglePoints(centerPosX, centerPosY, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) * Math.PI / 180));
            } else {
                GameScreen.objects.get(i).gravVelX = 0;
                GameScreen.objects.get(i).gravVelY = 0;
            }

            if (Math.sqrt(Math.abs(Math.pow(centerPosX - GameScreen.objects.get(i).centerPosX, 2) + Math.pow(centerPosY - GameScreen.objects.get(i).centerPosY, 2))) <= radius * 0.15) {
                if (GameScreen.objects.get(i) instanceof Ship) {
                    ((Ship) GameScreen.objects.get(i)).health = 0;
                } else if (GameScreen.objects.get(i) instanceof Asteroid) {
                    ((Asteroid) GameScreen.objects.get(i)).resources = 0;
                }
            }
        }
    }
}