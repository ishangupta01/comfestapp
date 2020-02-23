package com.comfest.cf18;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {
private RecyclerView mresultlist;
    private ImageButton mSearchbtn;
    private EditText msearchfield;
    private DatabaseReference mdatabase;

    private DatabaseReference mdata2;
    private Toolbar mtoolbar;
    private FirebaseRecyclerAdapter myadapter4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mresultlist= (RecyclerView) findViewById(R.id.result_list);
        mresultlist.setHasFixedSize(true);
        mresultlist.setLayoutManager(new LinearLayoutManager(this));
        mSearchbtn= (ImageButton) findViewById(R.id.searchbutton);
        msearchfield=(EditText) findViewById(R.id.searchfield);
        mdata2= FirebaseDatabase.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("User");
        mtoolbar=(Toolbar) findViewById(R.id.searchtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Search For Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext= msearchfield.getText().toString();
                if(!TextUtils.isEmpty(searchtext)) {
                    firebaseUserSearch(searchtext);
                }
            }


        });

    }

    private void firebaseUserSearch(String searchtext) {
        Query firebasesearchquery = mdatabase.orderByChild("name").startAt(searchtext).endAt(searchtext + "\uf8ff");

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(firebasesearchquery,Users.class).build();

        myadapter4= new FirebaseRecyclerAdapter<Users, UsersViewHolder2>(options) {
            @NonNull
            @Override
            public UsersViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single, parent, false);
                return new UsersViewHolder2(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder2 holder, int position, Users model) {
                holder.setDetails(getApplicationContext(),model.getName(), model.getStatus(), model.getThumb_image());
                final String userid = getRef(position).getKey();
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profintent = new Intent(SearchActivity.this, ProfilesActivity.class);
                        profintent.putExtra("userid", userid);
                        startActivity(profintent);
                    }
                });
            }

        };
        //old
       /* FirebaseRecyclerAdapter<Users, UsersViewHolder2> firebaserecycleradapter= new FirebaseRecyclerAdapter<Users, UsersViewHolder2>
                (Users.class,R.layout.users_single,UsersViewHolder2.class, firebasesearchquery) {
            @Override
            protected void populateViewHolder(UsersViewHolder2 viewHolder, Users model, int position) {

                viewHolder.setDetails(getApplicationContext(),model.getName(), model.getStatus(), model.getThumb_image());

            }
        };*/
        mresultlist.setAdapter(myadapter4);
        myadapter4.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public class UsersViewHolder2 extends RecyclerView.ViewHolder{
View mview;
        public UsersViewHolder2(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setDetails(Context ctx, String name, String status, String image)
        {
            TextView user_name= (TextView) mview.findViewById(R.id.display_name_alluser);
            TextView userstatus= (TextView) mview.findViewById(R.id.status_alluser);
            ImageView mimageview= (ImageView) mview.findViewById(R.id.image_user);

            user_name.setText(name);
            userstatus.setText(status);
            Picasso.with(ctx).load(image).placeholder(R.drawable.defpic).into(mimageview);

        }
    }
}
