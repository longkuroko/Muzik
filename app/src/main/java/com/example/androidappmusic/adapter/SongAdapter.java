package com.example.androidappmusic.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.models.Status;
import com.example.androidappmusic.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;

import com.example.androidappmusic.R;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private static final String TAG = "SongAdapter";

    private AlertDialog alertDialog;
    private AlertDialog alertDialog_1;

    private Dialog dialog_1;
    private Dialog dialog_2;
    private Dialog dialog_3;
    private Dialog dialog_4;


    int index = 0 ;
//    private boolean isDownloadSong = false;

    private ScaleAnimation scaleAnimation;

    private ArrayList<Song> favoriteSongArrayList;
    private ArrayList<Status> statusArrayList;

    private ArrayList<UserPlaylist> userPlaylistArrayList;
//    private UserPlaylistAdapter userPlaylistAdapter;

    private Context context;
    private ArrayList<Song> songArrayList;
    private int playlistID;
    private String layout;

    private final String GIF_URL = "https://i.stack.imgur.com/h6viz.gif";

    private static final String SONG = "SONG";
    private static final String FAVORITE_SONG = "FAVORITESONG";
    private static final String PLAYLIST_SONG = "PLAYLISTSONG";
    private static final String DOWNLOAD_SONG = "DOWNLOADSONG";
    private static final String SONG_SEARCH = "SONGSEARCH";
    private static final String LIST_SONG = "LISTSONG";

    //    private final String ACTION_INSERT_SONG_PLAYLIST = "insert";
    //    private final String ACTION_DELETE_SONG_PLAYLIST = "delete";
    //    private final String ACTION_DELETEALL_SONG_PLAYLIST = "deleteall";

    public SongAdapter(Context context, ArrayList<Song> songArrayList , String layout) {
        this.songArrayList = songArrayList;
        this.layout = layout;
        this.context = context;
    }

    public SongAdapter(Context context, ArrayList<Song> songArrayList, int playlistID, String layout) {
        this.context = context;
        this.songArrayList = songArrayList;
        this.playlistID = playlistID;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        DataLocalManager.init(context);

        holder.ivItemSongLove.setEnabled(false);
        Handle_Favourite_Icon_Color(holder.ivItemSongMore, position);


        Picasso.get()
                .load(songArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivItemSong);

        holder.tvItemSongName.setText(songArrayList.get(position).getImg().trim());
        holder.tvItemSongSinger.setText(songArrayList.get(position).getSinger().trim());

        if(!this.layout.equals(LIST_SONG)){
            holder.itemView.setOnClickListener(v ->{

                Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
                intent.putExtra("SONG", songArrayList.get(holder.getLayoutPosition()));
                v.getContext().startActivity(intent);
            });
        }

        holder.ivItemSongMore.setOnClickListener(v -> Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));

        holder.itemView.setOnLongClickListener( v -> {
            Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition());
            return false;

        });


    }


    private void Open_Info_Song_Dialog(int gravity, int position) {

        this.dialog_1 = new Dialog(this.context);

        this.dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog_1.setContentView(R.layout.layout_song_more);

        Window window = dialog_1.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần activity

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        this.dialog_1.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog


        ImageView ivInfoSongImage = this.dialog_1.findViewById(R.id.ivInfoSongImage);
        Picasso.get()
                .load(this.songArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(ivInfoSongImage);

        TextView tvInfoSongName = this.dialog_1.findViewById(R.id.tvInfoSongName);
        tvInfoSongName.setSelected(true);
        tvInfoSongName.setText(this.songArrayList.get(position).getName().trim());

        TextView tvInfoSongSinger = this.dialog_1.findViewById(R.id.tvInfoSongSinger);
        tvInfoSongSinger.setSelected(true);
        tvInfoSongSinger.setText(this.songArrayList.get(position).getSinger().trim());


        RelativeLayout rlPlaySong = this.dialog_1.findViewById(R.id.rlPlaySong);
        TextView tvPlaySong = this.dialog_1.findViewById(R.id.tvPlaySong);
        tvPlaySong.setSelected(true);

        RelativeLayout rlAddSongToPlaylist = this.dialog_1.findViewById(R.id.rlAddSongToPlaylist);
        TextView tvAddSongToPlaylist = this.dialog_1.findViewById(R.id.tvAddSongToPlaylist);
        tvAddSongToPlaylist.setSelected(true);

        RelativeLayout rlDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteSongToPlaylist);
        TextView tvDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteSongToPlaylist);
        tvDeleteSongToPlaylist.setSelected(true);

        RelativeLayout rlDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteAllSongToPlaylist);
        TextView tvDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteAllSongToPlaylist);
        tvDeleteAllSongToPlaylist.setSelected(true);

        //Delay dowload
