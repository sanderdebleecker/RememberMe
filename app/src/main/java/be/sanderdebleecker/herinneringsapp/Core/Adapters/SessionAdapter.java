package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;
import be.sanderdebleecker.herinneringsapp.Models.View.SessionVM;
import be.sanderdebleecker.herinneringsapp.R;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.MyViewHolder>  {
    private Context mContext;
    private SortedList<SessionVM> mSessions;
    private static final Comparator<SessionVM> comperator = new Comparator<SessionVM>() {
        @Override
        public int compare(SessionVM a, SessionVM b) {
            return a.getName().compareTo(b.getName());
        }
    };
    //CTOR
    public SessionAdapter(Context context) {
        this.mContext = context;
        loadSortedList();
    }
    //Viewholder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvName;
        private TextView txtvCreator;
        private TextView txtvDuration;
        private TextView txtvDate;

        public MyViewHolder(View v) {
            super(v);
            txtvName = (TextView) v.findViewById(R.id.row_session_txtvName);
            txtvCreator = (TextView) v.findViewById(R.id.row_session_txtvCreator);
            txtvDuration = (TextView) v.findViewById(R.id.row_session_txtvDuration);
            txtvDate = (TextView) v.findViewById(R.id.row_session_txtvDate);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_session,parent,false);
        return new SessionAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SessionVM session = mSessions.get(position);

    }
    //SortedList
    private void loadSortedList() {
        mSessions = new SortedList<>(SessionVM.class, new SortedList.Callback<SessionVM>() {
            @Override
            public int compare(SessionVM a, SessionVM b) {
                return comperator.compare(a,b);
            }
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }
            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }
            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }
            @Override
            public boolean areContentsTheSame(SessionVM item1, SessionVM item2) {
                boolean name = item1.getName().equals(item2.getName());
                boolean author = item1.getAuthor().equals(item2.getAuthor());
                boolean duration = item1.getDuration() == item2.getDuration();
                boolean date = item1.getDate().equals(item2.getDate());
                return name && author && duration && date;
            }
            @Override
            public boolean areItemsTheSame(SessionVM item1, SessionVM item2) {
                return item1.getId()==item2.getId();
            }
        });
    }
    public void add(List<SessionVM> sessions){
        mSessions.beginBatchedUpdates();
        for(SessionVM s : sessions) {
            mSessions.add(s);
        }
        mSessions.endBatchedUpdates();
    }
    public void add(SessionVM s) {
        mSessions.add(s);
    }
    public void remove(SessionVM s) {
        mSessions.remove(s);
    }
    public void remove(List<SessionVM> sessions) {
        mSessions.beginBatchedUpdates();
        for (SessionVM session : sessions) {
            mSessions.remove(session);
        }
        mSessions.endBatchedUpdates();
    }
    public void replaceAll(List<SessionVM> models) {
        mSessions.beginBatchedUpdates();
        for (int i = mSessions.size() - 1; i >= 0; i--) {
            final SessionVM session = mSessions.get(i);
            if (!models.contains(session)) {
                mSessions.remove(session);
            }
        }
        mSessions.addAll(models);
        mSessions.endBatchedUpdates();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mSessions.size();
    }

}
