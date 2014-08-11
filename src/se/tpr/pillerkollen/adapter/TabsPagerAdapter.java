package se.tpr.pillerkollen.adapter;
import se.tpr.pillerkollen.SettingsFragment;
import se.tpr.pillerkollen.medicines.MedicinesFragment;
import se.tpr.pillerkollen.schedule.ScheduleFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new ScheduleFragment();
        case 1:
            return new MedicinesFragment();
        case 2:
            return new SettingsFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}