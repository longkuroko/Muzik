package com.example.androidappmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.SongActivity;
import com.example.androidappmusic.models.Album;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AlbumAdapter  extends  RecyclerView.Adapter<AlbumAdapter.ViewHolder>{

    private static final String TAG = "AlbumAdapter";

    private Context context;
    private List<Album> albumArrayList;
    private ViewPager2 viewPager2; //

    public AlbumAdapter(Context context, List<Album> albumArrayList, ViewPager2 viewPager2) {
        this.context = context;
        this.albumArrayList = albumArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(albumArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivAlbum);
        holder.tvAlbumName.setText(albumArrayList.get(position).getName().trim());
        holder.tvAlbumSinger.setText(albumArrayList.get(position).getSinger().trim());

//        holder.itemView.setOnClickListener(v->{
//
//            Intent intent = new Intent(context, SongActivity.class);
//            intent.putExtra("ALBUM", albumArrayList.get(holder.getLayoutPosition()));
//            context.startActivity(intent);
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SongActivity.class);
                intent.putExtra("ALBUM", albumArrayList.get(holder.getLayoutPosition()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return albumArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cvAlbum;
        private ImageView ivAlbum;
        private TextView tvAlbumName;
        private TextView tvAlbumSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvAlbum = (CardView) itemView.findViewById(R.id.cvAlbum);
            ivAlbum = (ImageView) itemView.findViewById(R.id.ivAlbum);

            tvAlbumName = (TextView) itemView.findViewById(R.id.tvAlbumName);
            tvAlbumName.setSelected(true); // Text will be moved

            tvAlbumSinger = (TextView) itemView.findViewById(R.id.tvAlbumSinger);
            tvAlbumSinger.setSelected(true); // Text will be moved
        }
    }
}
