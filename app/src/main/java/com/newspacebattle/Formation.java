package com.newspacebattle;

import java.util.ArrayList;

/**
 * Created by Brandon and Dylan on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    ArrayList<Ship> ships;
    ArrayList<Ship> formationShips = new ArrayList<>();
    float initialSize;
    float degrees, formationMaxSpeed;
    float velocityX, velocityY, accelerationX, accelerationY, accelerate;

    boolean destination;
    float destX, destY;

    //visualize ship locations
    ArrayList<PointObject> globalCoordinates = new ArrayList<>();
    ArrayList<PointObject> relativeCoordinates = new ArrayList<>();
    double centerX, centerY;
    int type;

    //Constructor method
    Formation(ArrayList<Ship> ships, int type) {
        this.type = type;
        this.ships = new ArrayList<>();
        this.ships.addAll(ships);
        for (int i = 0; i < this.ships.size(); i++) {
            if (this.ships.get(i) instanceof SpaceStation) {
                this.ships.remove(this.ships.get(i));
            }
        }
        initialSize = this.ships.size();
        degrees = 0;
        formationMaxSpeed = Float.MAX_VALUE;
        Ship slowShip = null;
        for (int i = 0; i < this.ships.size(); i++) {
            if (this.ships.get(i).maxSpeed < formationMaxSpeed) {
                formationMaxSpeed = this.ships.get(i).maxSpeed;
                slowShip = this.ships.get(i);
            }
        }
        if (slowShip != null) {
            accelerate = slowShip.accelerate;
        }
        for (int i = 0; i < this.ships.size(); i++) {
            this.ships.get(i).maxSpeed = formationMaxSpeed;
        }
        setShips();
        setCenter();
        if (type == 0) {
            rectangleFormation();
        }
    }

    //update's the formation's properties
    void update() {
        resetCenter();
        if (degrees != 0) {
            getPositions();
        }
        updatePositions();
        moveFormation();
        rotateFormation();
    }

    //Finds the center of the selected ships
    void setCenter() {
        double sumX = 0, sumY = 0, sumMass = 0;
        for (int i = 0; i < ships.size(); i++) {
            sumX += ships.get(i).centerPosX * ships.get(i).mass;
            sumY += ships.get(i).centerPosY * ships.get(i).mass;
            sumMass += ships.get(i).mass;
        }
        centerX = sumX / sumMass;
        centerY = sumY / sumMass;
    }

    //get position of ships relative to formation's centre of mass (in n-t coordinates)
    void getPositions() {
        for (int i = 0; i < formationShips.size(); i++) {
            relativeCoordinates.get(i).x = (formationShips.get(i).centerPosX - centerX) * Math.cos(Math.toRadians(degrees)) + (formationShips.get(i).centerPosY - centerY) * Math.sin(Math.toRadians(degrees));
            relativeCoordinates.get(i).y = -(formationShips.get(i).centerPosX - centerX) * Math.sin(Math.toRadians(degrees)) + (formationShips.get(i).centerPosY - centerY) * Math.cos(Math.toRadians(degrees));
        }
    }

    //update position of ship if formation rotates
    void updatePositions() {
        globalCoordinates.clear();
        for (int i = 0; i < formationShips.size(); i++) {
            //globalCoordinates.get(i).x = centerX + relativeCoordinates.get(i).x * Math.cos(Math.toRadians(degrees)) - relativeCoordinates.get(i).y * Math.sin(Math.toRadians(degrees));
            //globalCoordinates.get(i).y = centerY + relativeCoordinates.get(i).x * Math.sin(Math.toRadians(degrees)) + relativeCoordinates.get(i).y * Math.cos(Math.toRadians(degrees));
            globalCoordinates.add(new PointObject(centerX + relativeCoordinates.get(i).x * Math.cos(Math.toRadians(degrees)) - relativeCoordinates.get(i).y * Math.sin(Math.toRadians(degrees)), centerY + relativeCoordinates.get(i).x * Math.sin(Math.toRadians(degrees)) + relativeCoordinates.get(i).y * Math.cos(Math.toRadians(degrees))));
        }
    }


    void setShips() {
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).formation = this;
        }
    }

    //renamed from remakeFormation
    void resetCenter() {

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).formation != this || !ships.get(i).exists) {
                System.out.println(ships.get(i));
                formationShips.remove(ships.get(i));
                if (ships.get(i) instanceof BattleShip) {
                    ships.get(i).maxSpeed = BattleShip.MAX_SPEED;
                } else if (ships.get(i) instanceof FlagShip) {
                    ships.get(i).maxSpeed = FlagShip.MAX_SPEED;
                } else if (ships.get(i) instanceof LaserCruiser) {
                    ships.get(i).maxSpeed = LaserCruiser.MAX_SPEED;
                } else if (ships.get(i) instanceof Fighter) {
                    ships.get(i).maxSpeed = Fighter.MAX_SPEED;
                } else if (ships.get(i) instanceof Bomber) {
                    ships.get(i).maxSpeed = Bomber.MAX_SPEED;
                } else if (ships.get(i) instanceof Scout) {
                    ships.get(i).maxSpeed = Scout.MAX_SPEED;
                } else if (ships.get(i) instanceof ResourceCollector) {
                    ships.get(i).maxSpeed = ResourceCollector.MAX_SPEED;
                } else if (ships.get(i) instanceof SpaceStation) {
                    ships.get(i).maxSpeed = SpaceStation.MAX_SPEED;
                }
                ships.remove(ships.get(i));
                setCenter();
                getPositions();
            }
        }
    }

    void moveFormation() {
        if (Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)) <= formationMaxSpeed){
            velocityX += accelerationX;
            velocityY += accelerationY;
        } else {
            velocityX -= velocityX / 150;
            velocityY -= velocityY / 150;
        }
        centerY -= velocityY;
        centerX += velocityX;

        //positionY += gravVelY;
        //positionX += gravVelX;

        //centerPosX = positionX + midX;
        //centerPosY = positionY + midY;
    }

    void setDestination(float x, float y) {
        destX = x;
        destY = y;

        new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                while (true) {
                    if (time == 500) {
                        time = 0;
                        driveFormation(destX, destY);
                    }
                    System.out.println(degrees);
                    if (checkDestination()){
                        stopMovement();
                        break;
                    }
                    Utilities.delay(1);
                    time++;
                }
            }
        }).start();
    }

    void driveFormation(double x, double y) {
        if (velocityX == 0 && velocityY == 0){
            accelerationX = accelerate * (float) Math.sin(Utilities.anglePoints(centerX, centerY, Utilities.circleAngleX(degrees, centerX, Fighter.constRadius), Utilities.circleAngleY(degrees, centerY, Fighter.constRadius)) * Math.PI / 180);
            accelerationY = accelerate * (float) Math.cos(Utilities.anglePoints(centerX, centerY, Utilities.circleAngleX(degrees, centerX, Fighter.constRadius), Utilities.circleAngleY(degrees, centerY, Fighter.constRadius)) * Math.PI / 180);
            return;
        }
        double requiredAngle = Utilities.anglePoints(centerX, centerY, x, y);

        if (Math.abs(degrees - requiredAngle) > 5) {
            int turnAngle = 100;

            double requiredPointX = Utilities.circleAngleX(requiredAngle, centerX, Fighter.constRadius);
            double requiredPointY = Utilities.circleAngleY(requiredAngle, centerY, Fighter.constRadius);
            double point1X = Utilities.circleAngleX(degrees + 100, centerX, Fighter.constRadius);
            double point1Y = Utilities.circleAngleY(degrees + 100, centerY, Fighter.constRadius);
            double point2X = Utilities.circleAngleX(degrees - 100, centerX, Fighter.constRadius);
            double point2Y = Utilities.circleAngleY(degrees - 100, centerY, Fighter.constRadius);

            if (Utilities.distanceFormula(point1X, point1Y, requiredPointX, requiredPointY) < Utilities.distanceFormula(point2X, point2Y, requiredPointX, requiredPointY)) {
                turnAngle *= -1;
            }

            float turnConstant = 1.5f;

            accelerationX = accelerate / turnConstant * (float) Math.sin(Utilities.anglePoints(centerX, centerY, Utilities.circleAngleX(degrees - turnAngle, centerX, Fighter.constRadius), Utilities.circleAngleY(degrees - turnAngle, centerY, Fighter.constRadius)) * Math.PI / 180);
            accelerationY = accelerate / turnConstant * (float) Math.cos(Utilities.anglePoints(centerX, centerY, Utilities.circleAngleX(degrees - turnAngle, centerX, Fighter.constRadius), Utilities.circleAngleY(degrees - turnAngle, centerY, Fighter.constRadius)) * Math.PI / 180);
        } else {
            accelerationX = accelerate * (float) Math.sin(Utilities.anglePoints(centerX, centerY, x, y) * Math.PI / 180);
            accelerationY = accelerate * (float) Math.cos(Utilities.anglePoints(centerX, centerY, x, y) * Math.PI / 180);
        }
    }

    boolean checkDestination(){
        return Utilities.distanceFormula(centerX, centerY, destX, destY) < Fighter.constRadius;
    }

    void stopMovement(){
        accelerationX = 0;
        accelerationY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    void rotateFormation() {
        if (accelerationX != 0 && accelerationY != 0) {
            degrees = (float) Utilities.angleDim(velocityX, velocityY);
        }
    }

        //Creates a rectangle formation
    void rectangleFormation() {
        int shipCounter = 0;
        float offsetX = 0, offsetY = 0;

        if (ships.size() <= 1) {
            return;
        }
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof BattleShip || ships.get(i) instanceof FlagShip) {
                offsetX += ships.get(i).avoidanceRadius * 1.5;
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                    relativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 1 || shipCounter % 4 == 2) {
                        offsetX -= ships.get(i).avoidanceRadius * 1.5;
                    }
                    ships.get(i).setDestination((float) (centerX + (offsetX * Math.pow(-1, shipCounter))), (float) centerY, false);
                    relativeCoordinates.add(new PointObject((float) (offsetX * Math.pow(-1, shipCounter)), (float) 0));
                    formationShips.add(ships.get(i));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof LaserCruiser) {
                offsetX += ships.get(i).avoidanceRadius * 1.5;
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                    relativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 1 || shipCounter % 4 == 2) {
                        offsetX -= ships.get(i).avoidanceRadius * 1.5;
                    }
                    ships.get(i).setDestination((float) (centerX + (offsetX * Math.pow(-1, shipCounter))), (float) centerY, false);
                    relativeCoordinates.add(new PointObject((float) (offsetX * Math.pow(-1, shipCounter)), (float) 0));
                    formationShips.add(ships.get(i));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof Fighter || ships.get(i) instanceof Bomber || ships.get(i) instanceof Scout || ships.get(i) instanceof ResourceCollector) {
                offsetX += ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) (centerY + ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4)), false);
                    relativeCoordinates.add(new PointObject((float) 0, (float) ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4)));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 0) {
                        offsetX -= ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                        offsetY = 1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 1) {
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 2) {
                        offsetX -= ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 3) {
                        offsetY = 1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    }
                }
                shipCounter++;
            }
        }
    }
}
