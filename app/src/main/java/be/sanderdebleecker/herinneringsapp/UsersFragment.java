package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import be.sanderdebleecker.herinneringsapp.Core.Adapters.UserAdapter;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.RecyclerTouchListener;
import be.sanderdebleecker.herinneringsapp.Data.UserDA;
import be.sanderdebleecker.herinneringsapp.Interfaces.IClickListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IUserFListener;
import be.sanderdebleecker.herinneringsapp.Models.View.UserVM;

public class UsersFragment extends Fragment {
    private UserAdapter adapter;
    private List<UserVM> userVMs = new ArrayList<>();
    private IUserFListener mListener;
    private RecyclerView recycUsers;
    private TextView btnOtherAccount;
    private Button btnNewAccount;

    //CTOR
    public UsersFragment() {
    }
    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();
        return fragment;
    }
    //LIFECYCLE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void loadList(){
        adapter = new UserAdapter(userVMs);
        //was applicationcontext
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycUsers.setLayoutManager(mLayoutManager);
        recycUsers.setItemAnimator(new DefaultItemAnimator());
        recycUsers.setAdapter(adapter);
        recycUsers.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycUsers, new IClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserVM userVM = userVMs.get(position);
                mListener.onUserSelect(userVM.getUsername(),userVM.getId());
                //Toast.makeText(getActivity().getApplicationContext(), user.getUsername() + " is selected!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        recycUsers = (RecyclerView) v.findViewById(R.id.recyc_users);
        btnOtherAccount = (TextView) v.findViewById(R.id.btnOtherAccount);
        btnNewAccount = (Button) v.findViewById(R.id.btnNewAccount);
        // loadView(v);
        addEvents();
        new LoadListTask().execute();
        return v;
    }

    private void addEvents() {
        btnOtherAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackToLogin();
            }
        });
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackToRegister();
            }
        });
    }
     @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (IUserFListener) context;
        } catch(ClassCastException ex) {
            throw new ClassCastException(context.getPackageName()+" : Must implement IUserFListener.");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener =null;
    }

    //ASYNC
    public class LoadListTask extends AsyncTask<Void, Void, Void> {

        public LoadListTask() {
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            UserDA usersData = new UserDA(getContext());
            usersData.open();
            userVMs = usersData.getAll();
            usersData.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadList();
        }
    }

}
