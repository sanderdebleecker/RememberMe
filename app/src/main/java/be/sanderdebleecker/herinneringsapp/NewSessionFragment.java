package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
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
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SessionPagerAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Data.SessionDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewSessionFListener;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class NewSessionFragment extends Fragment {
    private INewSessionFListener mListener;
    private SessionPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private Toolbar mToolbar;
    private MenuItem menuContinue;
    private List<Memory> mMemories;

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
        new Initializer().execute(v);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                mListener.cancel();
                break;
            case R.id.action_add:
                if (mMemories == null) {
                    NewSessionPagerFragment fragm = (NewSessionPagerFragment) mPagerAdapter.getItem(mPager.getCurrentItem());
                    if(fragm.validate()) {
                        MainApplication app = (MainApplication) getContext().getApplicationContext();
                        createSession(new Session(fragm.getName(),fragm.getDate(),app.getCurrSession().getAuthIdentity()),fragm.getAlbums());
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSession(Session newSession,List<Integer> albums) {
        //Acquire mems
        //methodname must correspond w/ content
        MemoryDA memoryData = new MemoryDA(getContext());
        memoryData.open();
        mMemories = memoryData.getAllFromAlbums(albums);
        memoryData.close();

        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        sessionData.insert(newSession,albums);
        sessionData.close();



        //Save session
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add,menu);
        menuContinue = menu.findItem(R.id.action_add);
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

    //Tasks
    private class Initializer extends AsyncTask<View, Void, Void> {

        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            loadViewPager();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            createToolbar();
        }
    }
}
