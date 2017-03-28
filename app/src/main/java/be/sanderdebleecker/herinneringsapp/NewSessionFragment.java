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
import be.sanderdebleecker.herinneringsapp.Interfaces.IEndSessionPagerFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewSessionFListener;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class NewSessionFragment extends Fragment implements IEndSessionPagerFListener {
    private INewSessionFListener mListener;
    private SessionPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private Toolbar mToolbar;
    private MenuItem menuContinue;
    private List<Memory> mMemories;
    private String mSession;

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
        boolean endingSession = mPager.getCurrentItem()+1==mPagerAdapter.getCount() && mPagerAdapter.getCount()>1;
        switch(itemId) {
            case android.R.id.home:
                if(endingSession) {
                    mListener.cancel();
                }else{
                    mListener.back();
                }
                break;
            case R.id.action_add:
                if(endingSession ) {
                    endSession();
                }else{
                    if (mMemories == null) {
                        NewSessionPagerFragment fragm = (NewSessionPagerFragment) mPagerAdapter.getItem(mPager.getCurrentItem());
                        new SaveSession().execute(fragm);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private String createSession(Session newSession,List<String> albums) {
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        String sessionIdentifier = sessionData.insert(newSession,albums);
        sessionData.close();
        return sessionIdentifier;
    }
    private void startSession() {
        //Get Memories
        getMemories(getAlbums(mSession));
        //Add MemoryFragments
        loadSession();
    }
    private void endSession() {
        EndSessionPagerFragment fragm = (EndSessionPagerFragment) mPagerAdapter.getItem(mPagerAdapter.getCount()-1);
        Session session = fragm.getSession();
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        boolean result = sessionData.update(session);
        sessionData.close();
        if(result) {
            mListener.onSaved();
        } else {
            getSaveSessionFailureDialog("").show();
        }
    }
    private void getMemories(List<String> albums) {
        MemoryDA memoryData = new MemoryDA(getContext());
        memoryData.open();
        mMemories = memoryData.getAllFromAlbums(albums);
        memoryData.close();
    }
    private void loadSession() {
        ArrayList<SessionPagerFragment> fragms = new ArrayList<>();
        for(int i=0;i<mMemories.size();i++) {
            Memory m = mMemories.get(i);
            MemorySessionPagerFragment frag = MemorySessionPagerFragment.newInstance(m.getTitle(),m.getPath(),m.getType());
            fragms.add(frag);
        }
        EndSessionPagerFragment frag = EndSessionPagerFragment.newInstance(this);
        fragms.add(frag);
        mPagerAdapter = new SessionPagerAdapter( getActivity().getSupportFragmentManager(), fragms, 0);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int endPage = mPagerAdapter.getCount()-1;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position==endPage) {
                    getEndSessionDialog().show();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private List<String> getAlbums(String sessionIdentifier) {
        List<String> albums;
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        albums = sessionData.getAlbums(sessionIdentifier);
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
    }
    private void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
    }

    //Interfaces
    @Override
    public Session getSession() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        Session currSession = sessionData.get(mSession,app.getCurrSession().getAuthIdentity());
        sessionData.close();
        return currSession;
    }
    @Override
    public int getSessionDuration() {
        return mPagerAdapter.getDuration();
    }
    @Override
    public void viewPreviousPage() {
        mPager.setCurrentItem(mPager.getCurrentItem()-1);
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
            mPager.setAdapter(mPagerAdapter);
        }
    }
    private class SaveSession extends AsyncTask<NewSessionPagerFragment, Void, String> {
        private String mErrorMessage="";
        @Override
        protected String doInBackground(NewSessionPagerFragment... fragms) {
            NewSessionPagerFragment fragm = fragms[0];
            mErrorMessage = fragm.validate();
            if(mErrorMessage.equals("")) {
                MainApplication app = (MainApplication) getContext().getApplicationContext();
                List<String> albums = fragm.getAlbums();
                Session session = new Session(fragm.getName(),fragm.getDate(),app.getCurrSession().getAuthIdentity());
                return createSession(session,albums);

            }else{
                return "";
            }
        }

        @Override
        protected void onPostExecute(String sessionId) {
            if(sessionId.equals("")){
                getSaveSessionFailureDialog(mErrorMessage).show();
            }else{
                mSession = sessionId;
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
    private AlertDialog getSaveSessionFailureDialog(String errorMessage) {
        if(errorMessage.equals("")) {
            errorMessage="Er was een fout bij het opslaan van de sessie.";
        }
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Fout : opslaan sessie")
                .setMessage(errorMessage)
                .setPositiveButton("Doorgaan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }
    private AlertDialog getEndSessionDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Einde sessie")
                .setMessage("Wil je de sessie beëindigen?")
                .setPositiveButton("Beëindigen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Blijven", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPreviousPage();
                    }
                })
                .create();
        return dialog;
    }
}
