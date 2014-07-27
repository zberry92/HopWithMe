package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Random;

public class hwm_game extends Activity
{
    /* Shared Preferences */
    public static final String SHARED_PREF_HWM = "com.androidprojects.zberry.hopwithme.sharedPref";
    public static final String PREF_HWM_BOARD = "com.androidprojects.zberry.hopwithme.board";
    public static final String PREF_LOAD_TUTORIAL = "com.androidprojects.zberry.hopwithme.tutorial";
    public static final String PREF_HWM_FROGTILETYPE = "com.androidprojects.zberry.hopwithme.frogTileType";
    public static final String PREF_HWM_FROGX = "com.androidprojects.zberry.hopwithme.frogX";
    public static final String PREF_HWM_FROGY = "com.androidprojects.zberry.hopwithme.frogY";
    public static final String PREF_HWM_FROGDIR = "com.androidprojects.zberry.hopwithme.frogDirection";
    public static final String PREF_HWM_LIVESLEFT = "com.androidprojects.zberry.hopwithme.livesLeft";
    public static final String PREF_HWM_FLIESCOLLECTED = "com.androidprojects.zberry.hopwithme.fliesCollected";
    public static final String PREF_HWM_TIMELEFT = "com.androidprojects.zberry.hopwithme.timeLeft";
    public static final String PREF_HWM_TIMEINCREMENT = "com.androidprojects.zberry.hopwithme.timeIncrement";
    public static final String PREF_HWM_TOTALSCORE = "com.androidprojects.zberry.hopwithme.totalScore";
    public static final String PREF_HWM_TOTALTIME = "com.androidprojects.zberry.hopwithme.totalTime";

    // Board data shared prefs
    public SharedPreferences boardData;
    public SharedPreferences.Editor editBoardData;

    /* Game Global Variables */
    private int board[][], flyX[], flyY[];
    private char frogDir;
    private int frogX, frogY, frogTileType, livesLeft = 3, fliesCollected,
                gameLevel = 0, gameScore = 0, totalTime = 0;
    private long currentTime = 120000;
    private float timerHackBit = 0f, platformSpeed = 48.0f;

    private SensorManager sensorManager;
    private MovementEventListener accListener;
    private static final int RATE = SensorManager.SENSOR_DELAY_NORMAL;

    private hwm_frogTurnDetection swipeListener;
    private GestureDetector swipeDetector;
    private CountDownTimer timer;

    private boolean hopBit = false, killSense = false;

    // Predefine various rows to choose from
    private final int[] safeZoneArrayTop = {9, 9, 9, 9, 9, 9, 9, 90, 90};
    private final int[] safeZoneArrayBot = {9, 9, 9, 9, 9, 9, 9, 9, 9};
    private final int[] moveArrayE1 = {2, 2, 2, 7, 7, 7, 7, 2, 2, 7, 7, 7, 2, 2, 2};
    private final int[] moveArrayE2 = {7, 7, 2, 2, 7, 7, 2, 7, 7, 7, 7, 2, 2 ,2, 7};
    private final int[] moveArrayE3 = {2, 3, 5, 2, 2, 2, 3, 4, 5, 2, 2, 3, 4, 4, 5};
    private final int[] moveArrayE4 = {2, 2, 7, 7, 7, 7, 2, 2, 2, 2, 7, 7, 7, 7, 2};
    private final int[] moveArrayE5 = {3, 4, 5, 2, 2, 3, 4, 5, 2, 2, 3, 4, 5, 2, 2};
    private final int[] moveArrayW1 = {3, 4, 5, 2, 2, 2, 3, 4, 4, 5, 2, 2, 3, 4, 5};
    private final int[] moveArrayW2 = {2, 2, 2, 3, 4, 5, 2, 2, 2, 3, 4, 4, 5, 2, 2};
    private final int[] moveArrayW3 = {2, 2, 2, 2, 5, 4, 4, 3, 2, 2, 5, 4, 3, 2, 2};
    private final int[] moveArrayW4 = {6, 6, 6, 6, 2, 2, 6, 6, 6, 2, 6, 6, 6, 2, 2};
    private final int[] moveArrayW5 = {6, 6, 6, 7, 2, 6, 6, 6, 2, 2, 2, 2, 6, 6, 6};
    private final int[] stationaryArray1 = {2, 2, 1, 1, 2, 2, 2, 1, 2};
    private final int[] stationaryArray2 = {1, 2, 2, 1, 2, 2, 2, 2, 1};
    private final int[] stationaryArray3 = {2, 2, 2, 1, 1, 2, 1, 1, 2};
    private final int[] stationaryArray4 = {2, 1, 2, 2, 2, 2, 1, 2, 1};
    private final int[] stationaryArray5 = {2, 1, 2, 2, 2, 1, 1, 2, 2};

