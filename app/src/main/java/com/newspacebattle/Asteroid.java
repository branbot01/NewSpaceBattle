package com.newspacebattle;

/**
 * Created by Dylan on 2018-06-30. Asteroid class defines the asteroid object.
 */
class Asteroid extends GameObject {

    private int scale;
    double resources;
    boolean hasResLeft;
    boolean[] dockingSpots = new boolean[4];

    PointObject dockingSpot1, dockingSpot2, dockingSpot3, dockingSpot4;

    //Constructor method
    Asteroid(float x, float y, int size) {
        type = "Asteroid";
        scale = size;
        resources = 10000 * scale;
        mass = 1000 * scale + (int) (0.1 * resources);
        width = Main.screenX / 9 * scale;
        height = Main.screenY / GameScreen.circleRatio / 9 * scale;
        midX = width / 2;
        midY = height / 2;
        radius = midX;
        positionX = x - midX;
        positionY = y - midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        velocityX = 0;
        velocityY = 0;
        hasResLeft = true;
        degrees = (int) (Math.random() * 360 + 1);
        dockingSpot1 = new PointObject(0, 0);
        dockingSpot2 = new PointObject(0, 0);
        dockingSpot3 = new PointObject(0, 0);
        dockingSpot4 = new PointObject(0, 0);
    }

    //Updates the object's properties
    public void update() {
        hasResLeft = checkResources();
        move();
        matrix();
    }

    //Checks whether asteroid still has resources, if not, asteroid blows up
    private boolean checkResources() {
        if (resources <= 0) {
            return false;
        }
        mass = 1000 * scale + (int) (0.1 * resources);
        return true;
    }

    //Draws object properly
    private void matrix() {
        appearance.setRotate(degrees, midX, midY);
        appearance.preScale(scale / 2, scale / 2);
        appearance.postTranslate(positionX, positionY);
    }

    private void updateDockingSpots(){
        dockingSpot1.x = Utilities.circleAngleX(degrees - 0, centerPosX, (radius) * 1.2);
        dockingSpot1.y = Utilities.circleAngleY(degrees - 0, centerPosY, (radius) * 1.2);

        dockingSpot2.x = Utilities.circleAngleX(degrees + 90, centerPosX, (radius) * 1.2);
        dockingSpot2.y = Utilities.circleAngleY(degrees + 90, centerPosY, (radius) * 1.2);

        dockingSpot3.x = Utilities.circleAngleX(degrees - 90, centerPosX, (radius) * 1.2);
        dockingSpot3.y = Utilities.circleAngleY(degrees - 90, centerPosY, (radius) * 1.2);

        dockingSpot4.x = Utilities.circleAngleX(degrees + 180, centerPosX, (radius) * 1.2);
        dockingSpot4.y = Utilities.circleAngleY(degrees + 180, centerPosY, (radius) * 1.2);

    }
}