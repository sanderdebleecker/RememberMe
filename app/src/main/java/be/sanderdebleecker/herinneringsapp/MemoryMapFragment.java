package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Interfaces.IQueryableFragment;
import be.sanderdebleecker.herinneringsapp.Models.MappedMemory;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

public class MemoryMapFragment extends Fragment implements OnMapReadyCallback,IQueryableFragment {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final LatLng DEFAULT_MAP_ORIGIN = new LatLng(50.70f,4.40f);
    private static final float DEFAULT_MAP_ZOOM = 7.00f;
    private BottomSheetBehavior mBottomsheetBehavior;
    private ViewGroup mBottomsheet;
    private HashMap<Marker,Integer> mMarkers = new HashMap<>();
    private Marker mCurrMarker;
    private MapView mapvMemories;
    private Bundle bundleMap;
    private TextView txtvTitle;
    private TextView txtvCreatorDate;
    private TextView txtvDescr;
    private TextView txtvLoc;
    private ImageView imgvMedia;

    private boolean performingMarkerLoad=false;
    //CTORS
    public MemoryMapFragment() {
    }
    public static MemoryMapFragment newInstance() {
        MemoryMapFragment fragment = new MemoryMapFragment();
        return fragment;
    }

    //Lifecycle
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public void onDetach() {
        super.onDetach();
    }
    public void onStart() {
        super.onStart();
        mapvMemories.onStart();
    }
    public void onStop() {
        super.onStop();
        mapvMemories.onStop();
    }
    public void onResume() {
        super.onResume();
        mapvMemories.onResume();
    }
    public void onPause() {
        mapvMemories.onPause();
        super.onPause();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            bundleMap = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_memory_map, container, false);
        loadView(v);
        initMap();
        initBottomsheet();
        loadBottomsheet();
        return v;
    }
    public void onDestroy() {
        mapvMemories.onDestroy();
        super.onDestroy();
    }
    public void onLowMemory() {
        super.onLowMemory();
        if(mapvMemories!=null){
            mapvMemories.onLowMemory();
        }
    }
    //METHODS
    private void loadView(View v) {
        mBottomsheet = (ViewGroup) v.findViewById(R.id.memory_map_bottomsheet);
        mapvMemories = (MapView) v.findViewById(R.id.memory_map_mapvMemories);
    }
    private void initMap() {
        mapvMemories.onCreate(bundleMap);
        mapvMemories.getMapAsync(this);
    }
    private void initBottomsheet() {
        mBottomsheetBehavior = BottomSheetBehavior.from(mBottomsheet);
        mBottomsheet.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        inflater.inflate(R.layout.bottomsheet_memory,mBottomsheet);
        mBottomsheetBehavior.setPeekHeight(0);
        mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
    private void openBottomsheet() {
        mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private void loadBottomsheet() {
        txtvTitle = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvTile);
        txtvCreatorDate = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvCreatorDate);
        txtvDescr = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvDescription);
        txtvLoc = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvLocation);
        imgvMedia = (ImageView) mBottomsheet.findViewById(R.id.bottomsheet_memory_imgvMemory);
    }
    //Map
    @Override
    public void onMapReady(GoogleMap map) {
        new CreateMarkersTask().execute(map);
        map.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_MAP_ORIGIN));
        map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAP_ZOOM));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return selectMarker(marker);
            }
        });
    }
    private void createMarkers(GoogleMap map) {
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        ArrayList<MappedMemory> memories = memoryDA.getMapped();
        memoryDA.close();
        for(MappedMemory memory : memories) {
            Marker m = map.addMarker(new MarkerOptions().position(new LatLng(memory.location.getLat(),memory.location.getLng())).title(memory.getTitle()));
            mMarkers.put(m,memory.getId());
        }
    }
    private boolean selectMarker(Marker marker) {
        boolean doubleClicked = mCurrMarker!=null && marker.getTitle().equals(mCurrMarker.getTitle());
        if(doubleClicked) {
            //when you click twice on the marker
            int id = mMarkers.get(marker);
            new LoadMarkerTask().execute(id);
        }else{
            //when you click once
            mCurrMarker = marker;
            mCurrMarker.showInfoWindow();
        }
        return true;
    }
    private Memory loadMarker(int id) {
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        Memory m = memoryDA.get(id);
        memoryDA.close();
        openBottomsheet();
        mCurrMarker=null;
        return m;
    }
    private void loadMemory(Memory mem) {
        txtvTitle.setText(mem.getTitle());
        txtvCreatorDate.setText(mem.getCreator()+ " - " + mem.getDate());
        txtvDescr.setText(mem.getDescription());
        txtvLoc.setText(mem.getLocation().getName());
        StorageHelper.loadMedia(getContext(),imgvMedia,new MediaItem(mem.getType(),mem.getPath()));
    }
    //Interface
    public void queryFragment(String filter) {

    }
    public void cancelQueryFragment() {

    }
    //Tasks
    public class CreateMarkersTask extends AsyncTask<GoogleMap,Void,Void> {
        @Override
        protected Void doInBackground(GoogleMap... params) {
            createMarkers(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    public class LoadMarkerTask extends AsyncTask<Integer,Void,Memory> {
        @Override
        protected Memory doInBackground(Integer... params) {
            if(!performingMarkerLoad) {
                performingMarkerLoad=true;
                return loadMarker(params[0]);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Memory memory) {
            performingMarkerLoad=false;
            if(memory!=null) {
                loadMemory(memory);
            }
        }
    }
}
