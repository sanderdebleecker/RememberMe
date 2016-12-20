package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;

public class SessionAlbumAdapter extends RecyclerView.Adapter<SessionAlbumAdapter.MyViewHolder> {
    private Context mContext;
    private SortedList<SelectableAlbum> mAlbums;
    private static final Comparator<SelectableAlbum> comperator = new Comparator<SelectableAlbum>() {
        @Override
        public int compare(SelectableAlbum a, SelectableAlbum b) {
            return a.getName().compareTo(b.getName());
        }
    };
    private float mUnitSize = 0;
    private int mColumns;
    //CTOR


    public SessionAlbumAdapter(Context context) {
        this.mContext = context;
        init();
    }
    public SessionAlbumAdapter(Context context,int columns) {
        this.mContext = context;
        init();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    //INIT
    private void init() {
        calculateUnitDimensions();
        loadSortedList();
    }
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        mUnitSize = dpWidth / mColumns;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
    private void loadSortedList() {
        mAlbums = new SortedList<>(SelectableAlbum.class, new SortedList.Callback<SelectableAlbum>() {
            @Override
            public int compare(SelectableAlbum a, SelectableAlbum b) {
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
            public boolean areContentsTheSame(SelectableAlbum oldItem, SelectableAlbum newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
            @Override
            public boolean areItemsTheSame(SelectableAlbum item1, SelectableAlbum item2) {
                return item1.getId() == item2.getId();
            }
        });
    }
}
