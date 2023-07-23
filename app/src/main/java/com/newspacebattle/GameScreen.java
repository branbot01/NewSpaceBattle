package com.newspacebattle;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.newspacebattle.R;

import java.util.ArrayList;

/**
 * Created by Dylan on 2018-06-18. Handles the graphics of the game.
 */
public class GameScreen extends View {

    static int offsetX, offsetY, mapSizeX, mapSizeY, clusterSize, grid_size;
    static int[] resources = new int[4];
    static float scaleX, scaleY, circleRatio;
    static float startSelX, startSelY, endSelX, endSelY;
    static float startAttX, startAttY, endAttX, endAttY;
    static double midPointX, midPointY;
    static float[] starXPos, starYPos;
    static boolean paused;

    static Blackboard[] blackboards = new Blackboard[4];

    static ArrayList<Formation> formationsTeam1 = new ArrayList<>();
    static ArrayList<Formation> formationsTeam2 = new ArrayList<>();
    static ArrayList<Formation> formationsTeam3 = new ArrayList<>();
    static ArrayList<Formation> formationsTeam4 = new ArrayList<>();

    //static int time;
    //static ArrayList<Ship> deadShips = new ArrayList<>();
    //static ArrayList<Ship> population = new ArrayList<>();
    //static File fitness;

    static ArrayList<GameObject> objects = new ArrayList<>();
    static ArrayList<Ship> ships = new ArrayList<>();
    static ArrayList<FlagShip> flagShips = new ArrayList<>();
    static ArrayList<ResourceCollector> resourceCollectors = new ArrayList<>();
    static ArrayList<Fighter> fighters = new ArrayList<>();
    static ArrayList<BattleShip> battleShips = new ArrayList<>();
    static ArrayList<Bomber> bombers = new ArrayList<>();
    static ArrayList<Scout> scouts = new ArrayList<>();
    static ArrayList<LaserCruiser> laserCruisers = new ArrayList<>();
    static ArrayList<SpaceStation> spaceStations = new ArrayList<>();
    static ArrayList<BlackHole> blackHole = new ArrayList<>();
    static ArrayList<AsteroidCluster> asteroidClusters = new ArrayList<>();
    static ArrayList<Asteroid> asteroids = new ArrayList<>();
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Missile> missiles = new ArrayList<>();
    static ArrayList<Laser> lasers = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();

    static Bitmap bitFlagShip, enFlagShip1;
    static Bitmap bitArrow, bitHarvestArrow, bitBlackHole, bitAsteroid, bitStar;
    static Bitmap bitResCollector, enResCollector1;
    static Bitmap bitFighter, enFighter1;
    static Bitmap bitBattleShip, enBattleShip1;
    static Bitmap bitBomber, enBomber1;
    static Bitmap bitScout, enScout1;
    static Bitmap bitLaserCruiser, enLaserCruiser1;
    static Bitmap bitStation, enBitStation, bitStationRing1, bitStationRing2, bitStationRing3;
    static Bitmap bitBullet, bitBullet2;
    static Bitmap bitMissile, bitMissile2;
    static Bitmap bitLaser, bitLaser2;
    static Bitmap[] bitExplosionLow = new Bitmap[26];

    static Paint green = new Paint(), red = new Paint(), yellow = new Paint(), blue = new Paint(), white = new Paint(), sensorPaint = new Paint(), lastSeenPaint = new Paint();

