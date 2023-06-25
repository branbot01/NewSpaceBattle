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
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.WindowManager.LayoutParams.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newspacebattle.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//Main class handles ui elements like the menus and buttons
public class Main extends AppCompatActivity {

    static int screenX, screenY, movedX, movedY;
    static boolean pressed, startSelection, selection, startAttack, following, shipBehave, loaded;
    static ArrayList<Ship> selectShips = new ArrayList<>(), enemySelect = new ArrayList<>();
    static int uiOptions;
    static Handler refresh = new Handler(), startUp = new Handler(), selectionChecker = new Handler();
    static ColorStateList fabColor;
    static Collisions collisions;
    ProgressBar loadingBar;
    FloatingActionButton move, stop, destroy, select, attack, shipMode, follow, harvest, dock, dockMenu, formation;
    Button special, normal;
    GameScreen gameScreen;
    TextView resourceCount;
    MediaPlayer rickRoll;
    Thread loader = new Thread(new Runnable() {
        @Override
        public void run() {
            gameScreen = new GameScreen(getApplicationContext());
            loaded = true;
            rickRoll.start();
        }
    });
    View decorView, gameView, bar;

    private ScaleGestureDetector mScaleDetector;

    private float mScaleFactor = 0.05f;

    boolean zooming = false;

