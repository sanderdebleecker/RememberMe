package be.sanderdebleecker.herinneringsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.AlbumAdapter;
import be.sanderdebleecker.herinneringsapp.Core.Adapters.SelectableAlbumAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.AlbumDA;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;

public class NewSessionPagerFragment extends SessionPagerFragment {
    private RecyclerView mRecyclerView;
    private SelectableAlbumAdapter mAdapter;
    private List<SelectableAlbum> mAlbums;
    private final int COLUMNS = 3;

    public static NewSessionPagerFragment newInstance() {
        NewSessionPagerFragment fragment = new NewSessionPagerFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_new_session_pager, container, false);
        loadView(v);
        new Initializer().execute();
        return v;
    }
    private void loadView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.new_session_recyclerview);
    }
    private void loadAlbums() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        AlbumDA albumSource = new AlbumDA(getContext());
        albumSource.open();
        mAlbums = albumSource.getSelectabl(app.getCurrSession().getAuthIdentity());
        albumSource.close();
    }
    private void loadList() {
        mAdapter = new SelectableAlbumAdapter(getContext());
    }
    private void loadListLayout() {
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.add(mAlbums);
    }

    //Tasks
    private class Initializer extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            loadAlbums();
            loadList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadListLayout();
        }
    }
}
