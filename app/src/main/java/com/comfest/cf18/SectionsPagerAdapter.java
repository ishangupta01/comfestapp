package com.comfest.cf18;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ishan on 18/1/18.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter{

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatFragment chatf= new ChatFragment();
                return chatf;
            case 1:
                FriendsFragment friendsf= new FriendsFragment();
                return friendsf;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int position)
    {
        switch(position){
            case 0:
                return "Messages";
            case 1:
                return "Coordinators";

            default:
                return null;
        }
    }
}
