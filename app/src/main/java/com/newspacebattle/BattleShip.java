package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-04. Defines a Battleship object.
 */
class BattleShip extends Ship {

    private PointObject gun1, gun2, missile1, missile2;

    static float constRadius;
    static float MAX_SPEED;
;
    static int buildTime, cost;

    //Constructor method
    BattleShip(float x, float y, int team) {
        type = "BattleShip";
        this.team = team;
        canAttack = true;
        width = Main.screenX * 3.5f;
        height = Main.screenY / GameScreen.circleRatio * 3.5f;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 2;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 10000;
        health = 20000;
        MAX_HEALTH = 20000;
        accelerate = 0.175f;
        maxSpeed = accelerate * 100;
        MAX_SPEED = maxSpeed;
        preScaleX = 3.5f;
        preScaleY = 3.5f;
        dockable = false;
        bulletPower = 150;
        missilePower = 300;
        gun1 = new PointObject(0, 0);
        gun2 = new PointObject(0, 0);
        missile1 = new PointObject(0, 0);
        missile2 = new PointObject(0, 0);
        shootTime = 2000;
        driveTime = 1000;
        sensorRadius = radius * 12;
        shipWeight = 7;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        if (autoAttack){
            destinationFinder.autoAttack();
        }
        move();
        rotate();
    }

    //Shoot two bullets forward and two missiles to the side
    void shoot() {
        gun1.x = Utilities.circleAngleX(degrees - 20, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun1.y = Utilities.circleAngleY(degrees - 20, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun2.x = Utilities.circleAngleX(degrees + 20, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun2.y = Utilities.circleAngleY(degrees + 20, centerPosY, (radius + Bullet.SIZE) * 1.2);

        missile1.x = Utilities.circleAngleX(degrees - 90, centerPosX, (radius + Missile.SIZE) * 1.2);
        missile1.y = Utilities.circleAngleY(degrees - 90, centerPosY, (radius + Missile.SIZE) * 1.2);

        missile2.x = Utilities.circleAngleX(degrees + 90, centerPosX, (radius + Missile.SIZE) * 1.2);
        missile2.y = Utilities.circleAngleY(degrees + 90, centerPosY, (radius + Missile.SIZE) * 1.2);

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

        for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
            if (!GameScreen.missiles.get(i).exists) {
                GameScreen.missiles.get(i).createMissile(
                        (float) missile1.x
                        , (float) missile1.y
                        , team
                        , (float) (Missile.MAX_SPEED * Math.sin((degrees - 90) * Math.PI / 180))
                        , (float) (Missile.MAX_SPEED * Math.cos((degrees - 90) * Math.PI / 180))
                        , degrees - 90
                        , missilePower
                        ,this
                );
                break;
            }
        }

        for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
            if (!GameScreen.missiles.get(i).exists) {
                GameScreen.missiles.get(i).createMissile(
                        (float) missile2.x
                        , (float) missile2.y
                        , team
                        , (float) (Missile.MAX_SPEED * Math.sin((degrees + 90) * Math.PI / 180))
                        , (float) (Missile.MAX_SPEED * Math.cos((degrees + 90) * Math.PI / 180))
                        , degrees + 90
                        , missilePower
                        ,this
                );
                break;
            }
        }
    }
}
