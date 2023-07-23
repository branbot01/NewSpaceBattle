package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;

/**
 * Created by Brandon and Dylan on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    ArrayList<Ship> ships;
    ArrayList<Ship> formationShips = new ArrayList<>();
    float degrees, formationMaxSpeed, velocityX, velocityY, accelerationX, accelerationY, accelerate, destX, destY;
    boolean destination, turning;

    //visualize ship locations
    ArrayList<PointObject> globalCoordinates = new ArrayList<>();
    ArrayList<PointObject> globalCoordinatesCopy = new ArrayList<>();
    ArrayList<PointObject> initialRelativeCoordinates = new ArrayList<>();

    double centerX, centerY;
    int type, initialSize;

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
        updatePositions();
        moveFormation();
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

    //update position of ship if formation rotates
    void updatePositions() {
        globalCoordinatesCopy.clear();
        if (globalCoordinates.size() > 0) {
            globalCoordinatesCopy.addAll(globalCoordinates);
        }
        globalCoordinates.clear();
        for (int i = 0; i < formationShips.size(); i++) {
            globalCoordinates.add(new PointObject(centerX + initialRelativeCoordinates.get(i).x * Math.cos(Math.toRadians(degrees)) - initialRelativeCoordinates.get(i).y * Math.sin(Math.toRadians(degrees)), centerY + initialRelativeCoordinates.get(i).x * Math.sin(Math.toRadians(degrees)) + initialRelativeCoordinates.get(i).y * Math.cos(Math.toRadians(degrees))));
        }
        if (formationShips.size() > 0 && globalCoordinates.size() > 0 && globalCoordinatesCopy.size() > 0) {
            boolean[] inPosition = new boolean[formationShips.size()];
            for (int i = 0; i < formationShips.size(); i++) {
                inPosition[i] = !(Utilities.distanceFormula(formationShips.get(i).centerPosX, formationShips.get(i).centerPosY, globalCoordinates.get(i).x, globalCoordinates.get(i).y) > formationShips.get(i).radius) && !formationShips.get(i).destination;
            }

            boolean allFalse = true;
            for (boolean b : inPosition) {
                if (b) {
                    allFalse = false;
                    break;
                }
            }

            if (allFalse) {
                stopMovement();
                return;
            }

            for (int i = 0; i < formationShips.size(); i++) {
                if (inPosition[i] && destination) {
                    formationShips.get(i).velocityX = (float) (globalCoordinates.get(i).x - globalCoordinatesCopy.get(i).x);
                    formationShips.get(i).velocityY = -(float) (globalCoordinates.get(i).y - globalCoordinatesCopy.get(i).y);
                    if (formationShips.get(i).velocityX != 0 && formationShips.get(i).velocityY != 0) {
                        formationShips.get(i).accelerationX = Float.MIN_VALUE;
                        formationShips.get(i).accelerationY = Float.MIN_VALUE;
                    }
                } else if (!inPosition[i] && !formationShips.get(i).destination && !turning) {
                    formationShips.get(i).setDestination((float) globalCoordinates.get(i).x, (float) globalCoordinates.get(i).y);
                }
                formationShips.get(i).destinationFinder.destX = (float) globalCoordinates.get(i).x;
                formationShips.get(i).destinationFinder.destY = (float) globalCoordinates.get(i).y;
            }
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
            }
        }
    }

    void moveFormation() {
        if (Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)) <= formationMaxSpeed) {
            velocityX += accelerationX;
            velocityY += accelerationY;
        } else {
            velocityX -= velocityX / 150;
            velocityY -= velocityY / 150;
        }
        centerY -= velocityY;
        centerX += velocityX;
    }

    void setDestination(float x, float y) {
        destination = true;
        destX = x;
        destY = y;
        if (velocityX != 0 || velocityY != 0 || turning) {
            return;
        }

        new Thread(() -> {
            int time = 0;
            Looper.prepare();
            double requiredAngle = Utilities.anglePoints(centerX, centerY, x, y);
            double requiredPointX = Utilities.circleAngleX(requiredAngle, centerX, Fighter.constRadius);
            double requiredPointY = Utilities.circleAngleY(requiredAngle, centerY, Fighter.constRadius);
            double point1X = Utilities.circleAngleX(degrees + 100, centerX, Fighter.constRadius);
            double point1Y = Utilities.circleAngleY(degrees + 100, centerY, Fighter.constRadius);
            double point2X = Utilities.circleAngleX(degrees - 100, centerX, Fighter.constRadius);
            double point2Y = Utilities.circleAngleY(degrees - 100, centerY, Fighter.constRadius);

            double increment = 0.0001;
            if (Utilities.distanceFormula(point1X, point1Y, requiredPointX, requiredPointY) < Utilities.distanceFormula(point2X, point2Y, requiredPointX, requiredPointY)) {
                increment *= -1;
            }
            turning = true;
            while (Math.abs(degrees - requiredAngle) > 1) {
                degrees -= increment;
                if (degrees > 360) {
                    degrees -= 360;
                } else if (degrees < 0) {
                    degrees += 360;
                }
                System.out.println(degrees);
            }
            turning = false;

            while (destination) {
                if (time == 500) {
                    time = 0;
                    driveFormation(destX, destY);
                }
                if (checkDestination()) {
                    stopMovement();
                    break;
                }
                rotateFormation();
                Utilities.delay(1);
                time++;
            }
        }).start();
    }

    void driveFormation(double x, double y) {
        double requiredAngle = Utilities.anglePoints(centerX, centerY, x, y);

        if (Math.abs(degrees - requiredAngle) > 5) {
            int turnAngle = 80;

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
        }
    }

    boolean checkDestination() {
        double requiredAngle = Utilities.anglePoints(centerX, centerY, destX, destY);
        if (Math.abs(degrees - requiredAngle) <= 5) {
            accelerationX = accelerate * (float) Math.sin(requiredAngle * Math.PI / 180);
            accelerationY = accelerate * (float) Math.cos(requiredAngle * Math.PI / 180);
        }
        return Utilities.distanceFormula(centerX, centerY, destX, destY) < Fighter.constRadius;
    }

    void stopMovement() {
        accelerationX = 0;
        accelerationY = 0;
        velocityX = 0;
        velocityY = 0;
        destination = false;

        for (int i = 0; i < formationShips.size(); i++) {
            if (!(Utilities.distanceFormula(formationShips.get(i).centerPosX, formationShips.get(i).centerPosY, globalCoordinates.get(i).x, globalCoordinates.get(i).y) > formationShips.get(i).radius) && !formationShips.get(i).destination) {
                ships.get(i).stop();
            }
        }
    }

    void rotateFormation() {
        if (accelerationX != 0 && accelerationY != 0 && velocityX != 0 && velocityY != 0) {
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
                    ships.get(i).setDestination((float) centerX, (float) centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 1 || shipCounter % 4 == 2) {
                        offsetX -= ships.get(i).avoidanceRadius * 1.5;
                    }
                    ships.get(i).setDestination((float) (centerX + (offsetX * Math.pow(-1, shipCounter))), (float) centerY);
                    initialRelativeCoordinates.add(new PointObject((float) (offsetX * Math.pow(-1, shipCounter)), (float) 0));
                    formationShips.add(ships.get(i));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof LaserCruiser) {
                offsetX += ships.get(i).avoidanceRadius * 1.5;
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 1 || shipCounter % 4 == 2) {
                        offsetX -= ships.get(i).avoidanceRadius * 1.5;
                    }
                    ships.get(i).setDestination((float) (centerX + (offsetX * Math.pow(-1, shipCounter))), (float) centerY);
                    initialRelativeCoordinates.add(new PointObject((float) (offsetX * Math.pow(-1, shipCounter)), (float) 0));
                    formationShips.add(ships.get(i));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof Fighter || ships.get(i) instanceof Bomber || ships.get(i) instanceof Scout || ships.get(i) instanceof ResourceCollector) {
                offsetX += ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) (centerY + ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4)));
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4)));
                    formationShips.add(ships.get(i));
                } else {
                    if (shipCounter % 4 == 0) {
                        offsetX -= ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                        offsetY = 1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 1) {
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 2) {
                        offsetX -= ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    } else if (shipCounter % 4 == 3) {
                        offsetY = 1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    }
                }
                shipCounter++;
            }
        }
    }
}
