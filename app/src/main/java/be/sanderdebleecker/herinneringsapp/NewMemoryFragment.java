package be.sanderdebleecker.herinneringsapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

import static android.app.Activity.RESULT_OK;

//TODO catch online library exceptions

public class NewMemoryFragment extends GenericMemoryFragment {
    private String username;

    //Ctor
    public NewMemoryFragment() {
    }
    public static NewMemoryFragment newInstance( ) {
        NewMemoryFragment frag = new NewMemoryFragment();
        return frag;
    }
    //Lifecycle
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_add,menu);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_new_memory, container, false);
        new Initializer().execute(v);
        return v;
    }
    //Lifecycle events
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE_REQUEST:
                    handleCameraResult();
                    txtvMedia.setText("camera afbeelding");
                    break;
                case PLACE_PICKER_REQUEST:
                    mPlace = PlacePicker.getPlace(getActivity(), data);
                    btnLocation.setText(mPlace.getName());
                    break;
                case GALLERY_PICKER_REQUEST:
                    if(data==null) return;
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    imgvMedia.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    txtvMedia.setText("gallerij afbeelding");
                    cursor.close();
                    mMediaItem.setPath(picturePath);
                    mMediaItem.setType(MediaItem.Type.GALLERY_IMAGE);
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                mListener.cancel();
                break;
            case R.id.action_add:
                if(validateMemory() && !performingQuery) {
                    performingQuery=true;
                    Memory m = getMemory();
                    new InsertMemoryTask().execute(m);
                } else {
                    Toast.makeText(getActivity(),"Kan herinnering niet opslaan.",Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private Memory getMemory() {
        Memory m = new Memory();
        m.setTitle(etxtTitle.getText().toString());
        m.setDescription( etxtDescription.getText().toString());
        m.setDate(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime()));
        return m;
    }
    private boolean insertMemory(Memory m) {
        if(m==null) return false;
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        //Assemble Memory
        m.setCreator(app.getCurrSession().getAuthIdentity());
        m.setPath(mMediaItem.getPath());
        m.setType(mMediaItem.getType().toString());
        MemoryDA memoriesData = new MemoryDA(getContext());
        memoriesData.open();
        if(mPlace !=null) { //OPTIONAL
            LatLng point = mPlace.getLatLng();
            m.setLocation(new Location(point.longitude, point.latitude, mPlace.getName().toString()));
        }
        boolean success = memoriesData.insertMemory(m);
        memoriesData.close();
        return success;
    }

    private boolean validateMemory() {
        boolean hasTitle=true;
        if(etxtTitle.getText().toString().trim().equals("")){
            hasTitle=false;
            etxtTitle.setError("Titel vereist");
        }
        boolean hasUser = !username.equals("");
        return (hasTitle && hasUser );
    }
    //Lifecycle methods
    private void loadView(View v) {
        coordinatorLayoutNewMemoryF = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayoutNewMemoryF);
        mToolbar = (Toolbar) v.findViewById(R.id.new_memory_toolbar);
        imgvMedia = (ImageView) v.findViewById(R.id.imgvMedia);
        mBottomsheet = (ViewGroup) v.findViewById(R.id.bottomsheet);
        etxtTitle = (TextInputEditText) v.findViewById(R.id.etxtTitle);
        etxtDescription = (TextInputEditText) v.findViewById(R.id.etxtDescription);
        btnDate = (Button) v.findViewById(R.id.btnDate);
        btnLocation = (Button) v.findViewById(R.id.btnLocation);
        txtvMedia = (TextView) v.findViewById(R.id.txtvMedia);

    }
    private void loadUser() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        username = app.getCurrSessionValue();
    }
    //MEDIA METHODS

    //GUI METHODS
    protected void createBottomSheet() {
        View bottomSheet = coordinatorLayoutNewMemoryF.findViewById(R.id.bottomsheet);
        mBottomsheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomsheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING && recorder.isRecording()) {
                    mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mBottomsheetBehavior.setPeekHeight(0);
    }

    protected void openBottomSheet() {
        mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    protected void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    //TOOLBAR
    protected void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_clear);
        setHasOptionsMenu(true);
    }
    //Tasks
    private class Initializer extends AsyncTask<View, Void,Void> {
        @Override
        protected Void doInBackground(View... views) {
            loadUser();
            loadView(views[0]);
            initMedia();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inflateBottomsheet(R.layout.bottomsheet_media);
            btnDate.setText(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(new Date()));
            createToolbar();
            createBottomSheet();
            addEvents();
        }
    }
    private class InsertMemoryTask extends AsyncTask<Memory, Void,Boolean> {
        @Override
        protected Boolean doInBackground(Memory... params) {
            return insertMemory(params[0]);
        }
        @Override
        protected void onPostExecute(Boolean success) {
            performingQuery=false;
            if(success) {
                mListener.memorySaved();
            }else{
                Toast.makeText(getContext(),"Herinnering niet opgeslaan!",Toast.LENGTH_LONG).show();
            }
        }
    }
}
