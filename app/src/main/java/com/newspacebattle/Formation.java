package com.newspacebattle;

import java.util.ArrayList;

/**
 * Created by Brandon and Dylan on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    ArrayList<Ship> ships;
    ArrayList<Ship> formationShips = new ArrayList<>();
    float initialSize;
    float direction, formationMaxSpeed;

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
        direction = 0;
        formationMaxSpeed = Float.MAX_VALUE;
        for(int i = 0; i < this.ships.size(); i++) {
            if(this.ships.get(i).maxSpeed < formationMaxSpeed) {
                formationMaxSpeed = this.ships.get(i).maxSpeed;
            }
        }
        for(int i = 0; i < this.ships.size(); i++) {
             this.ships.get(i).maxSpeed = formationMaxSpeed;
        }
        setShips();
        setCenter();
        if (type == 0) {
            rectangleFormation();
        }
        getPositions();
    }
    //update's the formation's properties
    void update() {
        resetCenter();
        if(direction != 0) {
            getPositions();
        }
        updatePositions();
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
        for(int i = 0; i < formationShips.size(); i++) {
            relativeCoordinates.get(i).x = (formationShips.get(i).centerPosX - centerX) * Math.cos(Math.toRadians(direction)) + (formationShips.get(i).centerPosY - centerY) * Math.sin(Math.toRadians(direction));
            relativeCoordinates.get(i).y = -(formationShips.get(i).centerPosX - centerX) * Math.sin(Math.toRadians(direction)) + (formationShips.get(i).centerPosY - centerY) * Math.cos(Math.toRadians(direction));
        }
    }

    //update position of ship if formation rotates
    void updatePositions() {
        for(int i = 0; i < formationShips.size(); i++){
            globalCoordinates.get(i).x = centerX + relativeCoordinates.get(i).x * Math.cos(Math.toRadians(direction)) - relativeCoordinates.get(i).y * Math.sin(Math.toRadians(direction));
            globalCoordinates.get(i).y = centerY + relativeCoordinates.get(i).x * Math.sin(Math.toRadians(direction)) + relativeCoordinates.get(i).y * Math.cos(Math.toRadians(direction));
        }
    }


    void setShips() {
        for(int i = 0; i < ships.size(); i++) {
            ships.get(i).formation = this;
        }
    }
    //renamed from remakeFormation
    void resetCenter() {

        for(int i = 0; i < ships.size(); i++) {
            if(ships.get(i).formation != this || !ships.get(i).exists){
                System.out.println(ships.get(i));
                formationShips.remove(ships.get(i));
                ships.remove(ships.get(i));
                if(ships.get(i) instanceof BattleShip){
                    ships.get(i).maxSpeed = BattleShip.MAX_SPEED;
                } else if (ships.get(i) instanceof FlagShip) {
                    ships.get(i).maxSpeed = FlagShip.MAX_SPEED;
                }else if (ships.get(i) instanceof LaserCruiser) {
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
                    setCenter();
                    getPositions();
                }
        }
    }

    //Creates a rectangle formation
    void rectangleFormation() {
        int shipCounter = 0;
        float offsetX = 0, offsetY = 0;

        if(ships.size() <= 1){
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
                if (shipCounter == 0){
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
                    }else if (shipCounter % 4 == 1){
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    }else if (shipCounter % 4 == 2){
                        offsetX -= ((Fighter.constRadius * 5 * Math.sqrt(3)) / 4);
                        offsetY = -1;
                        ships.get(i).setDestination((float) ((centerX + (offsetX * Math.pow(-1, shipCounter)))), (float) (centerY + Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY), false);
                        relativeCoordinates.add(new PointObject((float) ((offsetX * Math.pow(-1, shipCounter))), (float) (Fighter.constRadius * 5 * Math.sqrt(3) / 4 * offsetY)));
                        formationShips.add(ships.get(i));
                    }else if (shipCounter % 4 == 3){
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