//        RelativeLayout rlDownLoadSong = this.dialog_1.findViewById(R.id.rlDownLoadSong);
//        TextView tvDownLoadSong = this.dialog_1.findViewById(R.id.tvDownLoadSong);
 //       tvDownLoadSong.setSelected(true);

        RelativeLayout rlDeleteDownLoadSong = this.dialog_1.findViewById(R.id.rlDeleteDownLoadSong);
        TextView tvDeleteDownLoadSong = this.dialog_1.findViewById(R.id.tvDeleteDownLoadSong);
        tvDeleteDownLoadSong.setSelected(true);

        RelativeLayout rlCloseInfoPlaylist = this.dialog_1.findViewById(R.id.rlCloseInfoPlaylist);
        TextView tvCloseInfoSong = this.dialog_1.findViewById(R.id.tvCloseInfoSong);
        tvCloseInfoSong.setSelected(true);


        switch (layout) {
            case SONG:
            case FAVORITE_SONG:
            case SONG_SEARCH: {
                rlDeleteSongToPlaylist.setVisibility(View.GONE);
                rlDeleteAllSongToPlaylist.setVisibility(View.GONE);
                rlDeleteDownLoadSong.setVisibility(View.GONE);
                break;
            }

            case PLAYLIST_SONG: {
                rlAddSongToPlaylist.setVisibility(View.GONE);
                rlDeleteDownLoadSong.setVisibility(View.GONE);
                break;
            }

            case DOWNLOAD_SONG: {
                rlAddSongToPlaylist.setVisibility(View.GONE);
                rlDeleteSongToPlaylist.setVisibility(View.GONE);
                rlDeleteAllSongToPlaylist.setVisibility(View.GONE);
                break;
            }

            case LIST_SONG: {
                rlPlaySong.setVisibility(View.GONE);
                rlDeleteSongToPlaylist.setVisibility(View.GONE);
                rlDeleteAllSongToPlaylist.setVisibility(View.GONE);
                rlDeleteDownLoadSong.setVisibility(View.GONE);
                break;
            }
            default: {
                break;
            }
        }

        this.scaleAnimation = new ScaleAnimation(context, rlPlaySong);
        this.scaleAnimation.Event_RelativeLayout();
        rlPlaySong.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
            intent.putExtra("SONG", songArrayList.get(position));
            v.getContext().startActivity(intent);
        });

