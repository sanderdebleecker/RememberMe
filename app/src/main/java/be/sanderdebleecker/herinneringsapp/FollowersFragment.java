package be.sanderdebleecker.herinneringsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.FollowerAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Interfaces.IQueryableFragment;
import be.sanderdebleecker.herinneringsapp.Models.Trust;

public class FollowersFragment extends Fragment implements IQueryableFragment {
    private RecyclerView recycFollowers;
    private List<Trust> mTrusts;
    private FollowerAdapter mAdapter;

    public FollowersFragment() {

    }
    public static FollowersFragment newInstance() {
        FollowersFragment fragm = new FollowersFragment();
        return fragm;
    }

    //LF
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_followers, container, false);
        setHasOptionsMenu(true);
        init(v);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem sortItem = menu.findItem(R.id.action_timeline);
        sortItem.setVisible(false);
    }
    //LF M
    public void init(View v) {
        loadView(v);
        loadList();
    }
    private void loadView(View v) {
        recycFollowers = (RecyclerView) v.findViewById(R.id.recyc_followers);
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        //DbService db = new DbService(getContext());
        //mTrusts = db.getTrustList(app.getCurrSessionValue());
        //db.close();
        mAdapter = new FollowerAdapter(getContext(), mTrusts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycFollowers.setLayoutManager(mLayoutManager);
        recycFollowers.setItemAnimator(new DefaultItemAnimator());
        recycFollowers.setAdapter(mAdapter);
    }

    @Override
    public void queryFragment(String filter) {

    }
    @Override
    public void cancelQueryFragment() {

    }

}
