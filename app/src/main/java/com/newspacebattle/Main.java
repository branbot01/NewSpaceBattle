package com.newspacebattle;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.WindowManager.LayoutParams.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newspacebattle.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.processphoenix.ProcessPhoenix;

//Main class handles ui elements like the menus and buttons
public class Main extends AppCompatActivity {

    static int screenX, screenY, movedX, movedY, formationSelected;
    static float miniX, miniY;
    static boolean pressed, startSelection, selection, startAttack, following, loaded, minimapOn, restart;
    static ArrayList<Ship> selectShips = new ArrayList<>(), enemySelect = new ArrayList<>();
    static int uiOptions;
    static Handler refresh = new Handler(), startUp = new Handler(), selectionChecker = new Handler();
    static ColorStateList fabColor;
    static Collisions collisions;
    ProgressBar loadingBar;
    FloatingActionButton move, stop, destroy, select, attack, shipMode, follow, harvest, dock, dockMenu, buildMenu, formation, pause, minimap, scoutMode;
    FloatingActionButton resourceCollector, scout, fighter, bomber;
    FloatingActionButton buildSpaceStation, buildBattleShip, buildLaserCruiser, buildBomber, buildFighter, buildScout, buildResourceCollector;
    FloatingActionButton nextFormation, controlFormation, disbandFormation;
    FloatingActionButton rectangleFormation, vFormation, circleFormation, customFormation;
    ScrollView scroller;
    Button special, normal, dockedShips, buildShips, currentFormations, buildFormation, quitButton, guideButton;
    Button cancelSpaceStation, cancelBattleShip, cancelLaserCruiser, cancelBomber, cancelFighter, cancelScout, cancelResourceCollector;
    Button play, guide, exit;
    View title, gamemodeBackground, classicButton, annihilationButton, parameterBackground;
    TextView gamemodeTitle, gamemode_classicTitle, gamemode_annihilationTitle, gamemodeClassicExplanation, gamemodeAnnihilationExplanation;
    TextView parameterTitle, difficulty, enemies, galaxysize, resources, blackhole, bots;
    GameScreen gameScreen;
    TextView resourceCount, numResourceCollectors, numScouts, numFighters, numBombers;
    TextView costResourceCollector, costScout, costFighter, costBomber, costLaserCruiser, costBattleShip, costSpaceStation;
    TextView numFormations, team1Blackboard;
    ProgressBar progressResourceCollector, progressScout, progressFighter, progressBomber, progressLaserCruiser, progressBattleShip, progressSpaceStation;
    View decorView, gameView, bar, formationBar;

    private ScaleGestureDetector mScaleDetector;

    private float mScaleFactor = 0.05f;

    boolean zooming = false;

