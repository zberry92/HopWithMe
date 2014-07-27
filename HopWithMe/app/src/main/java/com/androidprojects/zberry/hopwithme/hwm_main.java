/*
 * HwM - Main title
 */

package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;;

public class hwm_main extends Activity implements OnClickListener
{
    private static final String PREF_INITAL_LOAD = "edu.neu.madcourse.hwm.il";

    // Shared prefs for load game
    public SharedPreferences boardData;
    public SharedPreferences.Editor editBoardData;

    private boolean initialLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hwm_main);

        // boardData is the shared preferences for this project
        boardData = getSharedPreferences("boardData", MODE_PRIVATE);
        editBoardData = boardData.edit();


        View gameStart = findViewById(R.id.hwm_game_start_button);
        gameStart.setOnClickListener(this);
        View gameContinue = findViewById(R.id.hwm_game_continue_button);
        gameContinue.setOnClickListener(this);
        View settings = findViewById(R.id.hwm_setting_button);
        settings.setOnClickListener(this);
        View tutorial = findViewById(R.id.hwm_tutotial_button);
        tutorial.setOnClickListener(this);
        View scores = findViewById(R.id.hwm_scores_button);
        scores.setOnClickListener(this);
        View quit = findViewById(R.id.hwm_quit_button);
        quit.setOnClickListener(this);

        if (!(getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                                    .getBoolean(PREF_INITAL_LOAD, false)))
        {
            initialLoad = true;
            initializeScorePrefs();
        }

        // set continue visibility
        if(boardData.getBoolean("GsaveameExists", false)){
            gameContinue.setVisibility(View.VISIBLE);
        }
        else
           gameContinue.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View gameContinue = findViewById(R.id.hwm_game_continue_button);

        // set continue visibility
        if(boardData.getBoolean("saveGameExists", false)){
            gameContinue.setVisibility(View.VISIBLE);
        }
        else
            gameContinue.setVisibility(View.INVISIBLE);

        hwm_music.play(this, R.raw.menu);
    }

    @Override
    protected void onPause() {
        super.onPause();

        hwm_music.stop(this);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.hwm_game_start_button:
                // Is a new game
                if (initialLoad)
                {
                    initialLoad = false;
                    getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                            .putBoolean(PREF_INITAL_LOAD, true).commit();
                    getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(hwm_game.PREF_LOAD_TUTORIAL, true).commit();
                    getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                            .putInt(hwm_tutorial.PREF_TUTORIAL_PLACE, 0).commit();
                }
                else
                {
                    editBoardData.putBoolean("isNewGame", true);
                    editBoardData.commit();
                    getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(hwm_game.PREF_LOAD_TUTORIAL, false).commit();

                }

                Intent startGame = new Intent(this, hwm_game.class);
                startActivity(startGame);
                break;
            case R.id.hwm_game_continue_button:
                // Is not a new game
                editBoardData.putBoolean("isNewGame", false);
                editBoardData.commit();

                Intent startGame2 = new Intent(this, hwm_game.class);
                startActivity(startGame2);
                break;
            case R.id.hwm_setting_button:
                Intent hwmSet = new Intent(this, hwm_settings.class);
                startActivity(hwmSet);
                break;
            case R.id.hwm_tutotial_button:
                getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                        .putBoolean(PREF_INITAL_LOAD, true).commit();
                getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(hwm_game.PREF_LOAD_TUTORIAL, true).commit();
                getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                        .putInt(hwm_tutorial.PREF_TUTORIAL_PLACE, 0).commit();

                Intent tut = new Intent(this, hwm_game.class);
                startActivity(tut);
                break;
            case R.id.hwm_scores_button:
                Intent score = new Intent(this, hwm_scores.class);
                startActivity(score);
                break;
            case R.id.hwm_quit_button:
                finish();
                break;
        }
    }

    public void initializeScorePrefs()
    {
        for (int i = 0; i < 10; i++)
        {
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i), "HWM~0~0")
                    .commit();
        }
    }
}
