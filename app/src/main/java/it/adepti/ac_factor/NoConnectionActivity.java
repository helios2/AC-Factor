package it.adepti.ac_factor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.adepti.ac_factor.utils.CheckConnectivity;

public class NoConnectionActivity extends Activity{

    private final String TAG = "NoConnectionActivity";

    private ImageView imageView;
    private Button button;
    private TextView textView;

    // Check Connectivity
    private CheckConnectivity checkConnectivity = new CheckConnectivity(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_splash_screen);

        // Hide the action bar
        if(Build.VERSION.SDK_INT >= 11)
            getActionBar().hide();

        // Set View Object
        imageView = (ImageView) findViewById(R.id.splashIcon);
        button = (Button) findViewById(R.id.splashButton);
        textView = (TextView) findViewById(R.id.splashTextView);

        button.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.error);
        imageView.setImageDrawable(drawable);

        textView.setText(getString(R.string.txt_no_connect));

        button.setText(getString(R.string.txt_retry));

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(checkConnectivity.isConnected()){
                    Intent intent = new Intent(NoConnectionActivity.this, SplashScreen.class);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });
    }
}
