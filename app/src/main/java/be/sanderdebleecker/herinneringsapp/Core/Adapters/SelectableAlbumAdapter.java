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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.SelectableAlbum;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;
import be.sanderdebleecker.herinneringsapp.R;

public class SelectableAlbumAdapter extends RecyclerView.Adapter<SelectableAlbumAdapter.MyViewHolder>{
    private int mColumns = 3;
    private float unitSize = 0;
    private SortedList<SelectableAlbum> mAlbums;
    private Context mContext;
    private boolean lockedSelection = false;
    private int[] resources = new int[]{R.drawable.ic_panorama_fish_eye_red_24dp,R.drawable.ic_lens_red_24dp};
    private static final Comparator<SelectableAlbum> comperator = new Comparator<SelectableAlbum>() {
        @Override
        public int compare(SelectableAlbum a, SelectableAlbum b) {
            return a.getName().compareTo(b.getName());
        }
    };

    //CTOR
    public SelectableAlbumAdapter(Context context) {
        this.mContext = context;
        init();
    }
    public SelectableAlbumAdapter(Context context, int columns) {
        this.mContext = context;
        this.mColumns = columns;
        init();
    }
    private void init() {
        calculateUnitDimensions();
        loadSortedList();
    }

    //Viewholder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvToggle;
        public ImageView imgvAlbum;
        public RelativeLayout container;

        public MyViewHolder(View v) {
            super(v);
            imgvAlbum = (ImageView) v.findViewById(R.id.selectable_album_imgvAlbum);
            imgvToggle = (ImageView) v.findViewById(R.id.selectable_album_imgvToggle);
            imgvToggle.setTag(R.id.imgvToggle_status,0);
            container = (RelativeLayout) v.findViewById(R.id.row_selectable_album);
            initEvents();
        }
        private void initEvents() {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lockedSelection) return;
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
    private void selectionChange(int pos,boolean selected) {
        mAlbums.get(pos).setSelected(selected);
    }

    //Recycler Methods
    public SelectableAlbumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selectable_album,parent,false);
        return new SelectableAlbumAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(SelectableAlbumAdapter.MyViewHolder holder, int position) {
        SelectableAlbum a = mAlbums.get(position);
        //Set toggled
        if(a.isSelected()) {
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[1]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,1);
        }else{
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[0]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,0);
        }
        //Get fields
        String name = a.getName();
        String type = a.getThumbnail().getType();
        String path = a.getThumbnail().getPath();
        //Arrange height
        TableRow.LayoutParams params = new TableRow.LayoutParams((int) unitSize, (int) unitSize); // (width, height)
        holder.container.setLayoutParams(params);
        //Set thumbnail
        if(type!=null && path!=null) {
            loadThumbnail(holder.imgvAlbum,type,path);
        }
    }
    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    //Selection Methods
    public List<String> getSelectedAlbums() {
        List<String> selectedAlbums = new ArrayList<String>();
        for(int i=0;i<mAlbums.size();i++) {
            if(mAlbums.get(i).isSelected()) {
                selectedAlbums.add(mAlbums.get(i).getUuid());
            }
        }
        return selectedAlbums;
    }
    public void filterSelected(List<String> selectedAlbums) {
        for(int i=0;i<mAlbums.size();i++) {
            if (selectedAlbums.contains(mAlbums.get(i).getUuid())) {
                mAlbums.get(i).setSelected(true);
            }
        }//cant remove in same loop since array.size is condition
        for(int j=0;j<mAlbums.size();j++) {
            if(!selectedAlbums.contains(mAlbums.get(j).getUuid())) {
                mAlbums.get(j).setSelected(false);
                mAlbums.remove(mAlbums.get(j));
            }
        }
        notifyDataSetChanged();
    }
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

    //SortedList methods
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
                return item1.getUuid() == item2.getUuid();
            }
        });
    }
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
        for (SelectableAlbum a : albums) {
            mAlbums.remove(a);
        }
        mAlbums.endBatchedUpdates();
    }
    public void replaceAll(List<SelectableAlbum> albums) {
        mAlbums.beginBatchedUpdates();
        for (int i = mAlbums.size() - 1; i >= 0; i--) {
            final SelectableAlbum album = mAlbums.get(i);
            if (!albums.contains(album)) {
                mAlbums.remove(album);
            }
        }
        mAlbums.addAll(albums);
        mAlbums.endBatchedUpdates();
        notifyDataSetChanged();
    }

    //Misc
    private void loadThumbnail(ImageView imgv, String type, String path) {
        StorageHelper.loadCroppedMedia(mContext,imgv,new MediaItem(MediaItem.Type.valueOf(type),path), (int) unitSize);
    }
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        unitSize = dpWidth / mColumns;
    }
}
