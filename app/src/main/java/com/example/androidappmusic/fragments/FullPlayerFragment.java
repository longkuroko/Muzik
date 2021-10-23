package com.example.androidappmusic.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.models.Status;
import com.example.androidappmusic.service.FullPlayerManagerService;
import com.example.androidappmusic.service.MiniPlayerOnLockScreenService;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class FullPlayerFragment extends Fragment {

    private static final String TAG = "FullPlayerFragment";

    private MediaPlayer mediaPlayer;

    private ImageView ivCover;
    private ImageView ivFavorite;
    private ImageView ivComment;
    private ImageView ivDownload;
    private ImageView ivMv;
    private ImageView ivShuffle;
    private ImageView ivPrev;
    private ImageView ivPlayPause;
    private ImageView ivNext;
    private ImageView ivRepeat;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private SeekBar sbSong;


    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;
    private Dialog dialog;
    private int position = 0;
    private boolean repeat = false;
    private boolean checkRandom = false;
    private boolean next = false;

    private ArrayList<Status> statusArrayList;

    private boolean isEvent_Of_FullPlayerFragment = false;
    private boolean isRegister = false;
    private ISendPositionListener iSendPositionListener;

    public interface ISendPositionListener{
        void Send_Position(int index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

        if (context instanceof ISendPositionListener) {
            iSendPositionListener = (ISendPositionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "You need implement");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataLocalManager.init(getContext());

        linkViews(view);
        addEvents();
        if (!FullPlayerManagerService.isRegister) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACkS"));
            FullPlayerManagerService.isRegister = true;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
            FullPlayerManagerService.isRegister = true;
        } catch (Exception e) {
            Log.e(TAG, "unregisterReceiver");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    private void linkViews(View view) {

        loadingDialog = new LoadingDialog(getActivity());

        this.ivCover = view.findViewById(R.id.ivCover);
//        this.ivDownload = view.findViewById(R.id.ivDownload);
//        this.ivComment = view.findViewById(R.id.ivComment);
        this.ivMv = view.findViewById(R.id.ivMv);
        this.ivShuffle = view.findViewById(R.id.ivShuffle);
        this.ivPrev = view.findViewById(R.id.ivPrev);
        this.ivPlayPause = view.findViewById(R.id.ivPlayPause);
        this.ivNext = view.findViewById(R.id.ivNext);
        this.ivRepeat = view.findViewById(R.id.ivRepeat);
        this.tvTimeStart = view.findViewById(R.id.tvTimeStart);
        this.tvTimeEnd = view.findViewById(R.id.tvTimeEnd);
        this.sbSong = view.findViewById(R.id.sbSong);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if(FullPlayerManagerService.listCurrentSong != null && FullPlayerActivity.dataSongs.size() <=0){
            FullPlayerActivity.dataSongs = new ArrayList<>(FullPlayerManagerService.listCurrentSong);
            position = FullPlayerManagerService.position;

        }
        else{
            FullPlayerManagerService.position = 0;
        }

        if(FullPlayerActivity.dataSongs.size() > 0){
//            try {
//                //FullPlayerManagerService.mediaPlayer.stop();
//                if (DownloadService.isSongDownloaded(FullPlayerActivity.dataSongArrayList.get(position).getId())) {
//                    Song songDownload = DownloadService.GetDownloadSongById(FullPlayerActivity.dataSongArrayList.get(position).getId());
//                    new PlayMP3().execute(songDownload.getLink());
//                    FullPlayerManagerService.currentSong = FullPlayerActivity.dataSongArrayList.get(position);
//                } else {
//                    new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(position).getLink());
//                }
//            } catch (Exception e) {
//                new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(position).getLink());
//            }




            FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongs.get(position).getName().trim());
            FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongs.get(position).getSinger().trim());


            if (FullPlayerManagerService.mediaPlayer != null && isCurrentSong()) {
                if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
                    this.ivPlayPause.setImageResource(R.drawable.ic_pause);
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                } else {
                    this.ivPlayPause.setImageResource(R.drawable.ic_play_2);
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
                }
            } else {
                this.ivPlayPause.setImageResource(R.drawable.ic_pause);
                CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FullPlayerActivity.dataSongs.size() > 0){
                        Picasso.get()
                                .load(FullPlayerActivity.dataSongs.get(position).getImg())
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo)
                                .into(ivCover);
                    }
                    else{
                        handler.postDelayed(this, 1000);
                    }
                }
            }, 1000);
        }

    }
    private void addEvents() {
        scaleAnimation = new ScaleAnimation(getActivity(), ivDownload);
        scaleAnimation.Event_ImageView();

        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivMv);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivShuffle);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPrev);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPlayPause);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivNext);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivRepeat);
        this.scaleAnimation.Event_ImageView();

        ivPlayPause.setOnClickListener(v->{
            onPlaySong();
        });

        ivRepeat.setOnClickListener(v -> {
            isEvent_Of_FullPlayerFragment = true;
            if (!repeat) {
                if (checkRandom) {
                    checkRandom = false;
                    FullPlayerManagerService.checkRandom = false;
                    this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle);
                    repeat = true;
                    FullPlayerManagerService.repeat = true;
                } else {
                    this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    repeat = true;
                    FullPlayerManagerService.repeat = true;
                }
            } else {
                this.ivRepeat.setImageResource(R.drawable.ic_loop);
                repeat = false;
                FullPlayerManagerService.repeat = false;
            }
        });


        ivShuffle.setOnClickListener(v -> {
            isEvent_Of_FullPlayerFragment = true;
            if (!checkRandom) {
                if (repeat) {
                    repeat = false;
                    FullPlayerManagerService.repeat = false;
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    this.ivRepeat.setImageResource(R.drawable.ic_loop);
                    checkRandom = true;
                    FullPlayerManagerService.checkRandom = true;
                } else {
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    checkRandom = true;
                    FullPlayerManagerService.checkRandom = true;
                }
            } else {
                this.ivShuffle.setImageResource(R.drawable.ic_shuffle);
                checkRandom = false;
                FullPlayerManagerService.checkRandom = false;
            }
        });


        sbSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                FullPlayerManagerService.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        this.ivNext.setOnClickListener(v -> {
            onSongNext();
        });


        this.ivPrev.setOnClickListener(v -> {
            onSongPrev();
        });

