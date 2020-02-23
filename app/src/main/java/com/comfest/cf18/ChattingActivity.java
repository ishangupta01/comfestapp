package com.comfest.cf18;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {

    private String mchatuser;
    private DatabaseReference mRootRef;
    private Toolbar toolbar;
    private FirebaseAuth mauth;
    private String mcurrentuserid;
    private ImageButton msendpicbtn;
    private ImageButton msendbtn;
    private EditText mchatmessageview;
    private RecyclerView mMessagesList;
    private List<Messages> messageList= new ArrayList<>();
    private LinearLayoutManager mlinearlayout;
    private MessageAdapter mAdapter;
    private static final int ITEMSTOLOAD= 32;
    private int mCurrentPage =1;
    private SwipeRefreshLayout mRefreshayout;
    private StorageReference mstore;
    private static int itemposition = 0;
    private String mlastkey="";
    private String mprevkey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        mstore = FirebaseStorage.getInstance().getReference();
        msendpicbtn= (ImageButton) findViewById(R.id.sendpic);
        mchatmessageview= (EditText) findViewById(R.id.chat_message_view);
        msendbtn= (ImageButton) findViewById(R.id.chatsendbtn);
        mauth = FirebaseAuth.getInstance();
        mcurrentuserid= mauth.getCurrentUser().getUid();
        mRootRef=FirebaseDatabase.getInstance().getReference();

        mAdapter= new MessageAdapter(messageList);
        mMessagesList= (RecyclerView) findViewById(R.id.messageslist);
        mRefreshayout= (SwipeRefreshLayout) findViewById(R.id.messageswipelayout);
        mlinearlayout= new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mlinearlayout);
        mMessagesList.setAdapter(mAdapter);


        toolbar = (Toolbar) findViewById(R.id.chatingtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView mlastseen =(TextView) findViewById(R.id.lastseen) ;
        mchatuser= getIntent().getStringExtra("userid");

        String userName= getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(userName);
        mRootRef.child("User").child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String thumbimage= dataSnapshot.child("thumb_image").getValue().toString();
                setuserimage(thumbimage, getApplicationContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msendpicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryint = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryint,3);
            }
        });
        mRootRef.child("User").child(mchatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online= dataSnapshot.child("online").getValue().toString();
                String image= dataSnapshot.child("thumb_image").getValue().toString();
                setImage(image, getApplicationContext());
                if(online.equals("true"))
                {
                    mlastseen.setText("Last online: Right now!");
                }
                else{
                    TimeSinceAgo getTimeAgo = new TimeSinceAgo();
                    long lasttime = Long.parseLong(online);
                    String lastseentime = getTimeAgo.getTimeAgo(lasttime, getApplicationContext());
                    mlastseen.setText("Last online: "+lastseentime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mcurrentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mchatuser))
                {
                    Map chataddmap= new HashMap();
                    chataddmap.put("seen", false);
                    chataddmap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap= new HashMap();
                    chatUserMap.put("Chat/"+mcurrentuserid+"/"+mchatuser, chataddmap);
                    chatUserMap.put("Chat/"+mchatuser+"/"+mcurrentuserid, chataddmap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null)
                            {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString() );
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        loadMessages();
        mRefreshayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemposition=0;
                loadMoreMessages();
            }
        });
    }

    private void loadMoreMessages() {
        final DatabaseReference messageref= mRootRef.child("messages").child(mcurrentuserid).child(mchatuser);
        Query messagequery= messageref.orderByKey().endAt(mlastkey).limitToLast(24);

        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message= dataSnapshot.getValue(Messages.class);

                String messagekey= dataSnapshot.getKey();
                if(!mprevkey.equals(messagekey))
                {
                    messageList.add(itemposition++, message);
                }
                else
                {
                    mprevkey=mlastkey;
                }
                if(itemposition==1)
                {

                    mlastkey=messagekey;
                }
                String mmessagestat= dataSnapshot.getKey();
                messageref.child(mmessagestat).child("seen").setValue(true);
                mAdapter.notifyDataSetChanged();
                mRefreshayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {
        final DatabaseReference messageref= mRootRef.child("messages").child(mcurrentuserid).child(mchatuser);
        Query messagequery= messageref.limitToLast(mCurrentPage*ITEMSTOLOAD);
        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message= dataSnapshot.getValue(Messages.class);

                itemposition++;


                if(itemposition==1)
                {
                    String messagekey= dataSnapshot.getKey();
                    mlastkey=messagekey;
                    mprevkey=messagekey;
                }
                String mmessagestat= dataSnapshot.getKey();
                messageref.child(mmessagestat).child("seen").setValue(true);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messageList.size()-1);
                mRefreshayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        String currentuseref= "messages/"+mcurrentuserid+"/"+mchatuser;
        String chatuseref= "messages/"+mchatuser+"/"+mcurrentuserid;
        String message= mchatmessageview.getText().toString();
        DatabaseReference usermessagepush= mRootRef.child("messages")
                .child(mcurrentuserid).child(mchatuser).push();
        String push_id= usermessagepush.getKey();
        if(!TextUtils.isEmpty(message))
        {
            Map messageMap= new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mcurrentuserid);
            mRootRef.child("Chat").child(mcurrentuserid).child(mchatuser).child("timestamp").setValue(ServerValue.TIMESTAMP);
            mRootRef.child("Chat").child(mchatuser).child(mcurrentuserid).child("timestamp").setValue(ServerValue.TIMESTAMP);
            Map messageusermap= new HashMap();
            messageusermap.put(currentuseref+"/"+push_id, messageMap);
            messageusermap.put(chatuseref+"/"+push_id, messageMap);
            mchatmessageview.setText("");
            mRootRef.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null)
                    {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                    else
                    {
                        mRootRef.child("Chat").child(mchatuser).child(mcurrentuserid).child("seen").setValue(false);
                    }
                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        mUsersDatabase.child("online").setValue("true");
    }
    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        mUsersDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }
    public void setImage(String thumb_image, Context ctx) {
        ImageView userimageview = (ImageView) findViewById(R.id.chatfriendpic);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defpic).into(userimageview);
    }
    public void setuserimage(String thumb_image, Context ctx)
    {
        ImageView userimageview = (ImageView) findViewById(R.id.userimagechat);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defpic).into(userimageview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3&&resultCode==RESULT_OK)
        {
            Uri imageuri = data.getData();

            CropImage.activity(imageuri).start(ChattingActivity.this);
        }
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri imageUri= result.getUri();

                final  String currentuseref= "messages/"+mcurrentuserid+"/"+mchatuser;
                final String chatuseref= "messages/"+mchatuser+"/"+mcurrentuserid;
                DatabaseReference user_message_push= mRootRef.child("messages")
                        .child(mcurrentuserid).child(mchatuser).push();
                final  String push_id= user_message_push.getKey();

                final StorageReference filepath = mstore.child("message_images").child(push_id+".jpg");
                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String[] download_url = {""};
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url[0]=uri.toString();
                                    Map messageMap= new HashMap();
                                    messageMap.put("message", download_url[0]);
                                    messageMap.put("seen", false);
                                    messageMap.put("type", "image");
                                    messageMap.put("time", ServerValue.TIMESTAMP);
                                    messageMap.put("from", mcurrentuserid);

                                    Map messageusermap= new HashMap();
                                    messageusermap.put(currentuseref+"/"+push_id, messageMap);
                                    messageusermap.put(chatuseref+"/"+push_id, messageMap);
                                    mchatmessageview.setText("");

                                    mRootRef.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if(databaseError!=null)
                                            {
                                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    }
                });

            }
        }




            }
    }

