package be.sanderdebleecker.herinneringsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SessionPagerFragment extends Fragment {
    private Pages mPage;
    public enum Pages {
        NewSession(R.layout.fragment_new_session_pager),
        SessionMemory(R.layout.fragment_new_session_pager),
        EndSession(R.layout.fragment_new_session_pager);

        private int resource;
        Pages(int resource) {
            this.resource = resource;
        }
        public int getResource() {
            return resource;
        }
    }

    public static SessionPagerFragment newInstance(Pages page) {
        SessionPagerFragment fragm = new SessionPagerFragment();
        fragm.mPage = page;
        return fragm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(mPage.getResource(), container, false);
        return v;
    }
}
