package com.example.androidappmusic.service;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FullPlayerManagerService {

    private static final String TAG = "FullPlayerManagerService";
    private static FullPlayerManagerService instance;

    public static boolean repeat;
    public static boolean checkRandom;
    public static MediaPlayer mediaPlayer;
    public static boolean isRegister = false;
    public static Song currentSong;
    public static List<Song> listCurrentSong;
    public static int position = 0;




    public static class PlayMP3 extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            return strings[0];
        }

        @Override
        protected void onPostExecute(String song) {
            super.onPostExecute(song);

            try{

                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });
                currentSong = FullPlayerActivity.dataSongs.get(position);
                mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                mediaPlayer.prepare();
                mediaPlayer.start();
                listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongs);
            }
            catch (IOException e){
                e.printStackTrace();
            }


        }
    }
    public static void CreateNotification(String action, Activity activity) {
        Intent intent = new Intent(activity, MiniPlayerOnLockScreenService.class);
        intent.setAction(action);
        activity.startService(intent);

    }


}
