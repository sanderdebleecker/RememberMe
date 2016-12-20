package be.sanderdebleecker.herinneringsapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import be.sanderdebleecker.herinneringsapp.Helpers.Hardware.Recorder;
import be.sanderdebleecker.herinneringsapp.Helpers.PermissionHelper;
import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewMemoryFListener;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;

public class GenericMemoryFragment extends Fragment {
    //CONST
    public static final int RECORD_AUDIO = 0;
    protected static final int TAKE_PICTURE_REQUEST = 1;
    protected static final int PLACE_PICKER_REQUEST = 2;
    protected static final int GALLERY_PICKER_REQUEST = 3;
    protected final String DATEFORMAT = "yyyy-MM-dd";
    //GUI & NAV
    protected INewMemoryFListener mListener;
    protected CoordinatorLayout coordinatorLayoutNewMemoryF;
    protected ViewGroup mBottomsheet;
    protected BottomSheetBehavior mBottomsheetBehavior;
    protected Toolbar mToolbar;
    protected ImageView imgvMedia, imgvCamera, imgvAudio, imgvBrowse;
    protected ImageButton imgbtnAudio;
    protected TextInputEditText etxtTitle,etxtDescription;
    protected TextView txtvMedia;
    protected Button btnLocation,btnDate;
    //Helpers & data
    protected StorageHelper mStorageHelper;
    protected Recorder recorder;
    protected Calendar mCalendar = Calendar.getInstance();
    protected Place mPlace;
    protected MediaItem mMediaItem;
    protected DatePickerDialog.OnDateSetListener date;

    //Media Methods
    /*protected void checkGPSSettings(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //demandGPSActivation();
        }
    }*/
    /*protected void demandGPSActivation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("De GPS is niet geactiveerd wil je deze activeren?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }*/
    protected void confirmAudioSave() {
        mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Snackbar snack = Snackbar.make(getActivity().findViewById(R.id.linearlayoutNewMemoryFContent),"Nieuw opname :",Snackbar.LENGTH_LONG).setAction("Opslaan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Audio opgeslaan",Toast.LENGTH_LONG).show();
                mStorageHelper.saveAudio();
                File audioFile = mStorageHelper.getFile();
                mMediaItem = new MediaItem(MediaItem.Type.AUDIO,audioFile.getPath());
                imgvMedia.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_mic_black_24dp));
                txtvMedia.setText("audio-opname "+recorder.getDurationString());
                //set thumbnail here
            }
        });
        snack.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {
            }
            public void onViewDetachedFromWindow(View v) {
                //will cancel audio unless saved
                mStorageHelper.cancelAudio();
            }
        });
        snack.show();
    }
    protected void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),  "Pic_"+System.currentTimeMillis()+".jpg");
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mStorageHelper.newImage());
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }
    protected void handleCameraResult() {
        if (mStorageHelper.hasFile()) {
            File imageFile;
            try {
                imageFile = mStorageHelper.getFile();
                Picasso.with(getActivity()).load(imageFile).into(imgvMedia);
                //File f = new File()
                mMediaItem.setPath(imageFile.getPath());
                mMediaItem.setType(MediaItem.Type.IMAGE);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), "Foto werdt niet aan gallerij toegevoegd", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Foto werdt niet opgeslaan", Toast.LENGTH_LONG).show();
        }
    }
    protected void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_PICKER_REQUEST);
    }

    //Subcycle
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (INewMemoryFListener) getActivity();
        }catch(ClassCastException ex) {
            throw new ClassCastException(getActivity().getPackageName()+" must impl INewMemoryFListener");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.ALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }else {
            Toast.makeText(getContext(),"Actie niet toegestaan",Toast.LENGTH_SHORT).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void init() {
        mMediaItem = new MediaItem();
        final Runnable initLoader = new Runnable() {
            public void run() {
                initMedia();
                // checkGPSSettings();
                addEvents();
                createToolbar();
            }
        };
        AsyncTask bottomsheetLoader = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                createBottomSheet();
                return "Executed";
            }

            @Override
            protected void onPostExecute(Object o) {
                inflateBottomsheet(R.layout.bottomsheet_media);
            }
        };

    }
    protected void initMedia() {
        date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                btnDate.setText(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime()));
            }
        };
        recorder = new Recorder();
        mStorageHelper = new StorageHelper();
    }
    protected void addEvents() {
        imgvMedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openBottomSheet();
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //check if google play services are available
                GoogleApiAvailability api = GoogleApiAvailability.getInstance();
                int code = api.isGooglePlayServicesAvailable(getActivity());
                if (code == ConnectionResult.SUCCESS) {
                    openPlacePicker();
                } else {
                    AlertDialog alertDialog = getDownloadGooglePlayDialog();
                    alertDialog.show();
                }
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
    private AlertDialog getDownloadGooglePlayDialog() {
        AlertDialog dialog =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Google Play")
                .setMessage("Je hebt Google Play Services nodig om deze functionaliteiten te gebruiken")
                .setIcon(R.drawable.ic_delete_white_24px)
                .setPositiveButton("Doorgaan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        return dialog;
    }
    protected void loadMediaView(View v) {
        imgvCamera = (ImageView) v.findViewById(R.id.imgvCamera);
        imgbtnAudio = (ImageButton) v.findViewById(R.id.imgbtnAudio);
        imgvBrowse = (ImageView) v.findViewById(R.id.imgvBrowse);
        addMediaEvents();
    }
    protected void addMediaEvents() {
        imgvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        imgbtnAudio.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO},RECORD_AUDIO);
                        } else {
                            recorder.startRecording(mStorageHelper.newAudio());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        try{
                            recorder.stopRecording();
                            confirmAudioSave();
                        } catch(Exception e) {
                            mBottomsheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            Toast.makeText(getActivity(),"Te korte opname",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });
        imgvBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseGallery();
            }
        });
    }
    public void onDetach() {
        super.onDetach();
        mListener =null;
    }
    //TO OVERRIDE
    protected void createToolbar() {}

    //GUI
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
    protected void inflateBottomsheet(int resource) {
        mBottomsheet.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        loadMediaView(inflater.inflate(resource, mBottomsheet));
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


}
