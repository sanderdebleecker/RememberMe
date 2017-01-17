package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.SessionPagerFragment;

public class SessionPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<SessionPagerFragment> mFragments = new ArrayList<>();
    private int duration;

    public SessionPagerAdapter(FragmentManager fm, ArrayList<SessionPagerFragment> frags) {
        super(fm);
        this.mFragments = frags;
    }
    public void add(SessionPagerFragment frag) {
        mFragments.add(frag);
    }
    public void remove(int pos) {
        mFragments.remove(pos);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    //GETSET
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getItemPosition(Object object) {
        int position = mFragments.indexOf(object);
        return position == -1 ? POSITION_NONE : position;
    }
}
