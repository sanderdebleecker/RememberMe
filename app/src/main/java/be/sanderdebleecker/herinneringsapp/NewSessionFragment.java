package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
    private int mSessionId;

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
                    new SaveSession().execute(fragm);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private int createSession(Session newSession,List<Integer> albums) {
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        int sessionId = sessionData.insert(newSession,albums);
        sessionData.close();
        return sessionId;
    }
    private void startSession() {
        //Get Memories
        getMemories(getAlbums(mSessionId));
        //Add MemoryFragments
        loadSession();
    }
    private void loadSession() {
        for(int i=0;i<mMemories.size();i++) {
            Memory m = mMemories.get(i);
            MemorySessionPagerFragment frag = MemorySessionPagerFragment.newInstance(m.getTitle(),m.getPath(),m.getType());
            mPagerAdapter.add(frag);
        }
        mPagerAdapter.notifyDataSetChanged();
    }
    private void getMemories(List<Integer> albums) {
        MemoryDA memoryData = new MemoryDA(getContext());
        memoryData.open();
        mMemories = memoryData.getAllFromAlbums(albums);
        memoryData.close();
    }
    private List<Integer> getAlbums(int session) {
        List<Integer> albums;
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        albums = sessionData.getAlbums(session);
        sessionData.close();
        return albums;
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
    private class SaveSession extends AsyncTask<NewSessionPagerFragment, Void, Integer> {

        @Override
        protected Integer doInBackground(NewSessionPagerFragment... fragms) {
            NewSessionPagerFragment fragm = fragms[0];
            if(fragm.validate()) {
                MainApplication app = (MainApplication) getContext().getApplicationContext();
                List<Integer> albums = fragm.getAlbums();
                return createSession(new Session(fragm.getName(),fragm.getDate(),app.getCurrSession().getAuthIdentity()),albums);
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer sessionId) {
            if(sessionId==-1){
                getSaveSessionFailureDialog().show();
            }else{
                mSessionId = sessionId;
                getStartSessionDialog().show();
            }
        }
    }
    //Dialogs
    private AlertDialog getStartSessionDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Nieuwe Sessie")
                .setMessage("Sessie direct starten?")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startSession();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.cancel();
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }
    private AlertDialog getSaveSessionFailureDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Opslaan Sessie")
                .setMessage("Er was een fout bij het opslaan")
                .setPositiveButton("Doorgaan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }
}
