package be.sanderdebleecker.herinneringsapp.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;

import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.R;

/*
*  Creates a file to save data to
*  you can retrieve it once you have saved the data to the file
*  also supports a cachedfile for audio to avoid overwritting the current file
*
*  Has some static workarounds for incompatible mediaworkers
* */

public class StorageHelper {
    private static final String IMG_DIR = "/imgs";
    private static final String AUDIO_DIR = "/rec";
    private static final String AUDIO_EXT = ".ogg";
    private static final String AUDIO_PRE = "Aud_";
    private static final String IMG_PRE = "Pic_";
    private static final String IMG_EXT = ".jpg";
    private File currFile;
    private File cachedFile;
    private boolean hasFile;
    private boolean hasCachedFile;

    public StorageHelper() {
        createPublicDirs();
    }
    private void createPublicDirs() {
        createPublicAudioDir();
        createPublicImageDir();
    }
    private void createPublicAudioDir() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File directoryTemplate = new File(filepath,AUDIO_DIR );
        if(!directoryTemplate.exists()){
            directoryTemplate.mkdirs();
        }
    }
    private void createPublicImageDir() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File directoryTemplate = new File(filepath,IMG_DIR );
        if(!directoryTemplate.exists()){
            directoryTemplate.mkdirs();
        }
    }
    //IO
    public Uri newImage() {
        if(hasFile) deleteFile();
        if(hasCachedFile) deleteCachedFile();
        hasFile = true;
        currFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),  IMG_PRE+System.currentTimeMillis()+IMG_EXT);
        return Uri.fromFile(currFile);
    }
    public boolean deleteFile() {
        return currFile.delete();
    }
    private boolean deleteCachedFile() {
        return cachedFile.delete();
    }
    public void cancelAudio() {
        if(hasCachedFile) {
            deleteCachedFile();
            hasCachedFile=false;
        }
    }
    public String newAudio() {
        if(hasCachedFile) deleteCachedFile();
        String filepath = Environment.getExternalStorageDirectory().getPath();
        cachedFile = new File(filepath+AUDIO_DIR, AUDIO_PRE + System.currentTimeMillis() + AUDIO_EXT);
        hasCachedFile = true;
        return cachedFile.getAbsolutePath();
    }
    public void saveAudio() {
        currFile = cachedFile;
        cachedFile.delete();
        cachedFile = null;
        hasCachedFile = false;
        hasFile = true;
    }
    public boolean hasFile() {
        return hasFile;
    }
    public boolean hasCachedFile() {
        return hasCachedFile;
    }
    public File getFile() {
        return currFile;
    }

    //Gallery
    public void addImageToGallery(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, currFile.getPath());
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    public void addAudioToGallery(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "audio/mp4");
        values.put(MediaStore.MediaColumns.DATA, currFile.getPath());
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    //Loading
    public static void loadMedia(Context context,ImageView imgv,MediaItem item) {
        switch(item.getType()) {
            case AUDIO:
                imgv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_audiotrack_black_24dp));
                break;
            case NONE:
                imgv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_landscape_black_24dp));
            default:
                File f = new File(item.getPath());
                if(f.exists()) {
                    Picasso.with(context).load(f).into(imgv);
                } else {
                    imgv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_landscape_black_24dp));
                }
        }
    }
    public static void loadCroppedMedia(Context context,ImageView imgv, MediaItem item,int size) {
        switch(item.getType()) {
            case AUDIO:
                imgv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_audiotrack_black_24dp));
                break;
            case NONE:
                imgv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_landscape_black_24dp));
            default:
                File f = new File(item.getPath());
                if(f.exists()) {
                    Picasso.with(context).load(f).resize(size, size).centerCrop().into(imgv);
                }else{
                    imgv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_landscape_black_24dp));
                }
        }
    }

    //MediaRecorder datasource hotfix
    public static void setMediaPlayerDataSource(Context context, MediaPlayer mp, String fileInfo) throws Exception {
        if (fileInfo.startsWith("content://")) {
            try {
                Uri uri = Uri.parse(fileInfo);
                fileInfo = getRingtonePathFromContentUri(context, uri);
            } catch (Exception e) {
            }
        }

        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
                try {
                    setMediaPlayerDataSourcePreHoneyComb(context, mp, fileInfo);
                } catch (Exception e) {
                    setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
                }
            else
                setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);

        } catch (Exception e) {
            try {
                setMediaPlayerDataSourceUsingFileDescriptor(context, mp,
                        fileInfo);
            } catch (Exception ee) {
                String uri = getRingtoneUriFromPath(context, fileInfo);
                mp.reset();
                mp.setDataSource(uri);
            }
        }
    }
    private static void setMediaPlayerDataSourcePreHoneyComb(Context context, MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(fileInfo);
    }
    private static void setMediaPlayerDataSourcePostHoneyComb(Context context, MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(context, Uri.parse(Uri.encode(fileInfo)));
    }
    private static void setMediaPlayerDataSourceUsingFileDescriptor(Context context, MediaPlayer mp, String fileInfo) throws Exception {
        File file = new File(fileInfo);
        FileInputStream inputStream = new FileInputStream(file);
        mp.reset();
        mp.setDataSource(inputStream.getFD());
        inputStream.close();
    }
    private static String getRingtoneUriFromPath(Context context, String path) {
        Uri ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor ringtoneCursor = context.getContentResolver().query(
                ringtonesUri, null,
                MediaStore.Audio.Media.DATA + "='" + path + "'", null, null);
        ringtoneCursor.moveToFirst();

        long id = ringtoneCursor.getLong(ringtoneCursor
                .getColumnIndex(MediaStore.Audio.Media._ID));
        ringtoneCursor.close();

        if (!ringtonesUri.toString().endsWith(String.valueOf(id))) {
            return ringtonesUri + "/" + id;
        }
        return ringtonesUri.toString();
    }
    public static String getRingtonePathFromContentUri(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        ringtoneCursor.moveToFirst();
        String path = ringtoneCursor.getString(ringtoneCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        ringtoneCursor.close();
        return path;
    }

}
