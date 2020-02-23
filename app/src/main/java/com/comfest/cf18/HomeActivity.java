package com.comfest.cf18;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button regbut;
    private Button logbut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        regbut= (Button) findViewById(R.id.reg);
        logbut= (Button) findViewById(R.id.login);
        View.OnClickListener btnclick = new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              Intent reg_intent = new Intent(HomeActivity.this, RegisterActivity.class);
              startActivity(reg_intent);
           }
       };
       regbut.setOnClickListener(btnclick);
        View.OnClickListener btnclick2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent2 = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(reg_intent2);
            }
        };
        logbut.setOnClickListener(btnclick2);

            }

    }

