package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import be.sanderdebleecker.herinneringsapp.Interfaces.IEndSessionPagerFListener;

public class EndSessionPagerFragment extends SessionPagerFragment {
    private TextView txtvName;
    private TextView txtvDate;
    private TextView txtvDuration;
    private EditText etxtNotes;
    private IEndSessionPagerFListener mListener;
    //CTOR
    public static EndSessionPagerFragment newInstance(IEndSessionPagerFListener listener) {
        EndSessionPagerFragment fragment = new EndSessionPagerFragment();
        fragment.mListener = listener;
        return fragment;
    }
    //lc
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_end_session_pager, container, false);
        new Initializer().execute(v);
        return v;
    }
    //lc m
    private void loadView(View v) {
        txtvName = (TextView) v.findViewById(R.id.end_session_txtvName);
        txtvDate = (TextView) v.findViewById(R.id.end_session_txtvDate);
        txtvDuration = (TextView) v.findViewById(R.id.end_session_txtvDuration);
        etxtNotes = (EditText) v.findViewById(R.id.end_session_etxtNotes);
    }
    private void loadSession() {
        txtvName.setText(mListener.getSessionName());
        txtvDate.setText(mListener.getSessionDate());
        txtvDuration.setText(""+mListener.getSessionDuration());
    }
    //Tasks
    private class Initializer extends AsyncTask<View, Void, Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            loadSession();
        }
    }

}
