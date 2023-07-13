package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-06. Defines a Bomber ship.
 */
class Bomber extends Ship {

    private PointObject missile1, missile2;

    static float constRadius;
    static float MAX_SPEED;

    static int buildTime, cost;

    //Constructor method
    Bomber(float x, float y, int team) {
        type = "Bomber";
        this.team = team;
        canAttack = true;
        width = Main.screenX / 1.25f;
        height = Main.screenY / GameScreen.circleRatio / 1.25f;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        avoidanceRadius = radius * 8;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 1500;
        health = 3000;
        MAX_HEALTH = 3000;
        accelerate = 0.4f;
        maxSpeed = accelerate * 100;
        MAX_SPEED = maxSpeed;
        preScaleX = 1;
        preScaleY = 1;
        dockable = true;
        missilePower = 150;
        missile1 = new PointObject(0, 0);
        missile2 = new PointObject(0, 0);
        shootTime = 5000;
        driveTime = 500;
    }

    //Updates the object's properties
    public void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    //Shoots two missiles forward
    void shoot() {
        missile1.x = Utilities.circleAngleX(degrees - 25, centerPosX, (radius + Missile.SIZE) * 1.2);
        missile1.y = Utilities.circleAngleY(degrees - 25, centerPosY, (radius + Missile.SIZE) * 1.2);

        missile2.x = Utilities.circleAngleX(degrees + 25, centerPosX, (radius + Missile.SIZE) * 1.2);
        missile2.y = Utilities.circleAngleY(degrees + 25, centerPosY, (radius + Missile.SIZE) * 1.2);

        for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
            if (!GameScreen.missiles.get(i).exists) {
                GameScreen.missiles.get(i).createMissile(
                        (float) missile1.x
                        , (float) missile1.y
                        , team
                        , (float) (Missile.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Missile.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , missilePower
                        , this
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
                        , (float) (Missile.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Missile.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , missilePower
                        , this
                );
                break;
            }
        }
    }
}