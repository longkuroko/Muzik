package com.example.androidappmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.androidappmusic.R;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.fragments.ChartFragment;
import com.example.androidappmusic.fragments.HomeFragment;
import com.example.androidappmusic.fragments.MiniPlayerFragment;
import com.example.androidappmusic.fragments.PersonalPlaylistFragment;
import com.example.androidappmusic.fragments.SettingFragment;
import com.example.androidappmusic.service.FullPlayerManagerService;
import com.example.androidappmusic.service.MyBroadcastReceiver;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaushikthedeveloper.doublebackpress.DoubleBackPress;
import com.kaushikthedeveloper.doublebackpress.helper.DoubleBackPressAction;
import com.kaushikthedeveloper.doublebackpress.helper.FirstBackPressAction;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullActivity extends AppCompatActivity {

    private static final String TAG = "FullActivity";

    private LoadingDialog loadingDialog;

    FirebaseAuth firebaseAuth;

    ImageView ivBell;
    FrameLayout frameMiniPlayer;
    MeowBottomNavigation meowBottomNavigation;
    Fragment fragment;

    ScaleAnimation scaleAnimation;

    private MyBroadcastReceiver myBroadcastReceiver;


    private FirstBackPressAction firstBackPressAction;
    private DoubleBackPressAction doubleBackPressAction;
    private DoubleBackPress doubleBackPress;
    private static final int TIME_DURATION = 2000;

    private CircleImageView circleImageView;
    private EditText edtSearch;

    private static final int ID_PERSONAL = 1;
    private static final int ID_CHART = 2;
    private static final int ID_HOME = 3;
    private static final int ID_RADIO = 4;
    private static final int ID_SETTING = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        DataLocalManager.init(this);
        myBroadcastReceiver = new MyBroadcastReceiver();



        linkViews();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter); // Đăng kí lắng nghe

        Check_Login();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FullPlayerManagerService.listCurrentSong != null) {
            loadFragmentMiniPlayer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Check_Login();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(myBroadcastReceiver); // Hủy đăng kí lắng nghe sự kiện
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        unregisterReceiver(myBroadcastReceiver); // Hủy đăng kí lắng nghe sự kiện
    }
    private  void Check_Login(){
        FirebaseUser currentUser = this.firebaseAuth.getCurrentUser();
        if(currentUser == null || DataLocalManager.getUserID().isEmpty()){
            this.firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            DataLocalManager.deleteUserID();
            DataLocalManager.deleteUserAvatar();
            finish();
            Intent intent = new Intent(FullActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void linkViews() {

        this.firebaseAuth = FirebaseAuth.getInstance();

        meowBottomNavigation = findViewById(R.id.bottomNavigation);
        ivBell = findViewById(R.id.imvBell);
        edtSearch = findViewById(R.id.etSearch);
        frameMiniPlayer = findViewById(R.id.frameMiniPlayer);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_PERSONAL, R.drawable.ic_music_note));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_CHART, R.drawable.ic_chart));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_RADIO, R.drawable.ic_radio));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_SETTING, R.drawable.ic_setting));

        circleImageView = findViewById(R.id.civAvatar);
        Picasso.get()
                .load(DataLocalManager.getUserAvatar())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(this.circleImageView);
    }

    private void addEvents() {

        this.scaleAnimation = new ScaleAnimation(FullActivity.this, this.ivBell);
        this.scaleAnimation.Event_ImageView();

        this.scaleAnimation = new ScaleAnimation(FullActivity.this, this.circleImageView);
        this.scaleAnimation.Event_CircleImageView();

        circleImageView.setOnClickListener(v -> {
            Intent intent = new Intent(FullActivity.this, PersonalActivity.class);
            startActivity(intent);
        });


        //bottom navigation
        meowBottomNavigation.setOnClickMenuListener(item -> Log.d(TAG, "Fragment: " + item.getId()));

        meowBottomNavigation.setOnShowListener(item ->{
            fragment = null;
            switch (item.getId()){
                case 1: {
                    fragment = new PersonalPlaylistFragment();
                    break;
                }
                case 2: {
                    fragment = new ChartFragment();
                    break;
                }
                case 3: {
                    fragment = new HomeFragment();
                    break;
                }
                case 4: {
                    fragment = new SettingFragment();
                    break;
                }
            }
            loadFragment(fragment);
        });


        meowBottomNavigation.show(ID_HOME, true);

        //event search
        edtSearch.setOnClickListener(v ->{
            Intent intent = new Intent(FullActivity.this, SearchActivity.class);
            startActivity(intent);
        });


        //double click event
        doubleBackPressAction = () ->{
            finish();
            moveTaskToBack(true);
            System.exit(0);
        };

        firstBackPressAction = () -> Toast.makeText(FullActivity.this, R.string.toast7, Toast.LENGTH_SHORT).show();
        doubleBackPress = new DoubleBackPress()
                .withDoublePressDuration(TIME_DURATION)
                .withFirstBackPressAction(firstBackPressAction)
                .withDoubleBackPressAction(doubleBackPressAction);

    }

    public void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commitAllowingStateLoss();
    }

    public void loadFragmentMiniPlayer(){
        frameMiniPlayer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.frameMiniPlayer,
                        new MiniPlayerFragment()
                )
                .commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {

       super.onBackPressed();
        this.doubleBackPress.onBackPressed();
        Log.d(TAG, "Back Twice To Exit!");
    }




}