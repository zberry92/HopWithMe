package com.androidprojects.zberry.hopwithme;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class hwm_frogTurnDetection extends GestureDetector.SimpleOnGestureListener
{
    // Thresholds, for swipe gesture detection
    private static final int swipeThreshold = 50;

    public hwm_game game;

    public hwm_frogTurnDetection(Context context)
    {
        this.game = (hwm_game) context;
    }

    public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        //Determine delta x and y for min swipe threshold
        float dx = e2.getX() - e1.getX();
        float dy = e2.getY() - e1.getY();

            if ((Math.abs(dx) > swipeThreshold) || (Math.abs(dy) > swipeThreshold))
            {
                if(Math.abs(dx) > Math.abs(dy))
                {
                    if (dx > 0)
                    {
                        game.onSwipeRight();
                    }
                    else
                    {
                        game.onSwipeLeft();
                    }
                }
                else
                {
                    if (dy > 0)
                    {
                        game.onSwipeUp();
                    }
                    else
                    {
                        game.onSwipeDown();
                    }
                }

                return true;
            }


        // No movement
        return false;
    }
}

