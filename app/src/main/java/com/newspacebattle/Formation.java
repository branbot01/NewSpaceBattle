package com.newspacebattle;

import java.util.ArrayList;

/**
 * Created by Dylan and Brandon on 2023-06-29. Defines a formation, which is a structured group of ships.
 */
class Formation {

    ArrayList<Ship> ships;
    ArrayList<PointObject> points;
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
        float offsetX = 0;

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof BattleShip || ships.get(i) instanceof FlagShip) {
                offsetX += (float) (ships.get(i).avoidanceRadius);
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                    points.add(new PointObject((float) centerX, (float) centerY));
                } else {
                    ships.get(i).setDestination((float) centerX + offsetX * (float) Math.pow(-1, shipCounter), (float) centerY, false);
                    points.add(new PointObject((float) centerX + offsetX * (float) Math.pow(-1, shipCounter), (float) centerY));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof LaserCruiser) {
                offsetX += (float) (ships.get(i).avoidanceRadius);
                if (shipCounter == 0) {
                    ships.get(i).setDestination((float) centerX, (float) centerY, false);
                    points.add(new PointObject((float) centerX, (float) centerY));
                } else {
                    ships.get(i).setDestination((float) centerX + offsetX * (float) Math.pow(-1, shipCounter), (float) centerY, false);
                    points.add(new PointObject((float) centerX + offsetX * (float) Math.pow(-1, shipCounter), (float) centerY));
                }
                shipCounter++;
            }
        }

        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i) instanceof Fighter || ships.get(i) instanceof Bomber || ships.get(i) instanceof Scout || ships.get(i) instanceof ResourceCollector) {
                offsetX += ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4);
                if (shipCounter == 0){
                    ships.get(i).setDestination((float) centerX, (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                    points.add(new PointObject((float) centerX, (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4))));
                } else {
                    if (shipCounter % 4 == 0 || shipCounter % 4 == 1) {
                        ships.get(i).setDestination((float) (centerX + offsetX * Math.pow(-1, shipCounter)), (float) (centerY - ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                        points.add(new PointObject((float) (centerX + offsetX * Math.pow(-1, shipCounter)), (float) (centerY - ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4))));
                    }else {
                        ships.get(i).setDestination((float) (centerX + offsetX * Math.pow(-1, shipCounter)), (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4)), false);
                        points.add(new PointObject((float) (centerX + offsetX * Math.pow(-1, shipCounter)), (float) (centerY + ((Fighter.constRadius * 10 * Math.sqrt(3)) / 4))));
                    }
                }
            }
        }
    }
}
