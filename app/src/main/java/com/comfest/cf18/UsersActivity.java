package com.comfest.cf18;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView muserslist;
    private DatabaseReference mdatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter myadapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);



        mtoolbar = (Toolbar) findViewById(R.id.allusertoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Search for Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mdatabase = FirebaseDatabase.getInstance().getReference().child("User");

        muserslist = (RecyclerView) findViewById(R.id.user_list);
        muserslist.setHasFixedSize(true);
        LinearLayoutManager llm= new LinearLayoutManager(getApplicationContext());
        muserslist.setLayoutManager(llm);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUsersDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mUsersDatabase.child("online").setValue("true");
        FirebaseRecyclerOptions<Users> options=
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mdatabase,Users.class).build();
        //new

        myadapter2= new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {


            @Override
            public UsersViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single, parent, false);
                return new UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(UsersViewHolder viewHolder, int position, Users users) {
                viewHolder.setName(users.getName());
                viewHolder.setUserStatus(users.getStatus());
                viewHolder.setImage(users.getThumb_image(), getApplicationContext());

                final String userid = getRef(position).getKey();

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profintent = new Intent(UsersActivity.this, ProfilesActivity.class);
                        profintent.putExtra("userid", userid);
                        startActivity(profintent);
                    }
                });
            }

        };

        //old
 /*       FirebaseRecyclerAdapter<Users, UsersViewHolder>firebaseRecyclerAdapter= new
                FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class, R.layout.users_single,
                        UsersViewHolder.class, mdatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int position) {

                viewHolder.setName(users.getName());
                viewHolder.setUserStatus(users.getStatus());
                viewHolder.setImage(users.getThumb_image(), getApplicationContext());

                final String userid = getRef(position).getKey();

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profintent = new Intent(UsersActivity.this, ProfilesActivity.class);
                        profintent.putExtra("userid", userid);
                        startActivity(profintent);
                    }
                });
            }
        };
*/
        muserslist.setAdapter(myadapter2);
        myadapter2.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myadapter2.stopListening();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setName(String name)
        {
            TextView musernameview = (TextView) mview.findViewById(R.id.display_name_alluser);
            musernameview.setText(name);
        }
        public void setUserStatus(String statusmera)
        {
            TextView musernameview = (TextView) mview.findViewById(R.id.status_alluser);
            musernameview.setText(statusmera);
        }
        public void setImage(String thumb_image, Context ctx)
        {
            ImageView userimageview = (ImageView) mview.findViewById(R.id.image_user);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defpic).into(userimageview);
        }
    }

}
