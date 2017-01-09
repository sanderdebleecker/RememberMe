package be.sanderdebleecker.herinneringsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SessionPagerAdapter;

public class NewSessionFragment extends Fragment {
    private SessionPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private Toolbar mToolbar;


    public NewSessionFragment() {
        // Required empty public constructor
    }
    public static NewSessionFragment newInstance() {
        return new NewSessionFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_session, container, false);
        init(v);
        return v;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add,menu);
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
        frags.add(SessionPagerFragment.newInstance(SessionPagerFragment.Pages.NewSession));
        mPagerAdapter = new SessionPagerAdapter(getActivity().getSupportFragmentManager(),frags);
        mPager.setAdapter(mPagerAdapter);
    }

}
