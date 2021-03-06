package com.example.androidappmusic.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.AddUpdateActivity;
import com.example.androidappmusic.activity.PersonalPlaylistActivity;
import com.example.androidappmusic.adapter.UserPlaylistAdapter;
import com.example.androidappmusic.animation.LoadingDialog;
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


public class PersonalPlaylistFragment extends Fragment {

    private static final String TAG = "PPFragment";

    private LinearLayout llFrameLoveSong;

    TextView tvNumberPlaylist;
    TextView tvNumberSongLove;
    ImageView ivAddPlaylist;
    TextView tvTitleLoveSong;
    TextView tvEmptyPlaylist;


    private ShimmerFrameLayout sflItemUserPlaylist;
    private RecyclerView rvYourPlaylist;
    private UserPlaylistAdapter userPlaylistAdapter;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ArrayList<Song> songArrayList;
    private ArrayList<UserPlaylist> userPlaylistArrayList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_personal_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataLocalManager.init(getContext());

        Log.d(TAG, "onViewCreated");
//
//        Handle_Number_Favorite_Song();
//        Handle_UserPlaylist();

        linkViews(view);
        addEvent();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");


        Handle_Number_Favorite_Song();
        Handle_UserPlaylist();
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

        llFrameLoveSong = view.findViewById(R.id.llFrameLoveSong);
        tvNumberPlaylist = view.findViewById(R.id.tvNumberPlaylist);
        tvNumberSongLove = view.findViewById(R.id.tvNumberSongLove);
        ivAddPlaylist = view.findViewById(R.id.ivAddPlaylist);
        tvTitleLoveSong = view.findViewById(R.id.tvTitleLoveSong);

        tvEmptyPlaylist = view.findViewById(R.id.tvEmptyPlaylist);
        tvEmptyPlaylist.setSelected(true); // Text will be moved

        sflItemUserPlaylist = view.findViewById(R.id.sflItemUserPlaylist);
        rvYourPlaylist = view.findViewById(R.id.rvYourPlaylist);

    }

    private void addEvent() {
        this.scaleAnimation = new ScaleAnimation(getContext(), this.ivAddPlaylist);
        this.scaleAnimation.Event_ImageView();
        this.ivAddPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddUpdateActivity.class);
            intent.putExtra("ADDPLAYLIST", "ADDPLAYLIST");
            startActivity(intent);
        });


        this.llFrameLoveSong.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PersonalPlaylistActivity.class);
            intent.putExtra("FAVORITESONG", this.tvTitleLoveSong.getText());
            startActivity(intent);
        });
    }



    private void Handle_Number_Favorite_Song() {
        DataService dataService = APIService.getService(); // Kh???i t???o Ph????ng th???c ????? ?????y l??n
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null) {
                    tvNumberSongLove.setText(String.valueOf(songArrayList.size()));

                    Log.d(TAG, "Number Favorite Song: " + songArrayList.size());
                } else {
                    tvNumberSongLove.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_Number_Favorite_Song(Error): " + t.getMessage());
            }
        });
    }



    private void Handle_UserPlaylist() {
        DataService dataService = APIService.getService(); // Kh???i t???o Ph????ng th???c ????? ?????y l??n
        Call<List<UserPlaylist>> callBack = dataService.getUserPlaylist(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>();
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null && userPlaylistArrayList.size() > 0) {
                    rvYourPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chi???u d???c
                    rvYourPlaylist.setLayoutManager(layoutManager);

                    userPlaylistAdapter = new UserPlaylistAdapter(getContext(), userPlaylistArrayList, tvNumberPlaylist);
                    rvYourPlaylist.setAdapter(userPlaylistAdapter);

                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvNumberPlaylist.setText(String.valueOf(userPlaylistAdapter.getItemCount())); // Hi???n th??? s??? l?????ng Playlist
                    rvYourPlaylist.setVisibility(View.VISIBLE); // Hi???n th??ng tin Playlist

//                    Log.d(TAG, "User Playlist: " + userPlaylistArrayList.get(0).getName());
                } else {
                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvEmptyPlaylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                Log.d(TAG, "Handle_User_Playlist(Error): " + t.getMessage());
            }
        });
    }
}