package com.newspacebattle;

import java.util.ArrayList;

/**
 * Created by Dylan on 2018-09-23. Not used right now.
 */
class Squad {

    private ArrayList<Ship> ships;
    private int squadType;

    Squad(ArrayList<Ship> ships, int squadType) {
        this.ships = ships;
        this.squadType = squadType;
    }
}