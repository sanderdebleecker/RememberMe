package be.sanderdebleecker.herinneringsapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SessionAdapter;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.DividerItemDecoration;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.RecyclerTouchListener;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.SessionDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.IClickListener;
import be.sanderdebleecker.herinneringsapp.Models.View.SessionVM;

public class SessionsFragment extends Fragment {
    private SessionAdapter mAdapter;
    private RecyclerView mRecycler;
    private FloatingActionButton fabAdd;

    //CTOR
    public SessionsFragment() {
        // Required empty public constructor
    }
    public static SessionsFragment newInstance() {
        SessionsFragment fragment = new SessionsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sessions, container, false);
        new Initializer().execute(v);
        return v;
    }
    private void loadView(View v) {
        mRecycler = (RecyclerView) v.findViewById(R.id.sessions_recyc);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.sessions_fabAdd);
    }
    private void loadAdapter() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        int identity = app.getCurrSession().getAuthIdentity();
        SessionDA sessionData = new SessionDA(getContext());
        sessionData.open();
        List<SessionVM> sessions = sessionData.get(identity);
        sessionData.close();
        mAdapter = new SessionAdapter(getContext());
        mAdapter.add(sessions);


    }
    //TASKS
    private class Initializer extends AsyncTask<View, Void, Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            loadAdapter();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecycler.setLayoutManager(mLayoutManager);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
            mRecycler.addItemDecoration(itemDecoration);
            mRecycler.setAdapter(mAdapter);

            mRecycler.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), mRecycler, new IClickListener() {
                @Override
                public void onClick(View view, int position) {
                }
                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
    }

}