//        this.ivDownload.setOnClickListener(v -> {
//            if (FullPlayerActivity.dataSongArrayList.size() > 0) {
//                DownloadService.DownloadSong(getContext(), FullPlayerActivity.dataSongArrayList.get(position));
//            }
//        });
    }

    private void CreateNotification(String action) {
        Intent intent = new Intent(getContext(), MiniPlayerOnLockScreenService.class);
        intent.setAction(action);
        getActivity().startService(intent);

    }

    private void onPlaySong(){
        isEvent_Of_FullPlayerFragment = true;
        if(FullPlayerManagerService.mediaPlayer.isPlaying()){
            FullPlayerManagerService.mediaPlayer.pause();
            ivPlayPause.setImageResource(R.drawable.ic_play_2);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
        }
        else{
            FullPlayerManagerService.mediaPlayer.start();
            ivPlayPause.setImageResource(R.drawable.ic_pause);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
        }
    }
    public void onSongNext() {
        isEvent_Of_FullPlayerFragment = true;
        if (FullPlayerActivity.dataSongs.size() > 0) {
            if (FullPlayerManagerService.mediaPlayer.isPlaying() || FullPlayerManagerService.mediaPlayer != null) {
                FullPlayerManagerService.mediaPlayer.stop();
                FullPlayerManagerService.mediaPlayer.release(); // Đồng bộ
                FullPlayerManagerService.mediaPlayer = null;
            }
            if (this.position < FullPlayerActivity.dataSongs.size()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause);
                this.position++;

                if (repeat) {
                    if (this.position == 0) {
                        this.position = FullPlayerActivity.dataSongs.size();
                    }
                    this.position -= 1;
                }

                if (checkRandom) {
                    Random random = new Random();
                    int index = random.nextInt(FullPlayerActivity.dataSongs.size());
                    if (index == this.position) {
                        this.position = index - 1;
                    }
                    this.position = index;
                }
                if (this.position > FullPlayerActivity.dataSongs.size() - 1) {
                    this.position = 0;
                }
            }
            new PlayMP3().execute(FullPlayerActivity.dataSongs.get(this.position).getLink());

            Picasso.get()
                    .load(FullPlayerActivity.dataSongs.get(this.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivCover);

            FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongs.get(this.position).getName());
            FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongs.get(this.position).getSinger());
            iSendPositionListener.Send_Position(this.position); // Chú ý
            UpdateTimeSong();
        }
        this.ivNext.setClickable(false);
        this.ivPrev.setClickable(false);

        new Handler().postDelayed(() -> {
            this.ivNext.setClickable(true);
            this.ivPrev.setClickable(true);
        }, 2000);
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }

    public void onSongPrev() {
        isEvent_Of_FullPlayerFragment = true;
        if (FullPlayerActivity.dataSongs.size() > 0) {
            if (FullPlayerManagerService.mediaPlayer.isPlaying() || FullPlayerManagerService.mediaPlayer != null) {
                FullPlayerManagerService.mediaPlayer.stop();
                FullPlayerManagerService.mediaPlayer.release(); // Đồng bộ
                FullPlayerManagerService.mediaPlayer = null;
            }
            if (this.position < FullPlayerActivity.dataSongs.size()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause);
                this.position--;

                if (this.position < 0) {
                    this.position = FullPlayerActivity.dataSongs.size() - 1;
                }

                if (repeat) {
                    this.position += 1;
                }

                if (checkRandom) {
                    Random random = new Random();
                    int index = random.nextInt(FullPlayerActivity.dataSongs.size());
                    if (index == this.position) {
                        this.position = index - 1;
                    }
                    this.position = index;
                }
            }
            new PlayMP3().execute(FullPlayerActivity.dataSongs.get(this.position).getLink());

            Picasso.get()
                    .load(FullPlayerActivity.dataSongs.get(this.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivCover);
            BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongs.get(this.position).getName());
            FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongs.get(this.position).getSinger());
            iSendPositionListener.Send_Position(this.position); // Chú
            UpdateTimeSong();
        }
        this.ivNext.setClickable(false);
        this.ivPrev.setClickable(false);

        new Handler().postDelayed(() -> {
            this.ivNext.setClickable(true);
            this.ivPrev.setClickable(true);
        }, 2000);
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }


    public class PlayMP3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String song) {
            super.onPostExecute(song);
            try {
                if (!isCurrentSong() || isEvent_Of_FullPlayerFragment) {

                    if (FullPlayerManagerService.mediaPlayer != null) {
                        try {
                            FullPlayerManagerService.mediaPlayer.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    FullPlayerManagerService.mediaPlayer = new MediaPlayer();
                    FullPlayerManagerService.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    FullPlayerManagerService.mediaPlayer.setOnCompletionListener(mp -> {
                        FullPlayerManagerService.mediaPlayer.stop();
                        FullPlayerManagerService.mediaPlayer.reset();
                    });
                    FullPlayerManagerService.currentSong = FullPlayerActivity.dataSongs.get(position);
                    FullPlayerManagerService.position = position;
                    //Log.d("CurrentSong",FullPlayerManagerService.currentSong.getName());
                    FullPlayerManagerService.mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                    FullPlayerManagerService.mediaPlayer.prepare();
                    FullPlayerManagerService.mediaPlayer.start();
                    mediaPlayer = FullPlayerManagerService.mediaPlayer;
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    FullPlayerManagerService.listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongs);
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            TimeSong();
            UpdateTimeSong();
        }
    }

    private boolean isCurrentSong() {
        try {
            if (FullPlayerManagerService.listCurrentSong != null) {
                if (FullPlayerManagerService.listCurrentSong.get(position).getId() == FullPlayerActivity.dataSongs
                        .get(position).getId()) {
                    repeat = FullPlayerManagerService.checkRandom;
                    if (repeat) {
                        this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    }
                    if (checkRandom) {
                        this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    }

                    return true;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        FullPlayerManagerService.repeat = false;
        FullPlayerManagerService.checkRandom = false;
        return false;
    }

    private void TimeSong() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

        this.tvTimeEnd.setText(simpleDateFormat.format(FullPlayerManagerService.mediaPlayer.getDuration()));
        this.sbSong.setMax(FullPlayerManagerService.mediaPlayer.getDuration());

    }
    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FullPlayerManagerService.mediaPlayer != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
                    try {
                        int time = FullPlayerManagerService.mediaPlayer.getCurrentPosition();
                        Log.d("Test", String.valueOf(time));
                        tvTimeStart.setText(simpleDateFormat.format(FullPlayerManagerService.mediaPlayer.getCurrentPosition()));
                        sbSong.setProgress(FullPlayerManagerService.mediaPlayer.getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this, 1000); // Gọi lại ham này thực thi 1s mỗi lần

                    FullPlayerManagerService.mediaPlayer.setOnCompletionListener(mp -> {
                        next = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }, 1000);

        Handler handler_1 = new Handler(); // Lằng nghe khi chuyển bài hát
        handler_1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (next) {
                    if (position < FullPlayerActivity.dataSongs.size()) {
                        ivPlayPause.setImageResource(R.drawable.ic_pause);
                        position++;

                        if (repeat) {
                            if (position == 0) {
                                position = FullPlayerActivity.dataSongs.size();
                            }
                            position -= 1;
                        }

                        if (checkRandom) {
                            Random random = new Random();
                            int index = random.nextInt(FullPlayerActivity.dataSongs.size());
                            if (index == position) {
                                position = index - 1;
                            }
                            position = index;
                        }
                        if (position > FullPlayerActivity.dataSongs.size() - 1) {
                            position = 0;
                        }
                    }
                    new PlayMP3().execute(FullPlayerActivity.dataSongs.get(position).getLink());

                    Picasso.get()
                            .load(FullPlayerActivity.dataSongs.get(position).getImg())
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(ivCover);

                    FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongs.get(position).getName());
                    FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongs.get(position).getSinger());
                    iSendPositionListener.Send_Position(position); // Chú ý
                    ivNext.setClickable(false);
                    ivPrev.setClickable(false);

                    new Handler().postDelayed(() -> { // Sau khi người dùng nhấn Next hoặc Prev thì cho dừng khoảng 2s sau mới cho tác động lại nút
                        ivNext.setClickable(true);
                        ivPrev.setClickable(true);
                    }, 2000);

                    next = false;
                    handler_1.removeCallbacks(this); // Lưu ý khúc này
                } else {
                    handler_1.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case MiniPlayerOnLockScreenService.ACTION_PLAY:
                    onPlaySong();
                    break;
                case MiniPlayerOnLockScreenService.ACTION_PAUSE:
                    onPlaySong();
                    break;
                case MiniPlayerOnLockScreenService.ACTION_PRE:
                    try {
                        onSongPrev();
                        //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MiniPlayerOnLockScreenService.ACTION_NEXT:

                    try {
                        onSongNext();
                        //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

}