package edu.neu.madcourse.zacharyberry;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.net.JarURLConnection;

public class MovementEventListener implements SensorEventListener
{
    private static final float jumpUpThreshold = 2f;
    private static final float jumpDownThreshold = -2f;
    private static final float ALPHA = 0.8f;
    private static final int HIGH_PASS_MINIMUM = 25;
    private float[] gravity;
    private int highPassCount;
    protected boolean squatLow, squatHigh;

    public hwm_game game;

    public MovementEventListener(Context context)
    {
        this.game = (hwm_game) context;
        gravity = new float[3];
        highPassCount = 0;
        squatHigh = false;
        squatLow = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() != 1)
        {
            return;
        }

        float[] values = event.values.clone();

        values = highPass(values[0], values[1], values[2]);

        if (++highPassCount >= HIGH_PASS_MINIMUM)
        {
            float deltaX = values[0];

            // A "movement" is only triggered of the total acceleration is
            // above a threshold
            if (deltaX < jumpDownThreshold)// && deltaX > jumpDownMaxThreshold)
            {
                if (!squatLow && !squatHigh)
                {
                    squatLow = true;
                }
                else if (squatLow && squatHigh)
                {
                    squatHigh = false;
                    squatLow = false;
                    game.moveFrog(0);

                }
                else
                {
                    squatLow = false;
                    squatHigh = false;
                }
            }
            else if(deltaX > jumpUpThreshold)// && deltaX < jumpUpMaxThreshold)
            {
                if (!squatHigh && squatLow)
                {
                    squatHigh = true;
                }
            }
            else
            {
                game.moveFrog(-1);
            }
        }
    }

    private float[] highPass(float x, float y, float z)
    {
        float[] filteredValues = new float[3];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        filteredValues[0] = x - gravity[0];
        filteredValues[1] = y - gravity[1];
        filteredValues[2] = z - gravity[2];

        return filteredValues;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // no-op
    }
}