    // Arrays of all above rows for random generation
    private final int[][] eastRows = {moveArrayE1, moveArrayE2, moveArrayE3,
            moveArrayE4, moveArrayE5};
    private final int[][] westRows = {moveArrayW1, moveArrayW2, moveArrayW3,
            moveArrayW4, moveArrayW5};
    private final int[][] stationaryRows = {stationaryArray1, stationaryArray2,
            stationaryArray3, stationaryArray4, stationaryArray5};

    // Array of all rows in the current board
    private int[][] boardRows;
    // Array of the current starting index of each row; -1 means stationary row
    private int[] startingIndices;

    // Sounds
    private Vibrator vib;
    private MediaPlayer hopSound;
    private MediaPlayer swipeSound;
    private MediaPlayer waterSound;
    private MediaPlayer edgeSound;
    private MediaPlayer lossSound;
    private MediaPlayer victorySound;
    private MediaPlayer flyEaten;

    // Wakelock globals
    PowerManager pm = null;
    PowerManager.WakeLock wl = null;

    // Tutorial Globals
    private final int[] tutorial1 = {2, 2, 2, 2, 1, 2, 2, 2, 2};
    private final int[] tutorial2 = {2, 2, 2, 2, 1, 1, 2, 2, 2};
    private final int[] tutorial3 = {2, 2, 2, 2, 2, 1, 2, 2, 2};
    private final int[] tutorial4 = {2, 2, 2, 2, 1, 2, 2, 2, 2};
    private final int[] tutorial5 = {2, 2, 2, 2, 5, 4, 4, 3, 2, 2, 5, 4, 3, 2, 2};
    private final int[] tutorial6 = {2, 2, 2, 2, 1, 2, 2, 2, 2};

