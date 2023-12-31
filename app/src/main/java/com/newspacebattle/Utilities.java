package com.newspacebattle;

/**
 * Created by Dylan on 2018-07-12. Some math functions that come in very handy.
 */
class Utilities {
    //Determines an angle from one point to another
    static double anglePoints(double point1x, double point1y, double point2x, double point2y) {
        double distanceX = point2x - point1x;
        double distanceY = point2y - point1y;
        double angle = Math.toDegrees(Math.atan(Math.abs(distanceY / distanceX)));
        if (distanceX == 0 && distanceY > 0) {
            angle = 0;
        } else if (distanceX == 0 && distanceY < 0) {
            angle = 180;
        }
        if (distanceX > 0 && distanceY == 0) {
            angle = 90;
        } else if (distanceX < 0 && distanceY == 0) {
            angle = 270;
        }
        if (distanceX < 0 && distanceY < 0) {
            angle += 270;
        } else if (distanceX > 0 && distanceY > 0) {
            angle += 90;
        } else if (distanceX > 0 && distanceY < 0) {
            angle = 90 - angle;
        } else if (distanceX < 0 && distanceY > 0) {
            angle = 90 - angle;
            angle += 180;
        }
        return angle;
    }

    //Based on two dimensions, get an angle out of it
    static double angleDim(float dimX, float dimY) {
        double angle = Math.toDegrees(Math.atan(Math.abs(dimY / dimX)));
        if (dimX == 0 && dimY > 0) {
            angle = 0;
        } else if (dimX == 0 && dimY < 0) {
            angle = 180;
        }
        if (dimX > 0 && dimY == 0) {
            angle = 90;
        } else if (dimX < 0 && dimY == 0) {
            angle = 270;
        }
        if (dimX < 0 && dimY < 0) {
            angle = 90 - angle;
            angle += 180;
        } else if (dimX > 0 && dimY > 0) {
            angle = 90 - angle;
        } else if (dimX > 0 && dimY < 0) {
            angle += 90;
        } else if (dimX < 0 && dimY > 0) {
            angle += 270;
        }
        return angle;
    }

    //Distance formula
    static double distanceFormula(double point1x, double point1y, double point2x, double point2y) {
        return Math.sqrt(Math.pow(point2x - point1x, 2) + Math.pow(point2y - point1y, 2));
    }

    //Delay function
    static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("nope");
        }
    }

    //Gets 1st x answer
    private static double quadraticEqu1x(double a, double b, double c) {
        return (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
    }

    //Gets 1st y answer
    private static double quadraticEqu1y(double a, double b, double c, double ml, double bl) {
        return ml * quadraticEqu1x(a, b, c) + bl;
    }

    //Gets 2nd x answer
    private static double quadraticEqu2x(double a, double b, double c) {
        return (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
    }

    //Gets 2nd y answer
    private static double quadraticEqu2y(double a, double b, double c, double ml, double bl) {
        return ml * quadraticEqu1x(a, b, c) + bl;
    }

    //Discriminant from quad formula
    private static double discriminant(double a, double b, double c) {
        return Math.pow(b, 2) - 4 * a * c;
    }

    //Determines if a line and circle intersect
    static PointObject lineCircleIntersect(Ship thisObj, double x1, double y1, double x2, double y2, GameObject object) {
        double ml = (y2 - y1) / (x2 - x1);
        double bl = y2 - ml * x2;

        double a = 1 + Math.pow(ml, 2);
        double b = -2 * object.centerPosX + 2 * ml * bl - 2 * object.centerPosY * ml;
        double c = Math.pow(object.centerPosX, 2) + Math.pow(bl, 2) - 2 * object.centerPosY * bl + Math.pow(object.centerPosY, 2) - Math.pow(object.radius + thisObj.radius + 20, 2);

        double intersects = discriminant(a, b, c);

        if (intersects < 0) {
            return null;
        }

        PointObject closestIntersect = new PointObject(0, 0);
        if (intersects == 0) {
            closestIntersect.x = quadraticEqu1x(a, b, c);
            closestIntersect.y = quadraticEqu1y(a, b, c, ml, bl);
        } else if (intersects > 0) {
            double quad1x = quadraticEqu1x(a, b, c);
            double quad1y = quadraticEqu1y(a, b, c, ml, bl);
            double quad2x = quadraticEqu2x(a, b, c);
            double quad2y = quadraticEqu2y(a, b, c, ml, bl);

            if (distanceFormula(quad1x, quad1y, x1, y1) > distanceFormula(quad2x, quad2y, x1, y1)) {
                closestIntersect.x = quad2x;
                closestIntersect.y = quad2y;
            } else {
                closestIntersect.x = quad1x;
                closestIntersect.y = quad1y;
            }
        }

        if (!(closestIntersect.x > Math.min(x1, x2) && closestIntersect.x < Math.max(x1, x2) && closestIntersect.y > Math.min(y1, y2) && closestIntersect.y < Math.max(y1, y2))) {
            closestIntersect.intersectCheck = false;
        } else {
            closestIntersect.object = object;
        }

        return closestIntersect;
    }

    //Circle formula x1
    static double circleEquation1(double x, double radius, double objX, double objY) {
        return Math.sqrt(Math.pow(radius, 2) - Math.pow(x - objX, 2)) + objY;
    }

    //Circle formula x2
    static double circleEquation2(double x, double radius, double objX, double objY) {
        return -Math.sqrt(Math.pow(radius, 2) - Math.pow(x - objX, 2)) + objY;
    }

    //Gets a x point on a circle from an angle
    static double circleAngleX(double degrees, double originX, double radius) {
        degrees = 180 + degrees + 90;
        return originX + radius * Math.cos(degrees * Math.PI / 180);
    }

    //Gets a y point on a circle from an angle
    static double circleAngleY(double degrees, double originY, double radius) {
        degrees = 180 + degrees + 90;
        return originY + radius * Math.sin(degrees * Math.PI / 180);
    }
}