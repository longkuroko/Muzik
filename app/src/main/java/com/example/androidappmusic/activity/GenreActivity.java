package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.GenreAdapter;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Genre;
import com.example.androidappmusic.models.Theme;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreActivity extends AppCompatActivity {

    private static final String TAG = "GenreActivity";

    ScaleAnimation scaleAnimation;
    LoadingDialog loadingDialog;

    ImageView ivBack;
    TextView tvTitle;
    RecyclerView rvGenre;

    Theme theme;
    private List<Genre> genreList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);


        linkViews();
        loadData();

    }


    private void linkViews() {

        loadingDialog = new LoadingDialog(this);
        loadingDialog.Start_Loading();

        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvGenre = findViewById(R.id.rvGenre);

    }

    private void loadData() {


    }

    private void Display_Genre(int id){

        DataService dataService = APIService.getService();
        Call<List<Genre>> callBack = dataService.getGenre(id);

        callBack.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                genreList = new ArrayList<>();
                genreList = response.body();

                if(genreList != null ){
                    rvGenre.setHasFixedSize(true);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(GenreActivity.this, 3);
                    gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvGenre.setLayoutManager(gridLayoutManager);

                    rvGenre.setAdapter(new GenreAdapter(GenreActivity.this, genreList));
                    loadingDialog.Cancel_Loading();
                }

            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                loadingDialog.Cancel_Loading();
                Log.d(TAG, "Display_Genre(Error)" + t.getMessage());

            }
        });

    }
}