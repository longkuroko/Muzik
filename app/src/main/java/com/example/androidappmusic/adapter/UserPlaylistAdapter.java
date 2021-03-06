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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.AddUpdateActivity;
import com.example.androidappmusic.activity.PersonalPlaylistActivity;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Status;
import com.example.androidappmusic.models.UserPlaylist;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private final String ACTION_DELETE_PLAYLIST = "delete";
    private final String ACTION_DELETEALL_PLAYLIST = "deleteall";
    private final String ACTION_INSERT_SONG_PLAYLIST = "insert";

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
    public void onBindViewHolder(@NonNull UserPlaylistAdapter.ViewHolder holder, int position) {
        DataLocalManager.init(context);

        holder.tvPlaylistName.setText(this.userPlaylistArrayList.get(position).getName().trim());
//        holder.tvNumberSongPlaylist.setText(String.valueOf(this.userPlaylistArrayList.get(position).getTotalSong()));
        holder.ivPlaylistMore.setOnClickListener(v -> Open_Info_Playlist_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));

        if (this.songID > -1) {
            holder.itemView.setOnClickListener(v -> {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog,null);
                alertBuilder.setView(view);
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();

                Handle_Add_Song_Playlist(ACTION_INSERT_SONG_PLAYLIST, DataLocalManager.getUserID(), this.userPlaylistArrayList.get(holder.getLayoutPosition()).getYouID(), songID, holder.getLayoutPosition());
            });
        }else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PersonalPlaylistActivity.class);
                intent.putExtra("SONGPLAYLIST", this.userPlaylistArrayList.get(holder.getLayoutPosition()));
                context.startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                Open_Info_Playlist_Dialog(Gravity.BOTTOM, holder.getLayoutPosition());
                return false;
            });
        }

    }

    private void Open_Info_Playlist_Dialog(int gravity, int position) {
        this.dialog_1 = new Dialog(this.context);

        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_1.setContentView(R.layout.layout_userplaylist_more);

        Window window = dialog_1.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set m??u m??? m??? cho background dialog, che ??i activity ch??nh, nh??ng v???n c?? th??? th???y ???????c m???t ph???n activity

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_1.setCancelable(true); // B???m ra ch??? kh??c s??? tho??t dialog

        TextView tvInfoPlaylistName = dialog_1.findViewById(R.id.tvInfoPlaylistName);
        tvInfoPlaylistName.setSelected(true); // Text will be moved
        tvInfoPlaylistName.setText(String.valueOf(userPlaylistArrayList.get(position).getName()));

//        RelativeLayout rlAddPlaylist = dialog.findViewById(R.id.rlAddPlaylist);
        RelativeLayout rlEditPlaylist = dialog_1.findViewById(R.id.rlEditPlaylist);
        TextView tvEditPlaylist = dialog_1.findViewById(R.id.tvEditPlaylist);
        tvEditPlaylist.setSelected(true);

        RelativeLayout rlDeletePlaylist = dialog_1.findViewById(R.id.rlDeletePlaylist);
        TextView tvDeletePlaylist = dialog_1.findViewById(R.id.tvDeletePlaylist);
        tvDeletePlaylist.setSelected(true);

        RelativeLayout rlDeleteAllPlaylist = dialog_1.findViewById(R.id.rlDeleteAllPlaylist);
        TextView tvDeleteAllPlaylist = dialog_1.findViewById(R.id.tvDeleteAllPlaylist);
        tvDeleteAllPlaylist.setSelected(true);

        RelativeLayout rlCloseInfoPlaylist = dialog_1.findViewById(R.id.rlCloseInfoPlaylist);
        TextView tvCloseInfoPlaylist = dialog_1.findViewById(R.id.tvCloseInfoPlaylist);
        tvCloseInfoPlaylist.setSelected(true);

//        this.scaleAnimation = new ScaleAnimation(context, rlAddPlaylist);
//        this.scaleAnimation.Event_RelativeLayout();

        if (songID > -1) {
            rlEditPlaylist.setVisibility(View.GONE);
        }

        this.scaleAnimation = new ScaleAnimation(context, rlEditPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlEditPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddUpdateActivity.class);
            intent.putExtra("UPDATEPLAYLIST", this.userPlaylistArrayList.get(position));
            context.startActivity(intent);

            dialog_1.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(context, rlDeletePlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeletePlaylist.setOnClickListener(v -> { // X??a m???t playlist
            Open_Delete_Playlist_Dialog(ACTION_DELETE_PLAYLIST, userPlaylistArrayList.get(position).getYouID(), DataLocalManager.getUserID(), userPlaylistArrayList.get(position).getName(), position);
        });

        this.scaleAnimation = new ScaleAnimation(context, rlDeleteAllPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeleteAllPlaylist.setOnClickListener(v -> { // X??a to??n b??? playlist
            Open_Delete_Playlist_Dialog(ACTION_DELETEALL_PLAYLIST, userPlaylistArrayList.get(position).getYouID(), DataLocalManager.getUserID(), userPlaylistArrayList.get(position).getName(), position);
        });

        this.scaleAnimation = new ScaleAnimation(context, rlCloseInfoPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlCloseInfoPlaylist.setOnClickListener(v -> {
            dialog_1.dismiss();
        });

        dialog_1.show(); // c??u l???nh n??y s??? hi???n th??? Dialog l??n
    }


    private void Open_Delete_Playlist_Dialog(String action, int playlistID, String userID, String playlistName, int position) {
        this.dialog_2 = new Dialog(this.context);

        dialog_2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_2.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog_2.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set m??u m??? m??? cho background dialog, che ??i activity ch??nh, nh??ng v???n c?? th??? th???y ???????c m???t ph???n

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_2.setCancelable(true); // B???m ra ch??? kh??c s??? tho??t dialog

        // so s??nh ?????i chi???u c??c gi?? tr??? cps tr??ng vs nhau kh??ng n???u tr??ng th?? x??a
        TextView tvDialogTitle = dialog_2.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setSelected(true);
        TextView tvDialogContent = dialog_2.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog_2.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog_2.findViewById(R.id.btnDialogAction);

        // X??t xem ng?????i d??ng nh???n ch???c n??ng x??a hay x??a to??n b??? th?? setText t????ng ???ng
        if (action.equals(ACTION_DELETE_PLAYLIST)) {
            tvDialogTitle.setText("X??a playlist");
            tvDialogContent.setText("b???n c?? ch???c l?? mu???n x??a ch??a?");
            btnDialogCancel.setText("H???y");
            btnDialogAction.setText("X??a");
        } else if (action.equals(ACTION_DELETEALL_PLAYLIST)) {
            tvDialogTitle.setText("X??a t???t c??? ???? nghe ch??a?");
            tvDialogContent.setText("Suy ngh?? k?? ch??a x??a t???t c??? ?????");
            btnDialogCancel.setText("H???y");
            btnDialogAction.setText("X??a");
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

            Handle_Add_Update_Delete_DeleteAll_UserPlaylist(action, playlistID, userID, playlistName, position);
        });

        dialog_2.show(); // c??u l???nh n??y s??? hi???n th??? Dialog l??n
    }


    private void Handle_Add_Update_Delete_DeleteAll_UserPlaylist(String action, int playlistID, String userID, String playlistName, int position) {
        DataService dataService = APIService.getService();
        Call<List<UserPlaylist>> callBack = dataService.addUpdateDeleteUserPlaylist(action, playlistID, userID, playlistName);
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayLists = new ArrayList<>();
                userPlaylistArrayLists = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayLists != null) {
                    if (action.equals(ACTION_DELETE_PLAYLIST)) { // X??a m???t playlist
                        if (userPlaylistArrayLists.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.remove(position);
                            notifyItemRemoved(position);
//                            notifyDataSetChanged();

                            if (tvNumberPlaylist != null) {
                                tvNumberPlaylist.setText(String.valueOf(userPlaylistArrayList.size()));
                            }
                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast19, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayLists.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast20, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    } else if (action.equals(ACTION_DELETEALL_PLAYLIST)) { // X??a to??n b??? playlist
                        if (userPlaylistArrayLists.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.clear();
                            notifyDataSetChanged();
                            if (tvNumberPlaylist != null) {
                                tvNumberPlaylist.setText(String.valueOf(userPlaylistArrayList.size()));
                            }
                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast19, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayLists.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast20, Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                alertDialog.dismiss();

                dialog_2.dismiss();
                dialog_1.dismiss();
                Log.d(TAG, "Handle_Add_Update_Delete_DeleteAll_UserPlaylist(Error): " + t.getMessage());
            }
        });
    }


    private void Handle_Add_Song_Playlist (String action, String userID, int playlistID, int songID, int position) {
        DataService dataService = APIService.getService();
        Call<List<Status>> callBack = dataService.addDeleteUserPlayListSong(action, userID, playlistID, songID);
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();

                if (statusArrayList != null) {
                    if (action.equals(ACTION_INSERT_SONG_PLAYLIST)) {
                        if (statusArrayList.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.remove(position);
                            notifyItemRemoved(position);

                            Toast.makeText(context, R.string.toast23, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 2) {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast24, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 3) {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast25, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                alertDialog.dismiss();
                Log.d(TAG, "Handle_Add_Song_Playlist(Error): " + t.getMessage());
            }
        });
    }
    @Override
    public int getItemCount() {
        if (this.userPlaylistArrayList != null) {
            return this.userPlaylistArrayList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvPlaylistCover;
        TextView tvPlaylistName;
        TextView tvNumberSongPlaylist;
        ImageView ivPlaylistMore;

        private ScaleAnimation scaleAnimation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cvPlaylistCover = itemView.findViewById(R.id.cvPlaylistCover);

            this.tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            this.tvPlaylistName.setSelected(true);

            this.tvNumberSongPlaylist = itemView.findViewById(R.id.tvNumberSongPlaylist);

            this.ivPlaylistMore = itemView.findViewById(R.id.ivPlaylistMore);
            this.scaleAnimation = new ScaleAnimation(itemView.getContext(), this.ivPlaylistMore);
            this.scaleAnimation.Event_ImageView();
        }
    }
}
