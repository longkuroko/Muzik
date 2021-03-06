package com.example.androidappmusic.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.adapter.AlbumAdapter;
import com.example.androidappmusic.adapter.PlaylistHomeAdapter;
import com.example.androidappmusic.adapter.SliderAdapter;
import com.example.androidappmusic.adapter.ThemeAdapter;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.Album;
import com.example.androidappmusic.models.Playlist;
import com.example.androidappmusic.models.Slider;
import com.example.androidappmusic.models.Theme;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    public static final String TAG_1 = "Album";
    public static final String TAG_2 = "Playlist";
    public static final String TAG_3 = "Theme";

    SliderView sliderView;
    ArrayList<Slider> sliders;
    ScaleAnimation scaleAnimation;

    // Album
    List<Album> albumArrayList;
    ShimmerFrameLayout sflItemAlbum;
    ViewPager2 vpg2Album;
    ImageView ivAlbumMore;
    TextView tvAlbum;

    // Playlist
    ArrayList<Playlist> playlistArrayList;
    ShimmerFrameLayout sflItemPlaylist;
    RecyclerView rvPlaylist;
    ImageView ivPlaylistMore;
    TextView tvPlaylist;

    // Theme
    List<Theme> themeArrayList;
    ShimmerFrameLayout sflItemTheme;
    RecyclerView rvTheme;
    ImageView ivThemeMore;
    TextView tvTheme;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linkViews(view);
        HandlerSlider();
        HandlerAlbum();
        HandlerPlaylist();
        HandlerTheme();
    }

    private void linkViews(View view) {
       sliderView = view.findViewById(R.id.isvSlider);

       //Album
        this.sflItemAlbum = view.findViewById(R.id.sflItemAlbum);
        this.vpg2Album = view.findViewById(R.id.vpg2Album);

        this.ivAlbumMore = view.findViewById(R.id.ivAlbumMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivAlbumMore);
        this.scaleAnimation.Event_ImageView();

        this.tvAlbum = view.findViewById(R.id.tvAlbum);
        this.tvAlbum.setSelected(true);

        // Playlist
        this.sflItemPlaylist = view.findViewById(R.id.sflItemPlaylist);
        this.rvPlaylist = view.findViewById(R.id.rvPlaylist);

        this.ivPlaylistMore = view.findViewById(R.id.ivPlaylistMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPlaylistMore);
        this.scaleAnimation.Event_ImageView();

        this.tvPlaylist = view.findViewById(R.id.tvPlaylist);
        this.tvPlaylist.setSelected(true); // Text will be moved

        // Theme
        this.sflItemTheme = view.findViewById(R.id.sflItemTheme);
        this.rvTheme = view.findViewById(R.id.rvTheme);

        this.ivThemeMore = view.findViewById(R.id.ivThemeMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivThemeMore);
        this.scaleAnimation.Event_ImageView();

        this.tvTheme = view.findViewById(R.id.tvTheme);
        this.tvTheme.setSelected(true); // Text will be moved

    }

    private void HandlerSlider() {
        DataService dataService = APIService.getService(); // Kh???i t???o ph????ng th???c
        Call<List<Slider>> callBack = dataService.getSlider(); // G???i ph????ng th???c getSlider() -> tr??? v??? d??? li???u cho bi???n callBack
        callBack.enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                sliders = new ArrayList<>();
                sliders =(ArrayList<Slider>) response.body();

                if(sliders != null){
                    sliderView.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION); // giao di???n
                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); // giao di???n
                    sliderView.setSliderAdapter(new SliderAdapter(getContext(), sliders));

                    Log.d(TAG, sliders.get(0).getImage());

                }
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                Log.d(TAG, "Handle_Slider(Error): " + t.getMessage());
            }
        });
    }

    private void HandlerAlbum() {
        DataService dataService = APIService.getService();
        Call<List<Album>> callBack = dataService.getAlbumCurrentDay();
        callBack.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                albumArrayList = new ArrayList<>();
                albumArrayList =(ArrayList<Album>) response.body();


                if(albumArrayList != null){

                    vpg2Album.setClipToPadding(false); // Set clip padding
                    vpg2Album.setClipChildren(false); // Set clip children
                    vpg2Album.setOffscreenPageLimit(3); // Set page limit
                    vpg2Album.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER); // Kh??ng bao gi??? cho ph??p ng?????i d??ng cu???n qu?? ch??? ????? xem n??y.

                   vpg2Album.setAdapter(new AlbumAdapter(getContext(), albumArrayList, vpg2Album));


                    // X??t c??c hi???u ???ng chuy???n ?????ng cho vpg2Album
                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(30));
                    compositePageTransformer.addTransformer((page, position) -> {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.8f + r * 0.2f);
                    });

                    vpg2Album.setPageTransformer(compositePageTransformer);

                    sflItemAlbum.setVisibility(View.GONE); // Load bi???n m???t
                    vpg2Album.setVisibility(View.VISIBLE); // Hi???n th??ng tin

//                    Log.d(TAG_1, albumArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.d(TAG_1, t.getMessage());
            }
        });


    }

    private void HandlerPlaylist() {
        DataService dataService = APIService.getService();
        Call<List<Playlist>> callBack = dataService.getPlaylistCurrentDay();
        callBack.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                playlistArrayList = new ArrayList<>();
                playlistArrayList = (ArrayList<Playlist>) response.body();

                if(playlistArrayList != null){

                    rvPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chi???u d???c xu???ng
                    rvPlaylist.setLayoutManager(layoutManager);
                    rvPlaylist.setAdapter(new PlaylistHomeAdapter(getContext(), playlistArrayList));

                    sflItemPlaylist.setVisibility(View.GONE); // Load bi???n m???t
                    rvPlaylist.setVisibility(View.VISIBLE); // Hi???n th??ng tin

//                    Log.d(TAG_2, playlistArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG_2, t.getMessage());
            }
        });

    }

    private void HandlerTheme() {

        DataService dataService = APIService.getService();
        Call<List<Theme>> callBack = dataService.getThemeCurrentDay();
        callBack.enqueue(new Callback<List<Theme>>() {
            @Override
            public void onResponse(Call<List<Theme>> call, Response<List<Theme>> response) {
                themeArrayList = new ArrayList<>();
                themeArrayList = response.body();

                if(themeArrayList != null){
                    rvTheme.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    rvTheme.setLayoutManager(layoutManager);
                    rvTheme.setAdapter(new ThemeAdapter(getContext(), themeArrayList));

                    sflItemTheme.setVisibility(View.GONE);
                    rvTheme.setVisibility(View.VISIBLE);

//                    Log.d(TAG_3, themeArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {
                    Log.d(TAG_3, t.getMessage());
            }
        });
    }
}