package org.onlineservice.rand.login;

/**
 * Created by Lillian Wu on 2016/7/20.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CarInfo tab1 = new CarInfo();
                return tab1;
            case 1:
                Garage tab2 = new Garage();
                return tab2;
            case 2:
                Nevigation tab3 = new Nevigation();
                return tab3;
            case 3:
               Setting tab4 = new Setting();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
