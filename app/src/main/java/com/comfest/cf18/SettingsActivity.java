package com.comfest.cf18;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private FirebaseUser curuser;
    private ImageView mimage;
    private TextView dispname;
    private TextView statuso;
    private Toolbar mtoolbar;
    private Button mybtn;
    private Button dpbutton;
    private StorageReference mstore;
    private ProgressBar mprogbar;
    private RelativeLayout mylay;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mybtn= (Button) findViewById(R.id.changestat);
        dpbutton = (Button) findViewById(R.id.changedp);
        mstore = FirebaseStorage.getInstance().getReference();
        mprogbar = (ProgressBar) findViewById(R.id.setprogbar);
        mtoolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mylay= (RelativeLayout) findViewById(R.id.activity_settings);



        mimage= (ImageView) findViewById(R.id.circleImageView);
        dispname= (TextView) findViewById(R.id.nametext);
        statuso= (TextView) findViewById(R.id.statustext);

        View.OnClickListener btnlis1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myint = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(myint);
            }
        };
        mybtn.setOnClickListener(btnlis1);





        curuser= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid= curuser.getUid();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("User").child(currentuid);
        mdatabase.keepSynced(true);
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                dispname.setText(name);
                statuso.setText(status);

                if (!image.equals("default")) {

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.defpic).into(mimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.defpic).into(mimage);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        View.OnClickListener btnlis2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CropImage.activity().setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                    //    .start(SettingsActivity.this);
                Intent galleryint = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(galleryint,2);
            }
        };
        dpbutton.setOnClickListener(btnlis2);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String mcurrentuser = curuser.getUid();
        if(requestCode==2&& resultCode==RESULT_OK)
        {
            Uri imageuri = data.getData();

            CropImage.activity(imageuri).setAspectRatio(1,1).start(SettingsActivity.this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                mprogbar.setVisibility(View.VISIBLE);
                mylay.setClickable(false);
                Uri resultUri= result.getUri();
                final File thumb_filePath = new File(resultUri.getPath());
                Bitmap thumb_bitmap = new Compressor(SettingsActivity.this).
                        setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mstore.child("profile_pictures").child(mcurrentuser+".jpg");
                final StorageReference thumbfilepath = mstore.child("profile_pictures").child("thumbs").child(mcurrentuser+".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String[] downloadurl = {""};
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadurl[0] =uri.toString();
                                }
                            });
                           /* filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    downloadurl[0] =task.getResult().toString();
                                }
                            });*/
                            thumbfilepath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {



                                    if(thumb_task.isSuccessful())
                                    {
                                        final String[] thumb_downloadurl = {""};
                                        thumbfilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                thumb_downloadurl[0] =uri.toString();
                                                Map mymap= new HashMap();
                                                mymap.put("image", downloadurl[0]);
                                                mymap.put("thumb_image", thumb_downloadurl[0]);
                                                mdatabase.updateChildren(mymap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            mprogbar.setVisibility(View.INVISIBLE);
                                                            mylay.setClickable(true);
                                                            Toast.makeText(SettingsActivity.this,"Profile Picture updated",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    }
                                    else
                                    {
                                        mprogbar.setVisibility(View.INVISIBLE);
                                        mylay.setClickable(true);
                                        Toast.makeText(SettingsActivity.this,"Profile Picture could not be updated",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }
                        else
                        {
                            mprogbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }


}
