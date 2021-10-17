package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.SongAdapter;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Album;
import com.example.androidappmusic.models.Genre;
import com.example.androidappmusic.models.Playlist;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = "SearchActivity";

    EditText edtSearchBox;
    ImageView ivBack;
    TextView tvSearchHint;

    RecyclerView rvSearchResult;
    ScaleAnimation scaleAnimation;

    ArrayList<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        DataLocalManager.init(this);
        linkViews();
        addEvents();
    }

    private void linkViews() {
        edtSearchBox = findViewById(R.id.etSearchBox);
        edtSearchBox.requestFocus();

        ivBack = findViewById(R.id.ivBack);
        tvSearchHint = findViewById(R.id.tvSearchHint);
        tvSearchHint.setSelected(true);

        rvSearchResult = findViewById(R.id.rvSearchResult);

    }

    private void addEvents() {
//        scaleAnimation = new ScaleAnimation(this, this.ivBack);
//        scaleAnimation.Event_ImageView();
//        ivBack.setOnClickListener( v -> finish());


        edtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyWord = s.toString().toLowerCase().trim(); // Chuyển kí tự về dạng chữ viết thường để tìm kiếm cho nhanh

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!keyWord.isEmpty()) {
                            HandleSearch(keyWord);
                        }
                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(runnable, 1000);
            }
        });

    }

    public void HandleSearch(String keyword){
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongSearch(keyword);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songs = new ArrayList<>();
                songs = (ArrayList<Song>) response.body();

                if(songs != null && songs.size()> 0){
                    tvSearchHint.setVisibility(View.GONE);

                    rvSearchResult.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvSearchResult.setLayoutManager(layoutManager);


                    rvSearchResult.setAdapter(new SongAdapter(SearchActivity.this, songs, "SONGSEARCH"));
                }
                else{
                    rvSearchResult.setAdapter(new SongAdapter(SearchActivity.this, songs, "SONGSEARCH"));
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_Search(Error)" + t.getMessage());
            }
        });
    }

}