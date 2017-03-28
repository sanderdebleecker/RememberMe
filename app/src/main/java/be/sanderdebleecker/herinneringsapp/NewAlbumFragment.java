package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SelectableMemoryAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.AlbumDA;
import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewAlbumFListener;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;

public class NewAlbumFragment extends Fragment {
    private static final int COLUMNS = 3;
    private RecyclerView recycAlbums;
    private TextInputEditText etxtName,etxtSearch;
    private SelectableMemoryAdapter mAdapter;
    private Toolbar mToolbar;
    private String username;
    private List<SelectableMemory> mMemories;
    private INewAlbumFListener mListener;
    private boolean performingQuery = false;

    //C
    public static NewAlbumFragment newInstance() {
        NewAlbumFragment frag = new NewAlbumFragment();
        return frag;
    }

    //LC

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (INewAlbumFListener) context;
        }catch(ClassCastException ex) {
            throw new ClassCastException("Activity must impl INewAlbumFListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if(mListener!=null) {
            mListener=null;
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_album, container, false);
        new Initializer().execute(v);
        return v;
    }

    //LC E
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add,menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                mListener.cancel();
                break;
            case R.id.action_add:
                if(validateAlbum() && username!="" && !performingQuery) {
                    performingQuery=true;
                    new AddAlbumTask().execute();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean insertAlbum() {
        AlbumDA albumDA = new AlbumDA(getContext());
        Album newAlbum = new Album();
        List<String> selMems = mAdapter.getSelectedMemories();
        Memory thumbnail = new Memory();
        thumbnail.setUuid(selMems.get(0));
        newAlbum.setName(etxtName.getText().toString().trim());
        newAlbum.setAuthor(username);
        newAlbum.setThumbnail(thumbnail);
        albumDA.open();
        boolean success = albumDA.insert(newAlbum,selMems);
        albumDA.close();
        return success;
    }

    private boolean validateAlbum() {
        boolean hasName = etxtName.getText().toString().trim().length()>0;
        if(!hasName) {
            Toast.makeText(getContext(),"Vul een titel in!",Toast.LENGTH_LONG).show();
        }
        boolean hasSelectedMemories = mAdapter.hasSelected();
        if(!hasSelectedMemories) {
            Toast.makeText(getContext(),"Selecteer herinneringen!",Toast.LENGTH_LONG).show();
        }
        return hasName && hasSelectedMemories;
    }
    //M
    private void loadView(View v) {
        recycAlbums = (RecyclerView) v.findViewById(R.id.new_album_recyclerview);
        etxtName = (TextInputEditText) v.findViewById(R.id.new_album_etxtName);
        etxtSearch = (TextInputEditText) v.findViewById(R.id.new_album_etxtSearch);
        mToolbar = (Toolbar) v.findViewById(R.id.new_album_toolbar);
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        username = app.getCurrSessionValue();
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        mMemories = memoryDA.getSelectabl(app.getCurrSession().getAuthIdentity());
        memoryDA.close();
        mAdapter = new SelectableMemoryAdapter(getContext(),COLUMNS);
        mAdapter.add(mMemories);
    }
    private void loadAdapter() {
        recycAlbums.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycAlbums.setLayoutManager(mLayoutManager);
    }
    protected void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_clear);
        setHasOptionsMenu(true);
    }

    private void addEvents() {
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!performingQuery) {
                    performingQuery=true;
                    new FilterTask().execute(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private static List<SelectableMemory> filter(List<SelectableMemory> mems, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<SelectableMemory> filteredModelList = new ArrayList<>();
        for (SelectableMemory mem : mems) {
            final String text = mem.getTitle().toLowerCase();
            if(text.contains(lowerCaseQuery)) {
                filteredModelList.add(mem);
            }
        }
        return filteredModelList;
    }
    //TASKS
    public class Initializer extends AsyncTask<View,Void,Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            loadList();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            loadAdapter();
            createToolbar();
            addEvents();
        }
    }
    public class AddAlbumTask extends AsyncTask<View,Void,Boolean> {
        @Override
        protected Boolean doInBackground(View... params) {
            return insertAlbum();
        }
        @Override
        protected void onPostExecute(Boolean result) {
            performingQuery=false;
            boolean success = insertAlbum();
            if(success) {
                mListener.albumSaved();
            }else{
                Toast.makeText(getContext(),"AMAlbum kon niet worden aangemaakt!",Toast.LENGTH_LONG).show();
            }
        }
    }
    public class FilterTask extends AsyncTask<String,Void,List<SelectableMemory>> {
        @Override
        protected List<SelectableMemory> doInBackground(String... params) {
            return filter(mMemories,params[0]);
        }
        @Override
        protected void onPostExecute(List<SelectableMemory> selectableMemories) {
            performingQuery=false;
            mAdapter.replaceAll(selectableMemories);
        }
    }
}
