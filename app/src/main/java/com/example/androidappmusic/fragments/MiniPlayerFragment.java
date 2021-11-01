package com.example.androidappmusic.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.service.FullPlayerManagerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class MiniPlayerFragment extends Fragment {

    private ImageView ivListItemSong;
    private ImageView ivPlay;
    private ImageView ivListItemSongLove;
    private ImageView ivListItemSongNext;
    private TextView tvListItemSongName;
    private TextView tvListItemSongSinger;
    private MediaPlayer mediaPlayer = FullPlayerManagerService.mediaPlayer;
    private View miniplayer;
    private int position = FullPlayerManagerService.position;
    public static boolean repeat = false;
    public static boolean checkRandom = false;
    public static final String MINI_PLAYER_CLICK = "MINI_PLAYER_CLICK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mini_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Mapping(view);
            Event();



//            if (FullPlayerManagerService.mediaPlayer != null) {
//                if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
//                } else {
//
//                }
//            } else {
//
//            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!FullPlayerManagerService.isRegister) {
            FullPlayerManagerService.isRegister = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {

            FullPlayerManagerService.isRegister = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    private void Mapping(View view) {
        ivListItemSong = view.findViewById(R.id.ivListItemSong);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivListItemSongLove = view.findViewById(R.id.ivListItemSongLove);
        ivListItemSongNext = view.findViewById(R.id.ivListItemSongNext);
        tvListItemSongName = view.findViewById(R.id.tvListItemSongName);
        tvListItemSongSinger = view.findViewById(R.id.tvListItemSongSinger);
        miniplayer = view.findViewById(R.id.miniPlayer);
        mediaPlayer = FullPlayerManagerService.mediaPlayer;

        if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
            this.ivPlay.setImageResource(R.drawable.ic_pause);
        } else {
            this.ivPlay.setImageResource(R.drawable.ic_play_2);
        }
    }

    private void Event() {
        Picasso.get()
                .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(this.ivListItemSong);
        this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName().trim());
        this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger().trim());

        this.ivPlay.setOnClickListener(v -> {
            onSongPlay();
        });
        this.ivListItemSongNext.setOnClickListener(v -> {
            onSongNext();
        });

        miniplayer.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), FullPlayerActivity.class);
            intent.putExtra("MINI_PLAYER_CLICK", MINI_PLAYER_CLICK);
            startActivity(intent);
        });
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
/*                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });
                mediaPlayer.setDataSource(song);
                mediaPlayer.prepare(); */


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
                //FullPlayerManagerService.position = position;
                Log.d("CurrentSong", FullPlayerManagerService.currentSong.getName());
                FullPlayerManagerService.mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                FullPlayerManagerService.mediaPlayer.prepare();
                FullPlayerManagerService.mediaPlayer.start();
                FullPlayerManagerService.listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongs);

                //FullPlayerManagerService.mediaPlayer = mediaPlayer;
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //mediaPlayer.start();
            //FullPlayerManagerService.mediaPlayer.start();
        }
    }


    private void onSongPlay() {
        if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
            FullPlayerManagerService.mediaPlayer.pause();
            this.ivPlay.setImageResource(R.drawable.ic_play_2);

        } else {
            FullPlayerManagerService.mediaPlayer.start();
            this.ivPlay.setImageResource(R.drawable.ic_pause);

        }
    }

    public void onSongNext() {
        this.ivListItemSongNext.setClickable(false);
        miniplayer.setClickable(false);
        new Handler().postDelayed(() -> {
            this.ivListItemSongNext.setClickable(true);
            miniplayer.setClickable(true);
        }, 2000);
        if (FullPlayerManagerService.listCurrentSong.size() > 0) {
            try {
                if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release(); // Đồng bộ
                    mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (FullPlayerManagerService.position < FullPlayerManagerService.listCurrentSong.size()) {
                ivPlay.setImageResource(R.drawable.ic_pause);
                FullPlayerManagerService.position++;
            }
            if (FullPlayerManagerService.position > FullPlayerManagerService.listCurrentSong.size() - 1) {
                FullPlayerManagerService.position = 0;
            }
            new PlayMP3().execute(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getLink());
            Picasso.get()
                    .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivListItemSong);
            this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName().trim());
            this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger().trim());
        }

    }

    public void onSongPrev() {
        this.ivListItemSongNext.setClickable(false);
        miniplayer.setClickable(false);
        new Handler().postDelayed(() -> {
            this.ivListItemSongNext.setClickable(true);
            miniplayer.setClickable(true);
        }, 2000);
        if (FullPlayerManagerService.listCurrentSong.size() > 0) {
            try {
                if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release(); // Đồng bộ
                    mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (FullPlayerManagerService.position < FullPlayerManagerService.listCurrentSong.size()) {
                ivPlay.setImageResource(R.drawable.ic_pause);
                FullPlayerManagerService.position--;
            }
            if (FullPlayerManagerService.position < 0) {
                FullPlayerManagerService.position = FullPlayerManagerService.listCurrentSong.size() - 1;
            }
            new PlayMP3().execute(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getLink());
            Picasso.get()
                    .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivListItemSong);
            this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName());
            this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger());
        }
    }

}