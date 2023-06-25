package com.newspacebattle;

import java.util.Objects;

/**
 * Created by Dylan on 2018-06-30. Defines a flagship object.
 */
class FlagShip extends Ship {

    double resources;
    private PointObject gun1, gun2, gun3, gun4;

    static float constRadius;

    //Constructor method
    FlagShip(float x, float y, int team) {
        type = "FlagShip";
        this.team = team;
        canAttack = true;
        width = Main.screenX * 4;
        height = Main.screenY / GameScreen.circleRatio * 4;
        positionX = x;
        positionY = y;
        midX = width / 2;
        midY = height / 2;
        radius = midY;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 11000;
        health = 15000;
        MAX_HEALTH = 15000;
        accelerate = 0.2f;
        maxSpeed = accelerate * 75;
        resources = 0;
        preScaleX = 4;
        preScaleY = 4;
        dockable = false;
        bulletPower = 100;
        gun1 = new PointObject(0, 0);
        gun2 = new PointObject(0, 0);
        gun3 = new PointObject(0, 0);
        gun4 = new PointObject(0, 0);
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        move();
        rotate();
    }

    //Shoots 4 bullets forward
    void shoot() {
        gun1.x = Utilities.circleAngleX(degrees - 10, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun1.y = Utilities.circleAngleY(degrees - 10, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun2.x = Utilities.circleAngleX(degrees + 10, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun2.y = Utilities.circleAngleY(degrees + 10, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun3.x = Utilities.circleAngleX(degrees - 30, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun3.y = Utilities.circleAngleY(degrees - 30, centerPosY, (radius + Bullet.SIZE) * 1.2);

        gun4.x = Utilities.circleAngleX(degrees + 30, centerPosX, (radius + Bullet.SIZE) * 1.2);
        gun4.y = Utilities.circleAngleY(degrees + 30, centerPosY, (radius + Bullet.SIZE) * 1.2);

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun1.x
                        , (float) gun1.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun2.x
                        , (float) gun2.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun3.x
                        , (float) gun3.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }

        for (int i = 0; i <= GameScreen.bullets.size() - 1; i++) {
            if (!GameScreen.bullets.get(i).exists) {
                GameScreen.bullets.get(i).createBullet(
                        (float) gun4.x
                        , (float) gun4.y
                        , team
                        , (float) (Bullet.MAX_SPEED * Math.sin(degrees * Math.PI / 180))
                        , (float) (Bullet.MAX_SPEED * Math.cos(degrees * Math.PI / 180))
                        , degrees
                        , bulletPower);
                break;
            }
        }
    }

    private PointObject setBuildPos(float degreeOffset) {
        PointObject buildPos = new PointObject(0, 0);
        buildPos.x = Utilities.circleAngleX(degrees - degreeOffset, centerPosX, (radius) * 2);
        buildPos.y = Utilities.circleAngleY(degrees - degreeOffset, centerPosY, (radius) * 2);
        return buildPos;
    }

    void buildShip(String type) {
        if (Objects.equals(type, "SpaceStation")) {
            System.out.println("building space station");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= SpaceStation.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            SpaceStation newSpaceStation = new SpaceStation((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.spaceStations.add(newSpaceStation);
            GameScreen.ships.add(newSpaceStation);
            GameScreen.objects.add(newSpaceStation);

        } else if (Objects.equals(type, "BattleShip")) {
            System.out.println("building battleship");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= BattleShip.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            BattleShip newBattleship = new BattleShip((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.battleShips.add(newBattleship);
            GameScreen.ships.add(newBattleship);
            GameScreen.objects.add(newBattleship);

        } else if (Objects.equals(type, "LaserCruiser")){
            System.out.println("building laser cruiser");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= LaserCruiser.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            LaserCruiser newLaserCruiser = new LaserCruiser((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.laserCruisers.add(newLaserCruiser);
            GameScreen.ships.add(newLaserCruiser);
            GameScreen.objects.add(newLaserCruiser);

        } else if (Objects.equals(type, "Bomber")) {
            System.out.println("building bomber");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= Bomber.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            Bomber newBomber = new Bomber((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.bombers.add(newBomber);
            GameScreen.ships.add(newBomber);
            GameScreen.objects.add(newBomber);

        } else if (Objects.equals(type, "Fighter")) {
            System.out.println("building fighter");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= Fighter.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            Fighter newFighter = new Fighter((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.fighters.add(newFighter);
            GameScreen.ships.add(newFighter);
            GameScreen.objects.add(newFighter);

        } else if (Objects.equals(type, "Scout")) {
            System.out.println("building scout");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= Scout.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            Scout newScout = new Scout((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.scouts.add(newScout);
            GameScreen.ships.add(newScout);
            GameScreen.objects.add(newScout);

        } else if (Objects.equals(type, "ResourceCollector")) {
            System.out.println("building resource collector");

            float degreeOffset = 0;
            PointObject finalBuildPos = setBuildPos(degreeOffset);
            for(int i = 0; i < GameScreen.objects.size(); i++){
                if(Utilities.distanceFormula(finalBuildPos.x, finalBuildPos.y, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= ResourceCollector.constRadius + GameScreen.objects.get(i).radius){
                    degreeOffset += 22.5;
                    finalBuildPos = setBuildPos(degreeOffset);
                    continue;
                }
                break;
            }
            ResourceCollector newResourceCollector = new ResourceCollector((float) finalBuildPos.x, (float) finalBuildPos.y, team);
            GameScreen.resourceCollectors.add(newResourceCollector);
            GameScreen.ships.add(newResourceCollector);
            GameScreen.objects.add(newResourceCollector);

        }
    }
}