package com.example.androidappmusic.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidappmusic.API.APIService;
import com.example.androidappmusic.API.DataService;
import com.example.androidappmusic.R;
import com.example.androidappmusic.animation.LoadingDialog;
import com.example.androidappmusic.animation.ScaleAnimation;
import com.example.androidappmusic.models.User;
import com.example.androidappmusic.sharedPreferences.DataLocalManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kaushikthedeveloper.doublebackpress.DoubleBackPress;
import com.kaushikthedeveloper.doublebackpress.helper.DoubleBackPressAction;
import com.kaushikthedeveloper.doublebackpress.helper.FirstBackPressAction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TAG_1 ="LOGIN WITH FACEBOOK";
    private static final String TAG_2 ="LOGIN WITH GOOGLE";

    CallbackManager callbackManager;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    

    ScaleAnimation scaleAnimation;
    Animation topAnimation, bottomAnimation;
    LoadingDialog loadingDialog;
    ImageView imvLogo;

    Button login;
    Button btnLoginGoogle;

    ArrayList<User> userArrayList;

    FirstBackPressAction firstBackPressAction;
    DoubleBackPressAction doubleBackPressAction;
    DoubleBackPress doubleBackPress;


    public static final int TIME_DURATION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataLocalManager.init(this);
        linkViews();
        addEvents();
        loginFacebook();
        loginGoogle();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = this.firebaseAuth.getCurrentUser();
        if(currentUser != null && !DataLocalManager.getUserID().isEmpty()){
            Update_UI();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    private void addEvents() {
        imvLogo.setAnimation(this.topAnimation);
        login.setAnimation(this.bottomAnimation);
        this.btnLoginGoogle.setAnimation(this.bottomAnimation);

        this.scaleAnimation = new ScaleAnimation(MainActivity.this, this.login);
        this.scaleAnimation.Event_Button();
        this.scaleAnimation = new ScaleAnimation(MainActivity.this, this.btnLoginGoogle);
        this.scaleAnimation.Event_Button();

        this.doubleBackPressAction = () -> {
            finish();
            moveTaskToBack(true);
            System.exit(0);
        };
        this.firstBackPressAction = () -> Toast.makeText(this, R.string.toast7, Toast.LENGTH_SHORT).show();
        this.doubleBackPress = new DoubleBackPress()
                .withDoublePressDuration(TIME_DURATION)
                .withFirstBackPressAction(this.firstBackPressAction)
                .withDoubleBackPressAction(this.doubleBackPressAction);
    }

    private void linkViews() {
        login = findViewById(R.id.btnLoginFacebook);
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        imvLogo = findViewById(R.id.imvLogo);
        login = findViewById(R.id.btnLoginFacebook);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);

        this.topAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_animation);
        this.bottomAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_animation);
    }

    private void loginFacebook() {
        login.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG_1, "facebook: onSuccess: " + loginResult.getAccessToken());

                    AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Log.d(TAG_1, "signInWithCredential:success");
                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if(user != null ){
                                            Log.d(TAG_1, "User Information: " + user.toString());

                                            String id = user.getUid();
                                            String name = user.getDisplayName();
                                            String email ;
                                            if(user.getEmail() != null){
                                                email = user.getEmail();
                                            }else{
                                                email = "Null";
                                            }

                                            String avatar;
                                            if(user.getPhotoUrl() != null ){
                                                avatar = user.getPhotoUrl() + "?height=500&access_token=" + loginResult.getAccessToken().getToken();
                                            }else{
                                                avatar = "null";
                                            }
                                            String isDark = "0";
                                            String isEnglish = "0";

//                                            loadingDialog.Start_Loading();
                                            HandleUser(id, name, email, avatar, isDark, isEnglish);

                                        }
                                    }else{
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG_1, "signInWithCredential:failure", task.getException());
                                        Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }



                @Override
                public void onCancel() {
                    Log.d(TAG_1, "facebook: onCancel");
                    Toast.makeText(MainActivity.this, R.string.toast2, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG_1, "facebook: onError ", error);
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    //Login google
    private void loginGoogle(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        this.btnLoginGoogle.setOnClickListener(v -> {
            activityResultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Log.d(TAG_2, "firebaseAuthWithGoogle:" + account.getId());

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG_2, "signInWithCredential:success");

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        Log.d(TAG_2, "User Information: " + user.toString());

                                        String id = user.getUid();
                                        String name = user.getDisplayName();
                                        String email = !Objects.requireNonNull(user.getEmail()).isEmpty() ? user.getEmail() : "Null";
                                        String avatarGoogle = !Objects.requireNonNull(user.getPhotoUrl()).toString().isEmpty() ? user.getPhotoUrl().toString().replace("s96-c", "s500-c") : "Null";
                                        String isDark = "0";
                                        String isEnglish = "0";

                                        loadingDialog.Start_Loading();
                                        HandleUser(id, name, email, avatarGoogle, isDark, isEnglish);
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG_2, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG_2, "Google sign in failed", e);
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void HandleUser(String id, String name, String email, String avatar, String isDark, String isEnglish) {
        DataService dataService = APIService.getService();
        Call<List<User>> callBack = dataService.addNewUser(id, name, email, avatar, isDark, isEnglish);
        callBack.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userArrayList = new ArrayList<>();
                userArrayList = (ArrayList<User>) response.body();

                if(userArrayList != null && userArrayList.size() > 0){
                    DataLocalManager.setUserID(id);
                    DataLocalManager.setUserAvatar(avatar);

                    if(!DataLocalManager.getUserID().isEmpty()){
                        Update_UI();

//                        loadingDialog.Cancel_Loading();
                        Toast.makeText(MainActivity.this, R.string.toast1, Toast.LENGTH_SHORT).show();

                    }
                    Log.d(TAG, "User_ID: " + userArrayList.get(0).getId());
                }else{
//                    loadingDialog.Cancel_Loading();
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Handle_User (Error): " + t.getMessage());
            }
        });
    }
    private void Update_UI() {
        Intent intent = new Intent(MainActivity.this, FullActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        this.doubleBackPress.onBackPressed();
        Log.d(TAG, "Back Twice To Exit!");
    }

}