package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.UserPlaylist;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdateActivity extends AppCompatActivity {

    private static final String TAG = "AddUpdateActivity";

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;
    private ImageView ivClose;
    private TextView tvDialogTitlePlaylist;
    private EditText etDialogContentPlaylist;
    private Button btnDialogActionPlaylist;

    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private UserPlaylist userPlaylist;

    private final String ACTION_INSERT_PLAYER = "insert";
    private final String ACTION_UPDATE_PLAYER = "update";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        linkViews();
        loadData();
        addEvents();
    }

    private void linkViews() {
        this.loadingDialog = new LoadingDialog(this);
        this.ivClose = findViewById(R.id.ivClose);
        this.tvDialogTitlePlaylist = findViewById(R.id.tvDialogTitlePlaylist);
        this.etDialogContentPlaylist = findViewById(R.id.etDialogContentPlaylist);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); //1
        this.btnDialogActionPlaylist = findViewById(R.id.btnDialogActionPlaylist);
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("ADDPLAYLIST")){
                this.scaleAnimation = new ScaleAnimation(this, this.btnDialogActionPlaylist);
                this.scaleAnimation.Event_Button();
                //Add new Playlist
                this.btnDialogActionPlaylist.setOnClickListener(v -> {
                    String playlistName = this.etDialogContentPlaylist.getText().toString().trim(); //2
                    if (playlistName.isEmpty()){
                        Toast.makeText(v.getContext(), R.string.toast12, Toast.LENGTH_SHORT).show();
                    }else{
                        loadingDialog.Start_Loading(); //3
                        Handle_Add_Update_UserPlaylist(ACTION_INSERT_PLAYER, 0, playlistName);
                    }
                });
            }  else if (intent.hasExtra("UPDATEPLAYLIST")){
                this.userPlaylist = (UserPlaylist) intent.getParcelableExtra("UPDATEPLAYLIST"); //4
                if (this.userPlaylist != null){
                    int playlistID = this.userPlaylist.getYouID();
                    String playlistName = this.userPlaylist.getName();

                    this.tvDialogTitlePlaylist.setText(R.string.tvDialogTitlePlaylist1);

                    this.etDialogContentPlaylist.setHint(R.string.etDialogContentPlaylist1);
                    this.etDialogContentPlaylist.setText(playlistName);

                    this.btnDialogActionPlaylist.setText(R.string.btnDialogActionPlaylist1);

                    this.scaleAnimation = new ScaleAnimation(this, this.btnDialogActionPlaylist);
                    this.scaleAnimation.Event_Button();
                    // Update Playlist
                    this.btnDialogActionPlaylist.setOnClickListener(v -> {
                        String newPlaylistName = this.etDialogContentPlaylist.getText().toString().trim();
                        if (newPlaylistName.isEmpty()){
                            Toast.makeText(v.getContext(), R.string.toast12, Toast.LENGTH_SHORT).show();
                        }else {
                            loadingDialog.Start_Loading();
                            Handle_Add_Update_UserPlaylist(ACTION_UPDATE_PLAYER, playlistID, newPlaylistName);
                        }
                    });
                }
            }
        }
    }

    private void addEvents() {
        this.scaleAnimation = new ScaleAnimation(this, this.ivClose);
        this.scaleAnimation.Event_ImageView();
        this.ivClose.setOnClickListener(v ->{
            finish();
        });
    }

    private void Handle_Add_Update_UserPlaylist(String action, int playlistID, String playlistName) {
        DataService dataService = APIService.getService(); //5
        Call<List<UserPlaylist>> callBack = dataService.addUpdateDeleteUserPlaylist(action, playlistID, DataLocalManager.getUserID(), playlistName); //6
        callBack.enqueue(new Callback<List<UserPlaylist>>() { //7
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>();
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null){
                    if (action.equals(ACTION_INSERT_PLAYER)){
                        //Them mot playlist
                        if (userPlaylistArrayList.get(0).getStatus() == 1){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast13, Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (userPlaylistArrayList.get(0).getStatus() == 2){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast14, Toast.LENGTH_SHORT).show();
                        }else if (userPlaylistArrayList.get(0).getStatus() == 3){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast15, Toast.LENGTH_SHORT).show();
                        }else {
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }else if (action.equals(ACTION_UPDATE_PLAYER)){
                        //Chinh sua mot playlist
                        if (userPlaylistArrayList.get(0).getStatus() == 1){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast16, Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (userPlaylistArrayList.get(0).getStatus() == 2){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast17, Toast.LENGTH_SHORT).show();
                        }else if (userPlaylistArrayList.get(0).getStatus() == 3){
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast18, Toast.LENGTH_SHORT).show();
                        }else {
                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                loadingDialog.Cancel_Loading();
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                loadingDialog.Cancel_Loading();
                Log.d(TAG, "Handle_Add_Update_Delete_DeleteAll_UserPlaylist(Error): " + t.getMessage());

            }
        });
    }
}