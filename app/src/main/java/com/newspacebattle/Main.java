package com.newspacebattle;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.*;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.WindowManager.LayoutParams.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newspacebattle.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.processphoenix.ProcessPhoenix;

//Main class handles ui elements like the menus and buttons
public class Main extends AppCompatActivity{

    static int screenX, screenY, movedX, movedY, formationSelected, guidePage;
    static float miniX, miniY;
    static boolean pressed, startSelection, selection, startAttack, following, loaded, minimapOn, restart, victoryDefeat_pressed, guideActive;
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
    Button play, guide, exit, backButton;
    Button difficulty_easy, difficulty_medium, difficulty_hard, enemy_1, enemy_2, enemy_3, small_map, medium_map, large_map;
    SeekBar initialResourcesBar;
    TextView resourceBarDisplay;
    CheckBox blackholeCheck, botsOnlyCheck;
    Button playGame;
    View titleBackground, logo, gamemodeBackground, classicButton, annihilationButton, parameterBackground;
    TextView gamemodeTitle, gamemode_classicTitle, gamemode_annihilationTitle, gamemodeClassicExplanation, gamemodeAnnihilationExplanation;
    TextView parameterTitle, difficulty, enemies, galaxysize, resources, blackhole, bots;
    TextView guideShipButton, guideFormationsButton, guideMiscellaneousButton, guideMenusButton;
    TextView guideResourceCollectorButton, guideScoutButton, guideFighterButton, guideBomberButton, guideLaserCruiserButton, guideBattleShipButton, guideFlagShipButton, guideSpaceStationButton;
    TextView guideRectangleFormationButton, guideVFormationButton, guideCircleFormationButton, guideCustomFormationButton;
    TextView guideResourcesButton, guideSensorsButton, guideStarMapButton, guideBlackHoleButton;
    TextView guideNormalMenuButton, guideSpecialMenuButton, guideCurrentFormationsMenuButton;
    TextView guideMoveButton, guideStopButton, guideTargetButton, guideAutoAttackButton, guideFollowButton, guideSalvageButton, guideMenuButton;
    TextView guideAutoScoutButton, guideGetResourcesButton, guideDockButton, guideDockedShipsMenuButton;
    TextView guideBackToNormalButton, guideNextFormationButton, guideDisbandFormationButton;
    TextView guideDescription;
    View guideImage, guideBackground, guideImageWhiteCircle;
    GameScreen gameScreen;
    TextView resourceCount, numResourceCollectors, numScouts, numFighters, numBombers;
    TextView costResourceCollector, costScout, costFighter, costBomber, costLaserCruiser, costBattleShip, costSpaceStation;
    TextView numFormations, team1Blackboard;
    ProgressBar progressResourceCollector, progressScout, progressFighter, progressBomber, progressLaserCruiser, progressBattleShip, progressSpaceStation;
    View decorView, gameView, bar, formationBar, guideView, victoryDefeat;
    MediaPlayer music;

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
        getGuideView();
        setTitleScreen();

