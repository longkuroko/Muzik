package com.example.androidappmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.SongActivity;
import com.example.androidappmusic.models.Genre;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder>{

    private static final String TAG = "GenreAdapter";

    private Context context;
    private List<Genre> genreList;

    public GenreAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get()
                .load(genreList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivGenreImage);

        holder.tvGenreName.setText(genreList.get(position).getName());

        holder.ivGenreImage.setOnClickListener( v -> {
            Intent intent = new Intent(context, SongActivity.class);
            intent.putExtra("GENRE", genreList.get(holder.getLayoutPosition()));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGenreImage;
        private TextView tvGenreName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.ivGenreImage = itemView.findViewById(R.id.ivGenreImage);

            this.tvGenreName = itemView.findViewById(R.id.tvGenreName);
            this.tvGenreName.setSelected(true);
        }
    }
}
