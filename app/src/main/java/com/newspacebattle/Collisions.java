package com.newspacebattle;

/**
 * Created by Dylan and Master Chef on 2018-07-02. Checks for collisions between objects
 */
class Collisions {

    Collisions() {
        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    detectCollision();
                }
                Utilities.delay(1);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    if (!GameScreen.gameOver && !GameScreen.botsOnly) {
                        checkVisibility();
                    } else {
                        setAllVisible();
                    }
                }
                Utilities.delay(1);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    checkObjectBoundariesX();
                }
                Utilities.delay(1);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    checkObjectBoundariesY();
                }
                Utilities.delay(1);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    if (GameScreen.bullets.size() > 0) {
                        checkBulletBoundaries();
                    }
                    if (GameScreen.missiles.size() > 0) {
                        checkMissileBoundaries();
                    }
                    if (GameScreen.lasers.size() > 0) {
                        checkLaserBoundaries();
                    }
                }
                Utilities.delay(1000);
            }
        }).start();

        if (GameScreen.bullets.size() > 0) {
            new Thread(() -> {
                while (true) {
                    if (!GameScreen.paused) {
                        bulletCollision(0, GameScreen.bullets.size());
                    }
                    Utilities.delay(1);
                }
            }).start();
        }

        if (GameScreen.missiles.size() > 0) {
            new Thread(() -> {
                while (true) {
                    if (!GameScreen.paused) {
                        missileCollision(0, GameScreen.missiles.size());
                    }
                    Utilities.delay(5);
                }
            }).start();
        }

        if (GameScreen.lasers.size() > 0) {
            new Thread(() -> {
                while (true) {
                    if (!GameScreen.paused) {
                        laserCollision(0, GameScreen.lasers.size());
                    }
                    Utilities.delay(5);
                }
            }).start();
        }
    }

    //When two objects collide, determine new velocities of both objects
    private void collisionEvent(GameObject object1, GameObject object2) {
        if (object1 instanceof BlackHole || object2 instanceof BlackHole) {
            return;
        }
        long m1, m2;
        float v1ix, v1iy;
        float v2ix, v2iy;
        float v1fx, v1fy;
        float v2fx, v2fy;

        m1 = object1.mass;
        v1ix = object1.velocityX + object1.gravVelX;
        v1iy = object1.velocityY + object1.gravVelY;
        if (object1 instanceof Bullet || object1 instanceof Missile || object1 instanceof Laser) {
            v1ix /= 30;
            v1iy /= 30;
        }

        m2 = object2.mass;
        v2ix = object2.velocityX + object2.gravVelX;
        v2iy = object2.velocityY + object2.gravVelY;
        if (object2 instanceof Bullet || object2 instanceof Missile || object2 instanceof Laser) {
            v2ix /= 30;
            v2iy /= 30;
        }

        v2fx = (v2ix * (m2 - m1) + 2 * m1 * v1ix) / (m1 + m2);
        v1fx = (v1ix * (m1 - m2) + 2 * m2 * v2ix) / (m1 + m2);

        v2fy = (v2iy * (m2 - m1) + 2 * m1 * v1iy) / (m1 + m2);
        v1fy = (v1iy * (m1 - m2) + 2 * m2 * v2iy) / (m1 + m2);

        if (!object1.colliding) {
            object1.velocityX = v1fx;
            object1.velocityY = v1fy;
        }
        if (object1 instanceof Bullet) {
            ((Bullet) object1).impact(object2);
        } else if (object1 instanceof Missile) {
            ((Missile) object1).impact(object2);
        } else if (object1 instanceof Laser) {
            ((Laser) object1).impact(object2);
        }

        if (!object2.colliding) {
            object2.velocityX = v2fx;
            object2.velocityY = v2fy;
        }
        if (object2 instanceof Bullet) {
            ((Bullet) object2).impact(object1);
        } else if (object2 instanceof Missile) {
            ((Missile) object2).impact(object1);
        } else if (object2 instanceof Laser) {
            ((Laser) object2).impact(object1);
        }

        object1.colliding = true;
        object2.colliding = true;
    }

    private void checkVisibility() {
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            try {
                boolean inRange = false;
                if (GameScreen.ships.get(i).team != 1) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == 1) {
                            if (Utilities.distanceFormula(GameScreen.ships.get(i).centerPosX, GameScreen.ships.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.ships.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                                inRange = true;
                                GameScreen.ships.get(i).visible = true;
                                break;
                            }
                        }
                    }
                    if (!inRange) {
                        GameScreen.ships.get(i).visible = false;
                    }
                } else {
                    GameScreen.ships.get(i).visible = true;
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            try {
                boolean inRange = false;
                if (GameScreen.bullets.get(i).team != 1) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == 1) {
                            if (Utilities.distanceFormula(GameScreen.bullets.get(i).centerPosX, GameScreen.bullets.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.bullets.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                                inRange = true;
                                GameScreen.bullets.get(i).visible = true;
                                break;
                            }
                        }
                    }
                    if (!inRange) {
                        GameScreen.bullets.get(i).visible = false;
                    }
                } else {
                    GameScreen.bullets.get(i).visible = true;
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                break;
            }
        }

        for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
            try {
                boolean inRange = false;
                if (GameScreen.missiles.get(i).team != 1) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == 1) {
                            if (Utilities.distanceFormula(GameScreen.missiles.get(i).centerPosX, GameScreen.missiles.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.missiles.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                                inRange = true;
                                GameScreen.missiles.get(i).visible = true;
                                break;
                            }
                        }
                    }
                    if (!inRange) {
                        GameScreen.missiles.get(i).visible = false;
                    }
                } else {
                    GameScreen.missiles.get(i).visible = true;
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                break;
            }
        }

        for (int i = 0; i <= GameScreen.lasers.size() - 1; i++) {
            try {
                boolean inRange = false;
                if (GameScreen.lasers.get(i).team != 1) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == 1) {
                            if (Utilities.distanceFormula(GameScreen.lasers.get(i).centerPosX, GameScreen.lasers.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.lasers.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                                inRange = true;
                                GameScreen.lasers.get(i).visible = true;
                                break;
                            }
                        }
                    }
                    if (!inRange) {
                        GameScreen.lasers.get(i).visible = false;
                    }
                } else {
                    GameScreen.lasers.get(i).visible = true;
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                break;
            }
        }

        for (int i = 0; i < GameScreen.explosions.size(); i++) {
            try {
                boolean inRange = false;
                if (GameScreen.explosions.get(i).explodingObj.team != 1) {
                    for (int ii = 0; ii <= GameScreen.ships.size() - 1; ii++) {
                        if (GameScreen.ships.get(ii).team == 1) {
                            if (Utilities.distanceFormula(GameScreen.explosions.get(i).centerPosX, GameScreen.explosions.get(i).centerPosY, GameScreen.ships.get(ii).centerPosX, GameScreen.ships.get(ii).centerPosY) <= GameScreen.explosions.get(i).radius + GameScreen.ships.get(ii).sensorRadius + GameScreen.ships.get(ii).radius) {
                                inRange = true;
                                GameScreen.explosions.get(i).visible = true;
                                break;
                            }
                        }
                    }
                    if (!inRange) {
                        GameScreen.explosions.get(i).visible = false;
                    }
                } else {
                    GameScreen.explosions.get(i).visible = true;
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                break;
            }
        }
    }

    private void setAllVisible() {
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            try {
                GameScreen.ships.get(i).visible = true;
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error in setAllShipsVisible" + e);
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            try {
                GameScreen.bullets.get(i).visible = true;
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error in setAllBulletsVisible" + e);
            }
        }

        for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
            try {
                GameScreen.missiles.get(i).visible = true;
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error in setAllMissilesVisible" + e);
            }
        }

        for (int i = 0; i <= GameScreen.lasers.size() - 1; i++) {
            try {
                GameScreen.lasers.get(i).visible = true;
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error in setAllLasersVisible" + e);
            }
        }

        for (int i = 0; i <= GameScreen.explosions.size() - 1; i++) {
            try {
                GameScreen.explosions.get(i).visible = true;
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                System.out.println("Error in setAllExplosionsVisible" + e);
            }
        }
    }

    //Checks that no object has gone outside of the map
    private void checkObjectBoundariesX() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            try {
                if (GameScreen.objects.get(i).centerPosX + GameScreen.objects.get(i).radius >= GameScreen.mapSizeX / 2) {
                    GameScreen.objects.get(i).velocityX = -GameScreen.objects.get(i).velocityX;
                    GameScreen.objects.get(i).accelerationX = -GameScreen.objects.get(i).accelerationX;
                    GameScreen.objects.get(i).positionX = GameScreen.mapSizeX / 2 - GameScreen.objects.get(i).radius * 2 - 1;
                }
                if (GameScreen.objects.get(i).centerPosX - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeX / 2) {
                    GameScreen.objects.get(i).velocityX = -GameScreen.objects.get(i).velocityX;
                    GameScreen.objects.get(i).accelerationX = -GameScreen.objects.get(i).accelerationX;
                    GameScreen.objects.get(i).positionX = -GameScreen.mapSizeX / 2 + 1;
                }
            } catch (Exception e) {
                System.out.println("Error in checkObjectBoundariesX");
            }
        }
    }

    private void checkObjectBoundariesY() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            try {
                if (GameScreen.objects.get(i).centerPosY + GameScreen.objects.get(i).radius >= GameScreen.mapSizeY / 2) {
                    GameScreen.objects.get(i).velocityY = -GameScreen.objects.get(i).velocityY;
                    GameScreen.objects.get(i).accelerationY = -GameScreen.objects.get(i).accelerationY;
                    GameScreen.objects.get(i).positionY = GameScreen.mapSizeY / 2 - GameScreen.objects.get(i).radius * 2 - 1;
                }
                if (GameScreen.objects.get(i).centerPosY - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeY / 2) {
                    GameScreen.objects.get(i).velocityY = -GameScreen.objects.get(i).velocityY;
                    GameScreen.objects.get(i).accelerationY = -GameScreen.objects.get(i).accelerationY;
                    GameScreen.objects.get(i).positionY = -GameScreen.mapSizeY / 2 + 1;
                }
            } catch (Exception e) {
                System.out.println("Error in checkObjectBoundariesY");
            }
        }
    }

    private void checkBulletBoundaries() {
        if (GameScreen.bullets.size() > 0) {
            for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
                if (GameScreen.bullets.get(i).exists) {
                    if (GameScreen.bullets.get(i).centerPosX + GameScreen.bullets.get(i).radius >= GameScreen.mapSizeX / 2 || GameScreen.bullets.get(i).centerPosX - GameScreen.bullets.get(i).radius <= -GameScreen.mapSizeX / 2) {
                        GameScreen.bullets.get(i).impact(null);
                    }

                    if (GameScreen.bullets.get(i).centerPosY + GameScreen.bullets.get(i).radius >= GameScreen.mapSizeY / 2 || GameScreen.bullets.get(i).centerPosY - GameScreen.bullets.get(i).radius <= -GameScreen.mapSizeY / 2) {
                        GameScreen.bullets.get(i).impact(null);
                    }
                }
            }
        }
    }

    private void checkMissileBoundaries() {
        if (GameScreen.missiles.size() > 0) {
            for (int i = 0; i <= GameScreen.missiles.size() - 1; i++) {
                if (GameScreen.missiles.get(i).exists) {
                    if (GameScreen.missiles.get(i).centerPosX + GameScreen.missiles.get(i).radius >= GameScreen.mapSizeX / 2 || GameScreen.missiles.get(i).centerPosX - GameScreen.missiles.get(i).radius <= -GameScreen.mapSizeX / 2) {
                        GameScreen.missiles.get(i).impact(null);
                    }

                    if (GameScreen.missiles.get(i).centerPosY + GameScreen.missiles.get(i).radius >= GameScreen.mapSizeY / 2 || GameScreen.missiles.get(i).centerPosY - GameScreen.missiles.get(i).radius <= -GameScreen.mapSizeY / 2) {
                        GameScreen.missiles.get(i).impact(null);
                    }
                }
            }
        }
    }

    private void checkLaserBoundaries() {
        if (GameScreen.lasers.size() > 0) {
            for (int i = 0; i <= GameScreen.lasers.size() - 1; i++) {
                if (GameScreen.lasers.get(i).exists) {
                    if (GameScreen.lasers.get(i).centerPosX + GameScreen.lasers.get(i).radius >= GameScreen.mapSizeX / 2 || GameScreen.lasers.get(i).centerPosX - GameScreen.lasers.get(i).radius <= -GameScreen.mapSizeX / 2) {
                        GameScreen.lasers.get(i).impact(null);
                    }

                    if (GameScreen.lasers.get(i).centerPosY + GameScreen.lasers.get(i).radius >= GameScreen.mapSizeY / 2 || GameScreen.lasers.get(i).centerPosY - GameScreen.lasers.get(i).radius <= -GameScreen.mapSizeY / 2) {
                        GameScreen.lasers.get(i).impact(null);
                    }
                }
            }
        }
    }

    //Runs through all objects to see if they collide
    private void detectCollision() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            boolean isColliding = false;
            for (int ii = 0; ii <= GameScreen.objects.size() - 1; ii++) {
                try {
                    if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, GameScreen.objects.get(ii).centerPosX, GameScreen.objects.get(ii).centerPosY) <= GameScreen.objects.get(i).radius + GameScreen.objects.get(ii).radius && i != ii) {
                        isColliding = true;
                        collisionEvent(GameScreen.objects.get(i), GameScreen.objects.get(ii));
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    break;
                }
            }
            if (!isColliding) {
                try {
                    GameScreen.objects.get(i).colliding = false;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Error in detectCollision");
                }
            }
        }
    }

    //Runs through all bullets to see if they collide with any objects
    private void bulletCollision(int start, int end) {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            for (int ii = start; ii < end; ii++) {
                try {
                    if (GameScreen.bullets.get(ii).exists && GameScreen.bullets.get(ii).team != GameScreen.objects.get(i).team) {
                        if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, GameScreen.bullets.get(ii).centerPosX, GameScreen.bullets.get(ii).centerPosY) <= GameScreen.objects.get(i).radius + GameScreen.bullets.get(ii).radius) {
                            collisionEvent(GameScreen.objects.get(i), GameScreen.bullets.get(ii));
                        }
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    break;
                }
            }
        }
    }

    //Runs through all missiles to see if they collide with any objects
    private void missileCollision(int start, int end) {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            for (int ii = start; ii < end; ii++) {
                try {
                    if (GameScreen.missiles.get(ii).exists && GameScreen.missiles.get(ii).team != GameScreen.objects.get(i).team) {
                        if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, GameScreen.missiles.get(ii).centerPosX, GameScreen.missiles.get(ii).centerPosY) <= GameScreen.objects.get(i).radius + GameScreen.missiles.get(ii).radius) {
                            collisionEvent(GameScreen.objects.get(i), GameScreen.missiles.get(ii));
                        }
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    break;
                }
            }
        }
    }

    //Runs through all lasers to see if they collide with any objects
    private void laserCollision(int start, int end) {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            for (int ii = start; ii < end; ii++) {
                try {
                    if (GameScreen.lasers.get(ii).exists && GameScreen.lasers.get(ii).team != GameScreen.objects.get(i).team) {
                        if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, GameScreen.lasers.get(ii).centerPosX, GameScreen.lasers.get(ii).centerPosY) <= GameScreen.objects.get(i).radius + GameScreen.lasers.get(ii).radius) {
                            collisionEvent(GameScreen.objects.get(i), GameScreen.lasers.get(ii));
                        }
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    break;
                }
            }
        }
    }

    //detect if two objects will collide at any point in the future given their current velocities
    static boolean relVelDetectCollision(GameObject object1, GameObject object2) {
        float distanceOfObj2wrtObj1 = (float) Utilities.distanceFormula(object1.centerPosX, object1.centerPosY, object2.centerPosX, object2.centerPosY);

        float relVelObj2X = object2.velocityX - object1.velocityX;
        float relVelObj2Y = object2.velocityY - object1.velocityY;

        float relVelAngleObj2 = (float) Utilities.angleDim(relVelObj2X, relVelObj2Y);

        float obj2AngleWrtObj1 = (float) Utilities.anglePoints(object2.centerPosX, object2.centerPosY, object1.centerPosX, object1.centerPosY);
        float deltaTheta = (float) (Math.toDegrees(Math.atan2(object1.radius + object2.radius, distanceOfObj2wrtObj1)));
        /*testing purposes only
        System.out.println("velAngleObj2: " + relVelAngleObj2 + " thetaObj2: " + obj2AngleWrtObj1 + " deltaTheta: " + deltaTheta);
        System.out.println(object1);*/
        return relVelAngleObj2 <= obj2AngleWrtObj1 + deltaTheta && relVelAngleObj2 >= obj2AngleWrtObj1 - deltaTheta;
    }
}
