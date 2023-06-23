package com.newspacebattle;

/**
 * Created by Dylan on 2018-06-30. Defines a flagship object.
 */
class FlagShip extends Ship {

    double resources;
    private PointObject gun1, gun2, gun3, gun4;

    //Constructor method
    FlagShip(float x, float y, int team) {
        type = "FlagShip";
        this.team = team;
        canAttack = true;
        width = Main.screenX * 4;
        height = Main.screenY / GameScreen.circleRatio * 4;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 11000;
        health = 15000;
        accelerate = 0.2f;
        maxSpeed = accelerate * 75;
        resources = 20000;
        preScaleX = 4;
        preScaleY = 4;
        dockable = false;
        bulletPower = 100;
        gun1 = new PointObject(0, 0);
        gun2 = new PointObject(0, 0);
        gun3 = new PointObject(0, 0);
        gun4 = new PointObject(0, 0);
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    //Shoots 4 bullets forward
    void shoot() {
        gun1.x = Utilities.circleAngleX(degrees - 10, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun1.y = Utilities.circleAngleY(degrees - 10, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun2.x = Utilities.circleAngleX(degrees + 10, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun2.y = Utilities.circleAngleY(degrees + 10, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun3.x = Utilities.circleAngleX(degrees - 30, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun3.y = Utilities.circleAngleY(degrees - 30, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun4.x = Utilities.circleAngleX(degrees + 30, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun4.y = Utilities.circleAngleY(degrees + 30, centerPosY, (radius + Bullet.SIZE) * 1.2);

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun1.x
                        , (float) gun1.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun2.x
                        , (float) gun2.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun3.x
                        , (float) gun3.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun4.x
                        , (float) gun4.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }
    }
}