package com.androidprojects.zberry.hopwithme;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

public class prefs extends PreferenceActivity
{
    private static final String OPT_MUSIC = "edu.neu.madcourse.zacharyberry.music";
    private static final boolean OPT_MUSIC_DEF = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    /* Get music of the current values of the music option. */
    public static boolean getMusic(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
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
