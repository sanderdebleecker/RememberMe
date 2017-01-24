package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.AlbumAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.RecyclerTouchListener;
import be.sanderdebleecker.herinneringsapp.Data.AlbumDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.IClickListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IAlbumsFListener;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.View.AlbumVM;


public class AlbumsFragment extends Fragment {
    private final int COLUMNS = 3;
    private RecyclerView recycAlbums;
    private AlbumAdapter mAdapter;
    private IAlbumsFListener mListener;

    //ctor
    public AlbumsFragment() {
    }
    public static AlbumsFragment newInstance() {
            return new AlbumsFragment();
    }
    //lc
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (IAlbumsFListener) context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.getPackageName()+" must impl IAlbumsFListener");
        }
    }
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_albums, container, false);
        new Initializer().execute(v);
        return v;
    }
    //lc m
    private void loadView(View v) {
        recycAlbums = (RecyclerView) v.findViewById(R.id.recyc_albums);
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        AlbumDA albumSource = new AlbumDA(getContext());
        albumSource.open();
        ArrayList<Album> albums = albumSource.getAll(app.getCurrSession().getAuthIdentity());
        albumSource.close();
        mAdapter = new AlbumAdapter(getContext(),albums);

    }
    private void loadAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycAlbums.setLayoutManager(mLayoutManager);
        recycAlbums.setAdapter(mAdapter);
        recycAlbums.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycAlbums, new IClickListener() {
            @Override
            public void onClick(View view, int position) {
                int id = mAdapter.getId(position);
                mListener.onAlbumSelect(id);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    //tasks
    private class Initializer extends AsyncTask<View,Void,Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            loadList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadAdapter();
        }
    }


}