        music = MediaPlayer.create(getApplicationContext(), R.raw.space_battle_menu_music);
        music.setLooping(true);
        music.start();
    }

    public void getGuideView(){
        setContentView(R.layout.guide);
        guideView = findViewById(R.id.guide);
    }

    public void setTitleScreen(){
        setContentView(R.layout.title_screen);
        titleBackground = findViewById(R.id.gametitleBackground);
        titleBackground.animate().rotationBy(-360).setDuration(3000).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                titleBackground.animate().rotationBy(-360).setDuration(3000).setInterpolator(new LinearInterpolator()).setListener(this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // do nothing
            }
        }).start();

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
        if (music != null) {
            music.start();
        }
    }

    //If app is left running
    protected void onPause() {
        super.onPause();
        GameScreen.paused = true;
        if (music != null) {
            music.pause();
        }
    }

    public void pauseButton(View view) {
        if (!GameScreen.paused) {
            GameScreen.paused = true;

            select.setVisibility(View.INVISIBLE);
            formation.setVisibility(View.INVISIBLE);
            buildMenu.setVisibility(View.INVISIBLE);
            minimap.setVisibility(View.INVISIBLE);
            shipBar(false);
            formationBar(false);
            buildBar(false);
            clearSelectionReferences();

            pause.setImageResource(R.drawable.ic_resumebutton);

            bar.setVisibility(View.VISIBLE);
            quitButton.setVisibility(View.VISIBLE);
            guideButton.setVisibility(View.VISIBLE);
        } else {
            GameScreen.paused = false;

            select.setVisibility(View.VISIBLE);
            formation.setVisibility(View.VISIBLE);
            buildMenu.setVisibility(View.VISIBLE);
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
        if (!guideActive){
            guideActive = true;
        } else {
            return;
        }

        guidePage = 0;

        if (!loaded){
            Button play = findViewById(R.id.play);
            Button guide = findViewById(R.id.guide);
            Button exit = findViewById(R.id.exit);
            View gameTitle = findViewById(R.id.gametitleBackground);
            play.setVisibility(View.INVISIBLE);
            guide.setVisibility(View.INVISIBLE);
            exit.setVisibility(View.INVISIBLE);
            gameTitle.setVisibility(View.INVISIBLE);
            logo = findViewById(R.id.logo);
            logo.setVisibility(View.INVISIBLE);
        }
        addContentView(guideView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));

        guideBackground = findViewById(R.id.guideBackground);

        guideShipButton = findViewById(R.id.guideShipButton);
        guideFormationsButton = findViewById(R.id.guideFormationButton);
        guideMiscellaneousButton = findViewById(R.id.guideMiscellaneousButton);
        guideMenusButton = findViewById(R.id.guideMenusButton);

        guideResourceCollectorButton = findViewById(R.id.guideResourceCollectorButton);
        guideScoutButton = findViewById(R.id.guideScoutButton);
        guideFighterButton = findViewById(R.id.guideFighterButton);
        guideBomberButton = findViewById(R.id.guideBomberButton);
        guideLaserCruiserButton = findViewById(R.id.guideLaserCruiserButton);
        guideBattleShipButton = findViewById(R.id.guideBattleShipButton);
        guideFlagShipButton = findViewById(R.id.guideFlagShipButton);
        guideSpaceStationButton = findViewById(R.id.guideSpaceStationButton);

        guideRectangleFormationButton = findViewById(R.id.guideRectangleFormationButton);
        guideVFormationButton = findViewById(R.id.guideVFormationButton);
        guideCircleFormationButton = findViewById(R.id.guideCircleFormationButton);
        guideCustomFormationButton = findViewById(R.id.guideCustomFormationButton);

        guideResourcesButton = findViewById(R.id.guideResourcesButton);
        guideSensorsButton = findViewById(R.id.guideSensorsButton);
        guideStarMapButton = findViewById(R.id.guideStarMapButton);
        guideBlackHoleButton = findViewById(R.id.guideBlackHoleButton);

        guideNormalMenuButton = findViewById(R.id.guideNormalMenuButton);
        guideSpecialMenuButton = findViewById(R.id.guideSpecialMenuButton);
        guideCurrentFormationsMenuButton = findViewById(R.id.guideCurrentFormationsMenuButton);

        guideMoveButton = findViewById(R.id.guideMoveButton);
        guideStopButton = findViewById(R.id.guideStopButton);
        guideTargetButton = findViewById(R.id.guideTargetButton);
        guideAutoAttackButton = findViewById(R.id.guideAutoAttackButton);
        guideFollowButton = findViewById(R.id.guideFollowButton);
        guideSalvageButton = findViewById(R.id.guideSalvageButton);
        guideMenuButton = findViewById(R.id.guideMenuButton);

        guideAutoScoutButton = findViewById(R.id.guideAutoScoutButton);
        guideGetResourcesButton = findViewById(R.id.guideGetResourcesButton);
        guideDockButton = findViewById(R.id.guideDockButton);
        guideDockedShipsMenuButton = findViewById(R.id.guideDockedShipsMenuButton);

        guideBackToNormalButton = findViewById(R.id.guideBackToNormalButton);
        guideNextFormationButton = findViewById(R.id.guideNextFormationButton);
        guideDisbandFormationButton = findViewById(R.id.guideDisbandFormationButton);

        guideDescription = findViewById(R.id.guideDescription);
        guideImage = findViewById(R.id.guideImage);
        guideImageWhiteCircle = findViewById(R.id.guideImageWhiteCircle);

        toggleOuterGuide(true);
    }

    public void guideBackButton(View view){
        guideImage.setRotation(0);
        if (guideShipButton.getVisibility() == View.VISIBLE){
            back_button(null);
            guideActive = false;
        } else if (guideResourceCollectorButton.getVisibility() == View.VISIBLE){
            toggleShipsGuide(false);
            toggleOuterGuide(true);
        } else if (guideRectangleFormationButton.getVisibility() == View.VISIBLE){
            toggleFormationsGuide(false);
            toggleOuterGuide(true);
        } else if (guideResourcesButton.getVisibility() == View.VISIBLE){
            toggleMiscellaneousGuide(false);
            toggleOuterGuide(true);
        } else if (guideNormalMenuButton.getVisibility() == View.VISIBLE){
            toggleMenusGuide(false);
            toggleOuterGuide(true);
        } else if (guideMoveButton.getVisibility() == View.VISIBLE){
            toggleNormalMenuGuide(false);
            toggleMenusGuide(true);
        } else if (guideAutoScoutButton.getVisibility() == View.VISIBLE){
            toggleSpecialMenuGuide(false);
            toggleMenusGuide(true);
        } else if (guideBackToNormalButton.getVisibility() == View.VISIBLE){
            toggleCurrentFormationsGuide(false);
            toggleMenusGuide(true);
        } else if (guidePage == 1) {
            toggleShipsGuide(true);
            toggleGuideEntry(false);
        } else if (guidePage == 2) {
            toggleFormationsGuide(true);
            toggleGuideEntry(false);
        } else if (guidePage == 3) {
            toggleMiscellaneousGuide(true);
            toggleGuideEntry(false);
        } else if (guidePage == 4) {
            toggleNormalMenuGuide(true);
            toggleGuideEntry(false);
        } else if (guidePage == 5) {
            toggleSpecialMenuGuide(true);
            toggleGuideEntry(false);
        } else if (guidePage == 6) {
            toggleCurrentFormationsGuide(true);
            toggleGuideEntry(false);
        }
    }

    public void toggleGuideEntry(boolean visible){
        if (visible){
            guideDescription.setVisibility(View.VISIBLE);
            guideImage.setVisibility(View.VISIBLE);
            guideBackground.setBackgroundResource(R.drawable.ic_guidemenubackground2);
        } else {
            guideDescription.setVisibility(View.INVISIBLE);
            guideImage.setVisibility(View.INVISIBLE);
            guideBackground.setBackgroundResource(R.drawable.ic_guidemenubackground1);
            guideImageWhiteCircle.setVisibility(View.INVISIBLE);
            guideImage.setZ(0);
        }
    }

    public void toggleOuterGuide(boolean visible){
        if (visible){
            guideShipButton.setVisibility(View.VISIBLE);
            guideFormationsButton.setVisibility(View.VISIBLE);
            guideMiscellaneousButton.setVisibility(View.VISIBLE);
            guideMenusButton.setVisibility(View.VISIBLE);
        } else {
            guideShipButton.setVisibility(View.INVISIBLE);
            guideFormationsButton.setVisibility(View.INVISIBLE);
            guideMiscellaneousButton.setVisibility(View.INVISIBLE);
            guideMenusButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleShipsGuide(boolean visible){
        if (visible){
            guideResourceCollectorButton.setVisibility(View.VISIBLE);
            guideScoutButton.setVisibility(View.VISIBLE);
            guideFighterButton.setVisibility(View.VISIBLE);
            guideBomberButton.setVisibility(View.VISIBLE);
            guideLaserCruiserButton.setVisibility(View.VISIBLE);
            guideBattleShipButton.setVisibility(View.VISIBLE);
            guideFlagShipButton.setVisibility(View.VISIBLE);
            guideSpaceStationButton.setVisibility(View.VISIBLE);
        } else {
            guideResourceCollectorButton.setVisibility(View.INVISIBLE);
            guideScoutButton.setVisibility(View.INVISIBLE);
            guideFighterButton.setVisibility(View.INVISIBLE);
            guideBomberButton.setVisibility(View.INVISIBLE);
            guideLaserCruiserButton.setVisibility(View.INVISIBLE);
            guideBattleShipButton.setVisibility(View.INVISIBLE);
            guideFlagShipButton.setVisibility(View.INVISIBLE);
            guideSpaceStationButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleFormationsGuide(boolean visible){
        if (visible){
            guideRectangleFormationButton.setVisibility(View.VISIBLE);
            guideVFormationButton.setVisibility(View.VISIBLE);
            guideCircleFormationButton.setVisibility(View.VISIBLE);
            guideCustomFormationButton.setVisibility(View.VISIBLE);
        } else {
            guideRectangleFormationButton.setVisibility(View.INVISIBLE);
            guideVFormationButton.setVisibility(View.INVISIBLE);
            guideCircleFormationButton.setVisibility(View.INVISIBLE);
            guideCustomFormationButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleMiscellaneousGuide(boolean visible){
        if (visible){
            guideResourcesButton.setVisibility(View.VISIBLE);
            guideSensorsButton.setVisibility(View.VISIBLE);
            guideStarMapButton.setVisibility(View.VISIBLE);
            guideBlackHoleButton.setVisibility(View.VISIBLE);
        } else {
            guideResourcesButton.setVisibility(View.INVISIBLE);
            guideSensorsButton.setVisibility(View.INVISIBLE);
            guideStarMapButton.setVisibility(View.INVISIBLE);
            guideBlackHoleButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleMenusGuide(boolean visible){
        if (visible){
            guideNormalMenuButton.setVisibility(View.VISIBLE);
            guideSpecialMenuButton.setVisibility(View.VISIBLE);
            guideCurrentFormationsMenuButton.setVisibility(View.VISIBLE);
        } else {
            guideNormalMenuButton.setVisibility(View.INVISIBLE);
            guideSpecialMenuButton.setVisibility(View.INVISIBLE);
            guideCurrentFormationsMenuButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleNormalMenuGuide(boolean visible){
        if (visible){
            guideMoveButton.setVisibility(View.VISIBLE);
            guideStopButton.setVisibility(View.VISIBLE);
            guideTargetButton.setVisibility(View.VISIBLE);
            guideAutoAttackButton.setVisibility(View.VISIBLE);
            guideFollowButton.setVisibility(View.VISIBLE);
            guideSalvageButton.setVisibility(View.VISIBLE);
            guideMenuButton.setVisibility(View.VISIBLE);
        } else {
            guideMoveButton.setVisibility(View.INVISIBLE);
            guideStopButton.setVisibility(View.INVISIBLE);
            guideTargetButton.setVisibility(View.INVISIBLE);
            guideAutoAttackButton.setVisibility(View.INVISIBLE);
            guideFollowButton.setVisibility(View.INVISIBLE);
            guideSalvageButton.setVisibility(View.INVISIBLE);
            guideMenuButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleSpecialMenuGuide(boolean visible){
        if (visible){
            guideAutoScoutButton.setVisibility(View.VISIBLE);
            guideGetResourcesButton.setVisibility(View.VISIBLE);
            guideDockButton.setVisibility(View.VISIBLE);
            guideDockedShipsMenuButton.setVisibility(View.VISIBLE);
        } else {
            guideAutoScoutButton.setVisibility(View.INVISIBLE);
            guideGetResourcesButton.setVisibility(View.INVISIBLE);
            guideDockButton.setVisibility(View.INVISIBLE);
            guideDockedShipsMenuButton.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleCurrentFormationsGuide(boolean visible){
        if (visible){
            guideBackToNormalButton.setVisibility(View.VISIBLE);
            guideNextFormationButton.setVisibility(View.VISIBLE);
            guideDisbandFormationButton.setVisibility(View.VISIBLE);
        } else {
            guideBackToNormalButton.setVisibility(View.INVISIBLE);
            guideNextFormationButton.setVisibility(View.INVISIBLE);
            guideDisbandFormationButton.setVisibility(View.INVISIBLE);
        }
    }

    public void guideShipsButton(View view){
        toggleOuterGuide(false);
        toggleShipsGuide(true);
    }

    public void guideFormationsButton(View view){
        toggleOuterGuide(false);
        toggleFormationsGuide(true);
    }

    public void guideMiscellaneousButton(View view){
        toggleOuterGuide(false);
        toggleMiscellaneousGuide(true);
    }

    public void guideMenusButton(View view){
        toggleOuterGuide(false);
        toggleMenusGuide(true);
    }

    public void guideResourceCollectorButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.resource_collector_description);
        guideImage.setBackgroundResource(R.drawable.ic_resourcecollector);
    }

    public void guideScoutButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.scout_description);
        guideImage.setBackgroundResource(R.drawable.ic_scout);
    }

    public void guideFighterButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.fighter_description);
        guideImage.setBackgroundResource(R.drawable.ic_fighter);
    }

    public void guideBomberButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.bomber_description);
        guideImage.setBackgroundResource(R.drawable.ic_bomber);
    }

    public void guideLaserCruiserButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.laser_cruiser_description);
        guideImage.setBackgroundResource(R.drawable.ic_lasercruiser);
    }

    public void guideBattleShipButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.battleship_description);
        guideImage.setBackgroundResource(R.drawable.ic_battleship);
    }

    public void guideFlagShipButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.flagship_description);
        guideImage.setBackgroundResource(R.drawable.ic_flagship);
    }

    public void guideSpaceStationButton(View view){
        toggleShipsGuide(false);
        guidePage = 1;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.space_station_description);
        guideImage.setBackgroundResource(R.drawable.ic_fullspacestation);
    }

    public void guideRectangleFormationButton(View view){
        toggleFormationsGuide(false);
        guidePage = 2;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.rectangle_formation);
        guideImage.setBackgroundResource(R.drawable.ic_rectangleformation);
    }

    public void guideVFormationButton(View view){
        toggleFormationsGuide(false);
        guidePage = 2;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.v_formation);
        guideImage.setBackgroundResource(R.drawable.ic_vformation);
    }

    public void guideCircleFormationButton(View view){
        toggleFormationsGuide(false);
        guidePage = 2;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.circle_formation);
        guideImage.setBackgroundResource(R.drawable.ic_circleformation);
    }

    public void guideCustomFormationButton(View view){
        toggleFormationsGuide(false);
        guidePage = 2;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.custom_formation);
        guideImage.setBackgroundResource(R.drawable.ic_customformationbutton);
    }

    public void guideResourcesButton(View view){
        toggleMiscellaneousGuide(false);
        guidePage = 3;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.resources_text);
        guideImage.setBackgroundResource(R.drawable.ic_asteroid);
    }

    public void guideSensorsButton(View view){
        toggleMiscellaneousGuide(false);
        guidePage = 3;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.sensors_text);
        guideImage.setBackgroundResource(R.drawable.ic_guidesensorpicture);
    }

    public void guideStarMapButton(View view){
        toggleMiscellaneousGuide(false);
        guidePage = 3;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.star_map_text);
        guideImage.setBackgroundResource(R.drawable.ic_mapbutton);
    }

    public void guideBlackHoleButton(View view){
        toggleMiscellaneousGuide(false);
        guidePage = 3;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.black_hole_text);
        guideImage.setBackgroundResource(R.drawable.ic_blackholefull);
    }

    public void guideNormalMenuButton(View view){
        toggleMenusGuide(false);
        toggleNormalMenuGuide(true);
    }

    public void guideSpecialMenuButton(View view){
        toggleMenusGuide(false);
        toggleSpecialMenuGuide(true);
    }

    public void guideCurrentFormationsMenuButton(View view){
        toggleMenusGuide(false);
        toggleCurrentFormationsGuide(true);
    }

    public void guideMoveButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.move_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_greenarrow);
        guideImage.setRotation(45);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideStopButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.stop_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_stop);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideTargetButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.target_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_attack);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideAutoAttackButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.autoattack_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_aggressive);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideFollowButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.view_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_followicon);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideSalvageButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.salvage_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_salvage);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideMenuButton(View view){
        toggleNormalMenuGuide(false);
        guidePage = 4;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.menu_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_guidenormalbutton);
    }

    public void guideAutoScoutButton(View view){
        toggleSpecialMenuGuide(false);
        guidePage = 5;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.auto_scout_button);
        guideImage.setBackgroundResource(R.drawable.ic_scout);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideGetResourcesButton(View view){
        toggleSpecialMenuGuide(false);
        guidePage = 5;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.resource_collector_button);
        guideImage.setBackgroundResource(R.drawable.ic_resourcecollector);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideDockButton(View view){
        toggleSpecialMenuGuide(false);
        guidePage = 5;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.dock_button);
        guideImage.setBackgroundResource(R.drawable.ic_dockbutton);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideDockedShipsMenuButton(View view){
        toggleSpecialMenuGuide(false);
        guidePage = 5;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.docked_ships_menu_button);
        guideImage.setBackgroundResource(R.drawable.ic_dockmenubutton);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideBackToNormalButton(View view){
        toggleCurrentFormationsGuide(false);
        guidePage = 6;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.back_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_greenarrow);
        guideImage.setRotation(45);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideNextFormationButton(View view){
        toggleCurrentFormationsGuide(false);
        guidePage = 6;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.next_formation_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_nextformation);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    public void guideDisbandFormationButton(View view){
        toggleCurrentFormationsGuide(false);
        guidePage = 6;
        toggleGuideEntry(true);
        guideDescription.setText(R.string.disband_formation_button_description);
        guideImage.setBackgroundResource(R.drawable.ic_disbandformation);
        guideImageWhiteCircle.setVisibility(View.VISIBLE);
    }

    //If user hits back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void victoryDefeat(View view) {
        victoryDefeat_pressed = true;
    }

    public void play(View view) {
        titleBackground = findViewById(R.id.gametitleBackground);
        logo = findViewById(R.id.logo);
        play = findViewById(R.id.play);
        guide = findViewById(R.id.guide);
        exit = findViewById(R.id.exit);
        backButton = findViewById(R.id.backButton);
        gamemodeBackground = findViewById(R.id.gamemodeBackground);
        gamemodeTitle = findViewById(R.id.gamemodeTitle);
        gamemode_classicTitle = findViewById(R.id.gamemode_classicTitle);
        gamemode_annihilationTitle = findViewById(R.id.gamemode_annihilationTitle);
        classicButton = findViewById(R.id.classicButton);
        annihilationButton = findViewById(R.id.annihilationButton);
        gamemodeClassicExplanation = findViewById(R.id.gamemodeClassicExplanation);
        gamemodeAnnihilationExplanation = findViewById(R.id.gamemodeAnnihilationExplanation);

        titleBackground.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        guide.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);
        gamemodeBackground.setVisibility(View.VISIBLE);
        gamemodeTitle.setVisibility(View.VISIBLE);
        gamemode_classicTitle.setVisibility(View.VISIBLE);
        gamemode_annihilationTitle.setVisibility(View.VISIBLE);
        classicButton.setVisibility(View.VISIBLE);
        annihilationButton.setVisibility(View.VISIBLE);
        gamemodeClassicExplanation.setVisibility(View.VISIBLE);
        gamemodeAnnihilationExplanation.setVisibility(View.VISIBLE);

    }

    public void classicButton(View view) {
        parameterView();
        GameScreen.classic = true;
    }

    public void annihilationButton(View view) {
        parameterView();
        GameScreen.classic = false;
    }

    public void parameterView() {
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

        difficulty_easy = findViewById(R.id.difficulty_easy);
        difficulty_medium = findViewById(R.id.difficulty_medium);
        difficulty_hard = findViewById(R.id.difficulty_hard);
        enemy_1 = findViewById(R.id.enemy_1);
        enemy_2 = findViewById(R.id.enemy_2);
        enemy_3 = findViewById(R.id.enemy_3);
        small_map = findViewById(R.id.small_map);
        medium_map = findViewById(R.id.medium_map);
        large_map = findViewById(R.id.large_map);
        initialResourcesBar = findViewById(R.id.initialResourcesBar);
        resourceBarDisplay = findViewById(R.id.resourceBarDisplay);
        blackholeCheck = findViewById(R.id.blackholeCheck);
        botsOnlyCheck = findViewById(R.id.botsOnlyCheck);
        playGame = findViewById(R.id.playGame);

        difficulty_easy.setVisibility(View.VISIBLE);
        difficulty_easy(null);
        difficulty_medium.setVisibility(View.VISIBLE);
        difficulty_hard.setVisibility(View.VISIBLE);
        enemy_1.setVisibility(View.VISIBLE);
        enemy_1(null);
        enemy_2.setVisibility(View.VISIBLE);
        enemy_3.setVisibility(View.VISIBLE);
        small_map.setVisibility(View.VISIBLE);
        small_map(null);
        medium_map.setVisibility(View.VISIBLE);
        large_map.setVisibility(View.VISIBLE);
        initialResourcesBar.setVisibility(View.VISIBLE);
        GameScreen.initialResources = 10000;
        initialResourcesBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                GameScreen.initialResources = progressChangedValue;
                resourceBarDisplay.setText(Integer.toString(progressChangedValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                GameScreen.initialResources = progressChangedValue;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                GameScreen.initialResources = progressChangedValue;
            }
        });
        resourceBarDisplay.setVisibility(View.VISIBLE);
        blackholeCheck.setVisibility(View.VISIBLE);
        blackholeCheck.setChecked(true);
        GameScreen.isBlackHole = true;
        blackholeCheck.setOnClickListener(view -> {
            GameScreen.isBlackHole = blackholeCheck.isChecked();
        });
        botsOnlyCheck.setVisibility(View.VISIBLE);
        botsOnlyCheck.setOnClickListener(view -> {
            GameScreen.botsOnly = botsOnlyCheck.isChecked();
        });
        playGame.setVisibility(View.VISIBLE);
    }

    public void difficulty_easy(View view) {
        difficulty_easy.setTextColor(Color.RED);
        difficulty_medium.setTextColor(Color.BLACK);
        difficulty_hard.setTextColor(Color.BLACK);
        GameScreen.difficulty = 1;
    }

    public void difficulty_medium(View view) {
        difficulty_easy.setTextColor(Color.BLACK);
        difficulty_medium.setTextColor(Color.RED);
        difficulty_hard.setTextColor(Color.BLACK);
        GameScreen.difficulty = 2;
    }

    public void difficulty_hard(View view) {
        difficulty_easy.setTextColor(Color.BLACK);
        difficulty_medium.setTextColor(Color.BLACK);
        difficulty_hard.setTextColor(Color.RED);
        GameScreen.difficulty = 3;
    }

    public void enemy_1(View view) {
        enemy_1.setTextColor(Color.RED);
        enemy_2.setTextColor(Color.BLACK);
        enemy_3.setTextColor(Color.BLACK);
        GameScreen.teams = 2;
    }

    public void enemy_2(View view) {
        enemy_1.setTextColor(Color.BLACK);
        enemy_2.setTextColor(Color.RED);
        enemy_3.setTextColor(Color.BLACK);
        GameScreen.teams = 3;
    }

    public void enemy_3(View view) {
        enemy_1.setTextColor(Color.BLACK);
        enemy_2.setTextColor(Color.BLACK);
        enemy_3.setTextColor(Color.RED);
        GameScreen.teams = 4;
    }

    public void small_map(View view) {
        small_map.setTextColor(Color.RED);
        medium_map.setTextColor(Color.BLACK);
        large_map.setTextColor(Color.BLACK);
        GameScreen.grid_size = 8;
    }

    public void medium_map(View view) {
        small_map.setTextColor(Color.BLACK);
        medium_map.setTextColor(Color.RED);
        large_map.setTextColor(Color.BLACK);
        GameScreen.grid_size = 20;
    }

    public void large_map(View view) {
        small_map.setTextColor(Color.BLACK);
        medium_map.setTextColor(Color.BLACK);
        large_map.setTextColor(Color.RED);
        GameScreen.grid_size = 32;
    }

    public void back_button(View view) {
        if (!loaded) {
            setTitleScreen();
        } else {
            ((ViewGroup) gameScreen.getParent()).removeView(guideView);
        }
    }

    //Upon pressing play, sets up game
    public void playGame(View view) {
        if (!pressed) {
            pressed = true;
            playGame.setVisibility(View.INVISIBLE);

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
                music.stop();
                music = MediaPlayer.create(getApplicationContext(), R.raw.space_battle_music);
                music.setLooping(true);
                music.start();
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
                if (GameScreen.gameOver && !GameScreen.botsOnly) {
                    if (!victoryDefeat_pressed) {
                        victoryDefeat.setVisibility(View.VISIBLE);
                        if (GameScreen.victory) {
                            victoryDefeat.setBackground(getDrawable(R.drawable.ic_victory));
                        } else {
                            victoryDefeat.setBackground(getDrawable(R.drawable.ic_defeat));
                        }
                    } else {
                        victoryDefeat.setVisibility(View.INVISIBLE);
                    }
                }
                if ((GameScreen.gameOver && !GameScreen.victory) || GameScreen.botsOnly){
                    shipBar(false);
                    formationBar(false);
                    buildBar(false);
                    select.setVisibility(View.INVISIBLE);
                    formation.setVisibility(View.INVISIBLE);
                    buildMenu.setVisibility(View.INVISIBLE);
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
                    int spaceStationCount = 0, resourceCollectorCount = 0, dockableShipsCount = 0, scoutCount = 0;
                    SpaceStation spaceStation = null;
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
                    FlagShip flagShip = null;
                    for (int i = 0; i < GameScreen.flagShips.size(); i++) {
                        if (GameScreen.flagShips.get(i).team == 1) {
                            flagShip = GameScreen.flagShips.get(i);
                        }
                    }
                    if (flagShip != null) {
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
            buildMenu.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);

            for (int i = 0; i < selectShips.size(); i++) {
                selectShips.get(i).movable = false;
            }
        } else {
            minimapOn = true;

            select.setVisibility(View.INVISIBLE);
            formation.setVisibility(View.INVISIBLE);
            buildMenu.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.INVISIBLE);
            shipBar(false);
            formationBar(false);
            buildBar(false);

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
        scroller.post(() -> scroller.fullScroll(ScrollView.FOCUS_DOWN));
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
                            if (selectShips.get(i) instanceof Scout) {
                                ((Scout) selectShips.get(i)).scouting = false;
                            } else if (selectShips.get(i) instanceof ResourceCollector) {
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
                                    if (selectShips.get(i) instanceof Scout) {
                                        ((Scout) selectShips.get(i)).scouting = false;
                                    } else if (selectShips.get(i) instanceof ResourceCollector) {
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
                        buildBar(false);
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

    public static void checkBorders() {
        if (GameScreen.offsetX / GameScreen.scaleX < -GameScreen.mapSizeX / 2) {
            GameScreen.offsetX = (int) (-GameScreen.mapSizeX / 2 * GameScreen.scaleX);
        } else if ((Main.screenX + GameScreen.offsetX) / GameScreen.scaleX > GameScreen.mapSizeX / 2) {
            GameScreen.offsetX = (int) (GameScreen.mapSizeX / 2 * GameScreen.scaleX - Main.screenX);
        }

        if (GameScreen.offsetY / GameScreen.scaleY < -GameScreen.mapSizeY / 2) {
            GameScreen.offsetY = (int) (-GameScreen.mapSizeY / 2 * GameScreen.scaleY);
        } else if ((Main.screenY + GameScreen.offsetY) / GameScreen.scaleY > GameScreen.mapSizeY / 2) {
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
        destroy = findViewById(R.id.salvageButton);
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
        victoryDefeat = findViewById(R.id.victoryDefeat);
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
            scoutMode.setVisibility(View.INVISIBLE);
            resourceCollector.setVisibility(View.INVISIBLE);
            scout.setVisibility(View.INVISIBLE);
            fighter.setVisibility(View.INVISIBLE);
            bomber.setVisibility(View.INVISIBLE);

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

    //Salvages selected ships
    public void salvageShip(View view) {
        following = false;

        for (int i = 0; i <= selectShips.size() - 1; i++) {
            if (selectShips.get(i) instanceof SpaceStation) {
                GameScreen.resources[selectShips.get(i).team - 1] += SpaceStation.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof BattleShip) {
                GameScreen.resources[selectShips.get(i).team - 1] += BattleShip.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof LaserCruiser) {
                GameScreen.resources[selectShips.get(i).team - 1] += LaserCruiser.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof Bomber) {
                GameScreen.resources[selectShips.get(i).team - 1] += Bomber.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof Fighter) {
                GameScreen.resources[selectShips.get(i).team - 1] += Fighter.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof Scout) {
                GameScreen.resources[selectShips.get(i).team - 1] += Scout.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
            } else if (selectShips.get(i) instanceof ResourceCollector) {
                GameScreen.resources[selectShips.get(i).team - 1] += ResourceCollector.cost * 0.5 * selectShips.get(i).health / selectShips.get(i).MAX_HEALTH;
                GameScreen.resources[selectShips.get(i).team - 1] += ((ResourceCollector) selectShips.get(i)).resources;
            }
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

    public boolean anyAutoAttack() {
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

        if (!anyAutoAttack()) {
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
        buildBar(false);

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
        scoutMode.setVisibility(View.INVISIBLE);

        buildBar(false);
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
        scoutMode.setVisibility(View.VISIBLE);

        resourceCollector.setVisibility(View.INVISIBLE);
        scout.setVisibility(View.INVISIBLE);
        fighter.setVisibility(View.INVISIBLE);
        bomber.setVisibility(View.INVISIBLE);

        numResourceCollectors.setVisibility(View.INVISIBLE);
        numScouts.setVisibility(View.INVISIBLE);
        numFighters.setVisibility(View.INVISIBLE);
        numBombers.setVisibility(View.INVISIBLE);

        buildBar(false);
    }

    public void buildMenu(View view) {
        shipBar(false);
        formationBar(false);
        buildBar(true);
    }

    public void buildBar(boolean hiddenOrNot) {
        if (hiddenOrNot) {
            for (int i = 0; i < GameScreen.flagShips.size(); i++) {
                if (GameScreen.flagShips.get(i).team == 1) {
                    selectShips.add(GameScreen.flagShips.get(i));
                    GameScreen.flagShips.get(i).selected = true;
                    following = true;
                    break;
                }
            }

            formationBar.setVisibility(View.VISIBLE);
            buildShips.setVisibility(View.VISIBLE);
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
        } else {
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

            cancelResourceCollector.setVisibility(View.INVISIBLE);
            cancelScout.setVisibility(View.INVISIBLE);
            cancelFighter.setVisibility(View.INVISIBLE);
            cancelBomber.setVisibility(View.INVISIBLE);
            cancelLaserCruiser.setVisibility(View.INVISIBLE);
            cancelBattleShip.setVisibility(View.INVISIBLE);
            cancelSpaceStation.setVisibility(View.INVISIBLE);
        }
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
        buildBar(false);
        startSelection = false;
        select.setBackgroundTintList(fabColor);
    }

    public void formationBar(boolean hiddenOrNot) {
        if (hiddenOrNot) {
            buildBar(false);
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
        if (formationSelected >= GameScreen.formationsTeam1.size()) {
            formationSelected = 0;
        }
    }

    public void controlFormation(View view) {
        if (selectShips.size() == 0) {
            return;
        }
        shipBar(true);
        formationBar(false);
        buildBar(false);
    }

    public void disbandFormation(View view) {
        Formation formation = isFormationSelected();
        if (formation != null) {
            formation.disbandFormation();
            if (GameScreen.formationsTeam1.contains(formation)) {
                GameScreen.formationsTeam1.remove(formation);
            } else if (GameScreen.formationsTeam2.contains(formation)) {
                GameScreen.formationsTeam2.remove(formation);
            } else if (GameScreen.formationsTeam3.contains(formation)) {
                GameScreen.formationsTeam3.remove(formation);
            } else GameScreen.formationsTeam4.remove(formation);
        }
    }

    public void setRectangleFormation(View view) {
        buildBar(false);
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.RECTANGLE_FORMATION));
    }

    public void setVFormation(View view) {
        buildBar(false);
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.V_FORMATION));
    }

    public void setCircleFormation(View view) {
        buildBar(false);
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.CIRCLE_FORMATION));
    }

    public void setCustomFormation(View view) {
        buildBar(false);
        formationBar(false);
        shipBar(true);

        GameScreen.formationsTeam1.add(new Formation(selectShips, Formation.CUSTOM_FORMATION));
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
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingSpaceStation = true;
                flagShip.countSpaceStation++;
                break;
            }
        }
    }

    public void buildBattleShip(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingBattleShip = true;
                flagShip.countBattleShip++;
                break;
            }
        }
    }

    public void buildLaserCruiser(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingLaserCruiser = true;
                flagShip.countLaserCruiser++;
                break;
            }
        }
    }

    public void buildBomber(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingBomber = true;
                flagShip.countBomber++;
                break;
            }
        }
    }

    public void buildFighter(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingFighter = true;
                flagShip.countFighter++;
                break;
            }
        }
    }

    public void buildScout(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingScout = true;
                flagShip.countScout++;
                break;
            }
        }
    }

    public void buildResourceCollector(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                flagShip.buildingResourceCollector = true;
                flagShip.countResourceCollector++;
                break;
            }
        }
    }

    public void cancelSpaceStation(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countSpaceStation <= 1) {
                    flagShip.stopBuilding("SpaceStation");
                    flagShip.countSpaceStation = 0;
                } else {
                    flagShip.countSpaceStation--;
                }
                break;
            }
        }
    }

    public void cancelBattleShip(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countBattleShip <= 1) {
                    flagShip.stopBuilding("BattleShip");
                    flagShip.countBattleShip = 0;
                } else {
                    flagShip.countBattleShip--;
                }
                break;
            }
        }
    }

    public void cancelLaserCruiser(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countLaserCruiser <= 1) {
                    flagShip.stopBuilding("LaserCruiser");
                    flagShip.countLaserCruiser = 0;
                } else {
                    flagShip.countLaserCruiser--;
                }
                break;
            }
        }
    }

    public void cancelBomber(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countBomber <= 1) {
                    flagShip.stopBuilding("Bomber");
                    flagShip.countBomber = 0;
                } else {
                    flagShip.countBomber--;
                }
                break;
            }
        }
    }

    public void cancelFighter(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countFighter <= 1) {
                    flagShip.stopBuilding("Fighter");
                    flagShip.countFighter = 0;
                } else {
                    flagShip.countFighter--;
                }
                break;
            }
        }
    }

    public void cancelScout(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            if (GameScreen.flagShips.get(i).team == 1) {
                FlagShip flagShip = GameScreen.flagShips.get(i);
                if (flagShip.countScout <= 1) {
                    flagShip.stopBuilding("Scout");
                    flagShip.countScout = 0;
                } else {
                    flagShip.countScout--;
                }
                break;
            }
        }
    }

    public void cancelResourceCollector(View view) {
        for (int i = 0; i < GameScreen.flagShips.size(); i++) {
            FlagShip flagShip = GameScreen.flagShips.get(i);
            if (flagShip.team == 1) {
                if (flagShip.countResourceCollector <= 1) {
                    flagShip.stopBuilding("ResourceCollector");
                    flagShip.countResourceCollector = 0;
                } else {
                    flagShip.countResourceCollector--;
                }
                break;
            }
        }
    }
}