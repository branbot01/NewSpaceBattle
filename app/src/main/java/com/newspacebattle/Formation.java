package com.newspacebattle;

import android.os.Looper;

import java.util.ArrayList;

/**
 * Created by Brandon and Dylan on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    static int RECTANGLE_FORMATION = 0, V_FORMATION = 1, CIRCLE_FORMATION = 2, CUSTOM_FORMATION = 3;

    ArrayList<Ship> ships;
    ArrayList<Ship> formationShips = new ArrayList<>();
    float degrees, formationMaxSpeed, velocityX, velocityY, accelerationX, accelerationY, accelerate, destX, destY;
    boolean destination, turning, inBlackHole;
    ArrayList<Boolean> inPosition = new ArrayList<>();

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
            ships.get(i).formation = this;
        }
        setCenter();
        if (type == RECTANGLE_FORMATION) {
            rectangleFormation();
        } else if (type == V_FORMATION) {
            VFormation();
        } else if (type == CIRCLE_FORMATION) {
            circleFormation();
        } else if (type == CUSTOM_FORMATION) {
            customFormation();
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

    void disbandFormation(){
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).formation = null;
            setShipNormalSpeed(ships.get(i));
        }
        ships.clear();
        formationShips.clear();
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

        inBlackHole = false;
        for (int i = 0; i < GameScreen.blackHole.size(); i++) {
            if (Utilities.distanceFormula(GameScreen.blackHole.get(i).centerPosX, GameScreen.blackHole.get(i).centerPosY, centerX, centerY) < GameScreen.blackHole.get(i).radius * GameScreen.blackHole.get(i).pullDistance) {
                inBlackHole = true;
                for (int j = 0; j < formationShips.size(); j++) {
                    if (!formationShips.get(j).destination) {
                        formationShips.get(j).setDestination((float) globalCoordinates.get(j).x, (float) globalCoordinates.get(j).y);
                        setShipNormalSpeed(formationShips.get(j));
                    } else {
                        formationShips.get(j).destinationFinder.destX = (float) globalCoordinates.get(j).x;
                        formationShips.get(j).destinationFinder.destY = (float) globalCoordinates.get(j).y;
                    }
                }
                return;
            }
        }

        if (formationShips.size() > 0 && globalCoordinates.size() > 0 && globalCoordinatesCopy.size() > 0) {
            inPosition.clear();
            for (int i = 0; i < formationShips.size(); i++) {
                inPosition.add(!(Utilities.distanceFormula(formationShips.get(i).centerPosX, formationShips.get(i).centerPosY, globalCoordinates.get(i).x, globalCoordinates.get(i).y) > formationShips.get(i).radius) && !formationShips.get(i).destination);
            }

            for (int i = 0; i < formationShips.size(); i++) {
                if (formationShips.get(i).attacking){
                    continue;
                }
                if (inPosition.get(i) && destination) {
                    formationShips.get(i).velocityX = (float) (globalCoordinates.get(i).x - globalCoordinatesCopy.get(i).x);
                    formationShips.get(i).velocityY = -(float) (globalCoordinates.get(i).y - globalCoordinatesCopy.get(i).y);
                    if (formationShips.get(i).velocityX != 0 && formationShips.get(i).velocityY != 0) {
                        formationShips.get(i).accelerationX = Float.MIN_VALUE;
                        formationShips.get(i).accelerationY = Float.MIN_VALUE;
                    }
                    formationShips.get(i).maxSpeed = formationMaxSpeed;
                } else if (!inPosition.get(i) && !formationShips.get(i).destination && !turning) {
                    formationShips.get(i).setDestination((float) globalCoordinates.get(i).x, (float) globalCoordinates.get(i).y);
                    setShipNormalSpeed(formationShips.get(i));
                }
                formationShips.get(i).destinationFinder.destX = (float) globalCoordinates.get(i).x;
                formationShips.get(i).destinationFinder.destY = (float) globalCoordinates.get(i).y;
            }
        }
    }

    void setShipNormalSpeed(Ship ship){
        if (ship instanceof BattleShip) {
            ship.maxSpeed = BattleShip.MAX_SPEED;
        } else if (ship instanceof FlagShip) {
            ship.maxSpeed = FlagShip.MAX_SPEED;
        } else if (ship instanceof LaserCruiser) {
            ship.maxSpeed = LaserCruiser.MAX_SPEED;
        } else if (ship instanceof Fighter) {
            ship.maxSpeed = Fighter.MAX_SPEED;
        } else if (ship instanceof Bomber) {
            ship.maxSpeed = Bomber.MAX_SPEED;
        } else if (ship instanceof Scout) {
            ship.maxSpeed = Scout.MAX_SPEED;
        } else if (ship instanceof ResourceCollector) {
            ship.maxSpeed = ResourceCollector.MAX_SPEED;
        } else if (ship instanceof SpaceStation) {
            ship.maxSpeed = SpaceStation.MAX_SPEED;
        }
    }

    //renamed from remakeFormation
    void resetCenter() {
        Ship attacker = null;
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).attacking){
                attacker = ships.get(i);
                break;
            }
            if (ships.get(i).formation != this || !ships.get(i).exists) {
                formationShips.remove(ships.get(i));
                setShipNormalSpeed(ships.get(i));
                ships.remove(i);
            }
        }

        if (attacker != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).attacking){
                    continue;
                }
                ships.get(i).stop();
                ships.get(i).destinationFinder.runAttack(new ArrayList<>(attacker.destinationFinder.enemies));
                formationShips.remove(ships.get(i));
                setShipNormalSpeed(ships.get(i));
                ships.remove(ships.get(i));
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
            double requiredAngle = Utilities.anglePoints(centerX, centerY, destX, destY);
            double requiredPointX = Utilities.circleAngleX(requiredAngle, centerX, Fighter.constRadius);
            double requiredPointY = Utilities.circleAngleY(requiredAngle, centerY, Fighter.constRadius);
            double point1X = Utilities.circleAngleX(degrees + 100, centerX, Fighter.constRadius);
            double point1Y = Utilities.circleAngleY(degrees + 100, centerY, Fighter.constRadius);
            double point2X = Utilities.circleAngleX(degrees - 100, centerX, Fighter.constRadius);
            double point2Y = Utilities.circleAngleY(degrees - 100, centerY, Fighter.constRadius);

            double increment = 0.1;
            if (Utilities.distanceFormula(point1X, point1Y, requiredPointX, requiredPointY) < Utilities.distanceFormula(point2X, point2Y, requiredPointX, requiredPointY)) {
                increment *= -1;
            }
            turning = true;
            while (Math.abs(degrees - requiredAngle) > 1) {
                requiredAngle = Utilities.anglePoints(centerX, centerY, destX, destY);
                degrees -= increment / ships.size();
                if (degrees > 360) {
                    degrees -= 360;
                } else if (degrees < 0) {
                    degrees += 360;
                }
                Utilities.delay(1);
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

    void rectangleFormation() {
        int shipCounter = 0;
        int i = 0, i2 = 2, i3 = 2, j = 1, leftOrRight = 1;
        float offsetX = 0, offsetY = 0, offsetYDirection = -1, previousLeftOffsetX = 0, previousRightOffsetX = 0, secondPreviousLeftOffsetX = 0, secondPreviousRightOffsetX = 0;
        float reduceDistance = 1f;
        boolean leftStart = true;

        if (ships.size() <= 1) {
            return;
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof FlagShip || ships.get(shipIndex) instanceof BattleShip){
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float)centerX, (float)centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(shipIndex));
                    i = 2;
                    leftOrRight = 1;
                    //System.out.println("L index 0");
                }else{
                    if(leftOrRight == 1){
                        offsetX = (float) -(BattleShip.constRadius * 3.5 * (i - 1));
                        previousLeftOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) 0));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight++;
                        //System.out.println("L1");
                        //System.out.println("left offset = " + previousLeftOffsetX);
                    }else if(leftOrRight == 2){
                        offsetX = (float) (BattleShip.constRadius * 3.5 * (i - 1));
                        previousRightOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) 0));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight = 1;
                        i++;
                        //System.out.println("L2");
                        //System.out.println("right offset = " + previousRightOffsetX);
                    }
                }
                shipCounter++;
            }
        }

        if(leftOrRight == 2){
            leftStart = false;
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof LaserCruiser){
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float)centerX, (float)centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(shipIndex));
                    i2 = 2;
                    leftOrRight = 1;
                    //System.out.println("M index 0");
                }else{
                    if(leftOrRight == 1){
                        offsetX = -previousLeftOffsetX + (float) -(LaserCruiser.constRadius * 4 * (i2 - 1));
                        secondPreviousLeftOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) 0));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight++;
                        if(!leftStart){
                            i2++;
                        }
                        //System.out.println("M1");
                    }else if(leftOrRight == 2){
                        offsetX = previousRightOffsetX + (float) (LaserCruiser.constRadius * 4 * (i2 - 1));
                        secondPreviousRightOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) 0));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight = 1;
                        if(leftStart){
                            i2++;
                        }
                        //System.out.println("M2");
                    }
                }
                shipCounter++;
            }
        }

        if(leftOrRight == 2){
            leftStart = false;
        }
        if(shipCounter != 0){
            offsetYDirection *= -1;
        }else{
            reduceDistance = 0.6f;
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof Fighter || ships.get(shipIndex) instanceof Bomber || ships.get(shipIndex) instanceof Scout || ships.get(shipIndex) instanceof ResourceCollector){
                if(shipCounter == 0){
                    offsetY = (float) -((Fighter.constRadius * 8 * reduceDistance) * (Math.sqrt(3) / 4));
                    //System.out.println("offsetY = " + offsetY);
                    ships.get(shipIndex).setDestination((float)centerX, (float)(centerY + offsetY));
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) offsetY));
                    formationShips.add(ships.get(shipIndex));
                    j = 1;
                    i3 = 2;
                    leftOrRight = 1;
                    //System.out.println("S index 0");
                }else{
                    if(leftOrRight == 1){
                        if(j == 1 || j == 3){
                            offsetYDirection *= -1;
                        }
                        offsetY = offsetYDirection * (float) ((Fighter.constRadius * 8 * reduceDistance) * (Math.sqrt(3) / 4));
                        //System.out.println("offsetY = " + offsetY);
                        if(secondPreviousLeftOffsetX == 0){
                            secondPreviousLeftOffsetX = previousLeftOffsetX;
                        }
                        offsetX = -secondPreviousLeftOffsetX + (float) -(Fighter.constRadius * 8 * reduceDistance * (0.5) * (i3 - 1));
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight++;
                        if(!leftStart){
                            i3++;
                        }
                        if(j == 4){
                            j = 1;
                        }else{
                            j++;
                        }
                        //System.out.println("S1");
                    }else if(leftOrRight == 2){
                        if(j == 1 || j == 3){
                            offsetYDirection *= -1;
                        }
                        offsetY = offsetYDirection * (float) ((Fighter.constRadius * 8 * reduceDistance) * (Math.sqrt(3) / 4));
                        //System.out.println("offsetY = " + offsetY);
                        if(secondPreviousRightOffsetX == 0){
                            secondPreviousRightOffsetX = previousRightOffsetX;
                        }
                        offsetX = secondPreviousRightOffsetX + (float) (Fighter.constRadius * 8 * reduceDistance * (0.5) * (i3 - 1));
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        leftOrRight = 1;
                        if(leftStart){
                            i3++;
                        }
                        if(j == 4){
                            j = 1;
                        }else{
                            j++;
                        }
                        //System.out.println("S2");
                    }
                }
                shipCounter++;
            }
        }
    }


    //Creates a rectangle formation
    void oldRectangleFormation() {
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

    void VFormation() {
        int i = 0, j = 0, j2 = 2, j3 = 2;
        float offsetX = 0, offsetY = 0, previousOffsetX = 0, previousOffsetY = 0, secondPreviousOffsetX = 0, secondPreviousOffsetY = 0;
        int shipCounter = 0;

        if(ships.size() <= 1){
            return;
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++) {
            if(ships.get(shipIndex) instanceof Fighter || ships.get(shipIndex) instanceof Bomber || ships.get(shipIndex) instanceof Scout || ships.get(shipIndex) instanceof ResourceCollector) {
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float)centerX, (float)centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(shipIndex));
                    i = 1;
                    j = 2;
                    //System.out.println("index 0");
                }else{
                    offsetY = (float) ((Fighter.constRadius * 5) * (Math.sin(Math.toRadians(55))) * (j - 1));
                    previousOffsetY = offsetY;
                    if(i == 1){
                        offsetX = (float) -((Fighter.constRadius * 5) * (Math.cos(Math.toRadians(55))) * (j - 1));
                        previousOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("1. i = " + i + " j = " + j);
                    }else if(i == 2){
                        offsetX = (float) ((Fighter.constRadius * 5) * (Math.cos(Math.toRadians(55))) * (j - 1));
                        previousOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i = 1;
                        j++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("2. i = " + i + " j = " + j);
                    }
                }
                shipCounter++;
            }
        }

        i = 1;
        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++) {
            if(ships.get(shipIndex) instanceof LaserCruiser) {
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float)centerX, (float)centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(shipIndex));
                    i = 1;
                    j2 = 2;
                    //System.out.println("index 0");
                }else{
                    offsetY = previousOffsetY + (float) ((LaserCruiser.constRadius * 5) * (Math.sin(Math.toRadians(55))) * (j2 - 1));
                    secondPreviousOffsetY = offsetY;
                    if(i == 1){
                        offsetX = -previousOffsetX + (float) -((LaserCruiser.constRadius * 5) * (Math.cos(Math.toRadians(55))) * (j2 - 1));
                        secondPreviousOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("1. i = " + i + " j = " + j);
                    }else if(i == 2){
                        offsetX = previousOffsetX + (float) ((LaserCruiser.constRadius * 5) * (Math.cos(Math.toRadians(55))) * (j2- 1));
                        secondPreviousOffsetX = Math.abs(offsetX);
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i = 1;
                        j2++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("2. i = " + i + " j = " + j);
                    }
                }
                shipCounter++;
            }
        }

        i = 1;
        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++) {
            if(ships.get(shipIndex) instanceof BattleShip || ships.get(shipIndex) instanceof FlagShip) {
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float)centerX, (float)centerY);
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) 0));
                    formationShips.add(ships.get(shipIndex));
                    i = 1;
                    j = 2;
                    //System.out.println("index 0");
                }else{
                    if(secondPreviousOffsetY == 0){
                        secondPreviousOffsetY = previousOffsetY;
                    }
                    offsetY = secondPreviousOffsetY + (float) ((BattleShip.constRadius * 3.5) * (Math.sin(Math.toRadians(55))) * (j3 - 1));
                    if(i == 1){
                        if(secondPreviousOffsetX == 0){
                            secondPreviousOffsetX = previousOffsetX;
                        }
                        offsetX = -secondPreviousOffsetX + (float) -((BattleShip.constRadius * 3.5) * (Math.cos(Math.toRadians(55))) * (j3 - 1));
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("1. i = " + i + " j = " + j);
                    }else if(i == 2){
                        if(secondPreviousOffsetX == 0){
                            secondPreviousOffsetX = previousOffsetX;
                        }
                        offsetX = secondPreviousOffsetX + (float) ((BattleShip.constRadius * 3.5) * (Math.cos(Math.toRadians(55))) * (j3 - 1));
                        ships.get(shipIndex).setDestination((float)(centerX + offsetX), (float)(centerY + offsetY));
                        initialRelativeCoordinates.add(new PointObject((float) offsetX, (float) offsetY));
                        formationShips.add(ships.get(shipIndex));
                        i = 1;
                        j3++;
                        //System.out.println(centerX + offsetX);
                        //System.out.println("2. i = " + i + " j = " + j);
                    }
                }
            }
            shipCounter++;
        }
    }

    void circleFormation() {
        float degrees = 0, radius = 0;
        float largestAvoidanceRadius = 0, avoidanceRadiusBuffer = 1;
        float offsetX = 0, offsetY = 0, degreeOffset = 0;
        int shipCounter = 0;

        if(ships.size() <= 1){
            return;
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex).avoidanceRadius > largestAvoidanceRadius){
                largestAvoidanceRadius = ships.get(shipIndex).avoidanceRadius;
            }
        }

        degrees = 360f / ships.size();

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof LaserCruiser){
                avoidanceRadiusBuffer = 1.1f;
            }else if(ships.get(shipIndex) instanceof BattleShip || ships.get(shipIndex) instanceof FlagShip){
                avoidanceRadiusBuffer = 1.4f;
            }
        }
        radius = (float) (0.5f * (largestAvoidanceRadius * avoidanceRadiusBuffer) * (1f / Math.cos(Math.toRadians(90f - degrees / 2f))));

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof Fighter || ships.get(shipIndex) instanceof Bomber || ships.get(shipIndex) instanceof Scout || ships.get(shipIndex) instanceof ResourceCollector){
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float) centerX, (float) (centerY - radius));
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) -radius));
                    formationShips.add(ships.get(shipIndex));
                    degreeOffset += degrees;
                }else{
                    offsetX = (float) (radius * Math.sin(Math.toRadians(degreeOffset)));
                    offsetY = (float) -(radius * Math.cos(Math.toRadians(degreeOffset)));
                    ships.get(shipIndex).setDestination((float) (centerX + offsetX), (float) (centerY + offsetY));
                    initialRelativeCoordinates.add(new PointObject((float) (offsetX), (float) (offsetY)));
                    formationShips.add(ships.get(shipIndex));
                    if(shipCounter % 2 == 0){
                        if(degreeOffset < 0){
                            degreeOffset *= -1;
                        }
                        degreeOffset += degrees;
                    }else if(shipCounter % 2 == 1){
                        degreeOffset *= -1;
                    }
                }
                shipCounter++;
            }
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof LaserCruiser){
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float) centerX, (float) (centerY - radius));
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) -radius));
                    formationShips.add(ships.get(shipIndex));
                    degreeOffset += degrees;
                }else{
                    offsetX = (float) (radius * Math.sin(Math.toRadians(degreeOffset)));
                    offsetY = (float) -(radius * Math.cos(Math.toRadians(degreeOffset)));
                    ships.get(shipIndex).setDestination((float) (centerX + offsetX), (float) (centerY + offsetY));
                    initialRelativeCoordinates.add(new PointObject((float) (offsetX), (float) (offsetY)));
                    formationShips.add(ships.get(shipIndex));
                    if(shipCounter % 2 == 0){
                        if(degreeOffset < 0){
                            degreeOffset *= -1;
                        }
                        degreeOffset += degrees;
                    }else if(shipCounter % 2 == 1){
                        degreeOffset *= -1;
                    }
                }
                shipCounter++;
            }
        }

        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            if(ships.get(shipIndex) instanceof BattleShip || ships.get(shipIndex) instanceof FlagShip){
                if(shipCounter == 0){
                    ships.get(shipIndex).setDestination((float) centerX, (float) (centerY - radius));
                    initialRelativeCoordinates.add(new PointObject((float) 0, (float) -radius));
                    formationShips.add(ships.get(shipIndex));
                    degreeOffset += degrees;
                }else{
                    offsetX = (float) (radius * Math.sin(Math.toRadians(degreeOffset)));
                    offsetY = (float) -(radius * Math.cos(Math.toRadians(degreeOffset)));
                    ships.get(shipIndex).setDestination((float) (centerX + offsetX), (float) (centerY + offsetY));
                    initialRelativeCoordinates.add(new PointObject((float) (offsetX), (float) (offsetY)));
                    formationShips.add(ships.get(shipIndex));
                    if(shipCounter % 2 == 0){
                        if(degreeOffset < 0){
                            degreeOffset *= -1;
                        }
                        degreeOffset += degrees;
                    }else if(shipCounter % 2 == 1){
                        degreeOffset *= -1;
                    }
                }
                shipCounter++;
            }
        }
    }

    void customFormation() {
        if(ships.size() <= 1){
            return;
        }
        for(int shipIndex = 0; shipIndex < ships.size(); shipIndex++){
            initialRelativeCoordinates.add(new PointObject(ships.get(shipIndex).centerPosX - centerX, ships.get(shipIndex).centerPosY - centerY));
            formationShips.add(ships.get(shipIndex));
        }
    }
}
