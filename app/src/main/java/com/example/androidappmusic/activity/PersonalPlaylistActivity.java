package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.SongAdapter;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.models.UserPlaylist;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPlaylistActivity extends AppCompatActivity {

    private static final String TAG = "PPActivity";

    ImageView ivPersonalPlaylistBack;
    ImageView ivPersonalPlaylistMore;
    TextView tvPersonalPlaylistTitle;
    TextView tvEmptySong;
    Button btnPersonalPlayAll;

    ShimmerFrameLayout sflItemSong;
    RecyclerView rvPersonalPlaylist;

    ScaleAnimation scaleAnimation;
    ArrayList<Song> songArrayList;
    UserPlaylist userPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_playlist);

        DataLocalManager.init(this); //Khoi tao

        linkViews();
        loadData();
        addEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void linkViews() {
        ivPersonalPlaylistBack = findViewById(R.id.ivPersonalPlaylistBack);
        tvPersonalPlaylistTitle = findViewById(R.id.tvPersonalPlaylistTitle);
        tvPersonalPlaylistTitle.setSelected(true); //Text will be moved

        ivPersonalPlaylistMore = findViewById(R.id.ivPersonalPlaylistMore);

        btnPersonalPlayAll = findViewById(R.id.btnPersonalPlayAll);
        btnPersonalPlayAll.setEnabled(false); //set false ????? cho n?? kh??ng ho???t ?????ng tr?????c, sau khi load xong h??t t???t c??? b??i h??t th?? g???i h??m Play_All_Song();

        tvEmptySong = findViewById(R.id.tvEmptySong);
        tvEmptySong.setSelected(true);

        sflItemSong = findViewById(R.id.sflItemSong);
        rvPersonalPlaylist = findViewById(R.id.rvPersonalPlaylist);
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("FAVORITESONG")){
                String titlePlaylist = intent.getStringExtra("FAVORITESONG");
                if (!titlePlaylist.isEmpty()){
                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);

                    Handle_Favorite_Song();
                }
            }else if (intent.hasExtra("SONGPLAYLIST")){
                this.userPlaylist = (UserPlaylist) intent.getParcelableExtra("SONGPLAYLIST");
                if (this.userPlaylist != null){
                    int playlistID = this.userPlaylist.getYouID();
                    String titlePlaylist = this.userPlaylist.getName();
                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);
                    Handle_UserPlaylist_Song(playlistID);
                }
            }else if (intent.hasExtra("DOWNLOADSONG")){
                String titlePlaylist = intent.getStringExtra("DOWNLOADSONG");
                if (!titlePlaylist.isEmpty()){
                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);

                }
            }
        }
    }

    private void addEvents() {
        scaleAnimation = new ScaleAnimation(this, this.ivPersonalPlaylistBack);
        scaleAnimation.Event_ImageView();
        ivPersonalPlaylistBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scaleAnimation = new ScaleAnimation(this, this.ivPersonalPlaylistMore);
        scaleAnimation.Event_ImageView();

        scaleAnimation = new ScaleAnimation(this, this.btnPersonalPlayAll);
        scaleAnimation.Event_Button();
    }



    private void Handle_UserPlaylist_Song(int playlistID) {
        DataService dataService = APIService.getService(); //Kh???i t???o ph????ng th???c ????? ?????y l??n
        Call<List<Song>> callBack = dataService.getSongUserPlaylist(DataLocalManager.getUserID(), playlistID);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    //tr?????ng h???p user ???? d??ng l???i b??i h??t
                    rvPersonalPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(PersonalPlaylistActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // chieu doc
                    rvPersonalPlaylist.setLayoutManager(layoutManager);
                    rvPersonalPlaylist.setAdapter(new SongAdapter(PersonalPlaylistActivity.this, songArrayList, playlistID, "PLAYLISTSONG"));
                    rvPersonalPlaylist.setItemAnimator(new DefaultItemAnimator());

                    sflItemSong.setVisibility(View.GONE);
                    rvPersonalPlaylist.setVisibility(View.VISIBLE);
                    Play_All_Song();
                    Log.d(TAG, songArrayList.get(0).getName());
                }else {
                    //Tr?????ng h???p user ch??a c?? l???i b??i h??t
                    sflItemSong.setVisibility(View.GONE);
                    tvEmptySong.setVisibility(View.VISIBLE); //Hi???n th??ng b??o ch??a c?? b??i h??t n??o
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_UserPlaylist_Song(Error): " + t.getMessage());
            }
        });
    }

    private void Handle_Favorite_Song() {
        DataService dataService = APIService.getService(); //Kh???i t???o ph????ng th???c ????? ?????y l??n
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0){
                    //Tr?????ng h???p user ???? c?? b??i h??t y??u th??ch
                    rvPersonalPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(PersonalPlaylistActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // chieu doc
                    rvPersonalPlaylist.setLayoutManager(layoutManager);
                    rvPersonalPlaylist.setAdapter(new SongAdapter(PersonalPlaylistActivity.this, songArrayList, "FAVORITESONG"));

                    rvPersonalPlaylist.setItemAnimator(new DefaultItemAnimator());
                    sflItemSong.setVisibility(View.GONE);
                    rvPersonalPlaylist.setVisibility(View.VISIBLE);
                    Play_All_Song();
                    Log.d(TAG, songArrayList.get(0).getName());
                }else {
                    //Tr?????ng h???p user ch??a c?? b??i h??t y??u th??ch
                    sflItemSong.setVisibility(View.GONE);
                    tvEmptySong.setVisibility(View.VISIBLE); //Hi???n th??ng b??o ch??a c?? b??i h??t n??o
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_Favorite_Song(Error): " + t.getMessage());
            }
        });
    }

    private void Play_All_Song() {
        //H??m n??y s??? ?????m b???o khi c??c b??i h??t load xong v??? giao di???n th?? button n??y m???i ho???t ?????ng
        this.btnPersonalPlayAll.setEnabled(true);
        this.btnPersonalPlayAll.setOnClickListener(v -> {
            if (songArrayList.size() > 0){
                Intent intent = new Intent(this, FullPlayerActivity.class);
                intent.putExtra("ALLFAVORITESONGS", songArrayList);
                startActivity(intent);
            }else {
                tvEmptySong.setVisibility(View.VISIBLE); //Hi???n th??ng b??o ch??a c?? b??i h??t n??o
            }
        });
    }
}