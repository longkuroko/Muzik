package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.SongAdapter;
import com.example.androidappmusic.models.Album;
import com.example.androidappmusic.models.Genre;
import com.example.androidappmusic.models.Playlist;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongActivity extends AppCompatActivity {
    
    private static final String TAG = "SongActivity";
    
    
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    
    
    RecyclerView rvListSong;
    ShimmerFrameLayout sflItemSong;
    
    private ArrayList<Song> songs;
    
    private Playlist playlist;
    private Album album;
    private Genre genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        DataLocalManager.init(this);
        
        linkViews();
        loadData();
        addEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void linkViews() {
        coordinatorLayout = findViewById(R.id.cdlListSong);
        collapsingToolbarLayout = findViewById(R.id.ctlImage);
//        toolbar = findViewById(R.id.tbListSong);

        floatingActionButton = findViewById(R.id.fabPlay);
        floatingActionButton.setEnabled(false);

        rvListSong = findViewById(R.id.rvListSong);
        sflItemSong = findViewById(R.id.sflItemSong);


//        setSupportActionBar(this.toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.ic_angle_left);

        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(getResources()
                .getColor(R.color.pink2)));

        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.pink2));

    }
    private void addEvent() {

//        toolbar.setNavigationOnClickListener(v-> finish());
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("PLAYLIST")) {
                this.playlist = (Playlist) intent.getParcelableExtra("PLAYLIST");
                if (this.playlist != null) {
                    Log.d(TAG, this.playlist.getName());

                    this.collapsingToolbarLayout.setTitle(this.playlist.getName());
                    Display_Song_Playlist(this.playlist.getId());
                }
            } else if (intent.hasExtra("ALBUM")) {
                this.album = (Album) intent.getParcelableExtra("ALBUM");
                if (this.album != null) {
                    Log.d(TAG, this.album.getName());

                    this.collapsingToolbarLayout.setTitle(this.album.getName());
                    Display_Song_Album(this.album.getId());
                }
            } else if (intent.hasExtra("GENRE")) {
                this.genre = (Genre) intent.getParcelableExtra("GENRE");
                if (this.genre != null) {
                    Log.d(TAG, this.genre.getName());

                    this.collapsingToolbarLayout.setTitle(this.genre.getName());
                    Display_Song_Genre(this.genre.getIdGenre());
                }
            }
        }
    }

    private void Display_Song_Album(int id ){
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongAlbum(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {

                songs = new ArrayList<>();
                songs = (ArrayList<Song>) response.body();

                if(songs != null && songs.size() > 0){
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songs, "SONG"));

                    sflItemSong.setVisibility(View.GONE);
                    rvListSong.setVisibility(View.VISIBLE);

                    Log.d(TAG, songs.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }
    private void Display_Song_Genre(int id) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongGenre(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songs = new ArrayList<>();
                songs = (ArrayList<Song>) response.body();

                if (songs != null && songs.size() > 0) {
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songs, "SONG"));

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvListSong.setVisibility(View.VISIBLE); // Hiện thông tin

                    Play_All_Song();

                    Log.d(TAG, songs.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Display_Song_Genre(Error)" + t.getMessage());
            }
        });
    }

    private void Display_Song_Playlist(int id) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongPlaylist(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songs = new ArrayList<>();
                songs = (ArrayList<Song>) response.body();

                if (songs != null && songs.size() > 0) {
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songs, "SONG"));

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvListSong.setVisibility(View.VISIBLE); // Hiện thông tin

                    Play_All_Song();

                    Log.d(TAG, songs.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Display_Song_Playlist(Error)" + t.getMessage());
            }
        });
    }

    private void Play_All_Song(){
        this.floatingActionButton.setEnabled(true);
        this.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SongActivity.this, FullPlayerActivity.class);
            intent.putExtra("ALLSONGS", songs);
            startActivity(intent);
        });
    }

}