package com.comfest.cf18;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mymail;
    private TextInputLayout mypass;
    private Toolbar mtoolbar;
    private Button btn;
    private ProgressBar progbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mymail= (TextInputLayout) findViewById(R.id.logemail);
        mypass= (TextInputLayout) findViewById(R.id.logpass);
        mtoolbar = (Toolbar) findViewById(R.id.logtoolbar);
        btn = (Button) findViewById(R.id.logbut);
        progbar= (ProgressBar) findViewById(R.id.progbar2);
        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        View.OnClickListener btn1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String email= mymail.getEditText().getText().toString();
               final String password= mypass.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password))
                {
                        progbar.setVisibility(View.VISIBLE);
                    loginuser(email, password);

                    }
                }
            };

        btn.setOnClickListener(btn1);
    }



    private void loginuser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            checkIfEmailVerified();

                        }
                        else
                        {
                            progbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Check your internet connection and credentials",
                                    Toast.LENGTH_SHORT).show();
                        }
                        }
                });
    }
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified())
        {
            mdatabase= FirebaseDatabase.getInstance().getReference().child("User");
            progbar.setVisibility(View.INVISIBLE);

            String curuser =mAuth.getCurrentUser().getUid();
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
        else
        {
            progbar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Email address is not verified. Check your inbox.", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();

        }
    }
}
