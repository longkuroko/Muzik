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
import com.example.androidappmusic.activity.GenreActivity;
import com.example.androidappmusic.models.Genre;
import com.example.androidappmusic.models.Theme;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    private Context context;
    private List<Theme> themes;

    public ThemeAdapter(Context context, List<Theme> themes){
        this.context = context;
        this.themes = themes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(themes.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivTheme);
        holder.ivTheme.setOnClickListener( v -> {
            Intent intent = new Intent(context, GenreActivity.class);
            intent.putExtra("THEME", themes.get(holder.getLayoutPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivTheme;
        CardView cvTheme;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTheme = itemView.findViewById(R.id.ivTheme);
        }
    }
}
