package com.comfest.cf18;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class EventsActivity extends AppCompatActivity {
private RecyclerView mlist;
    private FirebaseRecyclerAdapter myadapter10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.eventstoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Events");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mlist=(RecyclerView) findViewById(R.id.eventsrecyclerview);
        mlist.setHasFixedSize(true);
        mlist.setLayoutManager(new LinearLayoutManager(EventsActivity.this));
        String category= getIntent().getStringExtra("category");
        Query mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("events").child(category);
        mFriendsDatabase.keepSynced(true);
        FirebaseRecyclerOptions<Events> options =
                new FirebaseRecyclerOptions.Builder<Events>()
                        .setQuery(mFriendsDatabase, Events.class).build();
        myadapter10 = new FirebaseRecyclerAdapter<Events, EventsViewHolder>(options) {
            @NonNull
            @Override
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.eventlayout, parent, false);
                return new EventsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(EventsViewHolder holder, int position, Events model) {
                holder.setImage(model.getImageurl());
                holder.setHeading(model.getHeading());
                holder.setText(model.getText());

            }
        };
        mlist.setAdapter(myadapter10);
        myadapter10.startListening();
    }
    public class EventsViewHolder extends RecyclerView.ViewHolder{
        public EventsViewHolder(View itemView) {
            super(itemView);
        }
        public void setImage(String imageurl){
            TwoThreeImage mimage=itemView.findViewById(R.id.eventimage);
            Picasso.with(EventsActivity.this).load(imageurl).placeholder(R.drawable.cfest500px).into(mimage);
        }
        public void setHeading(String heading){
            TextView mtext= itemView.findViewById(R.id.eventheading);
            mtext.setText(heading);
        }
        public void setText(String text){
            TextView utext= itemView.findViewById(R.id.eventtext);
            utext.setText(text);
        }
    }
}