    //Stops selected ships from moving, stops completely
    public static void stopMovement() {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).destination = false;
            selectShips.get(i).movable = false;
            if (selectShips.get(i) instanceof ResourceCollector) {
                ((ResourceCollector) selectShips.get(i)).harvesting = false;
                ((ResourceCollector) selectShips.get(i)).unloading = false;
            }
            selectShips.get(i).stop();
        }
    }

    //Gets behaviour from selected ships
    public static boolean getBehaviour() {
        int[] behaviourTally = new int[2];

        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (!selectShips.get(i).behaviour) {
                behaviourTally[0]++;
            } else {
                behaviourTally[1]++;
            }
        }

        if (behaviourTally[0] > behaviourTally[1]) {
            shipBehave = false;
            return false;
        } else {
            shipBehave = true;
            return true;
        }
    }

    //Clears any ships that are selected
    public static void clearSelectionReferences() {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).selected = false;
        }
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            GameScreen.ships.get(i).attSelected = false;
        }
        selectShips.clear();
        enemySelect.clear();
    }

    // Shoot button for ships
    public void shoot(View view) {
        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).shoot();
        }
    }

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

        rickRoll = MediaPlayer.create(this, R.raw.music);

        setContentView(R.layout.game_screen);
        gameView = findViewById(R.id.game_screen);
        setContentView(R.layout.title_screen);
    }

    //When the app is resumed
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(uiOptions);
        GameScreen.paused = false;
    }

    //If app is left running
    protected void onPause() {
        super.onPause();
        GameScreen.paused = true;
    }

    //If user hits back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

            loader.start();
            loadingBar = findViewById(R.id.loading);
            loadingBar.setVisibility(View.VISIBLE);
            showGameScreen();
        }
    }

    //method runs when loading finishes
    public void showGameScreen() {
        startUp.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loaded) {
                    mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
                    setContentView(gameScreen);
                    addContentView(gameView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
                    collisions = new Collisions();
                    gameScreen.gameLoop();
                    UILoop();
                    loadingBar.setVisibility(View.INVISIBLE);
                    findIds();
                } else {
                    showGameScreen();
                }
            }
        }, 16);
    }

    //Loop updating ui elements
    public void UILoop() {
        selectionChecker.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (selectShips.size() == 0) {
                    shipBar(false);
                }
                resourceCount.setText("Resources: " + GameScreen.resources[0]);
                System.out.println("Resources: " + GameScreen.resources[0]);
                UILoop();
            }
        }, 16);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.05f, Math.min(mScaleFactor, 0.15f));
            return true;
        }
    }

    //When user touches the screen, not any buttons
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScaleDetector == null) {
            return true;
        }
        int x = (int) (event.getX()), y = (int) (event.getY()), eventAction = event.getAction();
        float trueX = (x + GameScreen.offsetX) / GameScreen.scaleX;
        float trueY = (y + GameScreen.offsetY) / GameScreen.scaleY;

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
//                    GameScreen.startAttX = trueX;
//                    GameScreen.startAttY = trueY;
                    GameScreen.endAttX = trueX;
                    GameScreen.endAttY = trueY;
                    shipBar(false);
                    for (int i = 0; i <= Main.selectShips.size() - 1; i++) {
                        Main.selectShips.get(i).destinationFinder.runAttack(GameScreen.attackSelect(GameScreen.startAttX, GameScreen.startAttY, GameScreen.endAttX, GameScreen.endAttY));
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

                        for (int i = 0; i <= selectShips.size() - 1; i++) {
                            if (selectShips.get(i).movable && !(selectShips.get(i) instanceof SpaceStation)) {
                                selectShips.get(i).setDestination(trueX, trueY, false);
                            }
                        }

                        clearSelectionReferences();
                        following = false;
                        shipBar(false);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 2 || zooming) {
                            break;
                        }
                        following = false;
                        clearSelectionReferences();
                        if (movedX - x < -16 * screenX) {
                            GameScreen.offsetX = -16 * screenX;
                        } else if (movedX - x > 15 * screenX) {
                            GameScreen.offsetX = 15 * screenX;
                        } else {
                            GameScreen.offsetX = movedX - x;
                        }

                        if (movedY - y < -16 * screenY) {
                            GameScreen.offsetY = -16 * screenY;
                        } else if (movedY - y > 15 * screenY) {
                            GameScreen.offsetY = 15 * screenY;
                        } else {
                            GameScreen.offsetY = movedY - y;
                        }
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

    //Get references for all of the buttons and ui elements
    public void findIds() {
        bar = findViewById(R.id.bottomBar);
        move = findViewById(R.id.moveButton);
        stop = findViewById(R.id.stopButton);
        destroy = findViewById(R.id.destroyButton);
        select = findViewById(R.id.select);
        attack = findViewById(R.id.attackButton);
        shipMode = findViewById(R.id.shipModeButton);
        follow = findViewById(R.id.followButton);
        special = findViewById(R.id.specialButton);
        normal = findViewById(R.id.normalButton);
        harvest = findViewById(R.id.harvestButton);
        dock = findViewById(R.id.dockButton);
        dockMenu = findViewById(R.id.dockMenuButton);
        formation = findViewById(R.id.shootButton);
        fabColor = select.getBackgroundTintList();
        resourceCount = findViewById(R.id.resourcesText);
    }

    //Either hides or shows the ship options bar depending if any ships are selected
    public void shipBar(boolean hiddenOrNot) {
        if (!getBehaviour()) {
            shipMode.setImageResource(R.drawable.ic_aggressive);
        } else {
            shipMode.setImageResource(R.drawable.ic_evasive);
        }

        if (!following) {
            follow.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            follow.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        }

        if (hiddenOrNot) {
            bar.setVisibility(View.VISIBLE);
            move.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            attack.setVisibility(View.VISIBLE);
            formation.setVisibility(View.VISIBLE);
            destroy.setVisibility(View.VISIBLE);
            shipMode.setVisibility(View.VISIBLE);
            follow.setVisibility(View.VISIBLE);
            normal.setVisibility(View.VISIBLE);

            special.setVisibility(View.INVISIBLE);
            harvest.setVisibility(View.INVISIBLE);
            dock.setVisibility(View.INVISIBLE);
            dockMenu.setVisibility(View.INVISIBLE);
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
            dockMenu.setVisibility(View.INVISIBLE);
            formation.setVisibility(View.INVISIBLE);

            clearButtonsToWhite();
        }
    }

    //When the move button is pressed, sets selected ships to movable
    public void moveShip(View view) {
        clearButtonsToWhite();
        move.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        startAttack = false;

        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).movable = true;
        }
    }

    //Stops all currently selected ships
    public void stopShip(View view) {
        following = false;
        stopMovement();
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
        } else {
            attack.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            startAttack = false;
        }
    }

    //Changes ship behaviour
    public void shipMode(View view) {
        clearButtonsToWhite();

        for (int i = 0; i <= selectShips.size() - 1; i++) {
            selectShips.get(i).behaviour = !shipBehave;
        }

        if (getBehaviour()) {
            shipMode.setImageResource(R.drawable.ic_evasive);
        } else {
            shipMode.setImageResource(R.drawable.ic_aggressive);
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

        if (!startSelection) {
            select.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
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

        harvest.setVisibility(View.INVISIBLE);
        dock.setVisibility(View.INVISIBLE);
        dockMenu.setVisibility(View.INVISIBLE);
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

        harvest.setVisibility(View.VISIBLE);
        dock.setVisibility(View.VISIBLE);
        dockMenu.setVisibility(View.VISIBLE);
    }

    //Sets all buttons background colours to white
    public void clearButtonsToWhite() {
        move.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        attack.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
    }
}