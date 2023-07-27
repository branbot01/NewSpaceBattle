package com.newspacebattle;

import java.util.Objects;

/**
 * Created by Dylan on 2018-06-30. Defines a flagship object.
 */
class FlagShip extends Ship {

    private PointObject gun1, gun2, gun3, gun4;
    boolean buildingSpaceStation, buildingBattleShip, buildingLaserCruiser, buildingBomber, buildingFighter, buildingScout, buildingResourceCollector;
    int countSpaceStation, countBattleShip, countLaserCruiser, countBomber, countFighter, countScout, countResourceCollector;
    static float constRadius;
    static float MAX_SPEED;
    int[] costCounter = new int[7];
    EnemyAI enemyAI;

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
        health = 30000;
        MAX_HEALTH = 30000;
        accelerate = 0.2f;
        maxSpeed = accelerate * 100;
        MAX_SPEED = maxSpeed;
        preScaleX = 4;
        preScaleY = 4;
        dockable = false;
        bulletPower = 150;
        gun1 = new PointObject(0, 0);
        gun2 = new PointObject(0, 0);
        gun3 = new PointObject(0, 0);
        gun4 = new PointObject(0, 0);
        shootTime = 1000;
        driveTime = 1000;
        sensorRadius = radius * 12;
        shipWeight = 3.5;
    }

    //Updates the object's properties
    void update() {
        exists = checkIfAlive();
        if (autoAttack){
            destinationFinder.autoAttack();
        }
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
                switch (type) {
                    case "SpaceStation":
                        countSpaceStation--;
                        if (countSpaceStation == 0) {
                            buildingSpaceStation = false;
                        }
                        break;
                    case "BattleShip":
                        countBattleShip--;
                        if (countBattleShip == 0) {
                            buildingBattleShip = false;
                        }
                        break;
                    case "LaserCruiser":
                        countLaserCruiser--;
                        if (countLaserCruiser == 0) {
                            buildingLaserCruiser = false;
                        }
                        break;
                    case "Bomber":
                        countBomber--;
                        if (countBomber == 0) {
                            buildingBomber = false;
                        }
                        break;
                    case "Fighter":
                        countFighter--;
                        if (countFighter == 0) {
                            buildingFighter = false;
                        }
                        break;
                    case "Scout":
                        countScout--;
                        if (countScout == 0) {
                            buildingScout = false;
                        }
                        break;
                    case "ResourceCollector":
                        countResourceCollector--;
                        if (countResourceCollector == 0) {
                            buildingResourceCollector = false;
                        }
                        break;
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
        if (buildingSpaceStation && countSpaceStation > 0) {
            updateResourceForBuild("SpaceStation", SpaceStation.cost, 0);
        }
        if (buildingBattleShip && countBattleShip > 0) {
            updateResourceForBuild("BattleShip", BattleShip.cost, 1);
        }
        if (buildingLaserCruiser && countLaserCruiser > 0) {
            updateResourceForBuild("LaserCruiser", LaserCruiser.cost, 2);
        }
        if (buildingBomber && countBomber > 0) {
            updateResourceForBuild("Bomber", Bomber.cost, 3);
        }
        if (buildingFighter && countFighter > 0) {
            updateResourceForBuild("Fighter", Fighter.cost, 4);
        }
        if (buildingScout && countScout > 0) {
            updateResourceForBuild("Scout", Scout.cost, 5);
        }
        if (buildingResourceCollector && countResourceCollector > 0) {
            updateResourceForBuild("ResourceCollector", ResourceCollector.cost, 6);
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
            newBattleShip.degrees = degrees;
            GameScreen.battleShips.add(newBattleShip);
            GameScreen.ships.add(newBattleShip);
            GameScreen.objects.add(newBattleShip);
            if (enemyAI != null){
                enemyAI.freeShips.add(newBattleShip);
            }
        } else if (Objects.equals(type, "LaserCruiser")){
            radius = LaserCruiser.constRadius;
            shipSpacing = 11f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return false;
            }
            LaserCruiser newLaserCruiser = new LaserCruiser((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newLaserCruiser.degrees = degrees;
            GameScreen.laserCruisers.add(newLaserCruiser);
            GameScreen.ships.add(newLaserCruiser);
            GameScreen.objects.add(newLaserCruiser);
            if (enemyAI != null){
                enemyAI.freeShips.add(newLaserCruiser);
            }
        } else if (Objects.equals(type, "Bomber")) {
            radius = Bomber.constRadius;
            shipSpacing = 11f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return false;
            }
            Bomber newBomber = new Bomber((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newBomber.degrees = degrees;
            GameScreen.bombers.add(newBomber);
            GameScreen.ships.add(newBomber);
            GameScreen.objects.add(newBomber);
            if (enemyAI != null){
                enemyAI.freeShips.add(newBomber);
            }
        } else if (Objects.equals(type, "Fighter")) {
            radius = Fighter.constRadius;
            shipSpacing = 5.5f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return false;
            }
            Fighter newFighter = new Fighter((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            newFighter.degrees = degrees;
            GameScreen.fighters.add(newFighter);
            GameScreen.ships.add(newFighter);
            GameScreen.objects.add(newFighter);
            if (enemyAI != null){
                enemyAI.freeShips.add(newFighter);
            }
        } else if (Objects.equals(type, "Scout")) {
            radius = Scout.constRadius;
            shipSpacing = 8f;
            PointObject finalBuildPos = finalBuildPos(radius, shipSpacing);
            if (finalBuildPos == null) {
                return false;
            }
            Scout newScout = new Scout((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
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
            newResourceCollector.degrees = degrees;
            GameScreen.resourceCollectors.add(newResourceCollector);
            GameScreen.ships.add(newResourceCollector);
            GameScreen.objects.add(newResourceCollector);
        }
        return true;
    }
}