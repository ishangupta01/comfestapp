package com.comfest.cf18;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;


import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ProgressBar mprogbar;
    private PhotoView mphoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mtoolbar = (Toolbar) findViewById(R.id.image_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("View Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mprogbar= (ProgressBar) findViewById(R.id.progbarimage);
        mprogbar.setVisibility(View.VISIBLE);
        String url = getIntent().getStringExtra("image");
        mphoto= (PhotoView) findViewById(R.id.imagetosee);
        Picasso.with(ImageActivity.this).load(url).into(mphoto, new Callback() {
            @Override
            public void onSuccess() {
                mprogbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {

            }
        });



    }
}
