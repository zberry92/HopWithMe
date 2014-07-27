package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class hwm_tutorial extends Activity
{
    public static final String PREF_TUTORIAL_PLACE = "edu.neu.madcourse.hwm.tutPlace";

    @Override
    protected void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int tutorialPlace = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).getInt(PREF_TUTORIAL_PLACE, 0);

        switch (tutorialPlace)
        {
            case 0:
                setContentView(R.layout.hwm_tutorial);
                break;
            case 1:
                setContentView(R.layout.hwm_tutorial1);
                break;
            case 2:
                setContentView(R.layout.hwm_tutorial2);
                break;
            case 3:
                setContentView(R.layout.hwm_tutorial3);
                break;
            case 4:
                setContentView(R.layout.hwm_tutorial4);
                break;
        }

        if (tutorialPlace < 4)
        {
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putInt(PREF_TUTORIAL_PLACE, ++tutorialPlace).commit();
        }
        else
        {
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putInt(PREF_TUTORIAL_PLACE, 0).commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }

    // Dismiss the AboutMe dialog box if the user touches outside the dialog.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())
                && ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            this.finish();
        }
        return super.dispatchTouchEvent(ev);
    }
}
