package edu.neu.madcourse.zacharyberry;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;

public class music
{
    private static MediaPlayer mp = null;

    public static void play(Context context, int resource)
    {
        stop(context);

        if (dabble_settings.getMusicOptionStatus(context))
        {
            mp = MediaPlayer.create(context, resource);
            mp.start();
        }
    }

    public static void stop(Context context)
    {
        if (mp != null)
        {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
