package com.newspacebattle;

import java.util.ArrayList;

class Formation {

    ArrayList<Ship> ships;
    double centerX, centerY;

    Formation(ArrayList<Ship> ships) {
        this.ships = ships;
        setCenter();
    }

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
}
