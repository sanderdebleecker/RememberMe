package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import be.sanderdebleecker.herinneringsapp.Models.View.AlbumVM;

public class AlbumFragment extends Fragment {
    private static final int COLUMNS = 3;
    private String albumIdentifier ="";
    private Toolbar mToolbar;
    private INewAlbumFListener mListener;
    private TextInputEditText etxtSearch;
    private TextInputEditText etxtName;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private RecyclerView recycAlbums;
    private SelectableMemoryAdapter mAdapter;
    private List<SelectableMemory> mMemories;
    private boolean editable;
    private boolean performingQuery = false;

    //c
    public AlbumFragment() {
    }
    public static AlbumFragment newInstance(String albumIdentifier) {
        AlbumFragment frag = new AlbumFragment();
        frag.albumIdentifier = albumIdentifier;
        return frag;
    }
    //lf
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (INewAlbumFListener) context;
        }catch(Exception e) {
            throw new ClassCastException("AlbumFragment must implement INewAlbumFListener!");
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        new Initializer().execute(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(editable) {
            activity.getMenuInflater().inflate(R.menu.menu_add,menu);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_clear);
        }else{
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            activity.getMenuInflater().inflate(R.menu.menu_memory,menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                if(editable) {
                    reload();
                } else {
                    mListener.cancel();
                }
                break;
            case R.id.action_add:
                if(validateAlbum() && !performingQuery) {
                    new UpdateAlbumTask().execute();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean updateAlbum() {
        List<String> selectedMemories = mAdapter.getSelectedMemories();
        Album a = new Album();
        a.setName(etxtName.getText().toString());
        a.setUuid(albumIdentifier);
        Memory thumbnail = new Memory();
        thumbnail.setUuid(selectedMemories.get(0));
        a.setThumbnail(thumbnail);
        AlbumDA albumsData = new AlbumDA(getContext());
        albumsData.open();
        boolean success = albumsData.update(a,selectedMemories);
        albumsData.close();
        return success;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mListener!=null)
            mListener = null;
    }
    //lf m

    private void loadView(View v) {
        recycAlbums = (RecyclerView) v.findViewById(R.id.album_recyclerview);
        etxtName = (TextInputEditText) v.findViewById(R.id.album_etxtName);
        etxtSearch = (TextInputEditText) v.findViewById(R.id.album_etxtSearch);
        mToolbar = (Toolbar) v.findViewById(R.id.album_toolbar);
        fabEdit = (FloatingActionButton) v.findViewById(R.id.album_fabEdit);
        fabDelete = (FloatingActionButton) v.findViewById(R.id.album_fabDelete);
    }
    private void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
    }
    private void addEvents() {
        etxtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    mAdapter.replaceAll(mMemories);
                }
            }
        });
        etxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.replaceAll(mMemories);
            }
        });
        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final List<SelectableMemory> filteredMemories = filter(mMemories,s.toString());
                mAdapter.replaceAll(filteredMemories);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void addActions() {
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editable=true;
                toggleLock();
                getActivity().invalidateOptionsMenu();

            }
        });
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = GetDeleteAlbumDialog();
                dialog.show();
            }
        });
    }
    private void loadAdapter() {
        recycAlbums.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycAlbums.setLayoutManager(mLayoutManager);
    }
    private void loadList() {
        //Set adapter
        mAdapter = new SelectableMemoryAdapter(getContext(),COLUMNS);
        //Load
        mAdapter.add(mMemories);
    }

    private void getMemories() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        mMemories = memoriesData.getSelectabl(app.getCurrSession().getAuthIdentity());
        memoriesData.close();
    }

    private AlbumVM loadAlbum() {
        AlbumVM viewmodel = new AlbumVM();
        mAdapter.setLockedSelection(true);
        //DbService db = new DbService(getContext());
        AlbumDA albumsData = new AlbumDA(getContext());
        albumsData.open();
        viewmodel.setAlbum(albumsData.get(albumIdentifier));
        viewmodel.setSelectedMemories(albumsData.getSelectedMemories(albumIdentifier));
        albumsData.close();
        return viewmodel;
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
    //m
    private void toggleLock() {
        etxtName.setEnabled(editable);
        etxtSearch.setEnabled(editable);
        mAdapter.setLockedSelection(!editable);
        if(editable) {
            fabDelete.setVisibility(View.INVISIBLE);
            fabEdit.setVisibility(View.INVISIBLE);
            fabDelete.setEnabled(false);
            fabEdit.setEnabled(false);
        } else{
            fabDelete.setVisibility(View.VISIBLE);
            fabEdit.setVisibility(View.VISIBLE);
            fabDelete.setEnabled(true);
            fabEdit.setEnabled(true);
        }
    }
    private void reload() {
        editable=false;
        toggleLock();
        loadAlbum();
        getActivity().invalidateOptionsMenu();
    }

    private static List<SelectableMemory> filter(List<SelectableMemory> mems, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<SelectableMemory> filteredModelList = new ArrayList<>();
        for (SelectableMemory mem : mems) {
            final String text = mem.getTitle().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(mem);
            }
        }
        return filteredModelList;
    }

    //DIALOGS
    private AlertDialog GetDeleteAlbumDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("AMAlbum dialoog")
                .setMessage("Bent u zeker?")
                .setIcon(R.drawable.ic_delete_white_24px)
                .setPositiveButton("Verwijder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlbumDA albumsData = new AlbumDA(getContext());
                        albumsData.open();
                        albumsData.delete(albumIdentifier);
                        albumsData.close();
                        mListener.cancel();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Annuleer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }
    //TASKS
    private class Initializer extends AsyncTask<View, Void, Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            new MemoryLoader().execute();
        }
    }
    private class MemoryLoader extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getMemories();
            loadList();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createToolbar();
            addEvents();
            addActions();
            loadAdapter();

            if(albumIdentifier.equals(""))
                new AlbumLoader().execute();
        }
    }
    private class AlbumLoader extends AsyncTask<Void,Void,AlbumVM> {
        @Override
        protected AlbumVM doInBackground(Void... params) {
            return loadAlbum();
        }

        @Override
        protected void onPostExecute(AlbumVM result) {
            super.onPostExecute(result);
            toggleLock();
            etxtName.setText(result.getAlbum().getName());
            mAdapter.filterSelected(result.getSelectedMemories());
        }
    }
    private class UpdateAlbumTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return updateAlbum();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                mListener.albumSaved();
            }else{
                Toast.makeText(getContext(),"Kon album niet wijzigen",Toast.LENGTH_SHORT).show();
            }
        }
    }





    }
