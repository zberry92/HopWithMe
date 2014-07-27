package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;




public class hwm_settings extends Activity implements View.OnClickListener
{

    public static final String PREF_MUSIC_OPTION = "MUSIC_OPTION";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.hwm_settings);

        CheckBox cb = (CheckBox) findViewById(R.id.dabble_musicOption);
        cb.setChecked(getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).getBoolean(PREF_MUSIC_OPTION, true));

        View ack = findViewById(R.id.hwm_acknowledgments_button);
        ack.setOnClickListener(this);
        View debugKill = findViewById(R.id.hwm_settingsreturn_button);
        debugKill.setOnClickListener(this);

    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.hwm_acknowledgments_button:
                Intent changeUser = new Intent(this, hwm_ack.class);
                startActivity(changeUser);
                return;
            case R.id.hwm_settingsreturn_button:
                finish();
                return;
        }

        boolean checked = ((CheckBox) view).isChecked();

        if (checked)
        {
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(PREF_MUSIC_OPTION, true).commit();
        }
        else
        {
            getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE).edit().putBoolean(PREF_MUSIC_OPTION, false).commit();
        }

    }

    public static boolean getMusicOptionStatus(Context c)
    {
        SharedPreferences sp = c.getApplicationContext().getSharedPreferences(hwm_game.SHARED_PREF_HWM, MODE_PRIVATE);
        return sp.getBoolean(PREF_MUSIC_OPTION, true);
    }
}
