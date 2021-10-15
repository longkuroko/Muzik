package com.example.androidappmusic.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.ChartAdapter;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChartFragment extends Fragment {
    public static final String TAG = "ChartFragment";
    private ArrayList<Song> songs;
    RecyclerView rvChart;

    ShimmerFrameLayout sflItemSong;
    ScaleAnimation scaleAnimation;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linkViews(view);
        HandleSongChart();

    }

    private void HandleSongChart() {
        DataService dataService = APIService.getService();
        Call<List<Song>> callback = dataService.getSongChart();
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songs = (ArrayList<Song>) response.body();

                if(songs != null && songs.size() > 0){
                    sflItemSong.setVisibility(View.GONE);

                    rvChart.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvChart.setLayoutManager(layoutManager);
                    rvChart.setAdapter(new ChartAdapter(getContext(), songs));
                    rvChart.setVisibility(View.VISIBLE); // Hiện thông tin

                    Log.d(TAG, songs.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    private void linkViews(View view) {

        rvChart = view.findViewById(R.id.rvChart);
        sflItemSong = view.findViewById(R.id.sflItemSong);
    }
}