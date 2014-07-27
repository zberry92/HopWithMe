package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;

public class hwm_ack extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hwm_ack);
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