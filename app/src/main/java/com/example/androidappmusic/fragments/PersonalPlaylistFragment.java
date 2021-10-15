package com.example.androidappmusic.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.UserPlaylistAdapter;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Song;
import com.example.androidappmusic.models.UserPlaylist;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;


public class PersonalPlaylistFragment extends Fragment {

    private static final String TAG = "PPFragment";

    private LinearLayout llFrameLoveSong;

    private TextView tvNumberPlaylist;
    private TextView tvNumberSongLove;
    private ImageView ivAddPlaylist;
    private TextView tvTitleLoveSong;
    private TextView tvEmptyPlaylist;

    private TextView tvNumberDownloadSong;
    private TextView tvTitleDownloadSong;
    private LinearLayout llFrameDownloadSong;

    private ShimmerFrameLayout sflItemUserPlaylist;
    private RecyclerView rvYourPlaylist;
    private UserPlaylistAdapter userPlaylistAdapter;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ArrayList<Song> songArrayList;
    private ArrayList<UserPlaylist> userPlaylistArrayList;





    private String mParam1;
    private String mParam2;

    public PersonalPlaylistFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalPlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalPlaylistFragment newInstance(String param1, String param2) {
        PersonalPlaylistFragment fragment = new PersonalPlaylistFragment();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(TAG);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_playlist, container, false);
    }
}