package com.comfest.cf18;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private String mcuruserid;
    private View mMainView;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerAdapter myadapter;
    public FriendsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       mMainView= inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList= (RecyclerView) mMainView.findViewById(R.id.friendfragview);
        mAuth= FirebaseAuth.getInstance();
        mcuruserid= mAuth.getCurrentUser().getUid();

        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mcuruserid);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friends> options=
                new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase,Friends.class).build();

        //new
        myadapter= new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single, parent, false);
                return new FriendsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final FriendsViewHolder viewHolder, int i, Friends model) {
                final String list_user_id = getRef(i).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName= dataSnapshot.child("name").getValue().toString();
                        String status= dataSnapshot.child("status").getValue().toString();
                        final String userThumb= dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online"))
                        {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }

                        viewHolder.setName(userName);
                        viewHolder.setStatus(status);
                        viewHolder.setImage(userThumb, getContext());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]{"Open Profile", "Chat with "+userName};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("What do you want to do?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(i==0) {

                                            Intent profintent = new Intent(getContext(), ProfilesActivity.class);
                                            profintent.putExtra("userid", list_user_id);
                                            startActivity(profintent);

                                        }
                                        if(i==1)
                                        {
                                            Intent chatintent = new Intent(getContext(), ChattingActivity.class);
                                            chatintent.putExtra("userid", list_user_id);
                                            chatintent.putExtra("username", userName);
                                            chatintent.putExtra("userimage", userThumb);
                                            startActivity(chatintent);
                                        }


                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        };
        mFriendsList.setAdapter(myadapter);
        myadapter.startListening();
    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView)
        {
            super(itemView);
            mView= itemView;
        }
        public void setStatus(String date)
        {
            TextView userNameView= (TextView) mView.findViewById(R.id.status_alluser);
            userNameView.setText(date);
        }
        public void setName(String name)
        {
            TextView userNameView = (TextView) mView.findViewById(R.id.display_name_alluser);
            userNameView.setText(name);
        }
        public void setImage(String thumb_image, Context ctx) {
            ImageView userimageview = (ImageView) mView.findViewById(R.id.image_user);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defpic).into(userimageview);
        }
        public void setUserOnline(String online_status){
            ImageView useronline = (ImageView) mView.findViewById(R.id.onlineicon);
            if(online_status.equals("true")){
                useronline.setVisibility(View.VISIBLE);
            }
            else
            {
                useronline.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        myadapter.stopListening();
    }
}








