package com.newspacebattle;

import java.util.Objects;

/**
 * Created by Dylan on 2018-06-30. Defines a flagship object.
 */
class FlagShip extends Ship {

    double resources;
    private PointObject gun1, gun2, gun3, gun4;
    boolean buildingSpaceStation, buildingBattleship, buildingLaserCruiser, buildingBomber, buildingFighter, buildingScout, buildingResourceCollector;
    static float constRadius;
    int[] costCounter = new int[7];

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

    void updateResourceForBuild(String type, int counter) {
        GameScreen.resources[team - 1] -= 4;
        costCounter[counter] += 4;
    }

    void checkResourceForBuild(String type) {

        if(Objects.equals(type, "SpaceStation")) {
            if(!buildingSpaceStation && SpaceStation.cost <= GameScreen.resources[team - 1]){
                buildingSpaceStation = true;
                updateResourceForBuild("SpaceStation", 0);
            }else if(buildingSpaceStation){

            }
        }else if(Objects.equals(type, "Battleship")) {

        }else if(Objects.equals(type, "LaserCruiser")) {

        }else if(Objects.equals(type, "Bomber")) {

        }else if(Objects.equals(type, "Fighter")) {

        }else if(Objects.equals(type, "Scout")) {

        }else if(Objects.equals(type, "ResourceCollector")) {

        }

    }

    PointObject setBuildPos(float degreeOffset, float radius) {
        return new PointObject(Utilities.circleAngleX(degreeOffset, centerPosX, (this.radius + radius)), Utilities.circleAngleY(degreeOffset, centerPosY, (this.radius + radius)));
    }

    PointObject finalBuildPos(float constRadius, float shipSpacing){
        PointObject finalBuildPos = null;
        boolean canBuild = false;
        for (float degreeOffset = 90; degreeOffset <= 270; degreeOffset += 45) {
            float trueDegree = degrees + degreeOffset;
            finalBuildPos = setBuildPos(trueDegree, constRadius * shipSpacing);
            canBuild = true;
            for (int i = 0; i < GameScreen.objects.size(); i++) {
                if (Utilities.distanceFormula(finalBuildPos.x - constRadius / 2, finalBuildPos.y - constRadius / 2, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= (constRadius + GameScreen.objects.get(i).radius) * 1.1f) {
                    canBuild = false;
                    break;
                }
            }
            if (canBuild) {
                break;
            }
        }
        // print canBuild and finalBuildPos
        System.out.println("canBuild: " + canBuild + " finalBuildPos: " + finalBuildPos);
        if (!canBuild || finalBuildPos == null) {
            return null;
        }
        return finalBuildPos;
    }

    void buildShip(String type) {
        float radius, shipSpacing;

        if (Objects.equals(type, "SpaceStation")) {
            radius = SpaceStation.constRadius;
            shipSpacing = 7f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            SpaceStation newSpaceStation = new SpaceStation((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.spaceStations.add(newSpaceStation);
            GameScreen.ships.add(newSpaceStation);
            GameScreen.objects.add(newSpaceStation);
        } else if (Objects.equals(type, "BattleShip")) {
            radius = BattleShip.constRadius;
            shipSpacing = 8.5f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            BattleShip newBattleShip = new BattleShip((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newBattleShip.velocityX = velocityX;
            newBattleShip.velocityY = velocityY;
            newBattleShip.degrees = degrees;
            GameScreen.battleShips.add(newBattleShip);
            GameScreen.ships.add(newBattleShip);
            GameScreen.objects.add(newBattleShip);
        } else if (Objects.equals(type, "LaserCruiser")){
            radius = LaserCruiser.constRadius;
            shipSpacing = 11f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            LaserCruiser newLaserCruiser = new LaserCruiser((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newLaserCruiser.velocityX = velocityX;
            newLaserCruiser.velocityY = velocityY;
            newLaserCruiser.degrees = degrees;
            GameScreen.laserCruisers.add(newLaserCruiser);
            GameScreen.ships.add(newLaserCruiser);
            GameScreen.objects.add(newLaserCruiser);
        } else if (Objects.equals(type, "Bomber")) {
            radius = Bomber.constRadius;
            shipSpacing = 11f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            Bomber newBomber = new Bomber((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newBomber.velocityX = velocityX;
            newBomber.velocityY = velocityY;
            newBomber.degrees = degrees;
            GameScreen.bombers.add(newBomber);
            GameScreen.ships.add(newBomber);
            GameScreen.objects.add(newBomber);
        } else if (Objects.equals(type, "Fighter")) {
            radius = Fighter.constRadius;
            shipSpacing = 5.5f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            Fighter newFighter = new Fighter((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newFighter.velocityX = velocityX;
            newFighter.velocityY = velocityY;
            newFighter.degrees = degrees;
            GameScreen.fighters.add(newFighter);
            GameScreen.ships.add(newFighter);
            GameScreen.objects.add(newFighter);
        } else if (Objects.equals(type, "Scout")) {
            radius = Scout.constRadius;
            shipSpacing = 8f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            Scout newScout = new Scout((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newScout.velocityX = velocityX;
            newScout.velocityY = velocityY;
            newScout.degrees = degrees;
            GameScreen.scouts.add(newScout);
            GameScreen.ships.add(newScout);
            GameScreen.objects.add(newScout);
        } else if (Objects.equals(type, "ResourceCollector")) {
            radius = ResourceCollector.constRadius;
            shipSpacing = 4f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return;
            }
            ResourceCollector newResourceCollector = new ResourceCollector((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newResourceCollector.velocityX = velocityX;
            newResourceCollector.velocityY = velocityY;
            newResourceCollector.degrees = degrees;
            GameScreen.resourceCollectors.add(newResourceCollector);
            GameScreen.ships.add(newResourceCollector);
            GameScreen.objects.add(newResourceCollector);
        }
    }
}