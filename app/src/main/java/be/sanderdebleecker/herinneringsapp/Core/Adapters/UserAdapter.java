package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Models.View.UserVM;
import be.sanderdebleecker.herinneringsapp.R;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private List<UserVM> userVMs;
    public UserAdapter(List<UserVM> userVMs) {
        this.userVMs = userVMs;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        MyViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.txtvRowUsername);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserVM userVM = userVMs.get(position);
        holder.username.setText(userVM.getUsername());
    }

    @Override
    public int getItemCount() {
        return userVMs.size();
    }
}