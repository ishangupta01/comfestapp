package com.comfest.cf18;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartedActivity extends AppCompatActivity {
    private ViewPager mviewpager;

    private SliderAdapter mslideradapter;
    private Button nextbut;
    private Button prevbut;
    private Button skipbut;
    private int mcurpage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);
        mviewpager= (ViewPager) findViewById(R.id.viewpagerstart);

        mslideradapter= new SliderAdapter(this);
        mviewpager.setAdapter(mslideradapter);
        nextbut=(Button) findViewById(R.id.nextbut);
        skipbut= (Button) findViewById(R.id.skipbut);
        prevbut=(Button) findViewById(R.id.prevbut);
        nextbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcurpage!=2)
                {
                mviewpager.setCurrentItem(mcurpage+1);
            }
            else{
                    Intent loadintent= new Intent(StartedActivity.this, HomeActivity.class);
                    startActivity(loadintent);
                }
            }
        });
        skipbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadintent= new Intent(StartedActivity.this, HomeActivity.class);
                startActivity(loadintent);
            }
        });
        prevbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mviewpager.setCurrentItem(mcurpage-1);
            }
        });

        ViewPager.OnPageChangeListener viewlistener= new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                    mcurpage=i;
                if(i==0)
                {
                    prevbut.setEnabled(false);
                    nextbut.setEnabled(true);
                    prevbut.setVisibility(View.INVISIBLE);
                    nextbut.setText("Next");
                } else if (i==1) {

                    prevbut.setEnabled(true);
                    nextbut.setEnabled(true);
                    prevbut.setVisibility(View.VISIBLE);
                    nextbut.setText("Next");
                }
                else if(i==2)
                {
                    prevbut.setEnabled(true);
                    nextbut.setEnabled(true);
                    prevbut.setVisibility(View.VISIBLE);
                    nextbut.setText("Finish");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mviewpager.addOnPageChangeListener(viewlistener);
    }

}
