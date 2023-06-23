package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-30. Defines a point using floats instead of integers.
 */
class PointObject {

    double x, y;
    boolean intersectCheck;
    GameObject object;

    //Constructor method
    PointObject(double xPos, double yPos) {
        x = xPos;
        y = yPos;
        intersectCheck = true;
    }

    public String toString() {
        return "PointObject: x: " + x + " y: " + y + " intersectCheck: " + intersectCheck;
    }
}
