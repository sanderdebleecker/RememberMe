package be.sanderdebleecker.herinneringsapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.MemoryDA;
import be.sanderdebleecker.herinneringsapp.Data.TimelineDA;
import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.Location;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.Memory;

import static android.app.Activity.RESULT_OK;

public class MemoryFragment extends GenericMemoryFragment {
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabDelete;
    private MenuItem menuTimeline;
    private int memoryId=-1;
    private Memory mMemory;
    private boolean editable=false;
    private boolean onTimeline=false;
    //CTOR
    public MemoryFragment() {
    }
    public static MemoryFragment newInstance(int id) {
        MemoryFragment frag = new MemoryFragment();
        frag.memoryId=id;
        return frag;
    }

    //LIFECYLE METHODS
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_memory, container, false);
        new Initializer().execute(v);
        return v;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(editable) {
            activity.getMenuInflater().inflate(R.menu.menu_add,menu);
            activity.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_clear);
        }else{
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            activity.getMenuInflater().inflate(R.menu.menu_memory,menu);
            menuTimeline = menu.findItem(R.id.action_timeline);
        }
    }
    public void updateTimelineStatus() {
        if(onTimeline) {
            menuTimeline.setIcon(R.drawable.ic_event_note_black_24dp);
        }else{
            menuTimeline.setIcon(R.drawable.ic_event_note_gray_24dp);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                //Cancels
                if(editable) {
                    reload();
                } else {
                    mListener.cancel();
                }
                break;
            case R.id.action_add:
                //Confirm
                boolean hasTitle = !etxtTitle.getText().toString().trim().equals("");
                if(hasTitle && !performingQuery) {
                    performingQuery=true;
                    //Assemble Memory From Fields
                    Memory m = getMemory();
                    new UpdateMemoryTask().execute(m);
                } else {
                    Toast.makeText(getContext(),"Geef een titel in!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_timeline:
                if(onTimeline) {
                    AlertDialog dialog = GetDeleteFromTimelineDialog();
                    dialog.show();
                }else {
                    AlertDialog dialog = GetMemoryToTimelineDialog();
                    dialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private Memory getMemory() {
        Memory m = new Memory();
        m.setTitle(etxtTitle.getText().toString().trim());
        m.setDescription(etxtDescription.getText().toString().trim());
        m.setDate(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime()));
        return m;
    }
    private Boolean updateMemory(Memory m) {
        if(m==null) return false;
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        m.setCreator(app.getCurrSession().getAuthIdentity());
        m.setId(memoryId);
        m.setPath(mMediaItem.getPath());
        m.setType(mMediaItem.getType().toString());
        //Open DB
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        if(mPlace !=null) { //OPTIONAL
            LatLng point = mPlace.getLatLng();
            m.setLocation(new Location(point.longitude,point.latitude, mPlace.getName().toString()));
        }
        boolean success = memoryDA.update(m);
        memoryDA.close();
        return success;
    }
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
    private void reload() {
        editable=false;
        toggleLock();
        loadMemory();
        getActivity().invalidateOptionsMenu();
    }
    //CYCLE SUBMETHODS
    private void loadView(View v) {
        coordinatorLayoutNewMemoryF = (CoordinatorLayout) v.findViewById(R.id.memory_coordinatorLayout);
        mToolbar = (Toolbar) v.findViewById(R.id.memory_toolbarAdd);
        imgvMedia = (ImageView) v.findViewById(R.id.memory_imgvMedia);
        mBottomsheet = (ViewGroup) v.findViewById(R.id.memory_bottomsheet);
        etxtTitle = (TextInputEditText) v.findViewById(R.id.memory_etxtTitle);
        etxtDescription = (TextInputEditText) v.findViewById(R.id.memory_etxtDescription);
        btnDate = (Button) v.findViewById(R.id.memory_btnDate);
        btnLocation = (Button) v.findViewById(R.id.memory_btnLocation);
        txtvMedia = (TextView) v.findViewById(R.id.memory_txtvMedia);
        fabEdit = (FloatingActionButton) v.findViewById(R.id.fabEdit);
        fabDelete= (FloatingActionButton) v.findViewById(R.id.fabDelete);
    }
    protected void addEvents() {
        imgvMedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editable) {
                    openBottomSheet();
                }else{
                    if(mMemory !=null && mMemory.getType().equals(MediaItem.Type.AUDIO.toString())) {
                        try {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(mMemory.getPath());
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Opname niet gevonden!",Toast.LENGTH_LONG).show();
                        } finally {
                        }
                    }
                }
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openPlacePicker();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void playAudio() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mMemory.getPath());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Opname is niet meer beschikbaar",Toast.LENGTH_LONG).show();
        } catch (Exception e ){
            e.printStackTrace();
            Toast.makeText(getActivity(),"Opname kon niet worden afgespeeld",Toast.LENGTH_LONG).show();
        }
    };
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
                AlertDialog dialog = GetDeleteMemoryDialog();
                dialog.show();
            }
        });
    }
    private void loadMemory() {
        if(memoryId<0) return;
        MemoryDA memoryDA = new MemoryDA(getContext());
        memoryDA.open();
        mMemory = memoryDA.get(memoryId);
        memoryDA.close();
    }
    private void showMemory() {
        etxtTitle.setText(mMemory.getTitle());
        etxtDescription.setText(mMemory.getDescription());
        btnDate.setText(mMemory.getDate());
        btnLocation.setText(mMemory.getLocation().getName());
        mMediaItem = new MediaItem(mMemory.getType(), mMemory.getPath());
        StorageHelper.loadMedia(getContext(),imgvMedia,new MediaItem(MediaItem.Type.valueOf(mMemory.getType()),mMemory.getPath()));
    }
    //GUI METHODS
    protected void createBottomSheet() {
        View bottomSheet = coordinatorLayoutNewMemoryF.findViewById(R.id.memory_bottomsheet);
        mBottomsheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomsheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING && recorder!=null && recorder.isRecording()) {
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
    //Edit Mode
    private void toggleLock() {
        mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        etxtTitle.setEnabled(editable);
        etxtDescription.setEnabled(editable);
        btnDate.setEnabled(editable);
        btnLocation.setEnabled(editable);

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
    // TOOLBAR
    protected void createToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
    }

    //Dialogs
    private AlertDialog GetDeleteMemoryDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Herinnering dialoog")
                .setMessage("Bent u zeker?")
                .setIcon(R.drawable.ic_delete_white_24px)
                .setPositiveButton("Verwijder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MemoryDA memoriesData = new MemoryDA(getContext());
                        memoriesData.open();
                        memoriesData.delete(memoryId);
                        memoriesData.close();
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
    private AlertDialog GetDeleteFromTimelineDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Tijdlijn")
                .setMessage("Wilt u de herinnering van de tijdlijn verwijderen?")
                .setIcon(R.drawable.ic_event_note_black_24dp)
                .setPositiveButton("Verwijder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainApplication app = (MainApplication) getContext().getApplicationContext();
                        TimelineDA timelineData = new TimelineDA(getContext());
                        timelineData.open();
                        timelineData.delete(memoryId,app.getCurrSession().getAuthIdentity());
                        timelineData.close();
                        mListener.cancel();
                        onTimeline=false;
                        updateTimelineStatus();
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
    private AlertDialog GetMemoryToTimelineDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Tijdlijn")
                .setMessage("Wilt u de herinnering op de tijdlijn plaatsen?")
                .setIcon(R.drawable.ic_event_note_black_24dp)
                .setPositiveButton("Toevoegen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainApplication app = (MainApplication) getContext().getApplicationContext();
                        TimelineDA timelineDA = new TimelineDA(getContext());
                        timelineDA.open();
                        timelineDA.insert(memoryId,app.getCurrSession().getAuthIdentity());
                        timelineDA.close();
                        mListener.cancel();
                        onTimeline=true;
                        updateTimelineStatus();
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

    //Tasks
    public class Initializer extends AsyncTask<View,Void,Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            initMedia();
            createBottomSheet();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            new MemoryLoaderTask().execute();
            inflateBottomsheet(R.layout.bottomsheet_media);
            btnDate.setText(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(new Date()));
            createToolbar();
            addEvents();
            toggleLock();
            addActions();
        }
    }
    public class MemoryLoaderTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            loadMemory();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            showMemory();
        }
    }
    public class UpdateMemoryTask extends AsyncTask<Memory,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Memory... params) {
            return updateMemory(params[0]);
        }
        @Override
        protected void onPostExecute(Boolean success) {
            performingQuery=false;
            if(success) {
                Toast.makeText(getContext(),"Herinnering gewijzigd",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"Fout bij het wijzigen",Toast.LENGTH_SHORT).show();
            }
            reload();
        }
    }


}
