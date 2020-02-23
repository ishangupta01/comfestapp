package com.comfest.cf18;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout dispname;
    private TextInputLayout email;
    private TextInputLayout pass;
    private TextInputLayout confirmpassf;
    private Button but;
    private Toolbar mtoolbar;
    private ProgressBar progbar;
    private DatabaseReference mdatabase;
    private TextInputLayout username;
    private FirebaseUser curuser;
    private DatabaseReference frienddata;
    private int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView textP =(TextView)findViewById(R.id.textprivacy);

        textP.setClickable(true);
        textP.setMovementMethod(LinkMovementMethod.getInstance());
        String text7 = "<a href='https://comfest-18.flycricket.io/privacy.html'> By creating an account, you agree to our privacy policy </a>";
        textP.setText(Html.fromHtml(text7));

        mtoolbar = (Toolbar) findViewById(R.id.regtoolbar);
        progbar = (ProgressBar) findViewById(R.id.progbar);

            n=1;

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        dispname = (TextInputLayout) findViewById(R.id.namef);
        email = (TextInputLayout) findViewById(R.id.emailf);
        confirmpassf= (TextInputLayout) findViewById(R.id.confirmpassf);
        pass = (TextInputLayout) findViewById(R.id.passf);
        username = (TextInputLayout) findViewById(R.id.usernamef);
        but = (Button) findViewById(R.id.createbut);

        View.OnClickListener btn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myname = dispname.getEditText().getText().toString();
                String mymail = email.getEditText().getText().toString();
                String mypass = pass.getEditText().getText().toString();
                String myconfirmpass= confirmpassf.getEditText().getText().toString();

                if(!TextUtils.isEmpty(myname)&&!TextUtils.isEmpty(mymail)&&!TextUtils.isEmpty(mypass)&&!TextUtils.isEmpty(mypass)&&!TextUtils.isEmpty(myconfirmpass)) {
                    if(mypass.equals(myconfirmpass)) {
                        progbar.setVisibility(View.VISIBLE);
                        register_user(myname, mymail, mypass);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "You can't leave a field empty!", Toast.LENGTH_SHORT).show();
                }

            }
        };
        but.setOnClickListener(btn);


    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                // after email is sent just logout the user and finish this activity
                                FirebaseAuth.getInstance().signOut();
                                progbar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Verification link sent on your email address. Click on it to verify your account",Toast.LENGTH_LONG).show();
                                Intent logint= new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(logint);
                            }
                            else
                            {
                                //email not sent, so display message
                                //restart this activity
                                Toast.makeText(RegisterActivity.this, "Verification email not sent, please contact us at info@comfest.in",Toast.LENGTH_LONG).show();
                                progbar.setVisibility(View.INVISIBLE);
                                startActivity(getIntent());

                            }
                        }
                    });
        }
    }

    public void register_user(final String myname, final String mymail, final String mypass)
    {

                    mAuth.createUserWithEmailAndPassword(mymail, mypass)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid=current_user.getUid();
                                        mdatabase= FirebaseDatabase.getInstance().getReference().child("User").child(uid);

                                        final HashMap<String, String> usermap = new HashMap<>();
                                        usermap.put("name", myname);
                                        usermap.put("status", "Hey! Whats up?");
                                        usermap.put("image", "default");
                                        usermap.put("thumb_image", "default");
                                        usermap.put("account_type", "user");


                                        mdatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {

                                                    FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.hasChild("admins")){
                                                                FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                            final String mid = ds.getKey();
                                                                            final String currentdate= DateFormat.getDateTimeInstance().format(new Date());
                                                                            curuser= FirebaseAuth.getInstance().getCurrentUser();
                                                                            frienddata=FirebaseDatabase.getInstance().getReference().child("Friends");
                                                                            frienddata.child(curuser.getUid()).child(mid).child("date").setValue(currentdate)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful())
                                                                                            {
                                                                                                frienddata.child(mid).child(curuser.getUid()).child("date").setValue(currentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {

                                                                                                            if (n == 1) {
                                                                                                                n=0;
                                                                                                                sendVerificationEmail();

                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }

                                                    });

                                                }
                                            }
                                        });

                                    }
                                    else
                                    {
                                        progbar.setVisibility(View.INVISIBLE);
                                        String error;
                                        try{
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            error="Too Weak Password";
                                        }
                                        catch (FirebaseAuthInvalidCredentialsException e) {
                                            error="Invalid Email ID";
                                        }
                                        catch (FirebaseAuthUserCollisionException e) {
                                            error="There is an existing account with this email ID.";
                                        }
                                        catch (Exception e) {
                                            error="Authentication failed, please check your internet connection and details";
                                        }

                                        Toast.makeText(RegisterActivity.this, error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

