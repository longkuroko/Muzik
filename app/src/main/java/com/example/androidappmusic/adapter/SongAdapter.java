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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.facebook.shimmer.ShimmerFrameLayout;
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
    private UserPlaylistAdapter userPlaylistAdapter;
    private ArrayList<UserPlaylist> userPlaylistArrayList;

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

    private final String ACTION_DELETE_SONG_PLAYLIST = "delete";
    private final String ACTION_DELETEALL_SONG_PLAYLIST = "deleteall";

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
        Handle_Favourite_Icon_Color(holder.ivItemSongLove, position);


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

        holder.ivItemSongLove.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder_1 = new AlertDialog.Builder(v.getContext());
            View view_1 = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_loading_dialog, null);
            alertBuilder_1.setView(view_1);
            alertBuilder_1.setCancelable(false);
            this.alertDialog = alertBuilder_1.create();
            this.alertDialog.show();

            Handle_Add_Delete_Favorite_Song(holder.ivItemSongLove, holder.getLayoutPosition());
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

        RelativeLayout rlDownLoadSong = this.dialog_1.findViewById(R.id.rlDownLoadSong);
        TextView tvDownLoadSong = this.dialog_1.findViewById(R.id.tvDownLoadSong);
        tvDownLoadSong.setSelected(true);

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

        this.scaleAnimation = new ScaleAnimation(context, rlAddSongToPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlAddSongToPlaylist.setOnClickListener(v -> {
            Open_Insert_Song_Playlist_Dialog(DataLocalManager.getUserID(), songArrayList.get(position).getId());
        });

        this.scaleAnimation = new ScaleAnimation(context, rlDeleteSongToPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeleteSongToPlaylist.setOnClickListener(v -> Open_Delete_SongPlaylist_Dialog(ACTION_DELETE_SONG_PLAYLIST, DataLocalManager.getUserID(), playlistID, songArrayList.get(position).getId(), position));

        this.scaleAnimation = new ScaleAnimation(context, rlDeleteAllSongToPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeleteAllSongToPlaylist.setOnClickListener(v -> Open_Delete_SongPlaylist_Dialog(ACTION_DELETEALL_SONG_PLAYLIST, DataLocalManager.getUserID(), playlistID, songArrayList.get(position).getId(), position));

//
//
//

        this.scaleAnimation = new ScaleAnimation(context, rlCloseInfoPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlCloseInfoPlaylist.setOnClickListener(v -> dialog_1.dismiss());

        this.dialog_1.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Open_Insert_Song_Playlist_Dialog(String userID, int songID){
        this.dialog_3 = new Dialog(this.context);

        dialog_3.requestWindowFeature(Window.FEATURE_NO_TITLE); // sự kiên để set full màn hình
        dialog_3.setContentView(R.layout.layout_add_song_to_playlist_dialog);

        Window window = (Window) dialog_3.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // chỉnh độ trong suốt của dialog

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_3.setCancelable(true);// bấm ra chỗ khác sẽ đóng dialog

        TextView tvSelectPlaylist = dialog_3.findViewById(R.id.tvSelectPlaylist);
        tvSelectPlaylist.setSelected(true);
        ShimmerFrameLayout sflItemUserPlaylist = dialog_3.findViewById(R.id.sflItemUserPlaylist);
        TextView tvEmptyPlaylist = dialog_3.findViewById(R.id.tvEmptyPlaylist);
        tvEmptyPlaylist.setSelected(true);
        RecyclerView rvYourPlaylist = dialog_3.findViewById(R.id.rvYourPlaylist);

        // khởi tạo phương thức lấy dữ liệu show lên
        DataService dataService = APIService.getService();
        Call<List<UserPlaylist>> callBack = dataService.getUserPlaylist(userID);
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>(); // mãng rộng
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null && userPlaylistArrayList.size() > 0) {
                    rvYourPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvYourPlaylist.setLayoutManager(layoutManager);

                    userPlaylistAdapter = new UserPlaylistAdapter(context, userPlaylistArrayList, songID);
                    rvYourPlaylist.setAdapter(userPlaylistAdapter);

                    sflItemUserPlaylist.setVisibility(View.GONE);
                    // Hiện thông tin Playlist
                    rvYourPlaylist.setVisibility(View.VISIBLE);

                    Log.d(TAG, "User Playlist: " + userPlaylistArrayList.get(0).getName());
                }else {
                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvEmptyPlaylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                Log.d(TAG, "(Error): " + t.getMessage());
            }
        });

        dialog_3.show();
    }
    private void Open_Delete_SongPlaylist_Dialog(String action, String userID, int playlistID, int songID, int position) {
        this.dialog_2 = new Dialog(this.context);

        dialog_2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_2.setContentView(R.layout.layout_textview_dialog);
        Window window = (Window) dialog_2.getWindow();

        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_2.setCancelable(true);

        // so sánh các thuộc tính đó có tồn tại hay không nếu có sẽ xóa
        TextView tvDialogTitle = dialog_2.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setSelected(true); // Văn bản sẽ bị xóa
        TextView tvDialogContent = dialog_2.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog_2.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog_2.findViewById(R.id.btnDialogAction);

        // Xét xem người dùng nhấn chức năng xóa hay xóa toàn bộ thì setText tương ứng
        if (action.equals(ACTION_DELETE_SONG_PLAYLIST)) {
            tvDialogTitle.setText("Xóa nhạc");
            tvDialogContent.setText("Bạn có muốn xóa bài này không nè?");
            btnDialogCancel.setText("Hủy");
            btnDialogAction.setText("Xóa");
        } else if (action.equals(ACTION_DELETEALL_SONG_PLAYLIST)) {
            tvDialogTitle.setText("Xóa hết bài luôn á");
            tvDialogContent.setText("Xóa không còn gì luôn á nha");
            btnDialogCancel.setText("Hủy");
            btnDialogAction.setText("Xóa thật đó");
        }

        this.scaleAnimation = new ScaleAnimation(context, btnDialogCancel);
        this.scaleAnimation.Event_Button();
        btnDialogCancel.setOnClickListener(v -> {
            dialog_2.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(context, btnDialogAction);
        this.scaleAnimation.Event_Button();

        btnDialogAction.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
            alertBuilder.setView(view);
            alertBuilder.setCancelable(false);
            this.alertDialog = alertBuilder.create();
            this.alertDialog.show();

            Handle_Add_Delete_DeleteAll_Song_Playlist(action, userID, playlistID, songID, position);
        });

        dialog_2.show(); // show cái dialog thông báo
    }
    private void Handle_Add_Delete_DeleteAll_Song_Playlist(String action, String userID, int playlistID, int songID, int position) {
        DataService dataService = APIService.getService();
        Call<List<Status>> callBack = dataService.addDeleteUserPlayListSong(action, userID, playlistID, songID);
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();
                if (statusArrayList != null) {
                    if (action.equals(ACTION_DELETE_SONG_PLAYLIST)) {
                        if (statusArrayList.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            songArrayList.remove(position);
                            notifyItemRemoved(position);
//                            notifyDataSetChanged();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast21, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast22, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    } else if (action.equals(ACTION_DELETEALL_SONG_PLAYLIST)) {
                        if (statusArrayList.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            songArrayList.clear();
                            notifyDataSetChanged();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast21, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast22, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                alertDialog.dismiss();

                dialog_2.dismiss();
                dialog_1.dismiss();
                Log.d(TAG, "Handle_Add_Delete_DeleteAll_Song_Playlist(Error): " + t.getMessage());
            }
        });
    }
    private void Handle_Favourite_Icon_Color(ImageView imageView, int position) {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                favoriteSongArrayList = new ArrayList<>();
                favoriteSongArrayList = (ArrayList<Song>) response.body();

                if (favoriteSongArrayList != null && favoriteSongArrayList.size() > 0) {
                    for (int i = 0; i < favoriteSongArrayList.size(); i++) {
                        if (songArrayList.get(position).getId() == favoriteSongArrayList.get(i).getId()) {
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

    private void Handle_Add_Delete_Favorite_Song(ImageView imageView, int position) {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Status>> callBack = dataService.addDeleteFavoriteSong(DataLocalManager.getUserID(), songArrayList.get(position).getId());
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();

                if (statusArrayList != null) {
                    if (statusArrayList.get(0).getStatus() == 1) {
                        alertDialog.dismiss();
                        imageView.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(context, "Added \"" + songArrayList.get(position).getName() + "\" in favorite songs", Toast.LENGTH_SHORT).show();
                    } else if (statusArrayList.get(0).getStatus() == 2) {
                        alertDialog.dismiss();
                        Toast.makeText(context, "Added \"" + songArrayList.get(position).getName() + "\" fail", Toast.LENGTH_SHORT).show();
                    } else if (statusArrayList.get(0).getStatus() == 3) {
                        alertDialog.dismiss();
                        Toast.makeText(context, "Removed \"" + songArrayList.get(position).getName() + "\" from favorite songs", Toast.LENGTH_SHORT).show();
                        if (layout.equals(FAVORITE_SONG)) {
                            songArrayList.remove(position);
                            notifyItemRemoved(position);
//                            notifyDataSetChanged();
                        } else {
                            imageView.setImageResource(R.drawable.ic_not_favorite);
                        }
                    } else if (statusArrayList.get(0).getStatus() == 4) {
                        alertDialog.dismiss();
                        Toast.makeText(context, "Removed \"" + songArrayList.get(position).getName() + "\" fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                alertDialog.dismiss();
                Log.d(TAG, "Handle_Add_Delete_Favorite_Song(Error)" + t.getMessage());
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

            this.ivItemSong = itemView.findViewById(R.id.ivItemSong);

            this.ivItemSongLove = itemView.findViewById(R.id.ivItemSongLove);
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
