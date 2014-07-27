package com.androidprojects.zberry.hopwithme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class hwm_gameView extends View
{
    private static final String VIEW_STATE = "viewState";

    /* Id of the view. */
    private static final int ID = 44;

    /* Private class variables. */
    private float tile_width, tile_height;
    private int lastFrogX, lastFrogY, lastFrogDir;
    private int[] flyX, flyY;

    // Graphics objects
    Bitmap bigFrogBitmapN;
    Bitmap bigFrogBitmapE;
    Bitmap bigFrogBitmapW;
    Bitmap bigFrogBitmapS;
    Bitmap frogBitmapN;
    Bitmap frogBitmapE;
    Bitmap frogBitmapW;
    Bitmap frogBitmapS;
    Bitmap flyBitmap;
    Rect flyRect1;
    Rect flyRect2;
    Rect flyRect3;
    Bitmap bigLogBitmap;
    Bitmap bigTurtleBitmapE;
    Bitmap bigTurtleBitmapW;
    Bitmap bigRockBitmap;
    Bitmap bigLillypadBitmap;
    Bitmap logBitmap;
    Bitmap turtleBitmapE;
    Bitmap turtleBitmapW;
    Bitmap rockBitmap;
    Bitmap lillypadBitmap;



    private Rect[] board;

    public hwm_game game;

    public hwm_gameView(Context context)
    {
        super(context);
        this.game = (hwm_game) context;

        setFocusable(true);
        setFocusableInTouchMode(true);

        setId(ID);

        // Set up frog position
        lastFrogX = game.getFrogX();
        lastFrogY = game.getFrogY();
        lastFrogDir = game.getFrogDir();

        // Run timer
        updateFrogPosition();
    }

    public hwm_gameView(Context context, AttributeSet attribs)
    {
        super(context, attribs);

        this.game = (hwm_game) context;

        setFocusable(true);
        setFocusableInTouchMode(true);

        setId(ID);

        // Set up frog position
        lastFrogX = game.getFrogX();
        lastFrogY = game.getFrogY();
        lastFrogDir = game.getFrogDir();

        // Run timer
        updateFrogPosition();
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable p = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VIEW_STATE, p);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        /* Size the tiles and place them on the canvas. */
        tile_width = w / 9f;
        tile_height = h / 8f;

        /* Instantiate all recs */
        int recCount = 0;
        board = new Rect[72];

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                board[recCount] = new Rect();
                getRect(j, i, board[recCount]);
                recCount++;
            }
        }

        // Set up images
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//important
        myOptions.inPurgeable = true;

        // Frog
        Rect frogRect = board[9 * game.getFrogY() + game.getFrogX()];

        bigFrogBitmapN = BitmapFactory.decodeResource(getResources(), R.drawable.frog_tile_n, myOptions);
        bigFrogBitmapE = BitmapFactory.decodeResource(getResources(), R.drawable.frog_tile_e, myOptions);
        bigFrogBitmapW = BitmapFactory.decodeResource(getResources(), R.drawable.frog_tile_s, myOptions);
        bigFrogBitmapS = BitmapFactory.decodeResource(getResources(), R.drawable.frog_tile_w, myOptions);

        frogBitmapN = CreateScaledBitmap(bigFrogBitmapN, frogRect.width(), frogRect.height(), false);
        frogBitmapE = CreateScaledBitmap(bigFrogBitmapE, frogRect.width(), frogRect.height(), false);
        frogBitmapW = CreateScaledBitmap(bigFrogBitmapW, frogRect.width(), frogRect.height(), false);
        frogBitmapS = CreateScaledBitmap(bigFrogBitmapS, frogRect.width(), frogRect.height(), false);

        // Fly
        flyRect1 = board[9 * game.getFlyY()[0] + game.getFlyX()[0]];
        flyRect2 = board[9 * game.getFlyY()[1] + game.getFlyX()[1]];
        flyRect3 = board[9 * game.getFlyY()[2] + game.getFlyX()[2]];

        Bitmap bigFlyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fly, myOptions);
        flyBitmap = CreateScaledBitmap(bigFlyBitmap, flyRect1.width(), flyRect1.height(), false);

        // Terrain

        bigLogBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.log);
        bigRockBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock_blue);
        bigLillypadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lillypad);
        bigTurtleBitmapE = BitmapFactory.decodeResource(getResources(), R.drawable.turtle_e);
        bigTurtleBitmapW = BitmapFactory.decodeResource(getResources(), R.drawable.turtle_w);

        logBitmap = Bitmap.createScaledBitmap(bigLogBitmap, frogRect.width(), frogRect.height(), false);
        rockBitmap = Bitmap.createScaledBitmap(bigRockBitmap, frogRect.width(), frogRect.height(), false);
        lillypadBitmap = Bitmap.createScaledBitmap(bigLillypadBitmap, frogRect.width(), frogRect.height(), false);
        turtleBitmapE = Bitmap.createScaledBitmap(bigTurtleBitmapE, frogRect.width(), frogRect.height(), false);
        turtleBitmapW = Bitmap.createScaledBitmap(bigTurtleBitmapW, frogRect.width(), frogRect.height(), false);


        updateFrogPosition();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Get current fly locations.
        flyX = game.getFlyX();
        flyY = game.getFlyY();

        // Draw the board
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 9; j++)
            {

                Rect currRect = board[9 * i + j];

                if( (game.getTile(i, j) == 2) || (game.getTile(i, j) == 90) )
                    canvas.drawRect(board[9 * i + j], parseTileColor(i, j));
                else
                    canvas.drawBitmap(parseTileBitmap(i, j), new Rect(0,0, (int)tile_width, (int)tile_height), currRect, null);
            }
        }

        /* Draw Frog */
        Rect frogRect = board[9 * game.getFrogY() + game.getFrogX()];

        if(game.getFrogDir() == 'e')
            canvas.drawBitmap(frogBitmapE, new Rect(0,0, (int)tile_width, (int)tile_height), frogRect, null);
        else if(game.getFrogDir() == 's')
            canvas.drawBitmap(frogBitmapW, new Rect(0,0, (int)tile_width, (int)tile_height), frogRect, null);
        else if(game.getFrogDir() == 'w')
            canvas.drawBitmap(frogBitmapS, new Rect(0,0, (int)tile_width, (int)tile_height), frogRect, null);
        else
            canvas.drawBitmap(frogBitmapN, new Rect(0,0, (int)tile_width, (int)tile_height), frogRect, null);


        // make paint for frog direction marker
        Paint directionPaint = new Paint();
        directionPaint.setColor(getResources().getColor(R.color.hwm_safe));
        directionPaint.setTextSize(50);
        Paint.FontMetrics wpm = directionPaint.getFontMetrics();
        float letterOffset = (wpm.ascent + wpm.descent)/2;

        /* Draw Flies */

        Boolean fly0Gotten = (game.getFlyY()[0] == -1);
        Boolean fly1Gotten = (game.getFlyY()[1] == -1);
        Boolean fly2Gotten = (game.getFlyY()[2] == -1);

        if(!fly0Gotten)
        {
            flyRect1 = board[9 * game.getFlyY()[0] + game.getFlyX()[0]];
            canvas.drawBitmap(flyBitmap, new Rect(0,0, (int)(tile_width*1.3), (int)(tile_height*1.3)), flyRect1, null);
        }
        if(!fly1Gotten)
        {
            flyRect2 = board[9 * game.getFlyY()[1] + game.getFlyX()[1]];
            canvas.drawBitmap(flyBitmap, new Rect(0,0, (int)(tile_width*1.3), (int)(tile_height*1.3)), flyRect2, null);
        }
        if(!fly2Gotten)
        {
            flyRect3 = board[9 * game.getFlyY()[2] + game.getFlyX()[2]];
            canvas.drawBitmap(flyBitmap, new Rect(0,0, (int)(tile_width*1.3), (int)(tile_height*1.3)), flyRect3, null);
        }



        // Make paint for the timer, lives and flies
        Paint pointsPaint = new Paint();
        pointsPaint.setColor(getResources().getColor(R.color.hwm_score_text));
        pointsPaint.setTextSize(18);
        Paint.FontMetrics w = directionPaint.getFontMetrics();
        float scoreOffset = (w.ascent + w.descent)/2;

        // Draw the current time
        canvas.drawText("Time: "+this.game.getCurrTime(), (float)7.5*tile_width + scoreOffset , (float)(.1)*tile_height - scoreOffset, pointsPaint);
        canvas.drawText("Lives: "+this.game.getLivesLeft(), (float)7.5*tile_width + scoreOffset , (float)(.8)*tile_height - scoreOffset, pointsPaint);
        canvas.drawText("Flies: "+this.game.getFliesCollected(), (float)7.5*tile_width + scoreOffset , (float)(1.5)*tile_height - scoreOffset, pointsPaint);

        invalidate();
    }

    // TimerTask and Timer to update from position
    public void updateFrogPosition()
    {
        Timer timer = new Timer();
        TimerTask updateKeyValueData = new TimerTask()
        {
            @Override
            public void run()
            {
                // Check if frog position has changed, if yes then redraw board
                if(lastFrogX != game.getFrogX() || lastFrogY != game.getFrogY() || lastFrogDir != game.getFrogDir())
                {
                    lastFrogX = game.getFrogX();
                    lastFrogY = game.getFrogY();
                    lastFrogDir = game.getFrogDir();

                    postInvalidate();
                }
            }
        };
        
        timer.schedule(updateKeyValueData, 0, 100);
    }

    private void getRect(int x, int y, Rect rect)
    {
        rect.set((int) (x * tile_width), (int) (y * tile_height), (int) (x
                * tile_width + tile_width), (int) (y * tile_height + tile_height));
    }

    Paint parseTileColor(int x, int y)
    {
        Paint tileColor = new Paint();

        int type = game.getTile(x, y);

        if (type == 2)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_water));
        }
        else if (type == 1)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_lilly_pad));
        }

        else if (type == 3 || type == 4 || type == 5)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_log));
        }

        else if (type == 6 || type == 7 || type == 8)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_turtle));
        }

        else if (type == 9)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_safe));
        }

        else if (type == 90)
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_score_background));
        }

        else
        {
            tileColor.setColor(getResources().getColor(R.color.hwm_error));
        }

        return tileColor;
    }

    Bitmap parseTileBitmap(int x, int y)
    {
        int type = game.getTile(x, y);

        // return bitmap
        Bitmap tileBitmap;

        if (type == 1)
        {
            tileBitmap = lillypadBitmap;
    }

        else if (type == 3 || type == 4 || type == 5)
        {
            tileBitmap = logBitmap;
        }

        else if (type == 6)
        {
            tileBitmap = turtleBitmapW;
        }
        else if (type == 7)
        {
            tileBitmap = turtleBitmapE;
        }

        else if (type == 9)
        {
            tileBitmap = rockBitmap;
        }

        else
        {
            tileBitmap = rockBitmap;
        }

        return tileBitmap;

    }

    public static Bitmap CreateScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
    {
        Matrix m = new Matrix();
        m.setScale(dstWidth  / (float)src.getWidth(), dstHeight / (float)src.getHeight());
        Bitmap result = Bitmap.createBitmap(dstWidth, dstHeight, src.getConfig());
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setFilterBitmap(filter);
        canvas.drawBitmap(src, m, paint);

        return result;

    }
}
