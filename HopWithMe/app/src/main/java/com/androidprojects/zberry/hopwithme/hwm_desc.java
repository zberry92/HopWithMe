package com.androidprojects.zberry.hopwithme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class hwm_desc extends Activity implements OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full screen the application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hwm_desc);

        View submitButton = findViewById(R.id.hwm_nextButton);
        submitButton.setOnClickListener(this);
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
        switch (v.getId())
        {
            case R.id.hwm_nextButton:
                Intent hwmMain = new Intent(this, hwm_main.class);
                startActivity(hwmMain);
                finish();
        }
    }
}
