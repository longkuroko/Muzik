package com.example.androidappmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.SongActivity;
import com.example.androidappmusic.models.Playlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaylistHomeAdapter extends RecyclerView.Adapter<PlaylistHomeAdapter.ViewHolder>{

    private static final String TAG = "PlaylistHomeAdapter";

    Context context;
    ArrayList<Playlist> playlistArrayList;

    public PlaylistHomeAdapter(Context context, ArrayList<Playlist> playlistArrayList) {
        this.context = context;
        this.playlistArrayList = playlistArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_home, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHomeAdapter.ViewHolder holder, int position) {
        Picasso.get()
                .load(playlistArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivPlaylist);

//        holder.itemView.setOnClickListener( v -> {
//            Intent intent = new Intent(context, SongActivity.class);
//            intent.putExtra("PLAYLIST", playlistArrayList.get(holder.getLayoutPosition()));
//            context.startActivity(intent);
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SongActivity.class);
                intent.putExtra("PLAYLIST", playlistArrayList.get(holder.getLayoutPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cvPlaylist;
        ImageView ivPlaylist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvPlaylist = itemView.findViewById(R.id.cvPlaylist);
            ivPlaylist = itemView.findViewById(R.id.ivPlaylistImage);

        }
    }
}
