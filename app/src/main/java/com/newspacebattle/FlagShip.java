package com.newspacebattle;

import java.util.Objects;

/**
 * Created by Dylan on 2018-06-30. Defines a flagship object.
 */
class FlagShip extends Ship {

    private PointObject gun1, gun2, gun3, gun4;
    boolean buildingSpaceStation, buildingBattleShip, buildingLaserCruiser, buildingBomber, buildingFighter, buildingScout, buildingResourceCollector;
    static float constRadius;
    static float MAX_SPEED;
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
        avoidanceRadius = radius * 2.25f;
        centerPosX = positionX + midX;
        centerPosY = positionY + midY;
        mass = 11000;
        health = 15000;
        MAX_HEALTH = 15000;
        accelerate = 0.2f;
        maxSpeed = accelerate * 75;
        MAX_SPEED = maxSpeed;
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
        checkResourceForBuild();
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
                        , bulletPower
                        , this);
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
                        , bulletPower
                        , this);
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
                        , bulletPower
                        , this);
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
                        , bulletPower
                        , this);
                break;
            }
        }
    }

    void updateResourceForBuild(String type, int shipCost, int counter) {
        if(GameScreen.resources[team - 1] >= 4 && costCounter[counter] < shipCost) {
            GameScreen.resources[team - 1] -= 4;
            costCounter[counter] += 4;
        }
        if(costCounter[counter] >= shipCost){
            if (buildShip(type)) {
                costCounter[counter] = 0;
                if (type.equals("SpaceStation")) {
                    buildingSpaceStation = false;
                } else if (type.equals("BattleShip")) {
                    buildingBattleShip = false;
                } else if (type.equals("LaserCruiser")) {
                    buildingLaserCruiser = false;
                } else if (type.equals("Bomber")) {
                    buildingBomber = false;
                } else if (type.equals("Fighter")) {
                    buildingFighter = false;
                } else if (type.equals("Scout")) {
                    buildingScout = false;
                } else if (type.equals("ResourceCollector")) {
                    buildingResourceCollector = false;
                }
            }
        }
    }

    void stopBuilding(String type){
        if (Objects.equals(type, "SpaceStation")){
            buildingSpaceStation = false;
            GameScreen.resources[team - 1] += costCounter[0];
            costCounter[0] = 0;
        } else if (Objects.equals(type, "BattleShip")){
            buildingBattleShip = false;
            GameScreen.resources[team - 1] += costCounter[1];
            costCounter[1] = 0;
        } else if (Objects.equals(type, "LaserCruiser")){
            buildingLaserCruiser = false;
            GameScreen.resources[team - 1] += costCounter[2];
            costCounter[2] = 0;
        } else if (Objects.equals(type, "Bomber")){
            buildingBomber = false;
            GameScreen.resources[team - 1] += costCounter[3];
            costCounter[3] = 0;
        } else if (Objects.equals(type, "Fighter")){
            buildingFighter = false;
            GameScreen.resources[team - 1] += costCounter[4];
            costCounter[4] = 0;
        } else if (Objects.equals(type, "Scout")){
            buildingScout = false;
            GameScreen.resources[team - 1] += costCounter[5];
            costCounter[5] = 0;
        } else if (Objects.equals(type, "ResourceCollector")){
            buildingResourceCollector = false;
            GameScreen.resources[team - 1] += costCounter[6];
            costCounter[6] = 0;
        }
    }

    void checkResourceForBuild() {
        if (buildingSpaceStation) {
            if (costCounter[0] == 0 && GameScreen.resources[team - 1] < SpaceStation.cost) {
                buildingSpaceStation = false;
            } else {
                updateResourceForBuild("SpaceStation", SpaceStation.cost, 0);
            }
        }
        if (buildingBattleShip) {
            if (costCounter[1] == 0 && GameScreen.resources[team - 1] < BattleShip.cost) {
                buildingBattleShip = false;
            } else {
                updateResourceForBuild("BattleShip", BattleShip.cost, 1);
            }
        }
        if (buildingLaserCruiser) {
            if (costCounter[2] == 0 && GameScreen.resources[team - 1] < LaserCruiser.cost) {
                buildingLaserCruiser = false;
            } else {
                updateResourceForBuild("LaserCruiser", LaserCruiser.cost, 2);
            }
        }
        if (buildingBomber) {
            if (costCounter[3] == 0 && GameScreen.resources[team - 1] < Bomber.cost) {
                buildingBomber = false;
            } else {
                updateResourceForBuild("Bomber", Bomber.cost, 3);
            }
        }
        if (buildingFighter) {
            if (costCounter[4] == 0 && GameScreen.resources[team - 1] < Fighter.cost) {
                buildingFighter = false;
            } else {
                updateResourceForBuild("Fighter", Fighter.cost, 4);
            }
        }
        if (buildingScout) {
            if (costCounter[5] == 0 && GameScreen.resources[team - 1] < Scout.cost) {
                buildingScout = false;
            } else {
                updateResourceForBuild("Scout", Scout.cost, 5);
            }
        }
        if (buildingResourceCollector) {
            if (costCounter[6] == 0 && GameScreen.resources[team - 1] < ResourceCollector.cost) {
                buildingResourceCollector = false;
            } else {
                updateResourceForBuild("ResourceCollector", ResourceCollector.cost, 6);
            }
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
        if (!canBuild || finalBuildPos == null) {
            return null;
        }
        return finalBuildPos;
    }

    boolean buildShip(String type) {
        float radius, shipSpacing;

        if (Objects.equals(type, "SpaceStation")) {
            radius = SpaceStation.constRadius;
            shipSpacing = 7f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
            }
            ResourceCollector newResourceCollector = new ResourceCollector((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newResourceCollector.velocityX = velocityX;
            newResourceCollector.velocityY = velocityY;
            newResourceCollector.degrees = degrees;
            GameScreen.resourceCollectors.add(newResourceCollector);
            GameScreen.ships.add(newResourceCollector);
            GameScreen.objects.add(newResourceCollector);
        }
        return true;
    }
}