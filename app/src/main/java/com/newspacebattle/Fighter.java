package com.newspacebattle;

/**
 * Created by Dylan on 2018-06-30. Defines a fighter object.
 */
class Fighter extends Ship {

    private PointObject gun1, gun2;

    static float constRadius;
    static float MAX_SPEED;

    static int cost;

    //Constructor method
    Fighter(float x, float y, int team) {
        type = "Fighter";
        this.team = team;
        canAttack = true;
        width = Main.screenX / 1.5f;
        height = Main.screenY / GameScreen.circleRatio / 1.5f;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 10;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 2500;
        health = 3000;
        MAX_HEALTH = 3000;
        accelerate = 0.6f;
        maxSpeed = accelerate * 90;
        MAX_SPEED = maxSpeed;
        preScaleX = 1;
        preScaleY = 1;
        dockable = true;
        bulletPower = 100;
        gun1 = new PointObject(0, 0);
        gun2 = new PointObject(0, 0);
        shootTime = 650;
        driveTime = 500;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    //Shoots two bullets forward
    void shoot() {
        gun1.x = Utilities.circleAngleX(degrees - 25, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun1.y = Utilities.circleAngleY(degrees - 25, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun2.x = Utilities.circleAngleX(degrees + 25, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun2.y = Utilities.circleAngleY(degrees + 25, centerPosY, (radius + Bullet.SIZE) * 1.2);

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun1.x
                        , (float) gun1.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower
                        , this);
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
                        , bulletPower
                        , this);
                break;
            }
        }
    }
}