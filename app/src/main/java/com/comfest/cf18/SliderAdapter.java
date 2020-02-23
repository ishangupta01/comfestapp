package com.comfest.cf18;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ishan on 10/2/18.
 */

public class SliderAdapter extends PagerAdapter{

    Context ctx;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context ctx)
    {
        this.ctx=ctx;

    }
    public int[] slideimages
            = {
            R.drawable.cfest350,
            R.drawable.jcccircle,
            R.drawable.friends
    };
    public String[] slideheadings
            = {

            "Comfest 2018",
            "Information",
            "For Participants"
    };
    public String[] slidedescrip
            = {

            "Welcome to the official app of Comfest, an annual national-level techno-cultural event organized by the Jaipuria Computer Club at Seth Anandram Jaipuria School, Kanpur.",
            "Simply login and get instant and easy access to all information regarding events, schedule, updates etc.",
            "Stay updated on any announcements, information and posts by the JCC. The app allows you to easily stay in touch with event coordinators and the core team."
    };

    @Override
    public int getCount() {
        return slideheadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view== (RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater= (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView mimage= (ImageView) view.findViewById(R.id.startedimage);
        TextView mheading= (TextView) view.findViewById(R.id.startedheading);
        TextView mdesc= (TextView) view.findViewById(R.id.starteddescription);
        mimage.setImageResource(slideimages[position]);
        mheading.setText(slideheadings[position]);
        mdesc.setText(slidedescrip[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
