package com.comfest.cf18;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRootRef;
    private DrawerLayout mlayout;
    private ActionBarDrawerToggle mtoggle;
    private FloatingActionButton fbut;
    private NavigationView mnavigationview;
    private StorageReference mstore;
    private ProgressBar mprogbar;
    private TextView mposttext;
    private RecyclerView mrecyclerview;
    private DatabaseReference mdata;
    private FirebaseRecyclerAdapter madapter;
    private FirebaseRecyclerAdapter madapter2;
    private TextView mtext;
    private TextView loadtext;
    private RecyclerView mrecyclerview2;
    private String nameofuser;
    private String imageofuser;
    private LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendtostart();
        } else {
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
            mUsersDatabase.child("online").setValue("true");
        }
        loadtext=(TextView) findViewById(R.id.loadtext);
        mstore = FirebaseStorage.getInstance().getReference();
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mposttext=(TextView) findViewById(R.id.post_text);
        mprogbar=(ProgressBar) findViewById(R.id.postprogbar);
        fbut=(FloatingActionButton) findViewById(R.id.addpostbut);
        mrecyclerview= (RecyclerView) findViewById(R.id.userdprecycler);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mrecyclerview.setHasFixedSize(true);
        mtext= (TextView) findViewById(R.id.noposttext);
        mrecyclerview2=(RecyclerView) findViewById(R.id.userpostrecycler);
        llm= new LinearLayoutManager(MainActivity.this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        mrecyclerview2.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mrecyclerview2.setHasFixedSize(true);

        mRootRef.child("admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
               {
                   fbut.setVisibility(View.VISIBLE);
                   fbut.setEnabled(true);
                   fbut.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {

                           Intent galleryint = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                           startActivityForResult(galleryint,2);
                       }
                   });
               }
                else
               {
                   View mview= findViewById(R.id.addpostbut);
                   mview.setVisibility(View.GONE);
                   fbut.setVisibility(View.INVISIBLE);
                   fbut.setEnabled(false);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mnavigationview= (NavigationView) findViewById(R.id.nav_menu);
        mnavigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_chat)
                {
                    Intent intent2 = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent2);
                }
                if(item.getItemId()==R.id.nav_events)
                {
                    CharSequence options[]= new CharSequence[]{"Technical", "Literary","Creative", "Miscellaneous" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Which event category?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if(i==0)
                            {
                                Intent myint= new Intent(MainActivity.this, EventsActivity.class);
                                myint.putExtra("category","tech");
                                startActivity(myint);
                            }
                            if(i==1)
                            {
                                Intent myint= new Intent(MainActivity.this, EventsActivity.class);
                                myint.putExtra("category","lit");
                                startActivity(myint);
                            }
                            if(i==2)
                            {
                                Intent myint= new Intent(MainActivity.this, EventsActivity.class);
                                myint.putExtra("category","creative");
                                startActivity(myint);
                            }
                            if(i==3)
                            {
                                Intent myint= new Intent(MainActivity.this, EventsActivity.class);
                                myint.putExtra("category","misc");
                                startActivity(myint);
                            }
                        }
                    });
                    builder.show();
                }
                if(item.getItemId()==R.id.nav_schedule)
                {
                    Intent newint= new Intent(MainActivity.this, ScheduleActivity.class);
                    startActivity(newint);
                    /*
                    CharSequence options[]= new CharSequence[]{"Day 0", "Day 1", "Day 2", "Day 3"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Which day's schedule do you want?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            if(i==0)
                            {
                                FirebaseDatabase.getInstance().getReference().child("schedule").child("day0").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Intent myint= new Intent(MainActivity.this, ImageActivity.class);
                                        myint.putExtra("image", dataSnapshot.getValue().toString());
                                        startActivity(myint);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            if(i==1)
                            {
                                FirebaseDatabase.getInstance().getReference().child("schedule").child("day1").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Intent myint= new Intent(MainActivity.this, ImageActivity.class);
                                        myint.putExtra("image", dataSnapshot.getValue().toString());
                                        startActivity(myint);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            if(i==2)
                            {
                                FirebaseDatabase.getInstance().getReference().child("schedule").child("day2").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Intent myint= new Intent(MainActivity.this, ImageActivity.class);
                                        myint.putExtra("image", dataSnapshot.getValue().toString());
                                        startActivity(myint);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            if(i==3)
                            {
                                FirebaseDatabase.getInstance().getReference().child("schedule").child("day3").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Intent myint= new Intent(MainActivity.this, ImageActivity.class);
                                        myint.putExtra("image", dataSnapshot.getValue().toString());
                                        startActivity(myint);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        }
                    });
                    builder.show();*/
                }
                if(item.getItemId()==R.id.nav_sponsors)
                {
                    Intent myint= new Intent(MainActivity.this, SponsorsActivity.class);
                    startActivity(myint);
                }
                if(item.getItemId()==R.id.nav_about)
                {
                    Intent myint= new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(myint);
                }


                return true;
            }
        });
        mlayout= (DrawerLayout) findViewById(R.id.activity_home);
        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mtoggle=new ActionBarDrawerToggle(MainActivity.this, mlayout, R.string.open, R.string.close);
        mlayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    public void sendtostart()
    {
        Intent startIntent = new Intent(MainActivity.this, StartedActivity.class);
        startActivity(startIntent);
        finish();
    }
    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendtostart();
        } else {
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
            mUsersDatabase.child("online").setValue("true");
        }

        assert currentUser != null;
        loadtext.setVisibility(View.VISIBLE);
        seticons();
    }
    public void seticons(){
        mRootRef.child("post_sort").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("GQabHE6r5zdP8a6QACtcc9BvrIj2"))
                {
                    mtext.setVisibility(View.INVISIBLE);
                    mdata= FirebaseDatabase.getInstance().getReference().child("post_sort").child("GQabHE6r5zdP8a6QACtcc9BvrIj2"/*mAuth.getCurrentUser().getUid()*/);
                    Query mquery= mdata.orderByChild("timestamp");
                    mquery.keepSynced(true);
                    FirebaseRecyclerOptions<PostImage> options=
                            new FirebaseRecyclerOptions.Builder<PostImage>()
                                    .setQuery(mquery,PostImage.class).build();
                    madapter= new FirebaseRecyclerAdapter<PostImage, UsersViewHolder3>(options) {


                        @NonNull
                        @Override
                        public UsersViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_images, parent, false);
                            return new UsersViewHolder3(view);

                        }
                        @Override
                        protected void onBindViewHolder(UsersViewHolder3 holder, int position, final PostImage model) {
                            final String userid = getRef(position).getKey();
                            holder.setImage(model.getFrom_image(), getApplicationContext(),userid);
                            if(position==0) {
                                loadposts(userid,model.getFrom_image());
                            }
                            Log.d("check1", "reached this point1");

                            Log.d("check2", "reached this point2");

                        }

                    };
                    mrecyclerview.setAdapter(madapter);
                    madapter.startListening();
                    loadtext.setVisibility(View.INVISIBLE);
                }
                else
                {
                    mtext.setVisibility(View.VISIBLE);
                    loadtext.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        }

    public void loadposts(final String userid, final String from_image) {
        Log.d("tah", "this is reached");
        DatabaseReference mdata3= FirebaseDatabase.getInstance().getReference().child("unseen_posts")
                .child("GQabHE6r5zdP8a6QACtcc9BvrIj2").child(userid);
        Query mq= mdata3.orderByChild("timestamp");
        mq.keepSynced(true);
        FirebaseRecyclerOptions<Post> options=
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(mq,Post.class).build();
        madapter2= new FirebaseRecyclerAdapter<Post, UsersViewHolder4>(options) {

            @NonNull
            @Override
            public UsersViewHolder4 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
                return new UsersViewHolder4(view);
            }
            @Override
            protected void onBindViewHolder(final UsersViewHolder4 holder, int position, final Post model) {
                final String userid= model.getFrom();
                holder.setImage(model.getLink(),getApplicationContext(),getRef(position).getKey(),userid);
                holder.setDP(from_image,getApplicationContext(),userid);
                holder.setNameAndTime(model.getFrom_name(), model.getTimestamp(), getApplicationContext(),userid);
                holder.setCaption(model.getCaption());

            }

        };
            mrecyclerview2.setAdapter(madapter2);
            madapter2.startListening();
        Log.d("tah2", "this is reached");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        mUsersDatabase.child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth.getCurrentUser()!=null) {
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
            mUsersDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.menuwebsite)
        {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.comfest.in"));
            startActivity(browserIntent);
        }
        if(item.getItemId()==R.id.menufacebook)
        {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/ComfestOfficial/"));
            startActivity(browserIntent);
        }
        if(item.getItemId()==R.id.menuinstagram)
        {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/comfestofficial/"));
            startActivity(browserIntent);
        }
        if(item.getItemId()==R.id.menumailus)
        {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mailto:info@comfest.in"));
            startActivity(browserIntent);
        }
        if(item.getItemId()==R.id.logoutmain) {
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
            mUsersDatabase.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            sendtostart();
        }
        if(item.getItemId()==R.id.settingsacc)
        {
            Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent1);
        }
        if(item.getItemId()==R.id.maildev)
        {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mailto:ishu.gupta2003@gmail.com"));
            startActivity(browserIntent);
        }
        if(mtoggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return true;
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2&&resultCode==RESULT_OK)
        {
            Uri imageuri = data.getData();

            CropImage.activity(imageuri).start(MainActivity.this);
        }
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Let's caption this!");
                final EditText input= new EditText(MainActivity.this);
                builder.setView(input);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str=input.getText().toString();
                        post(str, result);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"Just press back", Toast.LENGTH_SHORT);
                    }
                });
                builder.show();
              /*  final AlertDialog ad= builder.create();
                final String[] textmera = {""};
                mf.findViewById(R.id.donebut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textmera[0]=mn.getText().toString();
                        post(textmera[0], result);
                        ad.cancel();
                    }
                });
                mf.findViewById(R.id.cancelbut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.cancel();
                    }
                });*/

                /*
                builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textmera[0]=input.getText().toString();
                        post(textmera[0], result);
                        ad.cancel();
                    }
                });
                builder.setNegativeButton("Discard Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.cancel();
                    }
                });
                */
               // ad.show();

              /*    if(n[0]==1) {
                  mprogbar.setVisibility(View.VISIBLE);
                    mposttext.setVisibility(View.VISIBLE);
                    Uri imageUri = result.getUri();
                    final String mcurrentuserid = mAuth.getCurrentUser().getUid();
                    DatabaseReference user_message_push = mRootRef.child("posts")
                            .child(mcurrentuserid).push();
                    final String push_id = user_message_push.getKey();
                    mRootRef.child("User").child(mcurrentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            nameofuser = dataSnapshot.child("name").getValue().toString();
                            imageofuser = dataSnapshot.child("thumb_image").getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    StorageReference filepath = mstore.child("post_images").child(push_id + ".jpg");
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                final String download_url = task.getResult().getDownloadUrl().toString();
                                final Map usermap2 = new HashMap();
                                usermap2.put("link", download_url);
                                usermap2.put("rate", 0);
                                usermap2.put("caption", textmera[0]);
                                mRootRef.child("posts").child(mcurrentuserid).child(push_id).setValue(usermap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mRootRef.child("Friends").child(mcurrentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    final Map usermap = new HashMap();
                                                    usermap.put("from", mcurrentuserid);
                                                    usermap.put("link", download_url);
                                                    usermap.put("from_name", nameofuser);
                                                    usermap.put("timestamp", ServerValue.TIMESTAMP);
                                                    usermap.put("caption", textmera[0]);
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        final String friendid = ds.getKey();
                                                        mRootRef.child("unseen_posts").child(friendid).child(mcurrentuserid).child(push_id).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    final Map usermap3 = new HashMap();
                                                                    usermap3.put("from_image", imageofuser);
                                                                    usermap3.put("timestamp", ServerValue.TIMESTAMP);
                                                                    mRootRef.child("post_sort").child(friendid).child(mcurrentuserid).setValue(usermap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                mprogbar.setVisibility(View.INVISIBLE);
                                                                                mposttext.setVisibility(View.INVISIBLE);
                                                                            } else {
                                                                                Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                                                                mprogbar.setVisibility(View.INVISIBLE);
                                                                                mposttext.setVisibility(View.INVISIBLE);
                                                                            }
                                                                        }
                                                                    });

                                                                } else {
                                                                    Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                                                    mprogbar.setVisibility(View.INVISIBLE);
                                                                    mposttext.setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    mprogbar.setVisibility(View.INVISIBLE);
                                                    mposttext.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                            mprogbar.setVisibility(View.INVISIBLE);
                                            mposttext.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });

                            }
                        }
                    });
                }*/
            }
        }
    }
    public void post(final String caption,CropImage.ActivityResult result)
    {
        mprogbar.setVisibility(View.VISIBLE);
        mposttext.setVisibility(View.VISIBLE);
        Uri imageUri = result.getUri();
        final String mcurrentuserid = mAuth.getCurrentUser().getUid();
        DatabaseReference user_message_push = mRootRef.child("posts")
                .child(mcurrentuserid).push();
        final String push_id = user_message_push.getKey();
        mRootRef.child("User").child(mcurrentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameofuser = dataSnapshot.child("name").getValue().toString();
                imageofuser = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final StorageReference filepath = mstore.child("post_images").child(push_id + ".jpg");

        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    final String[] download_url = {""};
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_url[0]=uri.toString();
                            final Map usermap2 = new HashMap();
                            usermap2.put("link", download_url[0]);
                            usermap2.put("rate", 0);
                            usermap2.put("caption", caption);
                            mRootRef.child("posts").child(mcurrentuserid).child(push_id).setValue(usermap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mRootRef.child("Friends").child(mcurrentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                final Map usermap = new HashMap();
                                                usermap.put("from", mcurrentuserid);
                                                usermap.put("link", download_url[0]);
                                                usermap.put("from_name", nameofuser);
                                                usermap.put("timestamp", "-"+ServerValue.TIMESTAMP);
                                                usermap.put("caption", caption);
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    final String friendid = ds.getKey();
                                                    mRootRef.child("unseen_posts").child(friendid).child(mcurrentuserid).child(push_id).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                final Map usermap3 = new HashMap();
                                                                usermap3.put("from_image", imageofuser);
                                                                usermap3.put("timestamp", "-"+ServerValue.TIMESTAMP);
                                                                mRootRef.child("post_sort").child(friendid).child(mcurrentuserid).setValue(usermap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            mprogbar.setVisibility(View.INVISIBLE);
                                                                            mposttext.setVisibility(View.INVISIBLE);
                                                                        } else {
                                                                            Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                                                            mprogbar.setVisibility(View.INVISIBLE);
                                                                            mposttext.setVisibility(View.INVISIBLE);
                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                                                mprogbar.setVisibility(View.INVISIBLE);
                                                                mposttext.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                mprogbar.setVisibility(View.INVISIBLE);
                                                mposttext.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.this, "A problem occurred, try again later", Toast.LENGTH_SHORT).show();
                                        mprogbar.setVisibility(View.INVISIBLE);
                                        mposttext.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        }
                    });


                }
            }
        });
    }
    public class UsersViewHolder3 extends RecyclerView.ViewHolder{

        View mview;
        public UsersViewHolder3(View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setImage(final String myurl, Context ctx, final String userid)
        {
            ImageButton mbut= (ImageButton) mview.findViewById(R.id.userdp_post);
            Picasso.with(ctx).load(myurl).placeholder(R.drawable.defpic).into(mbut);
            mbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("check3", "reached this point3");
                    loadposts(userid,myurl);
                }
            });
        }

    }
    public class UsersViewHolder4 extends RecyclerView.ViewHolder{

        View mview;

        public UsersViewHolder4(View itemView) {
            super(itemView);
            mview=itemView;
        }



        public void setImage(final String myurl, Context ctx, final String key, final String userid)
        {
            final ImageButton mbut= (ImageButton) mview.findViewById(R.id.post_image);

            Picasso.with(ctx).load(myurl).placeholder(R.drawable.defpic).into(mbut);
            mbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myint= new Intent(MainActivity.this, ImageActivity.class);
                    myint.putExtra("image",myurl);
                    startActivity(myint);
                }
            });
            mRootRef.child("posts").child(userid).child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("rating"))
                    {
                        mRootRef.child("posts").child(userid).child(key).child("rating").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()!=0)
                                {

                                    if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                                    {
                                        Button mbut32=(Button) mview.findViewById(R.id.yourvote);
                                        mbut32.setText("+1");
                                    }

                                    long numberofvotes= dataSnapshot.getChildrenCount();
                                    Button mbuto= (Button) mview.findViewById(R.id.totalvote);
                                    String ms= "";
                                    if(numberofvotes==1)
                                    {
                                        ms="";
                                    }
                                    mbuto.setText(numberofvotes+ms);
                                }
                                else {
                                    Button mbuto= (Button) mview.findViewById(R.id.totalvote);
                                    mbuto.setText("0");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        Button mbuto= (Button) mview.findViewById(R.id.totalvote);
                        mbuto.setText("0");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Log.d("just a message", key);
            ImageButton rate5= (ImageButton) mview.findViewById(R.id.rate5);
            final Button mbut32=(Button) mview.findViewById(R.id.yourvote);
            rate5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRootRef.child("posts").child(userid).child(key).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                            {
                                mRootRef.child("posts").child(userid).child(key).child("rating").child(mAuth.getCurrentUser().getUid()).setValue(5);
                                mbut32.setText("+1");
                            }
                            else
                            {
                                if((long)dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue()!=5)
                                {
                                    mRootRef.child("posts").child(userid).child(key).child("rating").child(mAuth.getCurrentUser().getUid()).setValue(5);
                                    mbut32.setText("+1");
                                }
                                else
                                {
                                    mRootRef.child("posts").child(userid).child(key).child("rating").child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mbut32.setText("...");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        }
        public void setDP(String myurl, Context ctx, final String userid)
        {
            ImageButton mbut2= (ImageButton) mview.findViewById(R.id.post_dp);
            Picasso.with(ctx).load(myurl).placeholder(R.drawable.defpic).into(mbut2);


            mbut2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profintent = new Intent(MainActivity.this, ProfilesActivity.class);
                    profintent.putExtra("userid", userid);
                    startActivity(profintent);
                }
            });

        }
        public void setNameAndTime(String name, Long time, Context ctx, final String userid)
        {
            Button mbut3= (Button) mview.findViewById(R.id.post_name_button);
            Button mbut4= (Button) mview.findViewById(R.id.post_time_button);
            String time2= TimeSinceAgo.getTimeAgo(time, ctx);
            mbut3.setText(name);
            mbut4.setText("("+time2+")");
            mbut3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profintent = new Intent(MainActivity.this, ProfilesActivity.class);
                    profintent.putExtra("userid", userid);
                    startActivity(profintent);
                }
            });
        }

        public void setCaption(String caption) {
            Button capbut= (Button) mview.findViewById(R.id.caption_post);
            capbut.setText(caption);
        }
    }
}

