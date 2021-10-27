package com.example.androidappmusic.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.androidappmusic.adapter.FullPlayerAdapter;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.fragments.DetailPlayerFragment;
import com.example.androidappmusic.fragments.FullPlayerFragment;
import com.example.androidappmusic.fragments.LyricsPlayerFragment;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.service.FullPlayerManagerService;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;


import com.example.androidappmusic.R;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class FullPlayerActivity extends AppCompatActivity implements FullPlayerFragment.ISendPositionListener{

    private static final String TAG = "FullPlayerActivity";

    private ViewPager vpFullPlayer;
    public static FullPlayerAdapter fullPlayerAdapter;
    private PageIndicatorView pageIndicatorView;
//
    private ScaleAnimation scaleAnimation;
//


    private FullPlayerFragment fullPlayerFragment;
    private DetailPlayerFragment detailPlayerFragment;
    private LyricsPlayerFragment lyricsPlayerFragment;

    private Song song;
    private ArrayList<Song> songs = new ArrayList<>();

    public static ArrayList<Song> dataSongs = new ArrayList<>(); // truyền dữ liệu cho các fragment

    private ImageView ivBack;
    public static TextView tvSongName;
    public static TextView tvArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_player);

//        DataLocalManager.init(this);

        linkViews();
        loadData();
        addEvents();
    }

    private void linkViews() {
        vpFullPlayer = findViewById(R.id.vpFullPlayer);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        ivBack = findViewById(R.id.ivBack);

        tvSongName = findViewById(R.id.tvSongName);
        tvSongName.setSelected(true); // Text will be moved
        tvArtist = findViewById(R.id.tvArtist);
        tvArtist.setSelected(true); // Text will be moved

        fullPlayerFragment = new FullPlayerFragment();
        detailPlayerFragment = new DetailPlayerFragment();
        lyricsPlayerFragment = new LyricsPlayerFragment();

        fullPlayerAdapter = new FullPlayerAdapter(getSupportFragmentManager());
        fullPlayerAdapter.AddFragment(this.detailPlayerFragment); // 0
        fullPlayerAdapter.AddFragment(this.fullPlayerFragment); // 1
        fullPlayerAdapter.AddFragment(this.lyricsPlayerFragment); // 2

        vpFullPlayer.setAdapter(fullPlayerAdapter);
        vpFullPlayer.setCurrentItem(1); // Set default Fragment
//        vpFullPlayer.setOffscreenPageLimit(2); // Load trước 2 trang (theo cơ chế hoạt động của ViewPager)
    }

    private void loadData() {
        Intent intent = getIntent();
        dataSongs.clear();// Xóa hết dữ liệu bài hát khi nhận đc một dữ liệu bài hát mới
        if (FullPlayerManagerService.listCurrentSong != null && !intent.hasExtra("MINI_PLAYER_CLICK")) {
            FullPlayerManagerService.listCurrentSong.clear();
        }

        if (intent != null) {
            if (intent.hasExtra("SONG")) { // Khi chọn một bài hát
                this.song = (Song) intent.getParcelableExtra("SONG");
                if (this.song != null) {
                    dataSongs.add(this.song);

                    Log.d(TAG, "Bài hát người dùng chọn: " + this.song.getName());
                }
            } else if (intent.hasExtra("SONGSLIDER")) {
                this.songs = intent.getParcelableArrayListExtra("SONGSLIDER");
                if (this.songs != null) {
                    dataSongs = this.songs;

                    Log.d(TAG, "Bài hát từ Slider: " + this.songs.get(0).getName());
                }
            } else if (intent.hasExtra("SONGCHART")) { // Khi chọn một bài hát từ bảng xếp hạng bài hát
                this.song = (Song) intent.getParcelableExtra("SONGCHART");
                if (this.song != null) {
                    dataSongs.add(this.song);

                    Log.d(TAG, "Bài hát từ bảng xếp hạng: " + this.song.getName());
                }
            } else if (intent.hasExtra("ALLSONGS")) { // Khi chọn nghe tất cả bài hát từ SongActivity
                this.songs = intent.getParcelableArrayListExtra("ALLSONGS");
                if (this.songs != null) {
                    dataSongs = this.songs;

                    for (int i = 0; i < this.songs.size(); i++) {
                        Log.d(TAG, "Bài hát từ SongActivity " + (i + 1) + ": " + this.songs.get(i).getName());
                    }
                }
            } else if (intent.hasExtra("ALLFAVORITESONGS")) { // Khi chọn nghe tất cả bài hát từ PersonalPlaylistActivity
                this.songs = intent.getParcelableArrayListExtra("ALLFAVORITESONGS");
                if (this.songs != null) {
                    dataSongs = this.songs;

                    for (int i = 0; i < this.songs.size(); i++) {
                        Log.d(TAG, "Bài hát yêu thích " + (i + 1) + ": " + this.songs.get(i).getName());
                    }
                }
            }
        } else {
            Toast.makeText(FullPlayerActivity.this, R.string.toast11, Toast.LENGTH_SHORT).show();
        }

    }

    private void addEvents() {
        this.vpFullPlayer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                pageIndicatorView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        scaleAnimation = new ScaleAnimation(this, this.ivBack);
//        scaleAnimation.Event_ImageView();
//        this.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    public void Send_Position(int position ) {
        lyricsPlayerFragment.Get_Position(position);
    }



}