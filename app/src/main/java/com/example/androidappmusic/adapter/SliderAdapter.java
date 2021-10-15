package com.example.androidappmusic.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.activity.FullPlayerActivity;
import com.example.androidappmusic.models.Slider;
import com.example.androidappmusic.models.Song;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.MyViewHolder> {


    private  static final String TAG = "SliderAdapter";

    private AlertDialog alertDialog;

    private Context context;
    private ArrayList<Slider> imageSliders;
    private ArrayList<Song> songs;

    public SliderAdapter(Context context, ArrayList<Slider> imageSliders) {

        this.imageSliders = imageSliders;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider,
                parent,
                false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {

        Picasso.get()
                .load(imageSliders.get(position).getImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(viewHolder.ivSliderImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_loading_dialog, null);
                alertBuilder.setView(view);
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();
                Handle_Song_Slider(imageSliders.get(position).getSongID());
            }
        });
    }

    private void Handle_Song_Slider(int songID) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongFromSlider(songID);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songs = new ArrayList<>();
                songs = (ArrayList<Song>) response.body();

                if(songs != null && songs.size() > 0 ){
                    Intent intent = new Intent(context, FullPlayerActivity.class);

                    intent.putExtra("SONGSLIDER", songs);
                    context.startActivity(intent);

                    alertDialog.dismiss();


                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                alertDialog.dismiss();
                Log.d(TAG, "Handle_Song_Slider(Error): "+ t.getMessage());
            }
        });

    }


    @Override
    public int getCount() {
        return this.imageSliders.size();
    }


    public class MyViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView ivSliderImage;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.ivSliderImage = itemView.findViewById(R.id.ivSliderImage);
        }
    }

}
