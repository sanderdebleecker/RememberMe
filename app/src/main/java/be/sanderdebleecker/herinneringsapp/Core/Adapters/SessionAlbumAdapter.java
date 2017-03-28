package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import java.util.Comparator;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;
import be.sanderdebleecker.herinneringsapp.R;

public class SessionAlbumAdapter extends RecyclerView.Adapter<SessionAlbumAdapter.MyViewHolder> {
    private Context mContext;
    private int[] resources = new int[]{R.drawable.ic_panorama_fish_eye_red_24dp,R.drawable.ic_lens_red_24dp};
    private SortedList<SelectableAlbum> mAlbums;
    private static final Comparator<SelectableAlbum> comperator = new Comparator<SelectableAlbum>() {
        @Override
        public int compare(SelectableAlbum a, SelectableAlbum b) {
            return a.getName().compareTo(b.getName());
        }
    };
    private float mUnitSize = 0;
    private int mColumns = 3;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_session_album,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SelectableAlbum a = mAlbums.get(position);
        //Set toggled
        if(a.isSelected()) {
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[1]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,1);
        }else{
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[0]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,0);
        }
        //Arrange height
        TableRow.LayoutParams params = new TableRow.LayoutParams((int) mUnitSize, (int) mUnitSize); // (width, height)
        holder.container.setLayoutParams(params);
        //Set Image

        loadThumbnail(holder.imgvAlbum,a.getThumbnail().getType(),a.getThumbnail().getPath());
    }
    private void loadThumbnail(ImageView imgv, String type, String path) {
        StorageHelper.loadCroppedMedia(mContext,imgv,new MediaItem(MediaItem.Type.valueOf(type),path), (int) mUnitSize);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    // Init
    private void init() {
        calculateUnitDimensions();
        loadSortedList();
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
                return item1.getUuid().equals(item2.getUuid());
            }
        });
    }
    // Events
    private void selectionChange(int pos,boolean selected) {
        mAlbums.get(pos).setSelected(selected);
    }
    // List
    public void add(List<SelectableAlbum> albums){
        mAlbums.beginBatchedUpdates();
        for(SelectableAlbum a : albums) {
            mAlbums.add(a);
        }
        mAlbums.endBatchedUpdates();
    }
    public void add(SelectableAlbum a) {
        mAlbums.add(a);
    }
    public void remove(SelectableAlbum a) {
        mAlbums.remove(a);
    }
    public void remove(List<SelectableAlbum> albums) {
        mAlbums.beginBatchedUpdates();
        for (SelectableAlbum album : albums) {
            mAlbums.remove(album);
        }
        mAlbums.endBatchedUpdates();
    }
    public void replaceAll(List<SelectableAlbum> models) {
        mAlbums.beginBatchedUpdates();
        for (int i = mAlbums.size() - 1; i >= 0; i--) {
            final SelectableAlbum model = mAlbums.get(i);
            if (!models.contains(model)) {
                mAlbums.remove(model);
            }
        }
        mAlbums.addAll(models);
        mAlbums.endBatchedUpdates();
        notifyDataSetChanged();
    }
    //m
    public boolean hasSelected() {
        boolean hasSelected=false;
        for(int i=0;i<mAlbums.size();i++) {
            if(mAlbums.get(i).isSelected()) {
                hasSelected=true;
                break;
            }
        }
        return hasSelected;
    }
    
    // Misc
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        mUnitSize = dpWidth / mColumns;
    }

    // Viewholder
    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvAlbum;
        public ImageView imgvToggle;
        public RelativeLayout container;

        public MyViewHolder(View v) {
            super(v);
            imgvAlbum = (ImageView) v.findViewById(R.id.session_album_imgvAlbum);
            imgvToggle = (ImageView) v.findViewById(R.id.session_album_imgvToggle);
            imgvToggle.setTag(R.id.imgvToggle_status,0);
            container = (RelativeLayout) v.findViewById(R.id.row_album_memory);
            initEvents();
        }
        private void initEvents() {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selected=0;
                    if(Integer.parseInt(imgvToggle.getTag(R.id.imgvToggle_status).toString())==0) {
                        selected=1;
                    }
                    imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[selected]));
                    imgvToggle.setTag(R.id.imgvToggle_status,selected);
                    selectionChange(getAdapterPosition(),selected==1);
                }
            });
        }

    }
}
