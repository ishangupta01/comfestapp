package com.comfest.cf18;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ViewPager mviewpager;
    private SectionsPagerAdapter mpageradapter;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private TabLayout mtablayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mviewpager= (ViewPager) findViewById(R.id.tabpager);
        mpageradapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mpageradapter);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());


        mtablayout = (TabLayout) findViewById(R.id.tablayout);
        mtablayout.setupWithViewPager(mviewpager);

        mtoolbar = (Toolbar) findViewById(R.id.chattoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Message Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsersDatabase.child("online").setValue("true");
    }
    @Override
    protected void onPause() {
        super.onPause();
        mUsersDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }
}
