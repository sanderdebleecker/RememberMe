package be.sanderdebleecker.herinneringsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SessionPagerAdapter;

public class NewSessionFragment extends Fragment {
    private SessionPagerAdapter mPagerAdapter;
    private ViewPager mPager;


    public NewSessionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_session, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        loadView(v);
        loadViewPager();
    }
    private void loadView(View v) {
        mPager = (ViewPager) v.findViewById(R.id.new_session_viewpager);
    }
    private void loadViewPager()  {
        ArrayList<SessionPagerFragment> frags = new ArrayList<>();
        frags.add(NewSessionPagerFragment.newInstance());
        mPagerAdapter = new SessionPagerAdapter(getActivity().getSupportFragmentManager(),frags);
        mPager.setAdapter(mPagerAdapter);

    }



}