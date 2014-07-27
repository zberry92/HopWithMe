package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class hwm_scores extends Activity implements OnClickListener
{
    /* Shared Preferences */
    public static final String PREF_HWM_HIGHSCORE_BASE = "com.androidprojects.zberry.hopwithme.highscore";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hwm_scores);

        // Setup back button
        View back = findViewById(R.id.hwm_back_score_button);
        back.setOnClickListener(this);

        // Fill text boxes with scores
        renderScores();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        hwm_music.play(this, R.raw.menu);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        hwm_music.stop(this);
    }

    private void renderScores()
    {
        int tvID = R.id.hwm_highscore0;

        for (int i = 0; i < 10; i++)
        {
            TextView score = (TextView) findViewById(tvID);
            score.setText(parseScorePref(getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(PREF_HWM_HIGHSCORE_BASE + Integer.toString(i), null)));
            tvID++;
        }
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.hwm_back_score_button:
                finish();
                break;
        }
    }

    public String parseScorePref(String str)
    {
        String formattedScore = "";
        String[] scoreComponents = str.split("~");

        formattedScore = String.format(getResources().getString(R.string.hwm_highscore_listing),
                                        scoreComponents[0], scoreComponents[1], scoreComponents[2]);

        return formattedScore;
    }
}
