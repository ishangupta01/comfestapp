package com.comfest.cf18;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainint= new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainint);
                SplashActivity.this.finish();
            }
        },SPLASH);

    }
}
