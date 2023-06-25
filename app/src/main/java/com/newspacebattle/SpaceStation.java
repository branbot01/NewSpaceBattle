package com.newspacebattle;

import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Dylan on 2018-08-06. Defines a space station object.
 */
class SpaceStation extends Ship {

    Matrix ringSpiral1 = new Matrix(), ringSpiral2 = new Matrix(), ringSpiral3 = new Matrix();
    private int ring1Degrees, ring2Degrees, ring3Degrees;

    int maxDockedNum = 2; //to be changed

    ArrayList<Ship> dockedShips = new ArrayList<>();

    private PointObject initialRedeployLocation;

    //Constructor method
    SpaceStation(float x, float y, int team) {
        type = "SpaceStation";
        this.team = team;
        width = Main.screenX * 7;
        height = Main.screenY / GameScreen.circleRatio * 7;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 25000;
        health = 40000;
        MAX_HEALTH = 40000;
        accelerate = 0;
        maxSpeed = 0;
        preScaleX = 7;
        preScaleY = 7;
        dockable = false;
        canWarp = true;
        degrees = 0;
        ring1Degrees = 90;
        ring2Degrees = 180;
        ring3Degrees = 270;
        initialRedeployLocation = new PointObject(0, 0);
    }

    //Updates the object's properties
    public void update() {
        exists = checkIfAlive();
        move();
        setRotation();
        rotateRings();
    }

    //Handles the spinning of the station
    private void setRotation() {
        if (!GameScreen.paused) {
            degrees += 2;
            ring2Degrees += 2;
            ring1Degrees -= 2;
            ring3Degrees -= 2;
            if (degrees == 360) {
                degrees = 0;
                ring1Degrees = 90;
                ring2Degrees = 180;
                ring3Degrees = 0;
            }
        }
    }

    //Draws the ship properly
    private void rotateRings() {
        appearance.setRotate(degrees, midX, midY);
        appearance.postTranslate(positionX, positionY);
        appearance.preScale(preScaleX, preScaleY);

        ringSpiral1.setRotate(ring1Degrees, midX, midY);
        ringSpiral1.postTranslate(positionX, positionY);
        ringSpiral1.preScale(preScaleX * 2, preScaleY * 2);

        ringSpiral2.setRotate(ring2Degrees, midX, midY);
        ringSpiral2.postTranslate(positionX, positionY);
        ringSpiral2.preScale(preScaleX * 2, preScaleY * 2);

        ringSpiral3.setRotate(ring3Degrees, midX, midY);
        ringSpiral3.postTranslate(positionX, positionY);
        ringSpiral3.preScale(preScaleX * 2, preScaleY * 2);
    }

    //redeploys docked ship from space station
    void deployShip(String type) {

        int degreeOffset = 0;
        initialRedeployLocation.x = Utilities.circleAngleX(degrees - degreeOffset, centerPosX, (radius) * 1.5);
        initialRedeployLocation.y = Utilities.circleAngleX(degrees - degreeOffset, centerPosX, (radius) * 1.5);

        if(Objects.equals(type, "ResourceCollector")){
            for(int i = 0; i < dockedShips.size(); i++){
                if(dockedShips.get(i) instanceof ResourceCollector){
                    dockedShips.remove(dockedShips.get(i));
                    ResourceCollector newResourceCollector = new ResourceCollector(centerPosX, centerPosY - (2 * radius), team);
                    GameScreen.resourceCollectors.add(newResourceCollector);
                    GameScreen.ships.add(newResourceCollector);
                    GameScreen.objects.add(newResourceCollector);
                    break;
                }
            }
        }else if(Objects.equals(type, "Scout")){
            for(int i = 0; i < dockedShips.size(); i++){
                if(dockedShips.get(i) instanceof Scout){
                    dockedShips.remove(dockedShips.get(i));
                    Scout newScout = new Scout(centerPosX, centerPosY - (2 * radius), team);
                    GameScreen.scouts.add(newScout);
                    GameScreen.ships.add(newScout);
                    GameScreen.objects.add(newScout);
                    break;
                }
            }
        }else if(Objects.equals(type, "Fighter")){
            for(int i = 0; i < dockedShips.size(); i++){
                if(dockedShips.get(i) instanceof Fighter){
                    dockedShips.remove(dockedShips.get(i));
                    Fighter newFighter = new Fighter(centerPosX, centerPosY - (2 * radius), team);
                    GameScreen.fighters.add(newFighter);
                    GameScreen.ships.add(newFighter);
                    GameScreen.objects.add(newFighter);
                    break;
                }
            }
        }else if(Objects.equals(type, "Bomber")){
            for(int i = 0; i < dockedShips.size(); i++){
                if(dockedShips.get(i) instanceof Bomber){
                    dockedShips.remove(dockedShips.get(i));
                    Bomber newBomber = new Bomber(centerPosX, centerPosY - (2 * radius), team);
                    GameScreen.bombers.add(newBomber);
                    GameScreen.ships.add(newBomber);
                    GameScreen.objects.add(newBomber);
                    break;
                }
            }
        }
    }
}
