package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.MemoryAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.RecyclerTouchListener;
import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.IClickListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IMemoriesFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IQueryableFragment;

public class MemoriesFragment extends Fragment implements IQueryableFragment {
    private final int COLUMNS = 3;
    private boolean performingQuery =false;
    private IMemoriesFListener listener;
    private RecyclerView recycMemories;
    private MemoryAdapter adapter;
    private FloatingActionButton fabAdd;
    private String username;

    //Ctor
    public MemoriesFragment() {
    }
    public static MemoriesFragment newInstance() {
        MemoriesFragment fragment = new MemoriesFragment();
        return fragment;
    }
    //Lifecycle
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memories, container, false);
        new Initializer().execute(v);
        return v;
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (IMemoriesFListener) context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.getPackageName()+" must impl IMemFLstnr");
        }
    }
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    //Lifecycle methods
    private void loadView(View v) {
        recycMemories = (RecyclerView) v.findViewById(R.id.recyc_memories);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
    }
    private void loadEvents() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNewMemory();
            }
        });
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        username = app.getCurrSessionValue();
        memoriesData.open();
        adapter = new MemoryAdapter(getContext(),memoriesData.getAll(app.getCurrSession().getAuthIdentity()),COLUMNS);
        memoriesData.close();
    }
    private void loadAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycMemories.setLayoutManager(mLayoutManager);
        recycMemories.setAdapter(adapter);
        recycMemories.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycMemories, new IClickListener() {
            @Override
            public void onClick(View view, int position) {
                int id = adapter.getId(position);
                listener.onMemorySelect(id);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    //Interface
    public void queryFragment(String filter) {
        if(!performingQuery) {
            performingQuery =true;
            new FilterTask().execute(filter);
        }
    }
    public void cancelQueryFragment() {
        if(!performingQuery) {
            performingQuery =true;
            new CancelQueryTask().execute();
        }
    }
    //Interface methods
    private void query(String filter) {
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        adapter.loadMemories(memoriesData.getFiltered(username,filter));
        memoriesData.close();
    }
    private void cancelQuery() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        adapter.loadMemories(memoriesData.getAll(app.getCurrSession().getAuthIdentity()));
        memoriesData.close();
    }
    //Tasks
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
            loadEvents();
        }
    }
    public class FilterTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            query(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            performingQuery =false;
        }
    }
    public class CancelQueryTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            cancelQuery();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            performingQuery =false;
        }
    }
}
