package com.newspacebattle;

/**
 * Created by Dylan on 2018-06-30. Generates asteroids within its radius.
 */
class AsteroidCluster {

    float positionX, positionY, radius;

    //Constructor Method
    AsteroidCluster(float x, float y, int asteroidNum) {
        positionX = x;
        positionY = y;
        radius = GameScreen.clusterSize;
        generateField(asteroidNum);
    }

    //Generates a certain number of asteroids within its radius
    private void generateField(int asteroidNum) {
        int[] sizes = new int[asteroidNum], angles = new int[asteroidNum];
        float[] distanceFromCentre = new float[asteroidNum], xPos = new float[asteroidNum], yPos = new float[asteroidNum];
        boolean distanceIsGood;

        for (int i = 0; i <= asteroidNum - 1; i++) {
            sizes[i] = (int) (Math.random() * 3 + 2);
        }

        do {
            for (int i = 0; i <= asteroidNum - 1; i++) {
                angles[i] = (int) (Math.random() * 360 + 1);
                distanceFromCentre[i] = (float) (Math.random() * GameScreen.clusterSize - Main.screenX / 9 * 6);
                xPos[i] = (float) (distanceFromCentre[i] * Math.sin(angles[i] * Math.PI / 180));
                yPos[i] = (float) (distanceFromCentre[i] * Math.cos(angles[i] * Math.PI / 180));
            }
            distanceIsGood = true;

            for (int i = 0; i <= asteroidNum - 1; i++) {
                for (int ii = 0; ii <= asteroidNum - 1; ii++) {
                    if (Math.sqrt(Math.abs(Math.pow(xPos[ii] - xPos[i], 2) + Math.pow(yPos[ii] - yPos[i], 2))) < Main.screenX / 9 * 10) {
                        if (i != ii) {
                            distanceIsGood = false;
                        }
                    }
                }
            }
        } while (!distanceIsGood);

        for (int i = 0; i <= asteroidNum - 1; i++) {
            GameScreen.asteroids.add(new Asteroid(positionX + xPos[i], positionY + yPos[i], sizes[i]));
        }
    }
}