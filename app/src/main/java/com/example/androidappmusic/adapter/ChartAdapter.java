package com.example.androidappmusic.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.models.UserPlaylist;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private static final String TAG = "ChartAdapter";

    private Dialog dialog_1;
    private Dialog dialog_3;

    private ScaleAnimation scaleAnimation;

    int index = 0;

    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private UserPlaylistAdapter userPlaylistAdapter;

    private Context context;
    private ArrayList<Song> songArrayList;

    private boolean isFromRadio = false;
    private List<Integer> listRequest;
    private Dialog dialogRequestFromRadio;
    public static List<Integer> listSongRequested = new ArrayList<>();

    public ChartAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    public ChartAdapter(Context context, ArrayList<Song> songArrayList, boolean o, List<Integer> requests, Dialog dialog) {
        isFromRadio = true;
        this.context = context;
        this.songArrayList = songArrayList;
        listRequest = requests;
        dialogRequestFromRadio = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(this.songArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivSongChartImage);

        Handle_Position(holder.tvChartNumber, position);
        holder.tvChartSongName.setText(this.songArrayList.get(position).getName().trim());
        holder.tvChartSongSinger.setText(this.songArrayList.get(position).getSinger().trim());
        holder.tvChartLikeNumber.setText(this.songArrayList.get(position).getLike().trim());

        holder.ivChartSongMore.setOnClickListener(v -> Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition());
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
            intent.putExtra("SONGCHART", songArrayList.get(holder.getLayoutPosition()));
            v.getContext().startActivity(intent);
        });
        
    }
    private void Open_Info_Song_Dialog(int gravity, int position){
        dialog_1 = new Dialog(this.context);

        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_1.setContentView(R.layout.layout_song_more);

        Window window = dialog_1.getWindow();
        if(window == null){
            return;

        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần activity

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_1.setCancelable(true); //bam ra cho khac de thoat dialog
        ImageView ivInfoSongImage = this.dialog_1.findViewById(R.id.ivInfoSongImage);
        Picasso.get()
                .load(songArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(ivInfoSongImage);

        TextView tvInfoSongName = this.dialog_1.findViewById(R.id.tvInfoSongName);
        tvInfoSongName.setSelected(true);
        tvInfoSongName.setText(songArrayList.get(position).getName().trim());

        TextView tvInfoSongSinger = this.dialog_1.findViewById(R.id.tvInfoSongSinger);
        tvInfoSongSinger.setSelected(true);
        tvInfoSongSinger.setText(this.songArrayList.get(position).getSinger().trim());

        RelativeLayout rlPlaySong = this.dialog_1.findViewById(R.id.rlPlaySong);
        TextView tvPlaySong = this.dialog_1.findViewById(R.id.tvPlaySong);
        tvPlaySong.setSelected(true);
//
//        RelativeLayout rlAddSongToPlaylist = this.dialog_1.findViewById(R.id.rlAddSongToPlaylist);
//        TextView tvAddSongToPlaylist = this.dialog_1.findViewById(R.id.tvAddSongToPlaylist);
//        tvAddSongToPlaylist.setSelected(true);
//
//        RelativeLayout rlDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteSongToPlaylist);
//        TextView tvDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteSongToPlaylist);
//        tvDeleteSongToPlaylist.setSelected(true);
//
//        RelativeLayout rlDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteAllSongToPlaylist);
//        TextView tvDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteAllSongToPlaylist);
//        tvDeleteAllSongToPlaylist.setSelected(true);
//
//        RelativeLayout rlDownLoadSong = this.dialog_1.findViewById(R.id.rlDownLoadSong);
//        TextView tvDownLoadSong = this.dialog_1.findViewById(R.id.tvDownLoadSong);
//        tvDownLoadSong.setSelected(true);
//
//        RelativeLayout rlDeleteDownLoadSong = this.dialog_1.findViewById(R.id.rlDeleteDownLoadSong);
//        TextView tvDeleteDownLoadSong = this.dialog_1.findViewById(R.id.tvDeleteDownLoadSong);
//        tvDeleteDownLoadSong.setSelected(true);
//
//        RelativeLayout rlCloseInfoPlaylist = this.dialog_1.findViewById(R.id.rlCloseInfoPlaylist);
//        TextView tvCloseInfoSong = this.dialog_1.findViewById(R.id.tvCloseInfoSong);
//        tvCloseInfoSong.setSelected(true);

//        rlDeleteSongToPlaylist.setVisibility(View.GONE);
//        rlDeleteAllSongToPlaylist.setVisibility(View.GONE);
//        rlDeleteDownLoadSong.setVisibility(View.GONE);

        scaleAnimation = new ScaleAnimation(context, rlPlaySong);
        scaleAnimation.Event_CircleImageView();
        rlPlaySong.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
            intent.putExtra("SONG", songArrayList.get(position));
            v.getContext().startActivity(intent);
        });

        dialog_1.show();


    }
    private void Handle_Position(TextView tvPosition, int position) {
        tvPosition.setText(String.valueOf(position + 1));

        if(position == 0){
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain4));
        }
        else if (position == 1) {
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain7));
        } else if (position == 2) {
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain8));
        } else { // Ko biết tại sao, khí nào quay lại tìm hiểu sau
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorLight7));
        }

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvChartNumber;
        private final TextView tvChartSongName;
        private final TextView tvChartSongSinger;
        private final TextView tvChartLikeNumber;
        private final ImageView ivSongChartImage;
        private final ImageView ivChartSongMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvChartNumber = itemView.findViewById(R.id.tvChartNumber);
            this.tvChartNumber.setSelected(true);

            this.tvChartSongName = itemView.findViewById(R.id.tvChartSongName);
            this.tvChartSongName.setSelected(true);

            this.tvChartSongSinger = itemView.findViewById(R.id.tvChartSongSinger);
            this.tvChartSongSinger.setSelected(true);

            this.tvChartLikeNumber = itemView.findViewById(R.id.tvChartLikeNumber);
            this.tvChartLikeNumber.setSelected(true);

            this.ivSongChartImage = itemView.findViewById(R.id.ivSongChartImage);

            this.ivChartSongMore = itemView.findViewById(R.id.ivChartSongMore);
        }
    }
}