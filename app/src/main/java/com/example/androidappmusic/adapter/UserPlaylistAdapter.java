package com.example.androidappmusic.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.R;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Status;
import com.example.androidappmusic.models.UserPlaylist;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;

import java.util.ArrayList;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.ViewHolder> {
    private static final String TAG = "UserPlaylistAdapter";

    private Dialog dialog_1;
    private Dialog dialog_2;

    private Context context;


    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private int songID = -1;

    private ArrayList<UserPlaylist> userPlaylistArrayLists; // Data from API
    private ArrayList<Status> statusArrayList;

    private TextView tvNumberPlaylist;

    private ScaleAnimation scaleAnimation;
    private AlertDialog alertDialog;


    public UserPlaylistAdapter(Context context, ArrayList<UserPlaylist> userPlaylistArrayList, TextView tvNumberPlaylist) {
        this.context = context;
        this.userPlaylistArrayList = userPlaylistArrayList;
        this.tvNumberPlaylist = tvNumberPlaylist;
    }

    public UserPlaylistAdapter(Context context, ArrayList<UserPlaylist> userPlaylistArrayList, int songID) {
        this.context = context;
        this.userPlaylistArrayList = userPlaylistArrayList;
        this.songID = songID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataLocalManager.init(context);

        holder.tvPlaylistName.setText(this.userPlaylistArrayList.get(position).getName().trim());
        holder.tvNumberSongPlaylist.setText(String.valueOf(this.userPlaylistArrayList.get(position).getTotalSong()));
        holder.ivPlaylistMore.setOnClickListener(v -> Open_Info_Playlist_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));

        if (this.songID > -1) {
            holder.itemView.setOnClickListener(v -> {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog,null);
                alertBuilder.setView(view);
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();
            });
        }

    }

    private void Open_Info_Playlist_Dialog(int bottom, int layoutPosition) {
        this.dialog_1 = new Dialog(this.context);

        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_2.setContentView(R.layout.layout_userplaylist_more);

        Window window = dialog_1.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cvPlaylistCover;
        private TextView tvPlaylistName;
        private TextView tvNumberSongPlaylist;
        private ImageView ivPlaylistMore;

        private ScaleAnimation scaleAnimation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cvPlaylistCover = itemView.findViewById(R.id.cvPlaylistCover);
        }
    }
}
