package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
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
    private Bundle bundleMap;
    private MapView mapvMemories;
    private ViewGroup mBottomsheet;
    private Marker mCurrMarker;
    private HashMap<Marker,Integer> mMarkers = new HashMap<>();
    //CTORS
    public MemoryMapFragment() {
    }
    public static MemoryMapFragment newInstance() {
        MemoryMapFragment fragment = new MemoryMapFragment();
        return fragment;
    }

    //LIFECYCLE
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public void onStart() {
        super.onStart();
        mapvMemories.onStart();
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
        init();

        return v;
    }
    public void onResume() {
        super.onResume();
        mapvMemories.onResume();
    }
    public void onPause() {
        mapvMemories.onPause();
        super.onPause();
    }
    public void onStop() {
        super.onStop();
        mapvMemories.onStop();
    }
    public void onDestroy() {
        mapvMemories.onDestroy();
        super.onDestroy();
    }
    public void onDetach() {
        super.onDetach();
    }
    public void onLowMemory() {
        super.onLowMemory();
        if(mapvMemories!=null){
            mapvMemories.onLowMemory();
        }
    }
    //METHODS
    private void init() {
        initMap();
        initBottomsheet();
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
    private void loadView(View v) {
        mBottomsheet = (ViewGroup) v.findViewById(R.id.memory_map_bottomsheet);
        mapvMemories = (MapView) v.findViewById(R.id.memory_map_mapvMemories);
    }
    //MAP METHODS
    @Override
    public void onMapReady(GoogleMap map) {
        loadMarkers(map);
        map.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_MAP_ORIGIN));
        map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAP_ZOOM));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                if(mCurrMarker!=null && marker.getTitle().equals(mCurrMarker.getTitle()) ) {
                    int id = mMarkers.get(marker);
                    MemoryDA memoryDA = new MemoryDA(getContext());
                    memoryDA.open();
                    Memory m = memoryDA.get(id);
                    memoryDA.close();
                    loadMemory(m);
                    openBottomsheet();
                    mCurrMarker=null;
                }else{
                    mCurrMarker = marker;
                    mCurrMarker.showInfoWindow();
                }
                return true;
            }
        });
    }
    private void loadMarkers(GoogleMap map) {
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        ArrayList<MappedMemory> memories = memoryDA.getMapped();
        memoryDA.close();
        for(MappedMemory memory : memories) {
            Marker m = map.addMarker(new MarkerOptions().position(new LatLng(memory.location.getLat(),memory.location.getLng())).title(memory.getTitle()));
            mMarkers.put(m,memory.getId());
        }
    }
    private void loadMemory(Memory mem) {
        TextView txtvTitle = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvTile);
        TextView txtvCreatorDate = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvCreatorDate);
        TextView txtvDescr = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvDescription);
        TextView txtvLoc = (TextView) mBottomsheet.findViewById(R.id.bottomsheet_memory_txtvLocation);
        ImageView imgvMedia = (ImageView) mBottomsheet.findViewById(R.id.bottomsheet_memory_imgvMemory);

        txtvTitle.setText(mem.getTitle());
        txtvCreatorDate.setText(mem.getCreator()+ " - " + mem.getDate());
        txtvDescr.setText(mem.getDescription());
        txtvLoc.setText(mem.getLocation().getName());

        StorageHelper.loadMedia(getContext(),imgvMedia,new MediaItem(mem.getType(),mem.getPath()));
    }
    public void queryFragment(String filter) {

    }
    public void cancelQueryFragment() {

    }
}
