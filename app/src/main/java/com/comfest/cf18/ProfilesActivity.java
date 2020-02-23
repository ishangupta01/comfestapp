package com.comfest.cf18;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfilesActivity extends AppCompatActivity {

    private ImageView mprofimage;
    private TextView mprofname, mprofstat, mproffriends;
    private Button sendreqbut;
    private ProgressBar progbar;
    private String current_state;
    private DatabaseReference friendreqdata;
    private FirebaseUser curuser;
    private DatabaseReference frienddata;
    private Button decreqbut;
    private DatabaseReference mnotdata;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference mdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        final String mid = getIntent().getStringExtra("userid");

        progbar = (ProgressBar) findViewById(R.id.profprogbar);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mid);
        mprofname = (TextView) findViewById(R.id.dispnameprof);
        mprofstat = (TextView) findViewById(R.id.profstatus);
        mprofimage = (ImageView) findViewById(R.id.profimage);
        curuser = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());

        progbar.setVisibility(View.VISIBLE);

        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dispname = dataSnapshot.child("name").getValue().toString();
                String dispstat = dataSnapshot.child("status").getValue().toString();
                String dispimage = dataSnapshot.child("image").getValue().toString();

                mprofname.setText(dispname);
                mprofstat.setText(dispstat);
                Picasso.with(ProfilesActivity.this).load(dispimage).placeholder(R.drawable.defpic).into(mprofimage);
                progbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }}