    //Initiates the game
    public GameScreen(Context context) {
        super(context);

        Thread bitLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                bitArrow = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_greenarrow)), Main.screenX / 6, Main.screenY / 9, true);
                bitBlackHole = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_blackhole)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                bitHarvestArrow = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_yellowarrow)), Main.screenX / 6, Main.screenY / 9, true);
                bitAsteroid = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_asteroid)), (int) (Main.screenX / 4.5), (int) (Main.screenY / circleRatio / 4.5), true);
                bitStar = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_star)), Main.screenX / 6, (int) (Main.screenY / 6 / circleRatio), true);
                bitFlagShip = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_flagship)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                enFlagShip1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemyflagship1)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                bitResCollector = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_resourcecollector)), Main.screenX / 2, (int) (Main.screenY / 2 / circleRatio), true);
                enResCollector1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemyresourcecollector1)), Main.screenX / 2, (int) (Main.screenY / 2 / circleRatio), true);
                bitFighter = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_fighter)), (int) (Main.screenX / 1.5), (int) (Main.screenY / circleRatio / 1.5), true);
                enFighter1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemyfighter1)), (int) (Main.screenX / 1.5), (int) (Main.screenY / circleRatio / 1.5), true);
                bitBattleShip = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_battleship)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                enBattleShip1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemybattleship1)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                bitBomber = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_bomber)), (int) (Main.screenX / 1.25f), (int) (Main.screenY / circleRatio / 1.25f), true);
                enBomber1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemybomber1)), (int) (Main.screenX / 1.25f), (int) (Main.screenY / circleRatio / 1.25f), true);
                bitScout = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_scout)), (int) (Main.screenX / 1.9f), (int) (Main.screenY / 1.9f / circleRatio), true);
                enScout1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemyscout1)), (int) (Main.screenX / 1.9f), (int) (Main.screenY / 1.9f / circleRatio), true);
                bitLaserCruiser = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_lasercruiser)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                enLaserCruiser1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemylasercruiser1)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                bitStation = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_spacestation)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                enBitStation = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemyspacestation1)), Main.screenX, (int) (Main.screenY / circleRatio), true);
                bitStationRing1 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_spacestationring1)), Main.screenX / 2, (int) (Main.screenY / circleRatio / 2), true);
                bitStationRing2 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_spacestationring2)), Main.screenX / 2, (int) (Main.screenY / circleRatio / 2), true);
                bitStationRing3 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_spacestationring3)), Main.screenX / 2, (int) (Main.screenY / circleRatio / 2), true);
                bitBullet = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_bullet1)), (int) (Main.screenX / 6f), (int) (Main.screenY / 6f / circleRatio), true);
                bitBullet2 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_bullet2)), (int) (Main.screenX / 6f), (int) (Main.screenY / 6f / circleRatio), true);
                bitMissile = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_missile1)), (int) (Main.screenX / 3f), (int) (Main.screenY / 3f / circleRatio), true);
                bitMissile2 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_missile2)), (int) (Main.screenX / 3f), (int) (Main.screenY / 3f / circleRatio), true);
                bitLaser = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_laser)), (int) (Main.screenX / 6f), (int) (Main.screenY / 6f / circleRatio), true);
                bitLaser2 = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.ic_enemylaser)), (int) (Main.screenX / 6f), (int) (Main.screenY / 6f / circleRatio), true);

                bitExplosionLow[1] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp1)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[2] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp2)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[3] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp3)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[4] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp4)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[5] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp5)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[6] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp6)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[7] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp7)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[8] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp8)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[9] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp9)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[10] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp10)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[11] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp11)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[12] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp12)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[13] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp13)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[14] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp14)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[15] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp15)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[16] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp16)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[17] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp17)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[18] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp18)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[19] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp19)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[20] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp20)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[21] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp21)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[22] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp22)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[23] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp23)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[24] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp24)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
                bitExplosionLow[25] = Bitmap.createScaledBitmap(drawableToBitmap(getResources().getDrawable(R.drawable.exp25)), (int) (Main.screenX / 2.5f), (int) (Main.screenY / 2.5f / circleRatio), true);
            }
        });
        bitLoader.start();

        offsetX = -Main.screenX / 2;
        offsetY = -Main.screenY / 2;

        scaleX = 0.05f;
        scaleY = 0.05f;

        final int MAP_SIZE = 8;

        mapSizeX = (int) (Main.screenX / scaleX * MAP_SIZE);
        mapSizeY = (int) (Main.screenY / scaleY * MAP_SIZE);
        clusterSize = (int) (Main.screenX / 0.1);

        grid_size = MAP_SIZE;

        BattleShip.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 3.5);
        Bomber.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 1.25);
        Fighter.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 1.5);
        FlagShip.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 4);
        LaserCruiser.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 1.75);
        ResourceCollector.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) / 2);
        Scout.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) / 1.9);
        SpaceStation.constRadius = (float) (((Main.screenY / GameScreen.circleRatio) / 2f) * 7);

        BattleShip.cost = 35000;
        Bomber.cost = 7000;
        Fighter.cost = 5000;
        LaserCruiser.cost = 19000;
        ResourceCollector.cost = 10000;
        Scout.cost = 3000;
        SpaceStation.cost = 50000;

        resources[0] = 2000000;

        green.setColor(Color.GREEN);
        green.setStrokeWidth(20);
        green.setAntiAlias(true);

        red.setColor(Color.RED);
        red.setStrokeWidth(20);
        red.setAntiAlias(true);

        yellow.setColor(Color.YELLOW);
        yellow.setStrokeWidth(20);
        yellow.setAntiAlias(true);

        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(20);
        blue.setAntiAlias(true);

        white.setColor(Color.WHITE);
        white.setStrokeWidth(20);
        white.setAntiAlias(true);

        sensorPaint.setColor(Color.WHITE);
        sensorPaint.setStrokeWidth(20);
        sensorPaint.setAntiAlias(true);
        sensorPaint.setStyle(Paint.Style.STROKE);

        lastSeenPaint.setColor(Color.MAGENTA);
        lastSeenPaint.setStrokeWidth(20);
        lastSeenPaint.setAntiAlias(true);

        generateMap();

        try {
            bitLoader.join();
        } catch (InterruptedException e) {
            System.out.println("Nope");
        }
    }

    //Converts vector drawables into bitmaps
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //Puts all ships into the ships and objects array
    public static void placeShips() {
        ships.clear();
        ships.addAll(flagShips);
        ships.addAll(resourceCollectors);
        ships.addAll(fighters);
        ships.addAll(battleShips);
        ships.addAll(bombers);
        ships.addAll(scouts);
        ships.addAll(laserCruisers);
        ships.addAll(spaceStations);

        objects.clear();
        objects.addAll(ships);
        objects.addAll(asteroids);
    }

    //Checks before game loads to make sure ships aren't spawned on top on each other
    public static boolean doShipsCollide() {
        for (GameObject i : objects) {
            for (GameObject ii : objects) {
                if (Utilities.distanceFormula(i.centerPosX, i.centerPosY, ii.centerPosX, ii.centerPosY) <= i.radius + ii.radius & i != ii) {
                    return true;
                }
            }
            for (BlackHole ii : blackHole) {
                if (Utilities.distanceFormula(i.centerPosX, i.centerPosY, ii.centerPosX, ii.centerPosY) <= i.radius + ii.radius * ii.pullDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    //Generates asteroid clusters around the map
    public static void generateAsteroids(int clusters, int asteroidNum) {
        int[] clusterPosX = new int[clusters];
        int[] clusterPosY = new int[clusters];
        boolean distanceIsGood;

        do {
            for (int i = 0; i <= clusters - 1; i++) {
                checker:
                while (true) {
                    clusterPosX[i] = (int) (Math.random() * (mapSizeX - clusterSize * 2)) - (mapSizeX - clusterSize * 2) / 2;
                    clusterPosY[i] = (int) (Math.random() * (mapSizeY - clusterSize * 2)) - (mapSizeY - clusterSize * 2) / 2;
                    for (int ii = 0; ii <= blackHole.size() - 1; ii++) {
                        if (Utilities.distanceFormula(blackHole.get(ii).centerPosX, blackHole.get(ii).centerPosY, clusterPosX[i], clusterPosY[i]) > blackHole.get(ii).radius * blackHole.get(ii).pullDistance + clusterSize) {
                            break checker;
                        }
                    }
                    break;
                }
            }

            distanceIsGood = true;
            for (int i = 0; i <= clusters - 1; i++) {
                for (int ii = 0; ii <= clusters - 1; ii++) {
                    if (Utilities.distanceFormula(clusterPosX[i], clusterPosY[i], clusterPosX[ii], clusterPosY[ii]) < clusterSize * 2 & i != ii) {
                        distanceIsGood = false;
                    }
                }
            }
        } while (!distanceIsGood);
        for (int i = 0; i <= clusters - 1; i++) {
            asteroidClusters.add(new AsteroidCluster(clusterPosX[i], clusterPosY[i], asteroidNum));
        }
    }

    //Generates stars randomly around the map
    public static void generateStars(int starNum) {
        starXPos = new float[starNum];
        starYPos = new float[starNum];

        for (int i = 0; i <= starNum - 1; i++) {
            starXPos[i] = (float) (Math.random() * mapSizeX) - mapSizeX / 2;
            starYPos[i] = (float) (Math.random() * mapSizeY) - mapSizeY / 2;
        }
    }

    //Selects ships in its given x and y range
    public static boolean groupSelect(double x1, double y1, double x2, double y2) {
        double holder;

        if (x1 > x2) {
            holder = x1;
            x1 = x2;
            x2 = holder;
        }
        if (y1 > y2) {
            holder = y1;
            y1 = y2;
            y2 = holder;
        }

        boolean oneWasSelected = false;

        Main.selectShips.clear();
        for (int i = 0; i <= ships.size() - 1; i++) {
            if (ships.get(i).centerPosX >= x1 && ships.get(i).centerPosX <= x2 && ships.get(i).centerPosY >= y1 && ships.get(i).centerPosY <= y2/* && ships.get(i).team == 1*/) {
                oneWasSelected = true;
                Main.selectShips.add(ships.get(i));
                ships.get(i).selected = true;
                ships.get(i).attSelected = false;
            } else {
                ships.get(i).selected = false;
            }
        }
        return oneWasSelected;
    }

    public static void groupSelect(ArrayList<Ship> shipsToSelect) {
        if (shipsToSelect == null) {
            return;
        }
        if (shipsToSelect.size() == 0) {
            return;
        }

        for (int i = 0; i <= shipsToSelect.size() - 1; i++) {
            Main.selectShips.add(shipsToSelect.get(i));
            shipsToSelect.get(i).selected = true;
        }
    }

    //Attacks ships in its given x and y range
    public static ArrayList<Ship> attackSelect(double x1, double y1, double x2, double y2) {
        double holder;

        if (x1 > x2) {
            holder = x1;
            x1 = x2;
            x2 = holder;
        }
        if (y1 > y2) {
            holder = y1;
            y1 = y2;
            y2 = holder;
        }

        ArrayList<Ship> victims = new ArrayList<>();

        for (int i = 0; i <= ships.size() - 1; i++) {
            if (ships.get(i).centerPosX >= x1 && ships.get(i).centerPosX <= x2 && ships.get(i).centerPosY >= y1 && ships.get(i).centerPosY <= y2) {
                if (!ships.get(i).selected) {
                    ships.get(i).attSelected = true;
                    victims.add(ships.get(i));
                } else {
                    ships.get(i).attSelected = false;
                }
            }
        }

        return victims;
    }

    //Puts selected ships in the main view
    public static void followShips() {
        if (Main.selectShips.size() != 0 && Main.following) {
            double biggestX = 0, biggestY = 0, smallestX = 0, smallestY = 0;

            for (int i = 0; i <= Main.selectShips.size() - 1; i++) {
                biggestX = Main.selectShips.get(i).centerPosX;
                smallestX = Main.selectShips.get(i).centerPosX;
                biggestY = Main.selectShips.get(i).centerPosY;
                smallestY = Main.selectShips.get(i).centerPosY;
            }

            for (int i = 0; i <= Main.selectShips.size() - 1; i++) {
                if (Main.selectShips.get(i).centerPosX > biggestX) {
                    biggestX = Main.selectShips.get(i).centerPosX;
                }
                if (Main.selectShips.get(i).centerPosX < smallestX) {
                    smallestX = Main.selectShips.get(i).centerPosX;
                }

                if (Main.selectShips.get(i).centerPosY > biggestY) {
                    biggestY = Main.selectShips.get(i).centerPosY;
                }
                if (Main.selectShips.get(i).centerPosY < smallestY) {
                    smallestY = Main.selectShips.get(i).centerPosY;
                }
            }

            midPointX = (biggestX + smallestX) / 2;
            midPointY = (biggestY + smallestY) / 2;
            offsetX = (int) (midPointX * scaleX - Main.screenX / 2);
            offsetY = (int) (midPointY * scaleY - Main.screenY / 2);

            Main.checkBorders();
        }
    }

    //Generates ships in a standoff way
    public static void generateFaceoff() {
        offsetX = -227;
        offsetY = -12554;

        blackHole.add(new BlackHole(0, 0));
        generateStars(3000);
        generateAsteroids(0, 5);

        final int flagShipNum = 0;
        final int fighterNum = 0;
        final int battleShipNum = 0;
        final int bomberNum = 0;
        final int bulletNum = 750;
        final int explosionNum = 750;
        final int missileNum = 750;
        final int laserNum = 500;

        flagShips.clear();
        for (int i = 0; i <= flagShipNum - 1; i++) {
            //flagShips.add(new FlagShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, (int) (Math.random() * 2 + 1)));
        }
        flagShips.add(new FlagShip(0, 0 - mapSizeY / 6, 2));
        flagShips.add(new FlagShip(0, 0 - mapSizeY / 7, 1));

        fighters.clear();
        for (int i = 0; i <= fighterNum - 1; i++) {
            //fighters.add(new Fighter((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, (int) (Math.random() * 2 + 1)));
        }
        resourceCollectors.add(new ResourceCollector(-2880, 0 - mapSizeY / 6 + 2000, 2));
        resourceCollectors.add(new ResourceCollector(2880 * 2.5f * 2, 0 - mapSizeY / 6 + 2000, 2));
        scouts.add(new Scout(-2880 / 2, 0 - mapSizeY / 6 + 5000, 2));
        scouts.add(new Scout(2880 * 2.5f, 0 - mapSizeY / 6 + 5000, 2));
        fighters.add(new Fighter(-2880, 0 - mapSizeY / 6 + 5000, 2));
        fighters.add(new Fighter(2880 * 2.5f * 2, 0 - mapSizeY / 6 + 5000, 2));

        resourceCollectors.add(new ResourceCollector(-2880, 0 - mapSizeY / 7 - 2000, 1));
        resourceCollectors.add(new ResourceCollector(2880 * 2.5f * 2, 0 - mapSizeY / 7 - 2000, 1));
        scouts.add(new Scout(-2880 / 2, 0 - mapSizeY / 7 - 5000, 1));
        scouts.add(new Scout(2880 * 2.5f, 0 - mapSizeY / 7 - 5000, 1));
        fighters.add(new Fighter(-2880, 0 - mapSizeY / 7 - 5000, 1));
        fighters.add(new Fighter(2880 * 2.5f * 2, 0 - mapSizeY / 7 - 5000, 1));

        battleShips.clear();
        for (int i = 0; i <= battleShipNum - 1; i++) {
            //battleShips.add(new BattleShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, (int) (Math.random() * 2 + 1)));
        }
        battleShips.add(new BattleShip(2880 * 3, 0 - mapSizeY / 6, 2));
        battleShips.add(new BattleShip(2880 * 3, 0 - mapSizeY / 7, 1));

        bombers.clear();
        for (int i = 0; i <= bomberNum - 1; i++) {
            //bombers.add(new Bomber((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, (int) (Math.random() * 2 + 1)));
        }
        laserCruisers.add(new LaserCruiser(-2880, 0 - mapSizeY / 6, 2));
        bombers.add(new Bomber(2880 * 2.5f * 2, 0 - mapSizeY / 6, 2));

        laserCruisers.add(new LaserCruiser(-2880, 0 - mapSizeY / 7, 1));
        bombers.add(new Bomber(2880 * 2.5f * 2, 0 - mapSizeY / 7, 1));

        spaceStations.add(new SpaceStation(0, 0 - mapSizeY / 6.5f, 1));

        bullets.clear();
        for (int i = 0; i <= bulletNum - 1; i++) {
            bullets.add(new Bullet());
        }

        missiles.clear();
        for (int i = 0; i <= missileNum - 1; i++) {
            missiles.add(new Missile());
        }

        explosions.clear();
        for (int i = 0; i <= explosionNum - 1; i++) {
            explosions.add(new Explosion());
        }

        lasers.clear();
        for (int i = 0; i <= laserNum - 1; i++) {
            lasers.add(new Laser());
        }

        placeShips();
    }

    //Generates ships randomly around the map
    public static void generateMap() {
        //blackHole.add(new BlackHole(0, 0));
        final int flagShipNum = 2;
        final int resCollectorsNum = 20;
        final int fighterNum = 50;
        final int battleShipNum = 4;
        final int bomberNum = 20;
        final int scoutNum = 10;
        final int laserCruiserNum = 8;
        final int spaceStationNum = 2;
        int bulletNum = 500;
        int explosionNum = 500;
        final int missileNum = 200;
        final int laserNum = 50;

        generateStars(0);
        generateAsteroids(0, 5);

        do {
            resourceCollectors.clear();
            for (int i = 0; i <= resCollectorsNum - 1; i++) {
                resourceCollectors.add(new ResourceCollector((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            flagShips.clear();
            for (int i = 0; i <= flagShipNum - 1; i++) {
                flagShips.add(new FlagShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            fighters.clear();
            for (int i = 0; i <= fighterNum - 1; i++) {
                fighters.add(new Fighter((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            battleShips.clear();
            for (int i = 0; i <= battleShipNum - 1; i++) {
                battleShips.add(new BattleShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            bombers.clear();
            for (int i = 0; i <= bomberNum - 1; i++) {
                bombers.add(new Bomber((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            scouts.clear();
            for (int i = 0; i <= scoutNum - 1; i++) {
                scouts.add(new Scout((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            laserCruisers.clear();
            for (int i = 0; i <= laserCruiserNum - 1; i++) {
                laserCruisers.add(new LaserCruiser((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            spaceStations.clear();
            for (int i = 0; i <= spaceStationNum - 1; i++) {
                spaceStations.add(new SpaceStation((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY - mapSizeY / 48)) - mapSizeY / 2, i % 2 + 1));
            }

            bullets.clear();
            for (int i = 0; i <= bulletNum - 1; i++) {
                bullets.add(new Bullet());
            }

            missiles.clear();
            for (int i = 0; i <= missileNum - 1; i++) {
                missiles.add(new Missile());
            }

            lasers.clear();
            for (int i = 0; i <= laserNum - 1; i++) {
                lasers.add(new Laser());
            }

            explosions.clear();
            for (int i = 0; i <= explosionNum - 1; i++) {
                explosions.add(new Explosion());
            }

            placeShips();
        } while (doShipsCollide());

        for (int i = 0; i < blackboards.length; i++) {
            blackboards[i] = new Blackboard(i + 1);
        }
    }

    //Generates ships randomly around the map
    public static void generateGame() {
        blackHole.add(new BlackHole(0, 0));
        int bulletNum = 500;
        int explosionNum = 500;
        final int missileNum = 200;
        final int laserNum = 50;

        generateStars(0);
        do {
            asteroidClusters.clear();
            asteroids.clear();
            generateAsteroids(10, 5);

            resourceCollectors.clear();
            flagShips.clear();
            fighters.clear();
            battleShips.clear();
            bombers.clear();
            scouts.clear();
            laserCruisers.clear();
            spaceStations.clear();

            flagShips.add(new FlagShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY / 4 - mapSizeY / 48)) + mapSizeY / 4, 1));
            flagShips.add(new FlagShip((((float) Math.random() * (mapSizeX - mapSizeX / 48)) - mapSizeX / 2), ((float) Math.random() * (mapSizeY / 4 - mapSizeY / 48)) - mapSizeY / 2, 2));

            resourceCollectors.add(new ResourceCollector(flagShips.get(0).centerPosX + mapSizeX / 48, flagShips.get(0).centerPosY,1));
            resourceCollectors.add(new ResourceCollector(flagShips.get(0).centerPosX - mapSizeX / 48, flagShips.get(0).centerPosY,1));

            scouts.add(new Scout(flagShips.get(0).centerPosX, flagShips.get(0).centerPosY - mapSizeY / 48,1));

            resourceCollectors.add(new ResourceCollector(flagShips.get(1).centerPosX + mapSizeX / 48, flagShips.get(1).centerPosY,2));
            resourceCollectors.add(new ResourceCollector(flagShips.get(1).centerPosX - mapSizeX / 48, flagShips.get(1).centerPosY,2));

            scouts.add(new Scout(flagShips.get(1).centerPosX, flagShips.get(1).centerPosY + mapSizeY / 48,2));

            bullets.clear();
            for (int i = 0; i <= bulletNum - 1; i++) {
                bullets.add(new Bullet());
            }

            missiles.clear();
            for (int i = 0; i <= missileNum - 1; i++) {
                missiles.add(new Missile());
            }

            lasers.clear();
            for (int i = 0; i <= laserNum - 1; i++) {
                lasers.add(new Laser());
            }

            explosions.clear();
            for (int i = 0; i <= explosionNum - 1; i++) {
                explosions.add(new Explosion());
            }

            placeShips();
        } while (doShipsCollide());
        objects.addAll(blackHole);

        for (int i = 0; i < blackboards.length; i++) {
            blackboards[i] = new Blackboard(i + 1);
        }

        new EnemyAI(2, blackboards[1]);

        if (flagShips.size() > 0) {
            Main.selectShips.add(flagShips.get(0));
            Main.following = true;
        }

        for (int i = 0; i <= ships.size() - 1; i++) {
            if (ships.get(i) instanceof ResourceCollector) {
                ((ResourceCollector) ships.get(i)).goToAsteroid();
            } else if (ships.get(i) instanceof Scout) {
                ((Scout) ships.get(i)).scout();
            }
        }
    }

    void makeExplosion(GameObject object) {
        for (int i = 0; i <= explosions.size() - 1; i++) {
            if (!explosions.get(i).active) {
                explosions.get(i).createExplosion(object);
                break;
            }
        }
    }

    //The draw function, only drawing is done here
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (Main.minimapOn) {
            Paint color;
            canvas.translate(Main.screenX / 2, Main.screenY / 2);
            canvas.scale(0.8f, 0.8f);

            canvas.drawLine(-Main.screenX / 2, -Main.screenY / 2, Main.screenX / 2, -Main.screenY / 2, red);
            canvas.drawLine(-Main.screenX / 2, -Main.screenY / 2, -Main.screenX / 2, Main.screenY / 2, red);
            canvas.drawLine(Main.screenX / 2, -Main.screenY / 2, Main.screenX / 2, Main.screenY / 2, red);
            canvas.drawLine(-Main.screenX / 2, Main.screenY / 2, Main.screenX / 2, Main.screenY / 2, red);

            GameScreen.midPointX = (Main.screenX / 2 + GameScreen.offsetX) / GameScreen.scaleX;
            GameScreen.midPointY = (Main.screenY / 2 + GameScreen.offsetY) / GameScreen.scaleY;
            canvas.drawCircle((float) (Main.screenX * ((GameScreen.midPointX + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2), (float) (Main.screenY * ((GameScreen.midPointY + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2), 10, white);

            for (int i = 0; i <= blackHole.size() - 1; i++) {
                canvas.drawCircle(Main.screenX * ((blackHole.get(i).centerPosX + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2, Main.screenY * ((blackHole.get(i).centerPosY + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2, 20, blue);
            }

            for (int i = 0; i <= asteroids.size() - 1; i++) {
                canvas.drawCircle(Main.screenX * ((asteroids.get(i).centerPosX + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2, Main.screenY * ((asteroids.get(i).centerPosY + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2, 1, yellow);
            }

            color = green;
            float radius = 0;
            for (int i = 0; i <= ships.size() - 1; i++) {
                if (ships.get(i).team != 1){
                    continue;
                }
                if (ships.get(i) instanceof FlagShip || ships.get(i) instanceof BattleShip || ships.get(i) instanceof SpaceStation) {
                    radius = 10;
                } else if (ships.get(i) instanceof ResourceCollector || ships.get(i) instanceof Scout) {
                    radius = 1;
                } else if (ships.get(i) instanceof Fighter || ships.get(i) instanceof Bomber) {
                    radius = 4;
                } else if (ships.get(i) instanceof LaserCruiser) {
                    radius = 7;
                }
                canvas.drawCircle(Main.screenX * ((ships.get(i).centerPosX + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2, Main.screenY * ((ships.get(i).centerPosY + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2, radius, color);
            }

            color = red;
            for (int i = 0; i < blackboards[0].visibleEnemyShips.size(); i++) {
                if (blackboards[0].visibleEnemyShips.get(i) instanceof FlagShip || blackboards[0].visibleEnemyShips.get(i) instanceof BattleShip || blackboards[0].visibleEnemyShips.get(i) instanceof SpaceStation) {
                    radius = 10;
                } else if (blackboards[0].visibleEnemyShips.get(i) instanceof ResourceCollector || blackboards[0].visibleEnemyShips.get(i) instanceof Scout) {
                    radius = 1;
                } else if (blackboards[0].visibleEnemyShips.get(i) instanceof Fighter || blackboards[0].visibleEnemyShips.get(i) instanceof Bomber) {
                    radius = 4;
                } else if (blackboards[0].visibleEnemyShips.get(i) instanceof LaserCruiser) {
                    radius = 7;
                }
                canvas.drawCircle(Main.screenX * ((blackboards[0].visibleEnemyShips.get(i).centerPosX + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2, Main.screenY * ((blackboards[0].visibleEnemyShips.get(i).centerPosY + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2, radius, color);
            }

            color = lastSeenPaint;
            for (int i = 0; i < blackboards[0].possibleEnemyShips.size(); i++) {
                if (blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof FlagShip || blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof BattleShip || blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof SpaceStation) {
                    radius = 10;
                } else if (blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof ResourceCollector || blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof Scout) {
                    radius = 1;
                } else if (blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof Fighter || blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof Bomber) {
                    radius = 4;
                } else if (blackboards[0].possibleEnemyShips.get(i).getFirst() instanceof LaserCruiser) {
                    radius = 7;
                }
                canvas.drawCircle(Main.screenX * ((blackboards[0].possibleEnemyShips.get(i).getSecond() + (float) mapSizeX / 2) / mapSizeX) - Main.screenX / 2, Main.screenY * ((blackboards[0].possibleEnemyShips.get(i).getThird() + (float) mapSizeY / 2) / mapSizeY) - Main.screenY / 2, radius, color);
            }
            return;
        }
        canvas.translate(-offsetX, -offsetY);
        canvas.scale(scaleX, scaleY);

        double extraOffsetX = mapSizeX;
        double extraOffsetY = mapSizeY;

        if (grid_size == 32){
            extraOffsetX = mapSizeX / 6f;
            extraOffsetY = mapSizeY / 6f;
        }

        for (int i = 0; i <= starXPos.length - 1; i++) {
            if (starXPos[i] >= offsetX / scaleX && starXPos[i] <= offsetX / scaleX + extraOffsetX && starYPos[i] >= offsetY / scaleY && starYPos[i] <= offsetY / scaleY + extraOffsetY) {
                canvas.drawBitmap(bitStar, starXPos[i], starYPos[i], null);
            }
        }

        canvas.drawLine(-mapSizeX / 2, -mapSizeY / 2, mapSizeX / 2, -mapSizeY / 2, red);
        canvas.drawLine(-mapSizeX / 2, mapSizeY / 2, mapSizeX / 2, mapSizeY / 2, red);
        canvas.drawLine(-mapSizeX / 2, -mapSizeY / 2, -mapSizeX / 2, mapSizeY / 2, red);
        canvas.drawLine(mapSizeX / 2, -mapSizeY / 2, mapSizeX / 2, mapSizeY / 2, red);

        for (int i = 1; i < grid_size; i++) {
            canvas.drawLine(-mapSizeX / 2 + i * mapSizeX / grid_size, -mapSizeY / 2, -mapSizeX / 2 + i * mapSizeX / grid_size, mapSizeY / 2, green);
            canvas.drawLine(-mapSizeX / 2, -mapSizeY / 2 + i * mapSizeY / grid_size, mapSizeX / 2, -mapSizeY / 2 + i * mapSizeY / grid_size, green);
        }

        for (int i = 0; i <= blackHole.size() - 1; i++) {
            if (blackHole.get(i).centerPosX + blackHole.get(i).radius >= offsetX / scaleX && blackHole.get(i).centerPosX - blackHole.get(i).radius <= offsetX / scaleX + extraOffsetX && blackHole.get(i).centerPosY + blackHole.get(i).radius >= offsetY / scaleY && blackHole.get(i).centerPosY - blackHole.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                canvas.drawBitmap(bitBlackHole, blackHole.get(i).appearance, null);
            }
        }

        for (int i = 0; i <= bullets.size() - 1; i++) {
            if (bullets.get(i).exists) {
                if (bullets.get(i).centerPosX + bullets.get(i).radius >= offsetX / scaleX && bullets.get(i).centerPosX - bullets.get(i).radius <= offsetX / scaleX + extraOffsetX && bullets.get(i).centerPosY + bullets.get(i).radius >= offsetY / scaleY && bullets.get(i).centerPosY - bullets.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                    //canvas.drawCircle(bullets.get(i).centerPosX, bullets.get(i).centerPosY, bullets.get(i).radius, green);
                    if (bullets.get(i).team == 1) {
                        canvas.drawBitmap(bitBullet, bullets.get(i).appearance, null);
                    } else {
                        canvas.drawBitmap(bitBullet2, bullets.get(i).appearance, null);
                    }
                }
            }
        }

        for (int i = 0; i <= missiles.size() - 1; i++) {
            if (missiles.get(i).exists) {
                if (missiles.get(i).centerPosX + missiles.get(i).radius >= offsetX / scaleX && missiles.get(i).centerPosX - missiles.get(i).radius <= offsetX / scaleX + extraOffsetX && missiles.get(i).centerPosY + missiles.get(i).radius >= offsetY / scaleY && missiles.get(i).centerPosY - missiles.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                    //canvas.drawCircle(missiles.get(i).centerPosX, missiles.get(i).centerPosY, missiles.get(i).radius, green);
                    if (missiles.get(i).team == 1) {
                        canvas.drawBitmap(bitMissile, missiles.get(i).appearance, null);
                    } else if (missiles.get(i).team == 2) {
                        canvas.drawBitmap(bitMissile2, missiles.get(i).appearance, null);
                    }
                }
            }
        }

        for (int i = 0; i <= lasers.size() - 1; i++) {
            if (lasers.get(i).exists) {
                if (lasers.get(i).centerPosX + lasers.get(i).radius >= offsetX / scaleX && lasers.get(i).centerPosX - lasers.get(i).radius <= offsetX / scaleX + extraOffsetX && lasers.get(i).centerPosY + lasers.get(i).radius >= offsetY / scaleY && lasers.get(i).centerPosY - lasers.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                    if (lasers.get(i).team == 1) {
                        canvas.drawBitmap(bitLaser, lasers.get(i).appearance, null);
                    } else if (lasers.get(i).team == 2) {
                        canvas.drawBitmap(bitLaser2, lasers.get(i).appearance, null);
                    }
                }
            }
        }

        for (int i = 0; i <= explosions.size() - 1; i++) {
            if (explosions.get(i).active) {
                if (explosions.get(i).centerPosX + explosions.get(i).radius >= offsetX / scaleX && explosions.get(i).centerPosX - explosions.get(i).radius <= offsetX / scaleX + extraOffsetX && explosions.get(i).centerPosY + explosions.get(i).radius >= offsetY / scaleY && explosions.get(i).centerPosY - explosions.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                    canvas.drawBitmap(bitExplosionLow[explosions.get(i).frame], explosions.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= asteroids.size() - 1; i++) {
            if (asteroids.get(i).centerPosX + asteroids.get(i).radius >= offsetX / scaleX && asteroids.get(i).centerPosX - asteroids.get(i).radius <= offsetX / scaleX + extraOffsetX && asteroids.get(i).centerPosY + asteroids.get(i).radius >= offsetY / scaleY && asteroids.get(i).centerPosY - asteroids.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                canvas.drawBitmap(bitAsteroid, asteroids.get(i).appearance, null);
                /*if (asteroids.get(i).incomingResourceCollector != null) {
                    canvas.drawCircle(asteroids.get(i).centerPosX, asteroids.get(i).centerPosY, asteroids.get(i).incomingResourceCollector.radius, red);
                }*/
            }
        }

        /*for (int i = 0; i < asteroidClusters.size(); i++) {
            canvas.drawCircle(asteroidClusters.get(i).positionX, asteroidClusters.get(i).positionY, asteroidClusters.get(i).radius, sensorPaint);
        }*/

        for (int i = 0; i <= flagShips.size() - 1; i++) {
            if (!flagShips.get(i).visible){
                continue;
            }
            if (flagShips.get(i).centerPosX + flagShips.get(i).radius + flagShips.get(i).radius * 0.2f >= offsetX / scaleX && flagShips.get(i).centerPosX - flagShips.get(i).radius + flagShips.get(i).radius * 0.2f <= offsetX / scaleX + extraOffsetX && flagShips.get(i).centerPosY + flagShips.get(i).radius + flagShips.get(i).radius * 0.2f >= offsetY / scaleY && flagShips.get(i).centerPosY - flagShips.get(i).radius + flagShips.get(i).radius * 0.2f <= offsetY / scaleY + extraOffsetY) {
                if (flagShips.get(i).selected || flagShips.get(i).attSelected) {
                    canvas.drawCircle(flagShips.get(i).centerPosX, flagShips.get(i).centerPosY, flagShips.get(i).radius * 1.1f, flagShips.get(i).selector);

                    float health = flagShips.get(i).health / flagShips.get(i).MAX_HEALTH;
                    canvas.drawRect(flagShips.get(i).positionX, flagShips.get(i).positionY - flagShips.get(i).radius / 5, flagShips.get(i).positionX + flagShips.get(i).radius * 2, flagShips.get(i).positionY - flagShips.get(i).radius / 4, red);
                    canvas.drawRect(flagShips.get(i).positionX, flagShips.get(i).positionY - flagShips.get(i).radius / 5, flagShips.get(i).positionX + (flagShips.get(i).radius * 2 * health), flagShips.get(i).positionY - flagShips.get(i).radius / 4, green);

                    if (flagShips.get(i).attacking && flagShips.get(i).destinationFinder.enemies.size() > 0) {
                        canvas.drawLine(flagShips.get(i).centerPosX, flagShips.get(i).centerPosY, flagShips.get(i).destinationFinder.enemies.get(0).centerPosX, flagShips.get(i).destinationFinder.enemies.get(0).centerPosY, red);
                    }
                    canvas.drawCircle(flagShips.get(i).centerPosX, flagShips.get(i).centerPosY, flagShips.get(i).sensorRadius, sensorPaint);
                }
                if (flagShips.get(i).team == 1) {
                    canvas.drawBitmap(bitFlagShip, flagShips.get(i).appearance, null);
                    if (flagShips.get(i).destination) {
                        canvas.drawLine(flagShips.get(i).centerPosX, flagShips.get(i).centerPosY, flagShips.get(i).destinationFinder.destX, flagShips.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, flagShips.get(i).arrow, null);
                    }
                } else {
                    canvas.drawBitmap(enFlagShip1, flagShips.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= resourceCollectors.size() - 1; i++) {
            if (!resourceCollectors.get(i).visible){
                continue;
            }
            if (resourceCollectors.get(i).centerPosX + resourceCollectors.get(i).radius >= offsetX / scaleX && resourceCollectors.get(i).centerPosX - resourceCollectors.get(i).radius <= offsetX / scaleX + extraOffsetX && resourceCollectors.get(i).centerPosY + resourceCollectors.get(i).radius >= offsetY / scaleY && resourceCollectors.get(i).centerPosY - resourceCollectors.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (resourceCollectors.get(i).selected || resourceCollectors.get(i).attSelected) {
                    canvas.drawCircle(resourceCollectors.get(i).centerPosX, resourceCollectors.get(i).centerPosY, resourceCollectors.get(i).radius, resourceCollectors.get(i).selector);

                    float health = resourceCollectors.get(i).health / resourceCollectors.get(i).MAX_HEALTH;
                    canvas.drawRect(resourceCollectors.get(i).positionX, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 5.5f, resourceCollectors.get(i).positionX + resourceCollectors.get(i).radius * 2, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 3, red);
                    canvas.drawRect(resourceCollectors.get(i).positionX, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 5.5f, resourceCollectors.get(i).positionX + resourceCollectors.get(i).radius * 2 * health, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 3, green);

                    float resources = (float) (resourceCollectors.get(i).resources / resourceCollectors.get(i).RESOURCE_CAPACITY);
                    canvas.drawRect(resourceCollectors.get(i).positionX, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 2.5f, resourceCollectors.get(i).positionX + resourceCollectors.get(i).radius * 2 * resources, resourceCollectors.get(i).positionY - resourceCollectors.get(i).radius / 2f, yellow);

                    canvas.drawCircle(resourceCollectors.get(i).centerPosX, resourceCollectors.get(i).centerPosY, resourceCollectors.get(i).sensorRadius, sensorPaint);
                }
                if (resourceCollectors.get(i).team == 1) {
                    canvas.drawBitmap(bitResCollector, resourceCollectors.get(i).appearance, null);
                    if (resourceCollectors.get(i).destination) {
                        if (resourceCollectors.get(i).harvesting && !resourceCollectors.get(i).docking) {
                            canvas.drawLine(resourceCollectors.get(i).centerPosX, resourceCollectors.get(i).centerPosY, resourceCollectors.get(i).destinationFinder.destX, resourceCollectors.get(i).destinationFinder.destY, yellow);
                            canvas.drawBitmap(bitHarvestArrow, resourceCollectors.get(i).arrow, null);
                        } else {
                            canvas.drawLine(resourceCollectors.get(i).centerPosX, resourceCollectors.get(i).centerPosY, resourceCollectors.get(i).destinationFinder.destX, resourceCollectors.get(i).destinationFinder.destY, green);
                            canvas.drawBitmap(bitArrow, resourceCollectors.get(i).arrow, null);
                        }
                    }
                } else if (resourceCollectors.get(i).team == 2) {
                    canvas.drawBitmap(enResCollector1, resourceCollectors.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= fighters.size() - 1; i++) {
            if (!fighters.get(i).visible){
                continue;
            }
            if (fighters.get(i).centerPosX + fighters.get(i).radius >= offsetX / scaleX && fighters.get(i).centerPosX - fighters.get(i).radius <= offsetX / scaleX + extraOffsetX && fighters.get(i).centerPosY + fighters.get(i).radius >= offsetY / scaleY && fighters.get(i).centerPosY - fighters.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (fighters.get(i).selected || fighters.get(i).attSelected) {
                    canvas.drawCircle(fighters.get(i).centerPosX, fighters.get(i).centerPosY, fighters.get(i).radius, fighters.get(i).selector);

                    float health = fighters.get(i).health / fighters.get(i).MAX_HEALTH;
                    canvas.drawRect(fighters.get(i).positionX, fighters.get(i).positionY - fighters.get(i).radius / 5.5f, fighters.get(i).positionX + fighters.get(i).radius * 2, fighters.get(i).positionY - fighters.get(i).radius / 3.5f, red);
                    canvas.drawRect(fighters.get(i).positionX, fighters.get(i).positionY - fighters.get(i).radius / 5.5f, fighters.get(i).positionX + fighters.get(i).radius * 2 * health, fighters.get(i).positionY - fighters.get(i).radius / 3.5f, green);

                    if (fighters.get(i).attacking && fighters.get(i).destinationFinder.enemies.size() > 0) {
                        canvas.drawLine(fighters.get(i).centerPosX, fighters.get(i).centerPosY, fighters.get(i).destinationFinder.enemies.get(0).centerPosX, fighters.get(i).destinationFinder.enemies.get(0).centerPosY, red);
                    }
                    canvas.drawCircle(fighters.get(i).centerPosX, fighters.get(i).centerPosY, fighters.get(i).sensorRadius, sensorPaint);
                }
                if (fighters.get(i).team == 1) {
                    canvas.drawBitmap(bitFighter, fighters.get(i).appearance, null);
                    if (fighters.get(i).destination) {
                        canvas.drawLine(fighters.get(i).centerPosX, fighters.get(i).centerPosY, fighters.get(i).destinationFinder.destX, fighters.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, fighters.get(i).arrow, null);
                    }
                } else {
                    canvas.drawBitmap(enFighter1, fighters.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= battleShips.size() - 1; i++) {
            if (!battleShips.get(i).visible){
                continue;
            }
            if (battleShips.get(i).centerPosX + battleShips.get(i).radius >= offsetX / scaleX && battleShips.get(i).centerPosX - battleShips.get(i).radius <= offsetX / scaleX + extraOffsetX && battleShips.get(i).centerPosY + battleShips.get(i).radius >= offsetY / scaleY && battleShips.get(i).centerPosY - battleShips.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (battleShips.get(i).selected || battleShips.get(i).attSelected) {
                    canvas.drawCircle(battleShips.get(i).centerPosX, battleShips.get(i).centerPosY, battleShips.get(i).radius, battleShips.get(i).selector);

                    float health = battleShips.get(i).health / battleShips.get(i).MAX_HEALTH;
                    canvas.drawRect(battleShips.get(i).positionX, battleShips.get(i).positionY - battleShips.get(i).radius / 4, battleShips.get(i).positionX + battleShips.get(i).radius * 2, battleShips.get(i).positionY - battleShips.get(i).radius / 3.75f, red);
                    canvas.drawRect(battleShips.get(i).positionX, battleShips.get(i).positionY - battleShips.get(i).radius / 4, battleShips.get(i).positionX + battleShips.get(i).radius * 2 * health, battleShips.get(i).positionY - battleShips.get(i).radius / 3.75f, green);

                    if (battleShips.get(i).attacking && battleShips.get(i).destinationFinder.enemies.size() > 0) {
                        canvas.drawLine(battleShips.get(i).centerPosX, battleShips.get(i).centerPosY, battleShips.get(i).destinationFinder.enemies.get(0).centerPosX, battleShips.get(i).destinationFinder.enemies.get(0).centerPosY, red);
                    }
                    canvas.drawCircle(battleShips.get(i).centerPosX, battleShips.get(i).centerPosY, battleShips.get(i).sensorRadius, sensorPaint);
                }
                if (battleShips.get(i).team == 1) {
                    canvas.drawBitmap(bitBattleShip, battleShips.get(i).appearance, null);
                    if (battleShips.get(i).destination) {
                        canvas.drawLine(battleShips.get(i).centerPosX, battleShips.get(i).centerPosY, battleShips.get(i).destinationFinder.destX, battleShips.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, battleShips.get(i).arrow, null);
                    }
                } else if (battleShips.get(i).team == 2) {
                    canvas.drawBitmap(enBattleShip1, battleShips.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= bombers.size() - 1; i++) {
            if (!bombers.get(i).visible){
                continue;
            }
            if (bombers.get(i).centerPosX + bombers.get(i).radius >= offsetX / scaleX && bombers.get(i).centerPosX - bombers.get(i).radius <= offsetX / scaleX + extraOffsetX && bombers.get(i).centerPosY + bombers.get(i).radius >= offsetY / scaleY && bombers.get(i).centerPosY - bombers.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (bombers.get(i).selected || bombers.get(i).attSelected) {
                    canvas.drawCircle(bombers.get(i).centerPosX, bombers.get(i).centerPosY, bombers.get(i).radius, bombers.get(i).selector);

                    float health = bombers.get(i).health / bombers.get(i).MAX_HEALTH;
                    canvas.drawRect(bombers.get(i).positionX, bombers.get(i).positionY - bombers.get(i).radius / 4, bombers.get(i).positionX + bombers.get(i).radius * 2, bombers.get(i).positionY - bombers.get(i).radius / 3, red);
                    canvas.drawRect(bombers.get(i).positionX, bombers.get(i).positionY - bombers.get(i).radius / 4, bombers.get(i).positionX + bombers.get(i).radius * 2 * health, bombers.get(i).positionY - bombers.get(i).radius / 3, green);

                    if (bombers.get(i).attacking && bombers.get(i).destinationFinder.enemies.size() > 0) {
                        canvas.drawLine(bombers.get(i).centerPosX, bombers.get(i).centerPosY, bombers.get(i).destinationFinder.enemies.get(0).centerPosX, bombers.get(i).destinationFinder.enemies.get(0).centerPosY, red);
                    }
                    canvas.drawCircle(bombers.get(i).centerPosX, bombers.get(i).centerPosY, bombers.get(i).sensorRadius, sensorPaint);
                }
                if (bombers.get(i).team == 1) {
                    canvas.drawBitmap(bitBomber, bombers.get(i).appearance, null);
                    if (bombers.get(i).destination) {
                        canvas.drawLine(bombers.get(i).centerPosX, bombers.get(i).centerPosY, bombers.get(i).destinationFinder.destX, bombers.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, bombers.get(i).arrow, null);
                    }
                } else if (bombers.get(i).team == 2) {
                    canvas.drawBitmap(enBomber1, bombers.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= scouts.size() - 1; i++) {
            if (!scouts.get(i).visible){
                continue;
            }
            if (scouts.get(i).centerPosX + scouts.get(i).radius >= offsetX / scaleX && scouts.get(i).centerPosX - scouts.get(i).radius <= offsetX / scaleX + extraOffsetX && scouts.get(i).centerPosY + scouts.get(i).radius >= offsetY / scaleY && scouts.get(i).centerPosY - scouts.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (scouts.get(i).selected || scouts.get(i).attSelected) {
                    canvas.drawCircle(scouts.get(i).centerPosX, scouts.get(i).centerPosY, scouts.get(i).radius, scouts.get(i).selector);

                    float health = scouts.get(i).health / scouts.get(i).MAX_HEALTH;
                    canvas.drawRect(scouts.get(i).positionX, scouts.get(i).positionY - scouts.get(i).radius / 4.5f, scouts.get(i).positionX + scouts.get(i).radius * 2, scouts.get(i).positionY - scouts.get(i).radius / 3, red);
                    canvas.drawRect(scouts.get(i).positionX, scouts.get(i).positionY - scouts.get(i).radius / 4.5f, scouts.get(i).positionX + scouts.get(i).radius * 2 * health, scouts.get(i).positionY - scouts.get(i).radius / 3, green);

                    canvas.drawCircle(scouts.get(i).centerPosX, scouts.get(i).centerPosY, scouts.get(i).sensorRadius, sensorPaint);
                }
                if (scouts.get(i).team == 1) {
                    canvas.drawBitmap(bitScout, scouts.get(i).appearance, null);
                    if (scouts.get(i).destination) {
                        canvas.drawLine(scouts.get(i).centerPosX, scouts.get(i).centerPosY, scouts.get(i).destinationFinder.destX, scouts.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, scouts.get(i).arrow, null);
                    }
                } else if (scouts.get(i).team == 2) {
                    canvas.drawBitmap(enScout1, scouts.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= laserCruisers.size() - 1; i++) {
            if (!laserCruisers.get(i).visible){
                continue;
            }
            if (laserCruisers.get(i).centerPosX + laserCruisers.get(i).radius >= offsetX / scaleX && laserCruisers.get(i).centerPosX - laserCruisers.get(i).radius <= offsetX / scaleX + extraOffsetX && laserCruisers.get(i).centerPosY + laserCruisers.get(i).radius >= offsetY / scaleY && laserCruisers.get(i).centerPosY - laserCruisers.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (laserCruisers.get(i).selected || laserCruisers.get(i).attSelected) {
                    canvas.drawCircle(laserCruisers.get(i).centerPosX, laserCruisers.get(i).centerPosY, laserCruisers.get(i).radius, laserCruisers.get(i).selector);

                    float health = laserCruisers.get(i).health / laserCruisers.get(i).MAX_HEALTH;
                    canvas.drawRect(laserCruisers.get(i).positionX, laserCruisers.get(i).positionY - laserCruisers.get(i).radius / 4, laserCruisers.get(i).positionX + laserCruisers.get(i).radius * 2, laserCruisers.get(i).positionY - laserCruisers.get(i).radius / 3, red);
                    canvas.drawRect(laserCruisers.get(i).positionX, laserCruisers.get(i).positionY - laserCruisers.get(i).radius / 4, laserCruisers.get(i).positionX + laserCruisers.get(i).radius * 2 * health, laserCruisers.get(i).positionY - laserCruisers.get(i).radius / 3, green);

                    if (laserCruisers.get(i).attacking && laserCruisers.get(i).destinationFinder.enemies.size() > 0) {
                        canvas.drawLine(laserCruisers.get(i).centerPosX, laserCruisers.get(i).centerPosY, laserCruisers.get(i).destinationFinder.enemies.get(0).centerPosX, laserCruisers.get(i).destinationFinder.enemies.get(0).centerPosY, red);
                    }
                    canvas.drawCircle(laserCruisers.get(i).centerPosX, laserCruisers.get(i).centerPosY, laserCruisers.get(i).sensorRadius, sensorPaint);
                }
                if (laserCruisers.get(i).team == 1) {
                    canvas.drawBitmap(bitLaserCruiser, laserCruisers.get(i).appearance, null);
                    if (laserCruisers.get(i).destination) {
                        canvas.drawLine(laserCruisers.get(i).centerPosX, laserCruisers.get(i).centerPosY, laserCruisers.get(i).destinationFinder.destX, laserCruisers.get(i).destinationFinder.destY, green);
                        canvas.drawBitmap(bitArrow, laserCruisers.get(i).arrow, null);
                    }
                } else if (laserCruisers.get(i).team == 2) {
                    canvas.drawBitmap(enLaserCruiser1, laserCruisers.get(i).appearance, null);
                }
            }
        }

        for (int i = 0; i <= spaceStations.size() - 1; i++) {
            if (!spaceStations.get(i).visible){
                continue;
            }
            if (spaceStations.get(i).centerPosX + spaceStations.get(i).radius >= offsetX / scaleX && spaceStations.get(i).centerPosX - spaceStations.get(i).radius <= offsetX / scaleX + extraOffsetX && spaceStations.get(i).centerPosY + spaceStations.get(i).radius >= offsetY / scaleY && spaceStations.get(i).centerPosY - spaceStations.get(i).radius <= offsetY / scaleY + extraOffsetY) {
                if (spaceStations.get(i).selected || spaceStations.get(i).attSelected) {
                    canvas.drawCircle(spaceStations.get(i).centerPosX, spaceStations.get(i).centerPosY, spaceStations.get(i).radius, spaceStations.get(i).selector);

                    float health = spaceStations.get(i).health / spaceStations.get(i).MAX_HEALTH;
                    canvas.drawRect(spaceStations.get(i).positionX, spaceStations.get(i).positionY - spaceStations.get(i).radius / 9, spaceStations.get(i).positionX + spaceStations.get(i).radius * 2, spaceStations.get(i).positionY - spaceStations.get(i).radius / 8, red);
                    canvas.drawRect(spaceStations.get(i).positionX, spaceStations.get(i).positionY - spaceStations.get(i).radius / 9, spaceStations.get(i).positionX + spaceStations.get(i).radius * 2 * health, spaceStations.get(i).positionY - spaceStations.get(i).radius / 8, green);

                    canvas.drawCircle(spaceStations.get(i).centerPosX, spaceStations.get(i).centerPosY, spaceStations.get(i).sensorRadius, sensorPaint);
                }
                if (spaceStations.get(i).team == 1) {
                    canvas.drawBitmap(bitStation, spaceStations.get(i).appearance, null);
                } else if (spaceStations.get(i).team == 2) {
                    canvas.drawBitmap(enBitStation, spaceStations.get(i).appearance, null);
                }
                canvas.drawBitmap(bitStationRing1, spaceStations.get(i).ringSpiral1, null);
                canvas.drawBitmap(bitStationRing2, spaceStations.get(i).ringSpiral2, null);
                canvas.drawBitmap(bitStationRing3, spaceStations.get(i).ringSpiral3, null);
            }
        }

        for (int i = 0; i < formationsTeam1.size(); i++) {
            for (int j = 0; j < formationsTeam1.get(i).globalCoordinates.size(); j++) {
                if (formationsTeam1.get(i).globalCoordinates != null) {
                    canvas.drawCircle((float) formationsTeam1.get(i).globalCoordinates.get(j).x, (float) formationsTeam1.get(i).globalCoordinates.get(j).y, 50, green);
                }
            }
            canvas.drawCircle((float) formationsTeam1.get(i).centerX, (float) formationsTeam1.get(i).centerY, 50, red);
            canvas.drawCircle((float) formationsTeam1.get(i).destX, (float) formationsTeam1.get(i).destY, 50, white);
            canvas.drawCircle((float) Utilities.circleAngleX(formationsTeam1.get(i).degrees, formationsTeam1.get(i).centerX, Fighter.constRadius), (float) Utilities.circleAngleY(formationsTeam1.get(i).degrees, formationsTeam1.get(i).centerY, Fighter.constRadius), 50, blue);
        }

        if (Main.startSelection) {
            canvas.drawLine(startSelX, startSelY, endSelX, startSelY, green);
            canvas.drawLine(startSelX, startSelY, startSelX, endSelY, green);
            canvas.drawLine(endSelX, endSelY, endSelX, startSelY, green);
            canvas.drawLine(endSelX, endSelY, startSelX, endSelY, green);
        } else if (Main.startAttack) {
            canvas.drawLine(startAttX, startAttY, endAttX, startAttY, red);
            canvas.drawLine(startAttX, startAttY, startAttX, endAttY, red);
            canvas.drawLine(endAttX, endAttY, endAttX, startAttY, red);
            canvas.drawLine(endAttX, endAttY, startAttX, endAttY, red);
        }
    }

    //Main game loop, 60 fps!
    public void gameLoop() {
        Main.refresh.postDelayed(() -> {
            if (!paused) {
                invalidate();
                for (int i = 0; i <= blackHole.size() - 1; i++) {
                    blackHole.get(i).update();
                }
                for (int i = 0; i <= asteroids.size() - 1; i++) {
                    if (asteroids.get(i).hasResLeft) {
                        asteroids.get(i).update();
                    } else {
                        makeExplosion(asteroids.get(i));
                        objects.remove(asteroids.get(i));
                        asteroids.remove(i);
                    }
                }
                int bulletCount = 0;
                for (int i = 0; i <= bullets.size() - 1; i++) {
                    if (bullets.get(i).exists) {
                        bullets.get(i).update();
                        bulletCount++;
                    }
                }
                int missileCount = 0;
                for (int i = 0; i <= missiles.size() - 1; i++) {
                    if (missiles.get(i).exists) {
                        missiles.get(i).update();
                        missileCount++;
                    }
                }
                int laserCount = 0;
                for (int i = 0; i <= lasers.size() - 1; i++) {
                    if (lasers.get(i).exists) {
                        lasers.get(i).update();
                        laserCount++;
                    }
                }
                int explosionCount = 0;
                for (int i = 0; i <= explosions.size() - 1; i++) {
                    if (explosions.get(i).active) {
                        explosions.get(i).update();
                        explosionCount++;
                    }
                }
                for (int i = 0; i <= flagShips.size() - 1; i++) {
                    if (flagShips.get(i).exists) {
                        flagShips.get(i).update();
                    } else {
                        makeExplosion(flagShips.get(i));
                        objects.remove(flagShips.get(i));
                        ships.remove(flagShips.get(i));
                        flagShips.remove(i);
                    }
                }
                for (int i = 0; i <= resourceCollectors.size() - 1; i++) {
                    if (resourceCollectors.get(i).exists) {
                        resourceCollectors.get(i).update();
                    } else {
                        makeExplosion(resourceCollectors.get(i));
                        objects.remove(resourceCollectors.get(i));
                        ships.remove(resourceCollectors.get(i));
                        resourceCollectors.remove(i);
                    }
                }
                for (int i = 0; i <= fighters.size() - 1; i++) {
                    if (fighters.get(i).exists) {
                        fighters.get(i).update();
                    } else {
                        makeExplosion(fighters.get(i));
                        objects.remove(fighters.get(i));
                        ships.remove(fighters.get(i));
                        fighters.remove(i);
                    }
                }
                for (int i = 0; i <= battleShips.size() - 1; i++) {
                    if (battleShips.get(i).exists) {
                        battleShips.get(i).update();
                    } else {
                        makeExplosion(battleShips.get(i));
                        objects.remove(battleShips.get(i));
                        ships.remove(battleShips.get(i));
                        battleShips.remove(i);
                    }
                }
                for (int i = 0; i <= bombers.size() - 1; i++) {
                    if (bombers.get(i).exists) {
                        bombers.get(i).update();
                    } else {
                        makeExplosion(bombers.get(i));
                        objects.remove(bombers.get(i));
                        ships.remove(bombers.get(i));
                        bombers.remove(i);
                    }
                }
                for (int i = 0; i <= scouts.size() - 1; i++) {
                    if (scouts.get(i).exists) {
                        scouts.get(i).update();
                    } else {
                        makeExplosion(scouts.get(i));
                        objects.remove(scouts.get(i));
                        ships.remove(scouts.get(i));
                        scouts.remove(i);
                    }
                }
                for (int i = 0; i <= laserCruisers.size() - 1; i++) {
                    if (laserCruisers.get(i).exists) {
                        laserCruisers.get(i).update();
                    } else {
                        makeExplosion(laserCruisers.get(i));
                        objects.remove(laserCruisers.get(i));
                        ships.remove(laserCruisers.get(i));
                        laserCruisers.remove(i);
                    }
                }
                for (int i = 0; i <= spaceStations.size() - 1; i++) {
                    if (spaceStations.get(i).exists) {
                        spaceStations.get(i).update();
                    } else {
                        makeExplosion(spaceStations.get(i));
                        objects.remove(spaceStations.get(i));
                        ships.remove(spaceStations.get(i));
                        spaceStations.remove(i);
                    }
                }
                for (int i = 0; i <= Main.selectShips.size() - 1; i++) {
                    if (!Main.selectShips.get(i).exists) {
                        Main.selectShips.remove(Main.selectShips.get(i));
                    }
                }
                for (int i = 0; i <= formationsTeam1.size() - 1; i++) {
                    if (formationsTeam1.get(i).ships.size() == 0) {
                        formationsTeam1.remove(formationsTeam1.get(i));
                    } else {
                        formationsTeam1.get(i).update();
                    }
                }
                //System.out.println("Bullets: " + bulletCount + " Missiles: " + missileCount + " Lasers: " + laserCount + " Explosions: " + explosionCount);
                followShips();
            }
            gameLoop();
        }, 16);
    }
}

/*generation = 0;
        String fitnessFile = "fitnessc.txt";
        String gen_pattern = "gen(\\d+)";
        String ship_pattern = "ship(\\d+)";
        Pattern gen_regex = Pattern.compile(gen_pattern);
        Pattern ship_regex = Pattern.compile(ship_pattern);

        File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        if (files != null) {
            for (File file : files) {
                if (Objects.equals(file.getName(), fitnessFile)){
                    fitness = file;
                }
                Matcher matcher = gen_regex.matcher(file.getName());
                if (matcher.find()) {
                    String genNumber = matcher.group(1);
                    if (genNumber == null) {
                        throw new IllegalArgumentException("Gen Number was null.");
                    }
                    if (Integer.parseInt(genNumber) > generation) {
                        generation = Integer.parseInt(genNumber);
                    }
                }
            }
        }
        if (fitness == null){
            System.out.println("Fitness file not found, creating new one.");
            fitness = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fitnessFile);
        }*/

    /*generateGame();
        List<String[]> data = new ArrayList<>();
        for (int i = 0; i <= ships.size() - 1; i++) {
            if (generation == 0) {
                ships.get(i).destinationFinder.attacker = new NeuralNetwork(2, 2, 1);
            } else {
                for (File file : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles()) {
                    Matcher matcher = gen_regex.matcher(file.getName());
                    if (matcher.find()) {
                        String genNumber = matcher.group(1);
                        if (genNumber == null) {
                            throw new IllegalArgumentException("Gen Number was null.");
                        }
                        if (Integer.parseInt(genNumber) == generation) {
                            matcher = ship_regex.matcher(file.getName());
                            if (matcher.find()) {
                                String shipNumber = matcher.group(1);
                                if (shipNumber == null) {
                                    throw new IllegalArgumentException("Ship Number was null.");
                                }
                                if (Integer.parseInt(shipNumber) == i) {
                                    System.out.println("Loading ship " + i);
                                    data.clear();
                                    BufferedReader reader;
                                    try {
                                        reader = new BufferedReader(new FileReader(file));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            data.add(line.split(","));
                                        }

                                        ships.get(i).destinationFinder.attacker = new NeuralNetwork(data);

                                        reader.close();
                                    } catch (FileNotFoundException e) {
                                        throw new IllegalArgumentException("File not found.");
                                    } catch (IOException e) {
                                        throw new IllegalArgumentException("IO Exception.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/

/*time += 16;
                    if (time >= 100000) {
                        paused = true;

                        population.clear();
                        population.addAll(ships);
                        population.addAll(deadShips);

                        double avgFitness = 0;
                        ArrayList<double[]> standardDeviations = new ArrayList<>();
                        for (int i = 0; i <= population.size() - 1; i++) {
                            population.get(i).calculateFitness();
                            avgFitness += population.get(i).fitness;
                            standardDeviations.add(population.get(i).destinationFinder.attacker.getAllWeightsAndBiases());
                        }
                        avgFitness /= population.size();

                        double[] averageWeightsAndBiases = new double[standardDeviations.get(0).length];
                        for (int i = 0; i <= standardDeviations.size() - 1; i++) {
                            for (int j = 0; j <= standardDeviations.get(i).length - 1; j++) {
                                averageWeightsAndBiases[j] += standardDeviations.get(i)[j];
                            }
                        }
                        for (int i = 0; i <= averageWeightsAndBiases.length - 1; i++) {
                            averageWeightsAndBiases[i] /= standardDeviations.size();
                        }

                        double norm = 0;
                        for (int i = 0; i <= standardDeviations.size() - 1; i++) {
                            for (int j = 0; j <= standardDeviations.get(i).length - 1; j++) {
                                standardDeviations.get(i)[j] = Math.sqrt(Math.pow(standardDeviations.get(i)[j] - averageWeightsAndBiases[j], 2) / population.size());
                                norm += Math.pow(standardDeviations.get(i)[j], 2);
                            }
                        }
                        norm = Math.sqrt(norm);

                        try {
                            FileOutputStream fileinput = new FileOutputStream(fitness, true);
                            fileinput.write((generation + "," + avgFitness + "," + norm + "\n").getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        String gen_pattern = "gen(\\d+)";
                        Pattern gen_regex = Pattern.compile(gen_pattern);

                        for (File file : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles()) {
                            Matcher matcher = gen_regex.matcher(file.getName());
                            if (matcher.find()) {
                                String genNumber = matcher.group(1);
                                if (genNumber == null) {
                                    throw new IllegalArgumentException("Gen Number was null.");
                                }
                                if (Integer.parseInt(genNumber) % 20 != 0) {
                                    file.delete();
                                }
                            }
                        }

                        Random random = new Random();
                        double crossoverRate = 0.9;
                        double mutationRate = 0.025;
                        for (int i = 0; i <= population.size() - 1; i++) {
                            NeuralNetwork child;
                            if (random.nextDouble() < crossoverRate) {
                                Ship parent1 = Utilities.rouletteWheelSelection(population);
                                Ship parent2 = Utilities.rouletteWheelSelection(population);
                                child = NeuralNetwork.merge(parent1.destinationFinder.attacker, parent2.destinationFinder.attacker);
                                child.applyMutation(mutationRate);
                            } else {
                                child = Utilities.rouletteWheelSelection(population).destinationFinder.attacker;
                                child.applyMutation(mutationRate);
                            }
                            System.out.println("Child " + i + " created");
                            child.saveWeightsAndBiases(generation + 1, String.valueOf(i));
                        }
                        Main.restart = true;
                    }*/