//        this.scaleAnimation = new ScaleAnimation(context, rlAddSongToPlaylist);
//        this.scaleAnimation.Event_RelativeLayout();
//        rlAddSongToPlaylist.setOnClickListener(v -> {
//            Open_Insert_Song_Playlist_Dialog(DataLocalManager.getUserID(), songArrayList.get(position).getId());
//        });
//
//        this.scaleAnimation = new ScaleAnimation(context, rlDeleteSongToPlaylist);
//        this.scaleAnimation.Event_RelativeLayout();
//        rlDeleteSongToPlaylist.setOnClickListener(v -> Open_Delete_SongPlaylist_Dialog(ACTION_DELETE_SONG_PLAYLIST, DataLocalManager.getUserID(), playlistID, songArrayList.get(position).getId(), position));
//
//        this.scaleAnimation = new ScaleAnimation(context, rlDeleteAllSongToPlaylist);
//        this.scaleAnimation.Event_RelativeLayout();
//        rlDeleteAllSongToPlaylist.setOnClickListener(v -> Open_Delete_SongPlaylist_Dialog(ACTION_DELETEALL_SONG_PLAYLIST, DataLocalManager.getUserID(), playlistID, songArrayList.get(position).getId(), position));
//
//        this.scaleAnimation = new ScaleAnimation(context, rlDownLoadSong);
//        this.scaleAnimation.Event_RelativeLayout();
//        rlDownLoadSong.setOnClickListener(v -> {
//            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//            DownloadService downloadService = new DownloadService();
//            downloadService.DownloadSong(context, songArrayList.get(position));
//            dialog_1.dismiss();
//            notifyDataSetChanged();
//        });
//
//        this.scaleAnimation = new ScaleAnimation(context, rlDeleteDownLoadSong);
//        this.scaleAnimation.Event_RelativeLayout();
//        rlDeleteDownLoadSong.setOnClickListener(v -> {
//            Open_Delete_SongDownLoaded_Dialog(Gravity.CENTER, position);
//            dialog_1.dismiss();
//        });

        this.scaleAnimation = new ScaleAnimation(context, rlCloseInfoPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlCloseInfoPlaylist.setOnClickListener(v -> dialog_1.dismiss());

        this.dialog_1.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handle_Favourite_Icon_Color(ImageView imageView, int position){
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                favoriteSongArrayList = new ArrayList<>();
                favoriteSongArrayList = (ArrayList<Song>) response.body();


                if(favoriteSongArrayList != null && favoriteSongArrayList.size() > 0){
                    for(int i = 0; i < favoriteSongArrayList.size();i++ ){
                        if(songArrayList.get(position).getId() == favoriteSongArrayList.get(i).getId()){
                            imageView.setImageResource(R.drawable.ic_favorite);

                            Log.d(TAG, "Bài hát yêu thích: " + favoriteSongArrayList.get(i).getName());
                        }
                    }
                }
                imageView.setEnabled(true);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                imageView.setEnabled(true);
                Log.d(TAG, "Handle_Favourite_Icon_Color(Error): " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {

        return songArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        ImageView ivItemSong;
        ImageView ivItemSongLove;
        ImageView ivItemSongMore;
        TextView tvItemSongName;
        TextView tvItemSongSinger;

        private ScaleAnimation scaleAnimation;


        public ViewHolder(@NonNull View itemView) {


            super(itemView);
            ivItemSong = itemView.findViewById(R.id.ivItemSong);
            ivItemSongLove = itemView.findViewById(R.id.ivItemSongLove);
            if (layout.equals(FAVORITE_SONG)) {
                this.ivItemSongLove.setImageResource(R.drawable.ic_favorite);
            }
            if (layout.equals(DOWNLOAD_SONG)) {
                this.ivItemSongLove.setVisibility(View.GONE);
            }

            this.scaleAnimation = new ScaleAnimation(itemView.getContext(), this.ivItemSongLove);
            this.scaleAnimation.Event_ImageView();

            this.ivItemSongMore = itemView.findViewById(R.id.ivItemSongMore);
            this.scaleAnimation = new ScaleAnimation(itemView.getContext(), this.ivItemSongMore);
            this.scaleAnimation.Event_ImageView();

            this.tvItemSongName = itemView.findViewById(R.id.tvItemSongName);
            this.tvItemSongName.setSelected(true); // Text will be moved

            this.tvItemSongSinger = itemView.findViewById(R.id.tvItemSongSinger);
            this.tvItemSongSinger.setSelected(true); // Text will be moved
        }

    }

}
