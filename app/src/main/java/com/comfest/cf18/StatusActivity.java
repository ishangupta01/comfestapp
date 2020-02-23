package com.comfest.cf18;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {


    private Toolbar mtoolbar;
    private TextInputLayout textin;
    private Button mybtn;
    private DatabaseReference mstatdata;
    private FirebaseUser muser;
    private ProgressBar progbar;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        progbar= (ProgressBar) findViewById(R.id.statprogbar);
        mybtn= (Button) findViewById(R.id.statuschangebtn);
        textin= (TextInputLayout) findViewById(R.id.changedstat) ;
        muser= FirebaseAuth.getInstance().getCurrentUser();
        String curuid= muser.getUid();
        mstatdata= FirebaseDatabase.getInstance().getReference().child("User").child(curuid);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());

        mtoolbar = (Toolbar) findViewById(R.id.status_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View.OnClickListener btnlis1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progbar.setVisibility(View.VISIBLE);
            String status = textin.getEditText().getText().toString();
                mstatdata.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progbar.setVisibility(View.INVISIBLE);
                        }
                        else {
                            progbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(StatusActivity.this, "Could not update the status, check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                }
        };
        mybtn.setOnClickListener(btnlis1);

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

