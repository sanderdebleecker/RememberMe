package be.sanderdebleecker.herinneringsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;

public class MemorySessionPagerFragment extends SessionPagerFragment {
    private String mTitle;
    private String mPath;
    private String mType;
    private ImageView imgvMemory;
    private TextView txtvMemory;

    public static MemorySessionPagerFragment newInstance(String title,String path,String type) {
        MemorySessionPagerFragment fragment = new MemorySessionPagerFragment();
        fragment.mTitle = title;
        fragment.mPath = path;
        fragment.mType = type;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_memory_session_pager, container, false);
        new Initializer().execute(v);
        return v;
    }
    private void loadView(View v) {
        imgvMemory = (ImageView) v.findViewById(R.id.memory_session_imgvMemory);
        txtvMemory = (TextView) v.findViewById(R.id.memory_session_txtvMemory);
    }
    private void loadMemory() {
        txtvMemory.setText(mTitle);
        StorageHelper.loadMedia(getContext(),imgvMemory,new MediaItem(MediaItem.Type.valueOf(mType),mPath));
    }
    //TASKS
    private class Initializer extends AsyncTask<View, Void, Void> {
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadMemory();
        }
    }
}
