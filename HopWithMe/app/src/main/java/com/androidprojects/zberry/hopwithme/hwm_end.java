package edu.neu.madcourse.zacharyberry;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.SocketHandler;

public class hwm_end extends Activity implements OnClickListener
{
    String prefScore, prefTime;

    int levelPlacement = -1;

    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prefScore = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(hwm_game.PREF_HWM_TOTALSCORE, null);
        prefTime = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(hwm_game.PREF_HWM_TOTALTIME, null);

        int endScore = Integer.parseInt(prefScore);
        int endTime = Integer.parseInt(prefTime);

        levelPlacement = checkValidScore(endScore, endTime);

        if (levelPlacement != -1)
        {
            setContentView(R.layout.hwm_end);

            TextView topScoreNum = (TextView) findViewById(R.id.hwm_end_rank);
            TextView scoreTv = (TextView) findViewById(R.id.hwm_gameOver_lose_scoretext);
            TextView timeTv = (TextView) findViewById(R.id.hwm_gameOver_lose_timetext);

            topScoreNum.setText(String.format(getResources()
                    .getString(R.string.hwm_gameOver_rankCongrat), levelPlacement + 1));
            scoreTv.setText(String.format(getResources()
                    .getString(R.string.hwm_gameOver_scoreString), prefScore));
            timeTv.setText(String.format(getResources()
                    .getString(R.string.hwm_gameOver_timeString), prefTime));

            // Set up a text watcher to work when characters are entered.
            EditText editText = (EditText) findViewById(R.id.hwm_score_username);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    // Not used
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    username = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Not used
                }
            });

            View submitName = findViewById(R.id.hwm_end_button);
            submitName.setOnClickListener(this);
        }
        else
        {
            setContentView(R.layout.hwm_end_loss);

            TextView scoreTv = (TextView) findViewById(R.id.hwm_gameOver_lose_scoretext);
            TextView timeTv = (TextView) findViewById(R.id.hwm_gameOver_lose_timetext);

            scoreTv.setText(String.format(getResources()
                    .getString(R.string.hwm_gameOver_scoreString), prefScore));
            timeTv.setText(String.format(getResources()
                    .getString(R.string.hwm_gameOver_timeString), prefTime));

            View closeButton = findViewById(R.id.hwm_end_loss_close);
            closeButton.setOnClickListener(this);
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

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.hwm_end_button:
                if (username == "")
                {
                    CharSequence noUser = "Please enter a three character username!";
                    Toast t = Toast.makeText(getApplicationContext(), noUser, Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                updateHighScore(username, prefScore, prefTime, levelPlacement);
                finish();
                break;
            case R.id.hwm_end_loss_close:
                finish();
                break;
        }
    }

    public int checkValidScore(int score, int time)
    {
        String scoreVal = "";
        String[] scoreList;

        for (int i = 0; i < 10; i++)
        {
            scoreVal = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i), null);

            if (scoreVal == null)
            {
                return -1;
            }

            scoreList = scoreVal.split("~");

            // Check to see if score is higher.
            if (score >= Integer.parseInt(scoreList[1]) || Integer.parseInt(scoreList[1]) == 0)
            {
                // Check to see how their times compare.
                if (time < Integer.parseInt(scoreList[2]) || Integer.parseInt(scoreList[2]) == 0)
                {
                    return i;
                }
                else
                {
                    return ++i;
                }
            }
        }

        return -1;
    }

    public void updateHighScore(String name, String score, String time, int listPlace)
    {
        String saveValue = name + "~" + score + "~" + time;
        String swap1, swap2;

        // Adjust all previous scores down one value.
        for (int i = 10; i > listPlace; i--)
        {
            swap1 = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i), null);
            swap2 = getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE)
                    .getString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i - 1), null);

            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i), swap2)
                    .commit();
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                    .putString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(i - 1), swap1)
                    .commit();
        }

        // Write in new value.
        getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit()
                .putString(hwm_scores.PREF_HWM_HIGHSCORE_BASE + Integer.toString(listPlace), saveValue)
                .commit();
    }
}
