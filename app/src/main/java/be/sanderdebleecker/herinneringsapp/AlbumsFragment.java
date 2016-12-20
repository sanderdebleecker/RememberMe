package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
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


public class AlbumsFragment extends Fragment {
    private final int COLUMNS = 3;
    private RecyclerView recycAlbums;
    private AlbumAdapter mAdapter;
    private IAlbumsFListener mListener;

    public AlbumsFragment() {
    }
    public static AlbumsFragment newInstance() {
            return new AlbumsFragment();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_albums, container, false);
        recycAlbums = (RecyclerView) v.findViewById(R.id.recyc_albums);
        loadList();
        return v;
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        AlbumDA albumSource = new AlbumDA(getContext());
        albumSource.open();
        ArrayList<Album> albums = albumSource.getAll(app.getCurrSession().getAuthIdentity());
        albumSource.close();
        mAdapter = new AlbumAdapter(getContext(),albums);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycAlbums.setLayoutManager(mLayoutManager);
        recycAlbums.setItemAnimator(new DefaultItemAnimator());
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

}