    private boolean tutorial = false, tut1 = false, tut2 = false, tut3 = false, tut4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.hwm_game);

        // Get Wakelock for the game
        pm = (PowerManager)this.getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                "Debug");

        // Initialize Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accListener = new MovementEventListener(this);
        sensorManager.registerListener(accListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                RATE);

        swipeListener = new hwm_frogTurnDetection(this);
        swipeDetector = new GestureDetector(swipeListener);

        // boardData is one of the shared preferences for this project
        boardData = getSharedPreferences("boardData", MODE_PRIVATE);
        editBoardData = boardData.edit();

        // Either load saved game
        if(!boardData.getBoolean("isNewGame", false) && boardData.getBoolean("saveGameExists", false))
        {

            randomlyGenerateBoardRows();
            board = rePopulateBoard();
            resetGameValues();

            loadState();

            board = rePopulateBoard();

            gameScore = 0;

        }
        // Load the tutorial board.
        else if(getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getBoolean(PREF_LOAD_TUTORIAL, false))
        {
            tutorial = true;

            buildTutorialRows();
            board = rePopulateBoard();

            // Initial frog direction
            resetGameValues();

            gameScore = 0;

            livesLeft = 100000;

            Intent tutorialWelcome = new Intent(this, hwm_tutorial.class);
            startActivity(tutorialWelcome);
        }
        // Or start new one
        else // New game
        {
            //board = parsePuzzleString(testBoard);
            randomlyGenerateBoardRows();
            board = rePopulateBoard();

            // Initial frog direction
            resetGameValues();

            gameScore = 0;
        }

        // Saves game just created
        editBoardData.putBoolean("saveGameExists", true);
        editBoardData.apply();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Set wakelock
        if (wl != null)
        {
            wl.acquire();
        }

        // Start music
        hwm_music.play(this, R.raw.hwm_game);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Reactivate Sensing
        releaseKillSense();

        // Reactivate Timer
        if (getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getBoolean(PREF_LOAD_TUTORIAL, false))
        {
            currentTime = 100000000;
        }
        setupTimer();
        timerHackBit = 0;

        // Create sounds
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        hopSound = MediaPlayer.create(this, R.raw.hop_sound);
        swipeSound = MediaPlayer.create(this, R.raw.swipe_sound);
        waterSound = MediaPlayer.create(this, R.raw.water_sound);
        edgeSound = MediaPlayer.create(this, R.raw.offedge_sound);
        lossSound = MediaPlayer.create(this, R.raw.defeat_sound);
        victorySound = MediaPlayer.create(this, R.raw.victory_sound);
        flyEaten = MediaPlayer.create(this, R.raw.flyeaten_sound);

        // Start listening for sensing again
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accListener = new MovementEventListener(this);
        sensorManager.registerListener(accListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                RATE);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Release wakelock
        wl.release();

        // Stop all services
        hwm_music.stop(this);
        setKillSense();
        sensorManager.unregisterListener(accListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        timer.cancel();

        saveState();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        tutorial = false;
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(PREF_LOAD_TUTORIAL,false)
                .commit();
    }

    // Save all shared prefs
    private void saveState()
    {
        String saveStr = createSaveString(board);

        // Update shared preferences
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putString(PREF_HWM_BOARD, saveStr).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_FROGTILETYPE, frogTileType).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_FROGX, frogX).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_FROGY, frogY).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_FROGDIR, frogDir).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_LIVESLEFT, livesLeft).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_FLIESCOLLECTED, fliesCollected).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_TIMELEFT, (int)currentTime).commit();
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putInt(PREF_HWM_TIMEINCREMENT, (int) platformSpeed).commit();

        // Starting indices
        for(int i = 0; i<8; i++)
        {
            editBoardData.putInt("hwmStartIndex" + i, startingIndices[i]);
        }

        // Board rows; moving rows have length 15
        int currRowLength;

        for(int m = 0; m<8; m++)
        {
            currRowLength = boardRows[m].length;
            for(int n = 0; n<currRowLength; n++)
            {
                editBoardData.putInt("hwmBoardRows" + m + n, boardRows[m][n]);
            }

        }

        // Save 9*8 board
        for(int x = 0; x<8; x++)
            for(int y = 0; y<9; y++)
            {
                editBoardData.putInt("hwmCurrBoard" + x + y, board[x][y]);
            }

        // Save fly locations
        for(int z=0; z<3; z++)
        {
            editBoardData.putInt("hwmFlyX" + z, flyX[z]);
            editBoardData.putInt("hwmFlyY" + z, flyY[z]);
        }

        editBoardData.commit();
    }

    private void loadState(){

        // Update shared preferences
        frogTileType = getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_FROGTILETYPE, frogTileType);
        frogX = getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_FROGX, frogX);
        frogY = getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_FROGY, frogY);
        livesLeft = getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_LIVESLEFT, livesLeft);
        fliesCollected = getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_FLIESCOLLECTED, fliesCollected);
        currentTime = (long)getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_TIMELEFT, 120000);
        platformSpeed = (long)getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_HWM_TIMEINCREMENT, 48);

        board = new int[8][9];
        boardRows = new int[8][15];
        startingIndices = new int[8];
        flyX = new int[3];
        flyY = new int[3];

        // Starting indices
        for(int i = 0; i<8; i++)
        {
            startingIndices[i] = boardData.getInt("hwmStartIndex" + i, 0);
        }

        // Board rows; moving rows have length 15
        int currRowLength;

        for(int m = 0; m<8; m++)
        {
            if(startingIndices[m] == -1)
                currRowLength = 9;
            else
                currRowLength = 15;

            for(int n = 0; n<currRowLength; n++)
            {
                boardRows[m][n] = boardData.getInt("hwmBoardRows" + m + n, 50);
            }

        }

        // Save 9*8 board
        for(int x = 0; x<8; x++)
            for(int y = 0; y<9; y++)
            {
                board[x][y] = boardData.getInt("hwmCurrBoard" + x + y, 50);
            }

        for(int z=0; z<3; z++)
        {
            flyX[z] = boardData.getInt("hwmFlyX" + z, -1);
            flyY[z] = boardData.getInt("hwmFlyY" + z, -1);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }

    // Build tutorial board.
    private void buildTutorialRows()
    {
        // Create board data; this may be randomly generated later
        boardRows = new int[8][];
        startingIndices = new int[8];

        // Safe zones will always be the same
        boardRows[0] = safeZoneArrayTop;
        boardRows[6] = tutorial1;
        boardRows[5] = tutorial2;
        boardRows[4] = tutorial3;
        boardRows[2] = tutorial4;
        boardRows[3] = tutorial5;
        boardRows[1] = tutorial6;
        boardRows[7] = safeZoneArrayBot;

        for (int i = 0; i < 8; i++)
        {
            startingIndices[i] = -1;
        }

        startingIndices[3] = 0;

    }

    // Randomly generate board rows by picking from the predefined ones
    private void randomlyGenerateBoardRows(){

        // Create board data; this may be randomly generated later
        boardRows = new int[8][];
        startingIndices = new int[8];

        Random gen = new Random();
        int rowType;

        // Choose the type of the first row; 0 = stat, 1 = east, 2 = west
        rowType = gen.nextInt(3);

        // Safe zones will always be the same
        boardRows[0] = safeZoneArrayTop;
        boardRows[7] = safeZoneArrayBot;
        startingIndices[0] = -1;
        startingIndices[7] = -1;

        // Assign the middle rows
        for(int i = 1; i < 7; i++)
        {
            // Stationary row
            if(rowType == 0)
            {
                boardRows[i] = stationaryRows[gen.nextInt(4)];
                startingIndices[i] = -1;

                // Generate the next row type. Cannot be the same as a previous row.
                rowType = gen.nextInt(2) + 1;
            }

            // east row
            else if(rowType == 1)
            {
                boardRows[i] = eastRows[gen.nextInt(4)];
                startingIndices[i] = gen.nextInt(14) + 100;

                // Generate the next row type. Avoid 1.
                while(rowType == 1)
                    rowType = gen.nextInt(3);
            }

            // otherwise must be west row
            else
            {
                boardRows[i] = westRows[gen.nextInt(4)];
                startingIndices[i] = gen.nextInt(14);

                // Generate the next row type. Avoid 2.
                rowType = gen.nextInt(2);
            }
        }


    }

    // Takes boardRows and startingIndices to populate the current 8*9 board
    private int[][] rePopulateBoard(){

        // Load initial indices into
        int[][] board  = new int[8][9];

        for (int i = 0; i < 8; i++)
        {
            // if stationary, just put in data directly
            if(startingIndices[i] == -1)
            {
                for (int j = 0; j < 9; j++)
                {
                    board[i][j] = boardRows[i][j];
                }
            }

            // Otherwise is moving and must look at starting index
            else
            {
                // Adjust east/west starting indices
                int startingIndex;
                if(startingIndices[i] > 99)
                    startingIndex = startingIndices[i] - 100;
                else
                    startingIndex = startingIndices[i];

                // If the starting index is low enough to not overflow a 15 length array
                if(startingIndex + 9 < 15)
                    for (int j = 0; j < 9; j++)
                        board[i][j] = boardRows[i][j + startingIndex];

                // Otherwise must overlap
                else{
                    int colsUntilWrap = 15 - startingIndex;
                    int colsAfterWrap = 9 - colsUntilWrap;
                    int currCol = 0;

                    for(int m = 0; m <colsUntilWrap; m++){
                        board[i][currCol] = boardRows[i][m + startingIndex];
                        currCol++;
                    }
                    for(int n = 0; n < colsAfterWrap; n++){
                        board[i][currCol] = boardRows[i][n];
                        currCol++;
                    }
                }
            }
        }

        // Set bottom two score tile colors
        board[1][7] = 90;
        board[1][8] = 90;

        return board;
    }

    // Move moving tiles by one square in their respective direction
    private void moveRows(){

        for (int i = 0; i < 8; i++)
        {
            // If -1 then is stationary or safe; do not move
            if(startingIndices[i] == -1)
                continue;

            // Eastward moving
            else if(startingIndices[i] > 99){
                if(startingIndices[i] < 101)
                    startingIndices[i] = 114;
                else
                    startingIndices[i]--;
            }

            // Otherwise must be westward moving
            else{
                if(startingIndices[i] > 13)
                    startingIndices[i] = 0;
                else
                    startingIndices[i]++;
            }
        }

        // Move frog if on moving row
        if(startingIndices[frogY] == -1)
            {} // Do nothing

        // Eastward moving row
        else if(startingIndices[frogY] >99)
        {
            // went into bottom score tile
            if((frogX == 6) && (frogY == 1))
            {
                edgeSound.start();
                lifeLost("You fell off the board!");
            }

            else if(frogX < 8)
            {
                frogX++;
                frogTileType = board[frogY][frogX];
                updateFlies();
            }
            else
            {
                edgeSound.start();
                lifeLost("You fell off the board!");
            }
        }

        // Westward moving row
        else
        {
            if(frogX > 0)
            {
                frogX--;
                frogTileType = board[frogY][frogX];
                updateFlies();
            }
            else
            {
                edgeSound.start();
                lifeLost("You fell off the board!");
            }

        }
    }

    private void generateFlyLocations()
    {
        flyX = new int[3];
        flyY = new int[3];

        if(tutorial)
        {
            flyX[0] = 4;
            flyY[0] = 1;
            flyX[1] = 2;
            flyY[1] = 6;
            flyX[2] = 6;
            flyY[2] = 6;

            return;
        }


        Random gen = new Random();

        int i = 0;
        while(i < 3)
        {
            // Set spawn area as all non safe tiles
            flyX[i] = gen.nextInt(9);
            flyY[i] = gen.nextInt(7) + 1;

            // Prevent from spawning in water of stationary row or the score tiles
            if ((board[flyY[i]][flyX[i]] == 2) || (board[flyY[i]][flyX[i]] == 90) || (board[flyY[i]][flyX[i]] == 9))
                i--;

            // And make sure that there are no duplicates so that there are always 3 flies
            else if((i==1) &&  ((flyX[0]==flyX[i]) || (flyY[0]==flyY[i])))
                i--;

            else if((i==2) & ((flyX[0]==flyX[i]) || (flyY[0]==flyY[i]) || (flyX[1]==flyX[i]) || (flyY[1]==flyY[i])))
                i--;

            i++;
        }
    }

    private String createSaveString(int[][] board)
    {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                buf.append(board[i][j]);
            }
        }

        return buf.toString();
    }

    public int getTile(int x, int y)
    {
        return board[x][y];
    }

    public void moveFrog(int movement)
    {
        // Just move frog forward on any sensor move for now
        if (movement == 0 && !killSense)
        {
            primitiveHop();
        }

        else if (movement == -1)
        {
            /* Movement not accurately detected. */
        }
        else
        {
            return;
        }
    }

    private void primitiveHop()
    {
        // Replace frog's previous spot with the tile type
        board[frogY][frogX] = frogTileType;

        if (!hopBit)
        {
            // Hack this shit.
            hopBit = true;
            return;
        }

        switch(frogDir)
        {
            case 'n':
                // If jumping into score screen
                if(frogY == 2 && (frogX == 7 || frogX == 8))
                    frogY = 2;
                else if(frogY > 0)
                    frogY--;
                else
                    frogY = 0;
                break;

            case 'e':
                // If jumping into score screen
                if(frogX == 6 && (frogY == 0 || frogY == 1))
                    frogX = 6;

                else if(frogX < 8)
                    frogX++;
                else
                    frogX = 8;
                break;

            case 'w':
                if(frogX > 0)
                    frogX--;
                else
                    frogX = 0;
                break;

            case 's':
                if(frogY < 7)
                    frogY++;
                else
                    frogY = 7;
                break;

            default:
                // Invalid direction stored
                frogDir = 'n';
                break;
        }

        hopBit = false;

        // Update current frog square type
        frogTileType = board[frogY][frogX];

        if (tutorial)
        {
            checkTutorialLocation();
        }

        // Check frog tile type; if water you die and reset location
        if (frogTileType == 2)
        {
            waterSound.start();
            lifeLost("You landed in the water!");
        }
        // If reached the last row, then you win and restart
        else if (frogY == 0)
        {
            gameWon();
        }

        updateFlies();

        vib.vibrate(100);
        hopSound.start();
    }

    private void checkTutorialLocation()
    {
        if (frogX == 4 && frogY == 5)
        {
            if (!tut1)
            {
                tut1 = true;
                Intent tuto1 = new Intent(this, hwm_tutorial.class);
                startActivity(tuto1);
            }
        }
        else if (frogX == 5 && frogY == 4)
        {
            if (!tut2)
            {
                tut2 = true;
                Intent tuto2 = new Intent(this, hwm_tutorial.class);
                startActivity(tuto2);
            }
        }
        else if (frogX == 4 && frogY == 2)
        {
            if (!tut3)
            {
                tut3 = true;
                Intent tuto3 = new Intent(this, hwm_tutorial.class);
                startActivity(tuto3);
            }
        }
        else if (frogX == 4 && frogY == 0)
        {
            if (!tut4)
            {
                tut4 = true;
                Intent tuto4 = new Intent(this, hwm_tutorial.class);
                startActivity(tuto4);
            }
            finish();
        }
    }

    private void updateFlies()
    {
        // Update flies
        if (flyX[0] == frogX && flyY[0] == frogY)
        {
            fliesCollected++;
            flyX[0] = -1;
            flyY[0] = -1;
            flyEaten.start();
        }
        else if (flyX[1] == frogX && flyY[1] == frogY)
        {
            fliesCollected++;
            flyX[1] = -1;
            flyY[1] = -1;
            flyEaten.start();
        }
        else if (flyX[2] == frogX && flyY[2] == frogY)
        {
            fliesCollected++;
            flyX[2] = -1;
            flyY[2] = -1;
            flyEaten.start();
        }
    }

    private void levelUpGameValues()
    {
        // Set Frog position
        frogTileType = 9;
        frogX = 4;
        frogY = 7;
        frogDir = 'n';

        // Generate new board
        randomlyGenerateBoardRows();
        board = rePopulateBoard();

        // Reset flies
        fliesCollected = 0;
        generateFlyLocations();

        // Reset Timer
        timer.cancel();
        currentTime = 120000;
        timer.start();
    }

    // Reset values if life lost; not for death at the moment
    private void resetGameValues()
    {
        frogTileType = 9;
        frogX = 4;
        frogY = 7;
        frogDir = 'n';
        fliesCollected = 0;
        generateFlyLocations();
    }

    private void lifeLost(String deathType)
    {
        // Reduce Lives and reset board
        livesLeft--;

        resetGameValues();

        CharSequence noUser = deathType + " Life lost!";
        Toast t = Toast.makeText(getApplicationContext(), noUser, Toast.LENGTH_SHORT);
        t.show();

        if(livesLeft == 0)
        {
            // Game is over.
            gameLost();
        }
    }

    private void gameLost()
    {
        getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit()
                .putString(PREF_HWM_TOTALSCORE, Integer.toString(gameScore)).commit();

        if (totalTime == 0)
        {
            getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putString(PREF_HWM_TOTALTIME, Integer.toString((int) ((120000 - currentTime) / 1000)))
                    .commit();
        }
        else
        {
            getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putString(PREF_HWM_TOTALTIME, Integer.toString(totalTime)).commit();
        }

        Intent gameOver = new Intent(this, hwm_end.class);
        startActivity(gameOver);

        lossSound.start();

        // No more savegame
        editBoardData.putBoolean("saveGameExists", false);
        editBoardData.commit();
        finish();
    }

    // Level is won, increase difficulty.
    private void gameWon()
    {
        // If the user makes it to the end, end the tutorial.
        if (getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).getBoolean(PREF_LOAD_TUTORIAL, false))
        {
            getSharedPreferences(SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(PREF_LOAD_TUTORIAL, false).commit();
            livesLeft = 3;
            finish();
        }

        levelUpGameValues();
        platformSpeed -= 2;
        gameLevel++;

        // Score = 100 * Level + time
        gameScore += (100 * gameLevel) + (currentTime / 1000) + (100 * fliesCollected);
        totalTime += (120000 - currentTime) / 1000;

        if (platformSpeed < 0)
        {
            gameLost();
        }

        victorySound.start();

        CharSequence noUser = "Level " + Integer.toString(gameLevel + 1);
        Toast t = Toast.makeText(getApplicationContext(), noUser, Toast.LENGTH_LONG);
        t.show();
    }

    private void setKillSense()
    {
        killSense = true;
    }

    private void releaseKillSense()
    {
        killSense = false;
    }

    private void setupTimer()
    {
        timer = new CountDownTimer(currentTime, 16)
        {
            int secondClk = 0;

            @Override
            public void onTick(long milli)
            {
                secondClk++;
                if (secondClk == 16)
                {
                    currentTime = milli;
                    secondClk = 0;
                }

                // Call moveRows; temporary hack bit to control frequency because count down timer does
                // not work like I though it did
                // Move every X seconds (value of timerHackBit)
                if(timerHackBit == platformSpeed)//platformSpeed)
                {
                    moveRows();
                    board = rePopulateBoard();
                    timerHackBit = 0;
                }

                timerHackBit++;
            }

            @Override
            public void onFinish()
            {
                gameLost();
                finish();
            }
        };

        timer.start();
    }

    // Swipe in east
    public void onSwipeRight(){
        frogDir = 'e';
        swipeSound.start();
    }

    // Swipe in west
    public void onSwipeLeft(){
        frogDir = 'w';
        swipeSound.start();
    }

    // Swipe in south
    public void onSwipeUp(){
        frogDir = 's';
        swipeSound.start();
    }

    // Swipe in north
    public void onSwipeDown(){
        frogDir = 'n';
        swipeSound.start();
    }

    public int getFrogX()
    {
        return frogX;
    }

    public int getFrogY()
    {
        return frogY;
    }

    public char getFrogDir()
    {
        return frogDir;
    }

    public int[] getFlyX()
    {
        return flyX;
    }

    public int[] getFlyY()
    {
        return flyY;
    }

    public long getCurrTime()
    {
        return (int)(currentTime/1000);
    }

    public int getLivesLeft()
    {
        return livesLeft;
    }

    public int getFliesCollected()
    {
        return  fliesCollected;
    }
}




