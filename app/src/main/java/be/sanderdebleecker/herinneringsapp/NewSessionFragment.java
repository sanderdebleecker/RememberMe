package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SessionPagerAdapter;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewSessionFListener;

public class NewSessionFragment extends Fragment {
    private INewSessionFListener mListener;
    private SessionPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private Toolbar mToolbar;

    public NewSessionFragment() {}
    public static NewSessionFragment newInstance() {
        return new NewSessionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (INewSessionFListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener == null) {
            mListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_session, container, false);
        loadView(v);
        init();
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                mListener.cancel();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add,menu);
    }
    private void init() {
        loadViewPager();
        createToolbar();
    }
    private void loadView(View v) {
        mPager = (ViewPager) v.findViewById(R.id.new_session_viewpager);
        mToolbar = (Toolbar) v.findViewById(R.id.new_session_toolbar);

    }
    private void loadViewPager()  {
        ArrayList<SessionPagerFragment> frags = new ArrayList<>();
        frags.add(NewSessionPagerFragment.newInstance());
        mPagerAdapter = new SessionPagerAdapter(getActivity().getSupportFragmentManager(),frags);
        mPager.setAdapter(mPagerAdapter);
    }
    private void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
    }
}
