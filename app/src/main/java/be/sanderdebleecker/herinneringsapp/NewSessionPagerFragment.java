package be.sanderdebleecker.herinneringsapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.SelectableAlbumAdapter;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.AlbumDA;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;
import be.sanderdebleecker.herinneringsapp.Models.Session;

public class NewSessionPagerFragment extends SessionPagerFragment {
    protected final String DATEFORMAT = "yyyy-MM-dd";
    private RecyclerView mRecyclerView;
    private SelectableAlbumAdapter mAdapter;
    private List<SelectableAlbum> mAlbums;
    private Button btnDate;
    private EditText txtName;
    protected Calendar mCalendar = Calendar.getInstance();
    protected DatePickerDialog.OnDateSetListener date;
    private final int COLUMNS = 3;

    public static NewSessionPagerFragment newInstance() {
        NewSessionPagerFragment fragment = new NewSessionPagerFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_new_session_pager, container, false);
        new Initializer().execute(v);
        return v;
    }
    private void loadView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.new_session_recyclerview);
        btnDate = (Button) v.findViewById(R.id.new_session_btnDate);
        txtName =  (EditText) v.findViewById(R.id.new_session_txtName);
    }
    private void addCalendarEvent() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                btnDate.setText(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime()));
            }
        };
    }
    private void loadAlbums() {
        MainApplication app = (MainApplication) getContext().getApplicationContext();
        AlbumDA albumSource = new AlbumDA(getContext());
        albumSource.open();
        mAlbums = albumSource.getSelectabl(app.getCurrSession().getAuthIdentity());
        albumSource.close();
    }
    private void loadList() {
        mAdapter = new SelectableAlbumAdapter(getContext());
    }
    private void loadListLayout() {
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.add(mAlbums);
    }
    public String validate() {
        boolean hasTitle = txtName.getText().toString().trim().length()>0;
        boolean hasContent = mAdapter.getSelectedAlbums().size()>0;
        if(!hasTitle) {
            return getString(R.string.ErrorNamelessSession);
        }else {
            if(!hasContent) {
                return getString(R.string.ErrorContentlessSession);
            }else{
                return "";
            }
        }
    }

    //Get
    public List<Integer> getAlbums() {
        return mAdapter.getSelectedAlbums();
    }
    public String getName() {
        return txtName.getText().toString().trim();
    }
    public String getDate() {
        return new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime());
    }
    //Tasks
    private class Initializer extends AsyncTask<View, Void, Void> {
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            addCalendarEvent();
            loadAlbums();
            loadList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnDate.setText(new SimpleDateFormat(DATEFORMAT, Locale.ENGLISH).format(mCalendar.getTime()));
            loadListLayout();
        }
    }
}