    //Method runs on creation of app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        decorView = getWindow().getDecorView();
        uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        setContentView(R.layout.game_screen);
        gameView = findViewById(R.id.game_screen);
        setContentView(R.layout.title_screen);
    }

    //When the app is resumed
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(uiOptions);
        if (GameScreen.paused && loaded) {
            pauseButton(null);
        }
        GameScreen.paused = false;
    }

    //If app is left running
    protected void onPause() {
        super.onPause();
        GameScreen.paused = true;
    }

    public void pauseButton(View view) {
        if (!GameScreen.paused) {
            GameScreen.paused = true;

            select.setVisibility(View.INVISIBLE);
            formation.setVisibility(View.INVISIBLE);
            minimap.setVisibility(View.INVISIBLE);
            shipBar(false);
            formationBar(false);
            clearSelectionReferences();

            pause.setImageResource(R.drawable.ic_resumebutton);

            bar.setVisibility(View.VISIBLE);
            quitButton.setVisibility(View.VISIBLE);
            guideButton.setVisibility(View.VISIBLE);
        } else {
            GameScreen.paused = false;

            select.setVisibility(View.VISIBLE);
            formation.setVisibility(View.VISIBLE);
            minimap.setVisibility(View.VISIBLE);

            quitButton.setVisibility(View.INVISIBLE);
            guideButton.setVisibility(View.INVISIBLE);

            pause.setImageResource(R.drawable.ic_pausebutton);
        }
    }

    public void quitButton(View view) {
        restart = true;
    }

    public void exitButton(View view) {
        finish();
    }

    public void guideButton(View view) {
    }

    //If user hits back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void play(View view){
        title = findViewById(R.id.gametitle);
        play = findViewById(R.id.play);
        guide = findViewById(R.id.guide);
        exit = findViewById(R.id.exit);
        gamemodeBackground = findViewById(R.id.gamemodeBackground);
        gamemodeTitle = findViewById(R.id.gamemodeTitle);
        gamemode_classicTitle = findViewById(R.id.gamemode_classicTitle);
        gamemode_annihilationTitle = findViewById(R.id.gamemode_annihilationTitle);
        classicButton = findViewById(R.id.classicButton);
        annihilationButton = findViewById(R.id.annihilationButton);
        gamemodeClassicExplanation = findViewById(R.id.gamemodeClassicExplanation);
        gamemodeAnnihilationExplanation = findViewById(R.id.gamemodeAnnihilationExplanation);

        title.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        guide.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);
        gamemodeBackground.setVisibility(View.VISIBLE);
        gamemodeTitle.setVisibility(View.VISIBLE);
        gamemode_classicTitle.setVisibility(View.VISIBLE);
        gamemode_annihilationTitle.setVisibility(View.VISIBLE);
        classicButton.setVisibility(View.VISIBLE);
        annihilationButton.setVisibility(View.VISIBLE);
        gamemodeClassicExplanation.setVisibility(View.VISIBLE);
        gamemodeAnnihilationExplanation.setVisibility(View.VISIBLE);

    }

    public void classicButton(View view){
        gamemodeBackground.setVisibility(View.INVISIBLE);
        gamemodeTitle.setVisibility(View.INVISIBLE);
        gamemode_classicTitle.setVisibility(View.INVISIBLE);
        gamemode_annihilationTitle.setVisibility(View.INVISIBLE);
        classicButton.setVisibility(View.INVISIBLE);
        annihilationButton.setVisibility(View.INVISIBLE);
        gamemodeClassicExplanation.setVisibility(View.INVISIBLE);
        gamemodeAnnihilationExplanation.setVisibility(View.INVISIBLE);

        parameterBackground = findViewById(R.id.parameterBackground);
        parameterTitle = findViewById(R.id.parameterTitle);
        difficulty = findViewById(R.id.difficulty);
        enemies = findViewById(R.id.enemies);
        galaxysize = findViewById(R.id.galaxySize);
        resources = findViewById(R.id.resources);
        blackhole = findViewById(R.id.blackhole);
        bots = findViewById(R.id.bots);

        parameterBackground.setVisibility(View.VISIBLE);
        parameterTitle.setVisibility(View.VISIBLE);
        difficulty.setVisibility(View.VISIBLE);
        enemies.setVisibility(View.VISIBLE);
        galaxysize.setVisibility(View.VISIBLE);
        resources.setVisibility(View.VISIBLE);
        blackhole.setVisibility(View.VISIBLE);
        bots.setVisibility(View.VISIBLE);

        GameScreen.classic = true;
    }

    public void annihilationButton(View view){
        gamemodeBackground.setVisibility(View.INVISIBLE);
        gamemodeTitle.setVisibility(View.INVISIBLE);
        gamemode_classicTitle.setVisibility(View.INVISIBLE);
        gamemode_annihilationTitle.setVisibility(View.INVISIBLE);
        classicButton.setVisibility(View.INVISIBLE);
        annihilationButton.setVisibility(View.INVISIBLE);
        gamemodeClassicExplanation.setVisibility(View.INVISIBLE);
        gamemodeAnnihilationExplanation.setVisibility(View.INVISIBLE);

        parameterBackground = findViewById(R.id.parameterBackground);
        parameterTitle = findViewById(R.id.parameterTitle);
        difficulty = findViewById(R.id.difficulty);
        enemies = findViewById(R.id.enemies);
        galaxysize = findViewById(R.id.galaxySize);
        resources = findViewById(R.id.resources);
        blackhole = findViewById(R.id.blackhole);
        bots = findViewById(R.id.bots);

        parameterBackground.setVisibility(View.VISIBLE);
        parameterTitle.setVisibility(View.VISIBLE);
        difficulty.setVisibility(View.VISIBLE);
        enemies.setVisibility(View.VISIBLE);
        galaxysize.setVisibility(View.VISIBLE);
        resources.setVisibility(View.VISIBLE);
        blackhole.setVisibility(View.VISIBLE);
        bots.setVisibility(View.VISIBLE);

        GameScreen.classic = false;
    }

    //Upon pressing play, sets up game
    public void playGame(View view) {
        if (!pressed) {
            pressed = true;

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            screenY = size.y;
            screenX = size.x;
            GameScreen.circleRatio = (float) screenY / screenX;

            new Thread(() -> {
                gameScreen = new GameScreen(getApplicationContext());
                loaded = true;
            }).start();
            loadingBar = findViewById(R.id.loading);
            loadingBar.setVisibility(View.VISIBLE);
            showGameScreen();
        }
    }

    //method runs when loading finishes
    public void showGameScreen() {
        startUp.postDelayed(() -> {
            if (loaded) {
                formationSelected = 0;
                mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
                setContentView(gameScreen);
                addContentView(gameView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                collisions = new Collisions();
                gameScreen.gameLoop();
                UILoop();
                loadingBar.setVisibility(View.INVISIBLE);
                findIds();
            } else {
                showGameScreen();
            }
        }, 16);
    }

    //Loop updating ui elements
    public void UILoop() {
        if (restart) {
            ProcessPhoenix.triggerRebirth(getApplicationContext());
        }
        selectionChecker.postDelayed(() -> {
            if (!GameScreen.paused) {
                if (GameScreen.gameOver){
                    shipBar(false);
                    formationBar(false);
                    select.setVisibility(View.INVISIBLE);
                    formation.setVisibility(View.INVISIBLE);
                }
                if (GameScreen.blackboards[0].newMessage) {
                    team1Blackboard.setText(GameScreen.blackboards[0].getLog());
                    blackBoardClick(null);
                    GameScreen.blackboards[0].newMessage = false;
                }
                if (selectShips.size() == 0) {
                    shipBar(false);
                } else {
                    boolean allDocked = true;
                    for (int i = 0; i <= selectShips.size() - 1; i++) {
                        if (!selectShips.get(i).docked) {
                            allDocked = false;
                        }
                    }
                    if (allDocked) {
                        shipBar(false);
                    }
                    for (int i = 0; i <= selectShips.size() - 1; i++) {
                        if (selectShips.get(i).docked) {
                            selectShips.remove(selectShips.get(i));
                        }
                    }
                    int spaceStationCount = 0, resourceCollectorCount = 0, dockableShipsCount = 0, flagShipCount = 0, scoutCount = 0;
                    SpaceStation spaceStation = null;
                    FlagShip flagShip = null;
                    for (int i = 0; i <= selectShips.size() - 1; i++) {
                        if (selectShips.get(i) instanceof SpaceStation) {
                            spaceStationCount++;
                            spaceStation = (SpaceStation) selectShips.get(i);
                        }
                        if (selectShips.get(i) instanceof ResourceCollector) {
                            resourceCollectorCount++;
                        }
                        if (selectShips.get(i) instanceof Scout) {
                            scoutCount++;
                        }
                        if (selectShips.get(i).dockable) {
                            dockableShipsCount++;
                        }
                        if (selectShips.get(i) instanceof FlagShip) {
                            flagShipCount++;
                            flagShip = (FlagShip) selectShips.get(i);
                        }
                    }
                    if (spaceStationCount == 1) {
                        countDockedShips(spaceStation);
                    } else {
                        dockMenu.setAlpha(0.5f);
                    }
                    if (resourceCollectorCount == 0) {
                        harvest.setAlpha(0.5f);
                    }
                    if (scoutCount == 0) {
                        scoutMode.setAlpha(0.5f);
                    }
                    if (dockableShipsCount == 0) {
                        dock.setAlpha(0.5f);
                    }
                    if (flagShipCount == 1) {
                        if (flagShip.buildingResourceCollector) {
                            progressResourceCollector.setProgress((int) (flagShip.costCounter[6] / (double) (ResourceCollector.cost) * 100));
                        } else {
                            progressResourceCollector.setProgress(0);
                        }
                        if (flagShip.buildingScout) {
                            progressScout.setProgress((int) (flagShip.costCounter[5] / (double) (Scout.cost) * 100));
                        } else {
                            progressScout.setProgress(0);
                        }
                        if (flagShip.buildingFighter) {
                            progressFighter.setProgress((int) (flagShip.costCounter[4] / (double) (Fighter.cost) * 100));
                        } else {
                            progressFighter.setProgress(0);
                        }
                        if (flagShip.buildingBomber) {
                            progressBomber.setProgress((int) (flagShip.costCounter[3] / (double) (Bomber.cost) * 100));
                        } else {
                            progressBomber.setProgress(0);
                        }
                        if (flagShip.buildingLaserCruiser) {
                            progressLaserCruiser.setProgress((int) (flagShip.costCounter[2] / (double) (LaserCruiser.cost) * 100));
                        } else {
                            progressLaserCruiser.setProgress(0);
                        }
                        if (flagShip.buildingBattleShip) {
                            progressBattleShip.setProgress((int) (flagShip.costCounter[1] / (double) (BattleShip.cost) * 100));
                        } else {
                            progressBattleShip.setProgress(0);
                        }
                        if (flagShip.buildingSpaceStation) {
                            progressSpaceStation.setProgress((int) (flagShip.costCounter[0] / (double) (SpaceStation.cost) * 100));
                        } else {
                            progressSpaceStation.setProgress(0);
                        }

                        cancelSpaceStation.setText("x" + flagShip.countSpaceStation);
                        cancelBattleShip.setText("x" + flagShip.countBattleShip);
                        cancelLaserCruiser.setText("x" + flagShip.countLaserCruiser);
                        cancelBomber.setText("x" + flagShip.countBomber);
                        cancelFighter.setText("x" + flagShip.countFighter);
                        cancelScout.setText("x" + flagShip.countScout);
                        cancelResourceCollector.setText("x" + flagShip.countResourceCollector);
                    } else {
                        buildMenu.setAlpha(0.5f);
                    }

                    numFormations.setText("Number of Formations: " + GameScreen.formationsTeam1.size());
                }
                if (isFormationSelected() != null) {
                    disbandFormation.setAlpha(1f);
                } else {
                    disbandFormation.setAlpha(0.5f);
                }
                resourceCount.setText("Resources: " + GameScreen.resources[0]);
            }
            UILoop();
        }, 16);
    }

    //Clears any ships that are selected
    public static void clearSelectionReferences() {
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            GameScreen.ships.get(i).selected = false;
            GameScreen.ships.get(i).attSelected = false;
        }
        selectShips.clear();
        enemySelect.clear();
    }

    public void minimap(View view) {
        if (minimapOn) {
            minimapOn = false;

            select.setVisibility(View.VISIBLE);
            formation.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);

            for (int i = 0; i < selectShips.size(); i++) {
                selectShips.get(i).movable = false;
            }
        } else {
            minimapOn = true;

            select.setVisibility(View.INVISIBLE);
            formation.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.INVISIBLE);
            shipBar(false);
            formationBar(false);

            if (!isAllMovable()) {
                clearSelectionReferences();
            }
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.01f, Math.min(mScaleFactor, 0.15f));
            return true;
        }
    }

    public void blackBoardClick(View view) {
        scroller.post(new Runnable() {
            public void run() {
                scroller.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void showBlackboard(View view) {
        if (team1Blackboard.getVisibility() == View.VISIBLE) {
            team1Blackboard.setVisibility(View.GONE);
        } else {
            team1Blackboard.setVisibility(View.VISIBLE);
        }
    }

    //When user touches the screen, not any buttons
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (GameScreen.paused || mScaleDetector == null) {
            return true;
        }

        int x = (int) (event.getX()), y = (int) (event.getY()), eventAction = event.getAction();
        float trueX = (x + GameScreen.offsetX) / GameScreen.scaleX;
        float trueY = (y + GameScreen.offsetY) / GameScreen.scaleY;

        if (minimapOn) {
            miniX = (x - Main.screenX / 2) / 0.8f;
            miniY = (y - Main.screenY / 2) / 0.8f;
            if (isAllMovable()) {
                trueX = miniX * GameScreen.mapSizeX / Main.screenX;
                trueY = miniY * GameScreen.mapSizeY / Main.screenY;
                //System.out.println("X: " + trueX + " Y: " + trueY + " mapX: " + GameScreen.mapSizeX + " mapY: " + GameScreen.mapSizeY + " screenX: " + Main.screenX + " screenY: " + Main.screenY);
                Formation formation = isFormationSelected();
                if (formation == null) {
                    for (int i = 0; i <= selectShips.size() - 1; i++) {
                        if (selectShips.get(i).movable && !(selectShips.get(i) instanceof SpaceStation)) {
                            if (selectShips.get(i) instanceof Scout){
                                ((Scout) selectShips.get(i)).scouting = false;
                            } else if (selectShips.get(i) instanceof ResourceCollector){
                                ((ResourceCollector) selectShips.get(i)).harvesting = false;
                                ((ResourceCollector) selectShips.get(i)).unloading = false;
                            }
                            selectShips.get(i).setDestination(trueX, trueY);
                        }
                    }
                } else {
                    for (int i = 0; i < selectShips.size(); i++) {
                        selectShips.get(i).movable = false;
                    }
                    formation.setDestination(trueX, trueY);
                }

                following = true;
                for (int i = 0; i < selectShips.size(); i++) {
                    selectShips.get(i).movable = false;
                }
                shipBar(true);
                minimap(null);
            }
            return true;
        }

        mScaleDetector.onTouchEvent(event);
        if (mScaleDetector.isInProgress() && event.getPointerCount() == 2) {
            zooming = true;
            GameScreen.midPointX = (Main.screenX / 2 + GameScreen.offsetX) / GameScreen.scaleX;
            GameScreen.midPointY = (Main.screenY / 2 + GameScreen.offsetY) / GameScreen.scaleY;

            double scaleDiff = mScaleFactor - GameScreen.scaleX;
            GameScreen.scaleX = mScaleFactor;
            GameScreen.scaleY = mScaleFactor;

            GameScreen.offsetX += scaleDiff * GameScreen.midPointX;
            GameScreen.offsetY += scaleDiff * GameScreen.midPointY;

            checkBorders();
            return true;
        }

        if (startSelection) {
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN:
                    GameScreen.startSelX = trueX;
                    GameScreen.startSelY = trueY;
                    GameScreen.endSelX = trueX;
                    GameScreen.endSelY = trueY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    follow.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    following = false;
                    GameScreen.endSelX = trueX;
                    GameScreen.endSelY = trueY;
                    selection = GameScreen.groupSelect(GameScreen.startSelX, GameScreen.startSelY, GameScreen.endSelX, GameScreen.endSelY);
                    break;

                case MotionEvent.ACTION_UP:
                    startSelection = false;
                    select.setBackgroundTintList(fabColor);
                    GameScreen.startSelX = trueX;
                    GameScreen.startSelY = trueY;
                    GameScreen.endSelX = trueX;
                    GameScreen.endSelY = trueY;
                    if (selection) {
                        shipBar(true);
                    }
                    break;
            }
        } else if (startAttack) {
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN:
                    GameScreen.startAttX = trueX;
                    GameScreen.startAttY = trueY;
                    GameScreen.endAttX = trueX;
                    GameScreen.endAttY = trueY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    follow.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    following = false;
                    GameScreen.endAttX = trueX;
                    GameScreen.endAttY = trueY;
                    enemySelect = GameScreen.attackSelect(GameScreen.startAttX, GameScreen.startAttY, GameScreen.endAttX, GameScreen.endAttY);
                    break;

                case MotionEvent.ACTION_UP:
                    startAttack = false;
                    attack.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    GameScreen.endAttX = trueX;
                    GameScreen.endAttY = trueY;
                    shipBar(false);
                    for (int i = 0; i <= Main.selectShips.size() - 1; i++) {
                        if (selectShips.get(i).canAttack) {
                            selectShips.get(i).destinationFinder.runAttack(GameScreen.attackSelect(GameScreen.startAttX, GameScreen.startAttY, GameScreen.endAttX, GameScreen.endAttY));
                        }
                    }
                    clearSelectionReferences();
                    break;
            }
        } else {
            if (loaded) {
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN:
                        movedX = x + GameScreen.offsetX;
                        movedY = y + GameScreen.offsetY;
                        Formation formation = isFormationSelected();

                        if (formation == null) {
                            for (int i = 0; i <= selectShips.size() - 1; i++) {
                                if (selectShips.get(i).movable && !(selectShips.get(i) instanceof SpaceStation)) {
                                    if (selectShips.get(i) instanceof Scout){
                                        ((Scout) selectShips.get(i)).scouting = false;
                                    } else if (selectShips.get(i) instanceof ResourceCollector){
                                        ((ResourceCollector) selectShips.get(i)).harvesting = false;
                                        ((ResourceCollector) selectShips.get(i)).unloading = false;
                                    }
                                    selectShips.get(i).setDestination(trueX, trueY);
                                }
                            }
                        } else if (isAllMovable()) {
                            for (int i = 0; i < selectShips.size(); i++) {
                                selectShips.get(i).movable = false;
                            }
                            formation.setDestination(trueX, trueY);
                        }

                        clearSelectionReferences();
                        following = false;
                        shipBar(false);
                        formationBar(false);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 2 || zooming) {
                            break;
                        }
                        following = false;
                        clearSelectionReferences();
                        GameScreen.offsetX = movedX - x;
                        GameScreen.offsetY = movedY - y;
                        checkBorders();
                        break;

                    case MotionEvent.ACTION_UP:
                        selection = false;
                        zooming = false;
                        break;
                }
            }
        }
        return true;
    }

    public static void checkBorders(){
        if (GameScreen.offsetX / GameScreen.scaleX < -GameScreen.mapSizeX / 2){
            GameScreen.offsetX = (int) (-GameScreen.mapSizeX / 2 * GameScreen.scaleX);
        } else if ((Main.screenX + GameScreen.offsetX) / GameScreen.scaleX > GameScreen.mapSizeX / 2){
            GameScreen.offsetX = (int) (GameScreen.mapSizeX / 2 * GameScreen.scaleX - Main.screenX);
        }

        if (GameScreen.offsetY / GameScreen.scaleY < -GameScreen.mapSizeY / 2){
            GameScreen.offsetY = (int) (-GameScreen.mapSizeY / 2 * GameScreen.scaleY);
        } else if ((Main.screenY + GameScreen.offsetY) / GameScreen.scaleY > GameScreen.mapSizeY / 2){
            GameScreen.offsetY = (int) (GameScreen.mapSizeY / 2 * GameScreen.scaleY - Main.screenY);
        }
    }

    //Get references for all of the buttons and ui elements
    public void findIds() {
        team1Blackboard = findViewById(R.id.blackboard);
        scroller = findViewById(R.id.scroller);
        pause = findViewById(R.id.pauseButton);
        quitButton = findViewById(R.id.quitButton);
        guideButton = findViewById(R.id.guideButton);
        minimap = findViewById(R.id.minimapButton);
        bar = findViewById(R.id.bottomBar);
        formationBar = findViewById(R.id.bottomFormationBar);
        move = findViewById(R.id.moveButton);
        stop = findViewById(R.id.stopButton);
        destroy = findViewById(R.id.destroyButton);
        select = findViewById(R.id.select);
        formation = findViewById(R.id.formationButton);
        attack = findViewById(R.id.attackButton);
        shipMode = findViewById(R.id.shipModeButton);
        follow = findViewById(R.id.followButton);
        special = findViewById(R.id.specialButton);
        normal = findViewById(R.id.normalButton);
        dockedShips = findViewById(R.id.dockedShipsButton);
        harvest = findViewById(R.id.harvestButton);
        dock = findViewById(R.id.dockButton);
        dockMenu = findViewById(R.id.dockMenuButton);
        buildMenu = findViewById(R.id.buildButton);
        scoutMode = findViewById(R.id.scoutModeButton);
        fabColor = select.getBackgroundTintList();
        resourceCollector = findViewById(R.id.resourceCollectorButton);
        scout = findViewById(R.id.scoutButton);
        fighter = findViewById(R.id.fighterButton);
        bomber = findViewById(R.id.bomberButton);
        numResourceCollectors = findViewById(R.id.numResourceCollectors);
        numScouts = findViewById(R.id.numScouts);
        numFighters = findViewById(R.id.numFighters);
        numBombers = findViewById(R.id.numBombers);
        resourceCount = findViewById(R.id.resourcesText);
        buildShips = findViewById(R.id.buildShipsButton);
        buildSpaceStation = findViewById(R.id.makeSpaceStation);
        buildBattleShip = findViewById(R.id.makeBattleShip);
        buildLaserCruiser = findViewById(R.id.makeLaserCruiser);
        buildBomber = findViewById(R.id.makeBomber);
        buildFighter = findViewById(R.id.makeFighter);
        buildScout = findViewById(R.id.makeScout);
        buildResourceCollector = findViewById(R.id.makeResourceCollector);
        cancelSpaceStation = findViewById(R.id.cancelSpaceStation);
        cancelBattleShip = findViewById(R.id.cancelBattleShip);
        cancelLaserCruiser = findViewById(R.id.cancelLaserCruiser);
        cancelBomber = findViewById(R.id.cancelBomber);
        cancelFighter = findViewById(R.id.cancelFighter);
        cancelScout = findViewById(R.id.cancelScout);
        cancelResourceCollector = findViewById(R.id.cancelResourceCollector);
        costResourceCollector = findViewById(R.id.costResourceCollector);
        costScout = findViewById(R.id.costScout);
        costFighter = findViewById(R.id.costFighter);
        costBomber = findViewById(R.id.costBomber);
        costLaserCruiser = findViewById(R.id.costLaserCruiser);
        costBattleShip = findViewById(R.id.costBattleShip);
        costSpaceStation = findViewById(R.id.costSpaceStation);
        progressResourceCollector = findViewById(R.id.progressResourceCollector);
        progressScout = findViewById(R.id.progressScout);
        progressFighter = findViewById(R.id.progressFighter);
        progressBomber = findViewById(R.id.progressBomber);
        progressLaserCruiser = findViewById(R.id.progressLaserCruiser);
        progressBattleShip = findViewById(R.id.progressBattleShip);
        progressSpaceStation = findViewById(R.id.progressSpaceStation);
        currentFormations = findViewById(R.id.currentFormations);
        nextFormation = findViewById(R.id.nextFormation);
        controlFormation = findViewById(R.id.controlFormation);
        disbandFormation = findViewById(R.id.disbandFormation);
        buildFormation = findViewById(R.id.buildFormation);
        rectangleFormation = findViewById(R.id.rectangleFormation);
        vFormation = findViewById(R.id.vFormation);
        circleFormation = findViewById(R.id.circleFormation);
        customFormation = findViewById(R.id.customFormation);
        numFormations = findViewById(R.id.numFormations);
    }

    //Either hides or shows the ship options bar depending if any ships are selected
    public void shipBar(boolean hiddenOrNot) {
        if (!anyAutoAttack()) {
            shipMode.setImageResource(R.drawable.ic_evasive);
        } else {
            shipMode.setImageResource(R.drawable.ic_aggressive);
        }

        if (!following) {
            follow.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            follow.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        }

        if (hiddenOrNot) {
            //bar.setBackgroundColor(Color.parseColor("#0099CC"));

            bar.setVisibility(View.VISIBLE);
            move.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            attack.setVisibility(View.VISIBLE);
            destroy.setVisibility(View.VISIBLE);
            shipMode.setVisibility(View.VISIBLE);
            follow.setVisibility(View.VISIBLE);
            normal.setVisibility(View.VISIBLE);

            special.setVisibility(View.INVISIBLE);
            dockedShips.setVisibility(View.INVISIBLE);
            harvest.setVisibility(View.INVISIBLE);
            dock.setVisibility(View.INVISIBLE);
            dockMenu.setVisibility(View.INVISIBLE);
            buildShips.setVisibility(View.INVISIBLE);
        } else {
            bar.setVisibility(View.INVISIBLE);
            move.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.INVISIBLE);
            destroy.setVisibility(View.INVISIBLE);
            attack.setVisibility(View.INVISIBLE);
            shipMode.setVisibility(View.INVISIBLE);
            follow.setVisibility(View.INVISIBLE);
            special.setVisibility(View.INVISIBLE);
            normal.setVisibility(View.INVISIBLE);
            harvest.setVisibility(View.INVISIBLE);
            dock.setVisibility(View.INVISIBLE);
            dockedShips.setVisibility(View.INVISIBLE);
            dockMenu.setVisibility(View.INVISIBLE);
            buildMenu.setVisibility(View.INVISIBLE);
            scoutMode.setVisibility(View.INVISIBLE);
            resourceCollector.setVisibility(View.INVISIBLE);
            scout.setVisibility(View.INVISIBLE);
            fighter.setVisibility(View.INVISIBLE);
            bomber.setVisibility(View.INVISIBLE);
            numResourceCollectors.setVisibility(View.INVISIBLE);
            numScouts.setVisibility(View.INVISIBLE);
            numFighters.setVisibility(View.INVISIBLE);
            numBombers.setVisibility(View.INVISIBLE);
            buildShips.setVisibility(View.INVISIBLE);
            buildSpaceStation.setVisibility(View.INVISIBLE);
            buildBattleShip.setVisibility(View.INVISIBLE);
            buildLaserCruiser.setVisibility(View.INVISIBLE);
            buildBomber.setVisibility(View.INVISIBLE);
            buildFighter.setVisibility(View.INVISIBLE);
            buildScout.setVisibility(View.INVISIBLE);
            buildResourceCollector.setVisibility(View.INVISIBLE);

            costResourceCollector.setVisibility(View.INVISIBLE);
            costScout.setVisibility(View.INVISIBLE);
            costFighter.setVisibility(View.INVISIBLE);
            costBomber.setVisibility(View.INVISIBLE);
            costLaserCruiser.setVisibility(View.INVISIBLE);
            costBattleShip.setVisibility(View.INVISIBLE);
            costSpaceStation.setVisibility(View.INVISIBLE);

            progressResourceCollector.setVisibility(View.INVISIBLE);
            progressScout.setVisibility(View.INVISIBLE);
            progressFighter.setVisibility(View.INVISIBLE);
            progressBomber.setVisibility(View.INVISIBLE);
            progressLaserCruiser.setVisibility(View.INVISIBLE);
            progressBattleShip.setVisibility(View.INVISIBLE);
            progressSpaceStation.setVisibility(View.INVISIBLE);

            cancelSpaceStation.setVisibility(View.INVISIBLE);
            cancelBattleShip.setVisibility(View.INVISIBLE);
            cancelLaserCruiser.setVisibility(View.INVISIBLE);
            cancelBomber.setVisibility(View.INVISIBLE);
            cancelFighter.setVisibility(View.INVISIBLE);
            cancelScout.setVisibility(View.INVISIBLE);
            cancelResourceCollector.setVisibility(View.INVISIBLE);

            clearButtonsToWhite();
        }
    }

    //When the move button is pressed, sets selected ships to movable
    public void moveShip(View view) {
        clearButtonsToWhite();
        move.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        startAttack = false;

        if (isAllMovable()) {
            minimap(null);
        } else {
            for (int i = 0; i <= selectShips.size() - 1; i++) {
                selectShips.get(i).movable = true;
            }
        }
    }

    //Stops all currently selected ships
    public void stopShip(View view) {
        following = false;
        Formation formation = isFormationSelected();
        if (formation != null) {
            formation.stopMovement();
        }
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).destination = false;
            selectShips.get(i).movable = false;
            if (selectShips.get(i) instanceof ResourceCollector) {
                ((ResourceCollector) selectShips.get(i)).harvesting = false;
                ((ResourceCollector) selectShips.get(i)).unloading = false;
            }
            if (selectShips.get(i) instanceof Scout) {
                ((Scout) selectShips.get(i)).scouting = false;
            }
            selectShips.get(i).stop();
        }
        shipBar(false);
        clearSelectionReferences();
    }

    //Destroys selected ships
    public void destroyShip(View view) {
        following = false;

        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).health = 0;
        }

        shipBar(false);
        clearSelectionReferences();
    }

    //Initiates attack selection
    public void attackShip(View view) {
        if (!startAttack) {
            clearButtonsToWhite();
            attack.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
            startAttack = true;
            GameScreen.startAttX = 0;
            GameScreen.startAttY = 0;
            GameScreen.endAttX = 0;
            GameScreen.endAttY = 0;
        } else {
            attack.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            startAttack = false;
        }
    }

    public boolean anyAutoAttack(){
        boolean anyAutoAttack = false;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i).autoAttack) {
                anyAutoAttack = true;
                break;
            }
        }
        return anyAutoAttack;
    }

    //Changes ship behaviour
    public void shipAutoAttack(View view) {
        clearButtonsToWhite();

        if (!anyAutoAttack()){
            shipMode.setImageResource(R.drawable.ic_aggressive);
            for (int i = 0; i <= selectShips.size() - 1; i++) {
                if (selectShips.get(i).canAttack) {
                    selectShips.get(i).autoAttack = true;
                    selectShips.get(i).destinationFinder.searchingForEnemy = false;
                }
            }
        } else {
            shipMode.setImageResource(R.drawable.ic_evasive);
            for (int i = 0; i <= selectShips.size() - 1; i++) {
                selectShips.get(i).autoAttack = false;
                selectShips.get(i).destinationFinder.searchingForEnemy = false;
            }
        }
    }

    //Starts following selected ships
    public void followShip(View view) {
        follow.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        following = true;
    }

    //Starts the resource collecting method for the collectors
    public void shipHarvest(View view) {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof ResourceCollector) {
                ((ResourceCollector) selectShips.get(i)).goToAsteroid();
            }
        }
    }

    //Tells ships to dock
    public void dockShips(View view) {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i).dockable) {
                selectShips.get(i).dock();
            }
        }
    }

    // Opens the dock menu
    public void openDockMenu(View view) {
        int spaceStationCount = 0;
        SpaceStation spaceStation = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                spaceStationCount++;
                spaceStation = (SpaceStation) selectShips.get(i);
            }
        }
        if (spaceStationCount != 1) {
            return;
        }
        countDockedShips(spaceStation);

        dockedShips.setVisibility(View.VISIBLE);

        scoutMode.setVisibility(View.INVISIBLE);
        harvest.setVisibility(View.INVISIBLE);
        dock.setVisibility(View.INVISIBLE);
        dockMenu.setVisibility(View.INVISIBLE);
        buildMenu.setVisibility(View.INVISIBLE);
        special.setVisibility(View.INVISIBLE);

        costResourceCollector.setVisibility(View.INVISIBLE);
        costScout.setVisibility(View.INVISIBLE);
        costFighter.setVisibility(View.INVISIBLE);
        costBomber.setVisibility(View.INVISIBLE);
        costLaserCruiser.setVisibility(View.INVISIBLE);
        costBattleShip.setVisibility(View.INVISIBLE);
        costSpaceStation.setVisibility(View.INVISIBLE);

        resourceCollector.setVisibility(View.VISIBLE);
        scout.setVisibility(View.VISIBLE);
        fighter.setVisibility(View.VISIBLE);
        bomber.setVisibility(View.VISIBLE);
        numResourceCollectors.setVisibility(View.VISIBLE);
        numScouts.setVisibility(View.VISIBLE);
        numFighters.setVisibility(View.VISIBLE);
        numBombers.setVisibility(View.VISIBLE);
    }

    private void countDockedShips(SpaceStation spaceStation) {
        int dockedResourceCollectors = 0, dockedScouts = 0, dockedFighters = 0, dockedBombers = 0;
        for (int i = 0; i <= spaceStation.dockedShips.size() - 1; i++) {
            if (spaceStation.dockedShips.get(i) instanceof ResourceCollector) {
                dockedResourceCollectors++;
            } else if (spaceStation.dockedShips.get(i) instanceof Scout) {
                dockedScouts++;
            } else if (spaceStation.dockedShips.get(i) instanceof Fighter) {
                dockedFighters++;
            } else if (spaceStation.dockedShips.get(i) instanceof Bomber) {
                dockedBombers++;
            }
        }
        numResourceCollectors.setText("x" + dockedResourceCollectors);
        numScouts.setText("x" + dockedScouts);
        numFighters.setText("x" + dockedFighters);
        numBombers.setText("x" + dockedBombers);

        if (dockedResourceCollectors == 0) {
            resourceCollector.setAlpha(0.5f);
        } else {
            resourceCollector.setAlpha(1f);
        }
        if (dockedScouts == 0) {
            scout.setAlpha(0.5f);
        } else {
            scout.setAlpha(1f);
        }
        if (dockedFighters == 0) {
            fighter.setAlpha(0.5f);
        } else {
            fighter.setAlpha(1f);
        }
        if (dockedBombers == 0) {
            bomber.setAlpha(0.5f);
        } else {
            bomber.setAlpha(1f);
        }
    }

    //Initiates ship selection process
    public void selectShips(View view) {
        if (!following) {
            for (int i = 0; i <= selectShips.size() - 1; i++) {
                selectShips.get(i).movable = false;
            }
        }
        clearSelectionReferences();
        shipBar(false);
        formationBar(false);

        if (!startSelection) {
            select.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            startSelection = true;
        } else {
            select.setBackgroundTintList(fabColor);
            startSelection = false;
        }
    }

    //Opens special menu
    public void specialMenu(View view) {
        stop.setVisibility(View.VISIBLE);
        move.setVisibility(View.VISIBLE);
        attack.setVisibility(View.VISIBLE);
        destroy.setVisibility(View.VISIBLE);
        shipMode.setVisibility(View.VISIBLE);
        follow.setVisibility(View.VISIBLE);

        normal.setVisibility(View.VISIBLE);
        special.setVisibility(View.INVISIBLE);
        dockedShips.setVisibility(View.INVISIBLE);
        buildShips.setVisibility(View.INVISIBLE);

        harvest.setVisibility(View.INVISIBLE);
        dock.setVisibility(View.INVISIBLE);
        dockMenu.setVisibility(View.INVISIBLE);
        buildMenu.setVisibility(View.INVISIBLE);
        scoutMode.setVisibility(View.INVISIBLE);

        buildSpaceStation.setVisibility(View.INVISIBLE);
        buildBattleShip.setVisibility(View.INVISIBLE);
        buildLaserCruiser.setVisibility(View.INVISIBLE);
        buildBomber.setVisibility(View.INVISIBLE);
        buildFighter.setVisibility(View.INVISIBLE);
        buildScout.setVisibility(View.INVISIBLE);
        buildResourceCollector.setVisibility(View.INVISIBLE);

        costResourceCollector.setVisibility(View.INVISIBLE);
        costScout.setVisibility(View.INVISIBLE);
        costFighter.setVisibility(View.INVISIBLE);
        costBomber.setVisibility(View.INVISIBLE);
        costLaserCruiser.setVisibility(View.INVISIBLE);
        costBattleShip.setVisibility(View.INVISIBLE);
        costSpaceStation.setVisibility(View.INVISIBLE);

        progressResourceCollector.setVisibility(View.INVISIBLE);
        progressScout.setVisibility(View.INVISIBLE);
        progressFighter.setVisibility(View.INVISIBLE);
        progressBomber.setVisibility(View.INVISIBLE);
        progressLaserCruiser.setVisibility(View.INVISIBLE);
        progressBattleShip.setVisibility(View.INVISIBLE);
        progressSpaceStation.setVisibility(View.INVISIBLE);

        cancelResourceCollector.setVisibility(View.INVISIBLE);
        cancelScout.setVisibility(View.INVISIBLE);
        cancelFighter.setVisibility(View.INVISIBLE);
        cancelBomber.setVisibility(View.INVISIBLE);
        cancelLaserCruiser.setVisibility(View.INVISIBLE);
        cancelBattleShip.setVisibility(View.INVISIBLE);
        cancelSpaceStation.setVisibility(View.INVISIBLE);
    }

    //Opens normal menu
    public void normalMenu(View view) {
        stop.setVisibility(View.INVISIBLE);
        move.setVisibility(View.INVISIBLE);
        attack.setVisibility(View.INVISIBLE);
        destroy.setVisibility(View.INVISIBLE);
        shipMode.setVisibility(View.INVISIBLE);
        follow.setVisibility(View.INVISIBLE);

        normal.setVisibility(View.INVISIBLE);
        special.setVisibility(View.VISIBLE);
        dockedShips.setVisibility(View.INVISIBLE);
        buildShips.setVisibility(View.INVISIBLE);

        harvest.setVisibility(View.VISIBLE);
        dock.setVisibility(View.VISIBLE);
        dockMenu.setVisibility(View.VISIBLE);
        buildMenu.setVisibility(View.VISIBLE);
        scoutMode.setVisibility(View.VISIBLE);

        resourceCollector.setVisibility(View.INVISIBLE);
        scout.setVisibility(View.INVISIBLE);
        fighter.setVisibility(View.INVISIBLE);
        bomber.setVisibility(View.INVISIBLE);

        numResourceCollectors.setVisibility(View.INVISIBLE);
        numScouts.setVisibility(View.INVISIBLE);
        numFighters.setVisibility(View.INVISIBLE);
        numBombers.setVisibility(View.INVISIBLE);

        buildSpaceStation.setVisibility(View.INVISIBLE);
        buildBattleShip.setVisibility(View.INVISIBLE);
        buildLaserCruiser.setVisibility(View.INVISIBLE);
        buildBomber.setVisibility(View.INVISIBLE);
        buildFighter.setVisibility(View.INVISIBLE);
        buildScout.setVisibility(View.INVISIBLE);
        buildResourceCollector.setVisibility(View.INVISIBLE);

        costResourceCollector.setVisibility(View.INVISIBLE);
        costScout.setVisibility(View.INVISIBLE);
        costFighter.setVisibility(View.INVISIBLE);
        costBomber.setVisibility(View.INVISIBLE);
        costLaserCruiser.setVisibility(View.INVISIBLE);
        costBattleShip.setVisibility(View.INVISIBLE);
        costSpaceStation.setVisibility(View.INVISIBLE);

        progressResourceCollector.setVisibility(View.INVISIBLE);
        progressScout.setVisibility(View.INVISIBLE);
        progressFighter.setVisibility(View.INVISIBLE);
        progressBomber.setVisibility(View.INVISIBLE);
        progressLaserCruiser.setVisibility(View.INVISIBLE);
        progressBattleShip.setVisibility(View.INVISIBLE);
        progressSpaceStation.setVisibility(View.INVISIBLE);

        cancelResourceCollector.setVisibility(View.INVISIBLE);
        cancelScout.setVisibility(View.INVISIBLE);
        cancelFighter.setVisibility(View.INVISIBLE);
        cancelBomber.setVisibility(View.INVISIBLE);
        cancelLaserCruiser.setVisibility(View.INVISIBLE);
        cancelBattleShip.setVisibility(View.INVISIBLE);
        cancelSpaceStation.setVisibility(View.INVISIBLE);
    }

    public void buildMenu(View view) {
        if (checkIfOneFlagship() == null) {
            return;
        }
        buildShips.setVisibility(View.VISIBLE);

        harvest.setVisibility(View.INVISIBLE);
        dock.setVisibility(View.INVISIBLE);
        dockMenu.setVisibility(View.INVISIBLE);
        buildMenu.setVisibility(View.INVISIBLE);
        scoutMode.setVisibility(View.INVISIBLE);
        special.setVisibility(View.INVISIBLE);

        buildSpaceStation.setVisibility(View.VISIBLE);
        buildBattleShip.setVisibility(View.VISIBLE);
        buildLaserCruiser.setVisibility(View.VISIBLE);
        buildBomber.setVisibility(View.VISIBLE);
        buildFighter.setVisibility(View.VISIBLE);
        buildScout.setVisibility(View.VISIBLE);
        buildResourceCollector.setVisibility(View.VISIBLE);

        costResourceCollector.setVisibility(View.VISIBLE);
        costScout.setVisibility(View.VISIBLE);
        costFighter.setVisibility(View.VISIBLE);
        costBomber.setVisibility(View.VISIBLE);
        costLaserCruiser.setVisibility(View.VISIBLE);
        costBattleShip.setVisibility(View.VISIBLE);
        costSpaceStation.setVisibility(View.VISIBLE);

        progressResourceCollector.setVisibility(View.VISIBLE);
        progressScout.setVisibility(View.VISIBLE);
        progressFighter.setVisibility(View.VISIBLE);
        progressBomber.setVisibility(View.VISIBLE);
        progressLaserCruiser.setVisibility(View.VISIBLE);
        progressBattleShip.setVisibility(View.VISIBLE);
        progressSpaceStation.setVisibility(View.VISIBLE);

        cancelResourceCollector.setVisibility(View.VISIBLE);
        cancelScout.setVisibility(View.VISIBLE);
        cancelFighter.setVisibility(View.VISIBLE);
        cancelBomber.setVisibility(View.VISIBLE);
        cancelLaserCruiser.setVisibility(View.VISIBLE);
        cancelBattleShip.setVisibility(View.VISIBLE);
        cancelSpaceStation.setVisibility(View.VISIBLE);
    }

    public void scoutMode(View view) {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof Scout) {
                ((Scout) selectShips.get(i)).scout();
            }
        }
    }

    public void formationButton(View view) {
        shipBar(false);
        formationBar(true);
        startSelection = false;
        select.setBackgroundTintList(fabColor);
    }

    public void formationBar(boolean hiddenOrNot) {
        if (hiddenOrNot) {
            numFormations.setText("Number of Formations: " + GameScreen.formationsTeam1.size());

            formationBar.setVisibility(View.VISIBLE);
            currentFormations.setVisibility(View.VISIBLE);
            buildFormation.setVisibility(View.INVISIBLE);

            numFormations.setVisibility(View.VISIBLE);
            nextFormation.setVisibility(View.VISIBLE);
            controlFormation.setVisibility(View.VISIBLE);
            disbandFormation.setVisibility(View.VISIBLE);

        } else {
            formationBar.setVisibility(View.INVISIBLE);
            currentFormations.setVisibility(View.INVISIBLE);
            buildFormation.setVisibility(View.INVISIBLE);
            numFormations.setVisibility(View.INVISIBLE);
            nextFormation.setVisibility(View.INVISIBLE);
            disbandFormation.setVisibility(View.INVISIBLE);
            controlFormation.setVisibility(View.INVISIBLE);

        }
        rectangleFormation.setVisibility(View.INVISIBLE);
        vFormation.setVisibility(View.INVISIBLE);
        circleFormation.setVisibility(View.INVISIBLE);
        customFormation.setVisibility(View.INVISIBLE);
    }

    public void currentFormations(View view) {
        if (selectShips.size() <= 1) {
            return;
        }
        currentFormations.setVisibility(View.INVISIBLE);
        buildFormation.setVisibility(View.VISIBLE);

        rectangleFormation.setVisibility(View.VISIBLE);
        vFormation.setVisibility(View.VISIBLE);
        circleFormation.setVisibility(View.VISIBLE);
        customFormation.setVisibility(View.VISIBLE);

        numFormations.setVisibility(View.INVISIBLE);
        nextFormation.setVisibility(View.INVISIBLE);
        disbandFormation.setVisibility(View.INVISIBLE);
        controlFormation.setVisibility(View.INVISIBLE);
    }

    public void buildFormation(View view) {
        buildFormation.setVisibility(View.INVISIBLE);
        currentFormations.setVisibility(View.VISIBLE);

        numFormations.setVisibility(View.VISIBLE);
        nextFormation.setVisibility(View.VISIBLE);
        disbandFormation.setVisibility(View.VISIBLE);
        controlFormation.setVisibility(View.VISIBLE);

        rectangleFormation.setVisibility(View.INVISIBLE);
        vFormation.setVisibility(View.INVISIBLE);
        circleFormation.setVisibility(View.INVISIBLE);
        customFormation.setVisibility(View.INVISIBLE);
    }

    public void setNextFormation(View view) {
        if (GameScreen.formationsTeam1.size() == 0) {
            return;
        }
        for (int i = 0; i < GameScreen.formationsTeam1.size(); i++) {
            if (formationSelected == i) {
                clearSelectionReferences();
                GameScreen.groupSelect(GameScreen.formationsTeam1.get(i).ships);
                followShip(null);
            }
        }
        formationSelected++;
        if (formationSelected == GameScreen.formationsTeam1.size()) {
            formationSelected = 0;
        }
    }

    public void controlFormation(View view) {
        if (selectShips.size() == 0) {
            return;
        }
        shipBar(true);
        formationBar(false);
    }

    public void disbandFormation(View view){
        Formation formation = isFormationSelected();
        if (formation != null){
            formation.disbandFormation();
            if (GameScreen.formationsTeam1.contains(formation)){
                GameScreen.formationsTeam1.remove(formation);
            } else if (GameScreen.formationsTeam2.contains(formation)){
                GameScreen.formationsTeam2.remove(formation);
            } else if (GameScreen.formationsTeam3.contains(formation)){
                GameScreen.formationsTeam3.remove(formation);
            } else GameScreen.formationsTeam4.remove(formation);
        }
    }

    public void setRectangleFormation(View view) {
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.RECTANGLE_FORMATION));
    }

    public void setVFormation(View view) {
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.V_FORMATION));
    }

    public void setCircleFormation(View view) {
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.CIRCLE_FORMATION));
    }

    public void setCustomFormation(View view) {
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.CUSTOM_FORMATION));
    }

    public FlagShip checkIfOneFlagship() {
        int flagShipCount = 0;
        FlagShip flagShip = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof FlagShip) {
                flagShipCount++;
                flagShip = (FlagShip) selectShips.get(i);
            }
        }
        if (flagShipCount == 1) {
            return flagShip;
        }
        return null;
    }

    public static Formation isFormationSelected() {
        Formation formation = null;
        if (selectShips.size() > 0) {
            int counter = 0;
            for (int i = 0; i <= selectShips.size() - 1; i++) {
                if (selectShips.get(i).formation != null) {
                    formation = selectShips.get(i).formation;
                    counter++;
                    continue;
                }
                if (selectShips.get(i).formation != formation) {
                    return null;
                }
                counter++;
            }
            if (formation != null) {
                if (counter != formation.ships.size()) {
                    formation = null;
                }
            }
        }
        return formation;
    }

    public static boolean isAllMovable() {
        if (selectShips.size() == 0) {
            return false;
        }
        boolean allMovable = true;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (!selectShips.get(i).movable) {
                allMovable = false;
            }
        }
        return allMovable;
    }

    //Sets all buttons background colours to white
    public void clearButtonsToWhite() {
        move.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        attack.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
    }

    public void deployResourceCollector(View view) {
        SpaceStation spaceStation = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                spaceStation = (SpaceStation) selectShips.get(i);
            }
        }
        if (spaceStation != null) {
            spaceStation.deployShip("ResourceCollector");
            countDockedShips(spaceStation);
        }
    }

    public void deployScout(View view) {
        SpaceStation spaceStation = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                spaceStation = (SpaceStation) selectShips.get(i);
            }
        }
        if (spaceStation != null) {
            spaceStation.deployShip("Scout");
            countDockedShips(spaceStation);
        }
    }

    public void deployFighter(View view) {
        SpaceStation spaceStation = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                spaceStation = (SpaceStation) selectShips.get(i);
            }
        }
        if (spaceStation != null) {
            spaceStation.deployShip("Fighter");
            countDockedShips(spaceStation);
        }
    }

    public void deployBomber(View view) {
        SpaceStation spaceStation = null;
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                spaceStation = (SpaceStation) selectShips.get(i);
            }
        }
        if (spaceStation != null) {
            spaceStation.deployShip("Bomber");
            countDockedShips(spaceStation);
        }
    }

    public void buildSpaceStation(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingSpaceStation = true;
        flagShip.countSpaceStation++;
    }

    public void buildBattleShip(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingBattleShip = true;
        flagShip.countBattleShip++;
    }

    public void buildLaserCruiser(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingLaserCruiser = true;
        flagShip.countLaserCruiser++;
    }

    public void buildBomber(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingBomber = true;
        flagShip.countBomber++;
    }

    public void buildFighter(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingFighter = true;
        flagShip.countFighter++;
    }

    public void buildScout(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingScout = true;
        flagShip.countScout++;
    }

    public void buildResourceCollector(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        flagShip.buildingResourceCollector = true;
        flagShip.countResourceCollector++;
    }

    public void cancelSpaceStation(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countSpaceStation <= 1){
            flagShip.stopBuilding("SpaceStation");
            flagShip.countSpaceStation = 0;
        } else {
            flagShip.countSpaceStation--;
        }
    }

    public void cancelBattleShip(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countBattleShip <= 1){
            flagShip.stopBuilding("BattleShip");
            flagShip.countBattleShip = 0;
        } else {
            flagShip.countBattleShip--;
        }
    }

    public void cancelLaserCruiser(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countLaserCruiser <= 1){
            flagShip.stopBuilding("LaserCruiser");
            flagShip.countLaserCruiser = 0;
        } else {
            flagShip.countLaserCruiser--;
        }
    }

    public void cancelBomber(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countBomber <= 1){
            flagShip.stopBuilding("Bomber");
            flagShip.countBomber = 0;
        } else {
            flagShip.countBomber--;
        }
    }

    public void cancelFighter(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countFighter <= 1){
            flagShip.stopBuilding("Fighter");
            flagShip.countFighter = 0;
        } else {
            flagShip.countFighter--;
        }
    }

    public void cancelScout(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countScout <= 1){
            flagShip.stopBuilding("Scout");
            flagShip.countScout = 0;
        } else {
            flagShip.countScout--;
        }
    }

    public void cancelResourceCollector(View view) {
        FlagShip flagShip = checkIfOneFlagship();
        if (flagShip == null) {
            return;
        }
        if (flagShip.countResourceCollector <= 1){
            flagShip.stopBuilding("ResourceCollector");
            flagShip.countResourceCollector = 0;
        } else {
            flagShip.countResourceCollector--;
        }
    }
}