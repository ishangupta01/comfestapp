package com.comfest.cf18;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SponsorsActivity extends AppCompatActivity {
private FirebaseRecyclerAdapter myadapter;
    private RecyclerView mlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.sponsorstoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Sponsors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mlist=(RecyclerView) findViewById(R.id.sponsorsrecycler);
        mlist.setHasFixedSize(true);
        mlist.setLayoutManager(new LinearLayoutManager(this));
        Query mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("sponsors");
        mFriendsDatabase.keepSynced(true);
        FirebaseRecyclerOptions<Sponsors> options =
                new FirebaseRecyclerOptions.Builder<Sponsors>()
                        .setQuery(mFriendsDatabase, Sponsors.class).build();

        //new
        myadapter = new FirebaseRecyclerAdapter<Sponsors, SponsorsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(SponsorsViewHolder holder, int position, Sponsors model) {
                    holder.setImage(model.getUrl());
            }
            @NonNull
            @Override
            public SponsorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sponsorlayoutsingle, parent, false);
                return new SponsorsViewHolder(view);
            }
        };
        mlist.setAdapter(myadapter);
        myadapter.startListening();
    }

    public class SponsorsViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public SponsorsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setImage(String url)
        {
            TwoThreeImage a=mview.findViewById(R.id.sponsorimage);
            Picasso.with(SponsorsActivity.this).load(url).placeholder(R.drawable.defpic).into(a);
        }
    }
}
