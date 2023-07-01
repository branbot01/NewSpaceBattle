package com.newspacebattle;

import java.util.ArrayList;

/**
 * Created by Dylan and Brandon on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    ArrayList<Ship> ships;
    double centerX, centerY;
    int type;

    //Constructor method
    Formation(ArrayList<Ship> ships, int type) {
        this.type = type;
        this.ships = ships;
        setCenter();
        if (type == 0) {
            rectangleFormation();
        }
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

    void rectangleFormation() {
        int shipCounter = 0;

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof BattleShip || ships.get(i) instanceof FlagShip) {
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                } else {
                    ships.get(i).setDestination((float) centerX + (ships.get(i - 1).avoidanceRadius) * (float) Math.pow(-1, shipCounter), (float) centerY, false);
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof LaserCruiser) {
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                } else {
                    ships.get(i).setDestination((float) centerX + (ships.get(i - 1).avoidanceRadius) * (float) Math.pow(-1, shipCounter), (float) centerY, false);
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof Fighter || ships.get(i) instanceof Bomber || ships.get(i) instanceof Scout || ships.get(i) instanceof ResourceCollector) {
                if (shipCounter == 0){
                    ships.get(i).setDestination((float) centerX, (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                } else {
                    if (shipCounter % 3 == 0) {
                        ships.get(i).setDestination((float) ((centerX + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)) * (float) Math.pow(-1, shipCounter)), (float) (centerY - ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                    }else {
                        ships.get(i).setDestination((float) ((centerX + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)) * (float) Math.pow(-1, shipCounter)), (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                    }
                }
            }
        }
    }
}
