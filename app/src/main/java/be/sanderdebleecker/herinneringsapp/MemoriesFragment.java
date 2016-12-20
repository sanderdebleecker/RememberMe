package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
    private IMemoriesFListener listener;
    private RecyclerView recycMemories;
    private MemoryAdapter adapter;
    private FloatingActionButton fabAdd;
    private String username;

    //CTOR
    public MemoriesFragment() {

    }
    public static MemoriesFragment newInstance() {
        MemoriesFragment fragment = new MemoriesFragment();
        return fragment;
    }

    //LIFECYCLE
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_memories, container, false);
        recycMemories = (RecyclerView) v.findViewById(R.id.recyc_memories);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        loadList();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNewMemory();
            }
        });
        return v;
    }

    public void onResume() {
        super.onResume();
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        adapter.loadMemories(memoriesData.getAll(app.getCurrSession().getAuthIdentity()));
        memoriesData.close();
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

    //METHODS
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        username = app.getCurrSessionValue();
        memoriesData.open();
        adapter = new MemoryAdapter(getContext(),memoriesData.getAll(app.getCurrSession().getAuthIdentity()),COLUMNS);
        memoriesData.close();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        recycMemories.setLayoutManager(mLayoutManager);
        recycMemories.setItemAnimator(new DefaultItemAnimator());
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
    public void queryFragment(String filter) {
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        adapter.loadMemories(memoriesData.getFiltered(username,filter));
        memoriesData.close();
    }
    public void cancelQueryFragment() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        adapter.loadMemories(memoriesData.getAll(app.getCurrSession().getAuthIdentity()));
        memoriesData.close();
    }
}
