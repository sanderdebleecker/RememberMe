package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Models.Trust;
import be.sanderdebleecker.herinneringsapp.R;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.MyViewHolder> {
    private List<Trust> mTrusts;
    private Context mContext;

    public FollowerAdapter(Context context,List<Trust> trusts) {
        this.mTrusts = trusts;
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvFollower;
        private TextView txtvFollowerState;
        private ImageView imgvFollowerAction;

        public MyViewHolder(View v) {
            super(v);
            txtvFollower = (TextView) v.findViewById(R.id.txtvFollower);
            txtvFollowerState = (TextView) v.findViewById(R.id.txtvFollowerState);
            imgvFollowerAction = (ImageView) v.findViewById(R.id.imgvFollowerAction);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follower,parent,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trust t = mTrusts.get(position);
        holder.txtvFollower.setText(t.getB().getName());
        switch(t.getRel()) {
            case MUTUAL:
                holder.txtvFollowerState.setText("volgend");
                Picasso.with(mContext).load(R.drawable.ic_group_black_24dp).into(holder.imgvFollowerAction);
                break;
            case RECEIVED:
                holder.txtvFollowerState.setText("te bevestigen");
                Picasso.with(mContext).load(R.drawable.ic_check_circle_black_24dp).into(holder.imgvFollowerAction);
                break;
            case REQUESTED:
                holder.txtvFollowerState.setText("verzoek verzonden");
                Picasso.with(mContext).load(R.drawable.ic_email_black_24dp).into(holder.imgvFollowerAction);
                break;
            default:
                holder.txtvFollowerState.setText("onbepaald");
                break;
        }
    }
    @Override
    public int getItemCount() {
        return mTrusts.size();
    }
}
