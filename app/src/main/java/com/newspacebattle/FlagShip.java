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

    PointObject setBuildPos(float degreeOffset, float radius) {
        return new PointObject(Utilities.circleAngleX(degrees - degreeOffset, centerPosX, (this.radius + radius) * 2.25), Utilities.circleAngleY(degrees - degreeOffset, centerPosY, (this.radius + radius) * 2.25));
    }

    PointObject finalBuildPos(float constRadius){
        PointObject finalBuildPos = null;
        boolean canBuild = false;
        for (float degreeOffset = 0; degreeOffset < 360; degreeOffset += 45) {
            finalBuildPos = setBuildPos(degreeOffset, constRadius);
            canBuild = true;
            for (int i = 0; i < GameScreen.objects.size(); i++) {
                if (Utilities.distanceFormula(finalBuildPos.x - constRadius / 2, finalBuildPos.y - constRadius / 2, GameScreen.objects.get(i).centerPosX, GameScreen.objects.get(i).centerPosY) <= constRadius + GameScreen.objects.get(i).radius) {
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

    void buildShip(String type) {
        float radius;

        if (Objects.equals(type, "SpaceStation")) {
            radius = SpaceStation.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            SpaceStation newSpaceStation = new SpaceStation((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.spaceStations.add(newSpaceStation);
            GameScreen.ships.add(newSpaceStation);
            GameScreen.objects.add(newSpaceStation);
        } else if (Objects.equals(type, "BattleShip")) {
            radius = BattleShip.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            BattleShip newBattleShip = new BattleShip((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.battleShips.add(newBattleShip);
            GameScreen.ships.add(newBattleShip);
            GameScreen.objects.add(newBattleShip);
        } else if (Objects.equals(type, "LaserCruiser")){
            radius = LaserCruiser.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            LaserCruiser newLaserCruiser = new LaserCruiser((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.laserCruisers.add(newLaserCruiser);
            GameScreen.ships.add(newLaserCruiser);
            GameScreen.objects.add(newLaserCruiser);
        } else if (Objects.equals(type, "Bomber")) {
            radius = Bomber.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            Bomber newBomber = new Bomber((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.bombers.add(newBomber);
            GameScreen.ships.add(newBomber);
            GameScreen.objects.add(newBomber);
        } else if (Objects.equals(type, "Fighter")) {
            radius = Fighter.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            Fighter newFighter = new Fighter((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.fighters.add(newFighter);
            GameScreen.ships.add(newFighter);
            GameScreen.objects.add(newFighter);
        } else if (Objects.equals(type, "Scout")) {
            radius = Scout.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            Scout newScout = new Scout((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.scouts.add(newScout);
            GameScreen.ships.add(newScout);
            GameScreen.objects.add(newScout);
        } else if (Objects.equals(type, "ResourceCollector")) {
            radius = ResourceCollector.constRadius;
            PointObject finalBuildPos = finalBuildPos(radius);
            if (finalBuildPos == null) {
                return;
            }
            ResourceCollector newResourceCollector = new ResourceCollector((float) finalBuildPos.x - radius / 2, (float) finalBuildPos.y - radius / 2, team);
            GameScreen.resourceCollectors.add(newResourceCollector);
            GameScreen.ships.add(newResourceCollector);
            GameScreen.objects.add(newResourceCollector);
        }
    }
}