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
            }
        }).start();

        /*new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    checkObjectBoundariesX();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    checkObjectBoundariesY();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (!GameScreen.paused) {
                    checkBulletBoundaries();
                }
            }
        }).start();*/

        for (int i = 0; i <= GameScreen.bullets.size(); i += 500) {
            int temp_i = i;
            new Thread(() -> {
                while (true) {
                    if (!GameScreen.paused) {
                        bulletCollision(temp_i, temp_i + 500);
                    }
                }
            }).start();
        }
    }

    //When two objects collide, determine new velocities of both objects
    private void collisionEvent(GameObject object1, GameObject object2) {
        long m1, m2;
        float v1ix, v1iy;
        float v2ix, v2iy;
        float v1fx, v1fy;
        float v2fx, v2fy;

        m1 = object1.mass;
        v1ix = object1.velocityX + object1.gravVelX;
        v1iy = object1.velocityY + object1.gravVelY;
        if (object1 instanceof Bullet || object1 instanceof Missile) {
            v1ix /= 30;
            v1iy /= 30;
        }

        m2 = object2.mass;
        v2ix = object2.velocityX + object2.gravVelX;
        v2iy = object2.velocityY + object2.gravVelY;
        if (object2 instanceof Bullet || object2 instanceof Missile) {
            v2ix /= 30;
            v2iy /= 30;
        }

        //float contactAngle = (float) Utilities.anglePoints(object1.centerPosX, object1.centerPosY, object2.centerPosX, object2.centerPosY);

        v2fx = (v2ix * (m2 - m1) + 2 * m1 * v1ix) / (m1 + m2);
        v1fx = (v1ix * (m1 - m2) + 2 * m2 * v2ix) / (m1 + m2);

        v2fy = (v2iy * (m2 - m1) + 2 * m1 * v1iy) / (m1 + m2);
        v1fy = (v1iy * (m1 - m2) + 2 * m2 * v2iy) / (m1 + m2);

//        v1fx = (float) (((v1ix * Math.cos(Utilities.angleDim(v1ix, v1iy) - contactAngle) * (m1 - m2) + 2 * m2 * v2ix * Math.cos(Utilities.angleDim(v2ix, v2iy) - contactAngle)) / (m1 + m2)) * Math.cos(contactAngle) + v1ix * Math.sin(Utilities.angleDim(v1ix, v1iy) - contactAngle) * Math.cos(contactAngle + 90));
//        v2fx = (float) (((v2ix * Math.cos(Utilities.angleDim(v2ix, v2iy) - contactAngle) * (m2 - m1) + 2 * m1 * v1ix * Math.cos(Utilities.angleDim(v1ix, v1iy) - contactAngle)) / (m2 + m1)) * Math.cos(contactAngle) + v2ix * Math.sin(Utilities.angleDim(v2ix, v2iy) - contactAngle) * Math.cos(contactAngle + 90));
//
//        v1fy = (float) (((v1ix * Math.cos(Utilities.angleDim(v1ix, v1iy) - contactAngle) * (m1 - m2) + 2 * m2 * v2ix * Math.cos(Utilities.angleDim(v2ix, v2iy) - contactAngle)) / (m1 + m2)) * Math.sin(contactAngle) + v1ix * Math.sin(Utilities.angleDim(v1ix, v1iy) - contactAngle) * Math.sin(contactAngle + 90));
//        v2fy = (float) (((v2ix * Math.cos(Utilities.angleDim(v2ix, v2iy) - contactAngle) * (m2 - m1) + 2 * m1 * v1ix * Math.cos(Utilities.angleDim(v1ix, v1iy) - contactAngle)) / (m2 + m1)) * Math.sin(contactAngle) + v2ix * Math.sin(Utilities.angleDim(v2ix, v2iy) - contactAngle) * Math.sin(contactAngle + 90));

        //System.out.println(contactAngle);
        //System.out.println(v1fx + ", " + v1fy);
        //System.out.println(v2fx + ", " + v2fy);

        object1.velocityX = v1fx;
        object1.velocityY = v1fy;
        if (object1 instanceof Bullet) {
            ((Bullet) object1).impact(object2);
        } else if (object1 instanceof Missile) {
            ((Missile) object1).impact(object2);
        } else if (object1 instanceof Laser) {
            ((Laser) object1).impact(object2);
        }
        if (object1 instanceof ResourceCollector) {
            if (((ResourceCollector) object1).harvesting || ((ResourceCollector) object1).unloading) {
                ((ResourceCollector) object1).harvesting = true;
            }
            ((ResourceCollector) object1).unloading = false;
        }

        object2.velocityX = v2fx;
        object2.velocityY = v2fy;
        if (object2 instanceof Bullet) {
            ((Bullet) object2).impact(object1);
        } else if (object2 instanceof Missile) {
            ((Missile) object2).impact(object1);
        } else if (object2 instanceof Laser) {
            ((Laser) object2).impact(object1);
        }
        if (object2 instanceof ResourceCollector) {
            if (((ResourceCollector) object2).harvesting || ((ResourceCollector) object2).unloading) {
                ((ResourceCollector) object2).harvesting = true;
            }
            ((ResourceCollector) object2).unloading = false;
        }

        if (object1 instanceof Ship && object2 instanceof Ship) {
            if(object1.destinationFinder.enemies.contains(object2)) {
                ((Ship) object1).colls += 500;
            }
            if(object2.destinationFinder.enemies.contains(object1)) {
                ((Ship) object2).colls += 500;
            }
        }

        //To avoid objects colliding more than once
        while (Utilities.distanceFormula(object1.centerPosX, object1.centerPosY, object2.centerPosX, object2.centerPosY) <= object1.radius + object2.radius && object1.exists && object2.exists) {
            //System.out.println("collision detected");
        }
    }

    //Checks that no object has gone outside of the map
    private void checkObjectBoundariesX() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            try {
                if (GameScreen.objects.get(i).centerPosX + GameScreen.objects.get(i).radius >= GameScreen.mapSizeX / 2 || GameScreen.objects.get(i).centerPosX - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeX / 2) {
                    GameScreen.objects.get(i).velocityX = -GameScreen.objects.get(i).velocityX;
                    GameScreen.objects.get(i).accelerationX = 0;
                    GameScreen.objects.get(i).accelerationY = 0;

                    while (GameScreen.objects.get(i).centerPosX + GameScreen.objects.get(i).radius >= GameScreen.mapSizeX / 2 || GameScreen.objects.get(i).centerPosX - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeX / 2) {
                        //System.out.println("borderx collision detected");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in checkObjectBoundariesX");
            }
        }
    }

    private void checkObjectBoundariesY() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            try {
                if (GameScreen.objects.get(i).centerPosY + GameScreen.objects.get(i).radius >= GameScreen.mapSizeY / 2 || GameScreen.objects.get(i).centerPosY - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeY / 2) {
                    GameScreen.objects.get(i).velocityY = -GameScreen.objects.get(i).velocityY;
                    GameScreen.objects.get(i).accelerationX = 0;
                    GameScreen.objects.get(i).accelerationY = 0;

                    while (GameScreen.objects.get(i).centerPosY + GameScreen.objects.get(i).radius >= GameScreen.mapSizeY / 2 || GameScreen.objects.get(i).centerPosY - GameScreen.objects.get(i).radius <= -GameScreen.mapSizeY / 2) {
                        //System.out.println("bordery collision detected");
                    }
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
            for (int ii = 0; ii <= GameScreen.objects.size() - 1; ii++) {
                try {
                    if (Utilities.distanceFormula(GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY, GameScreen.objects.get(ii).centerPosX, GameScreen.objects.get(ii).centerPosY) <= GameScreen.objects.get(i).radius + GameScreen.objects.get(ii).radius && i != ii) {
                        collisionEvent(GameScreen.objects.get(i), GameScreen.objects.get(ii));
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    break;
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
    private void missileCollision() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            for (int ii = 0; ii <= GameScreen.missiles.size() - 1; ii++) {
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

    //Runs through all missiles to see if they collide with any objects
    private void laserCollision() {
        for (int i = 0; i <= GameScreen.objects.size() - 1; i++) {
            for (int ii = 0; ii <= GameScreen.lasers.size() - 1; ii++) {
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
