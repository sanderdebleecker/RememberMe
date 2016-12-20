package be.sanderdebleecker.herinneringsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.TimelineMemoryAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.TimelineDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.IQueryableFragment;

public class MemoryTimelineFragment extends Fragment implements IQueryableFragment {
    private RecyclerView recycTimeline;
    private TimelineMemoryAdapter adapter;

    //CTOR
    public MemoryTimelineFragment() {
    }
    public static MemoryTimelineFragment newInstance() {
        MemoryTimelineFragment frag = new MemoryTimelineFragment();
        return frag;
    }
    //LIFECYCLE
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory_timeline, container, false);
        loadView(v);
        loadList();
        return v;
    }
    //METHODS
    private void loadView(View v) {
        recycTimeline = (RecyclerView) v.findViewById(R.id.recyc_timeline);
    }
    private void loadList() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        TimelineDA timelineData = new TimelineDA(getContext());
        timelineData.open();
        adapter = new TimelineMemoryAdapter(getContext(),timelineData.getAll(app.getCurrSession().getAuthIdentity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recycTimeline.setLayoutManager(mLayoutManager);
        recycTimeline.setItemAnimator(new DefaultItemAnimator());
        recycTimeline.setAdapter(adapter);
        timelineData.close();
    }
    //INTERFACES
    public void queryFragment(String filter) {

    }
    public void cancelQueryFragment() {

    }
}
