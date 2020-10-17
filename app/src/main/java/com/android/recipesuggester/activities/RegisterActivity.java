package com.android.recipesuggester.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.recipesuggester.custom.MyToast;
import com.android.recipesuggester.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private final static String DEFAULT_IMAGE_URL = "https://i7.pngguru.com/preview/177/551/742/user-interface-design-computer-icons-default-stephen-salazar-photography-thumbnail.jpg";

    private ImageView register_IMG_topBG;
    private ImageView register_IMG_botBG;
    private ImageView register_IMG_logo;

    private EditText register_EDT_email, register_EDT_password, register_EDT_name;
    private Button register_BTN_register, register_BTN_loginscreen;
    private ProgressBar register_BAR_progress;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        findViews();
        glideIMGS();
        bindButtonListeners();

    }

    private void bindButtonListeners() {
        register_BTN_loginscreen.setOnClickListener(loginscreenListener);
        register_BTN_register.setOnClickListener(registerListener);
    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.wave).into(register_IMG_botBG);
        Glide.with(this).load(R.drawable.wave).into(register_IMG_topBG);
        Glide.with(this).load(R.mipmap.ic_launcher).into(register_IMG_logo);

        register_IMG_topBG.setRotation(180);

    }

    private void findViews() {
        register_IMG_topBG = findViewById(R.id.register_IMG_topBG);
        register_IMG_botBG = findViewById(R.id.register_IMG_botBG);
        register_IMG_logo = findViewById(R.id.register_IMG_logo);

        register_EDT_email = findViewById(R.id.register_EDT_email);
        register_EDT_password = findViewById(R.id.register_EDT_password);
        register_EDT_name = findViewById(R.id.register_EDT_name);

        register_BTN_register = findViewById(R.id.register_BTN_register);
        register_BTN_loginscreen = findViewById(R.id.register_BTN_loginscreen);

        register_BAR_progress = findViewById(R.id.register_BAR_progress);
    }

    // Go back to the login screen listener
    private View.OnClickListener loginscreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    };

    // Registering the user listener
    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = register_EDT_email.getText().toString().trim();
            String password = register_EDT_password.getText().toString().trim();
            String name = register_EDT_name.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                MyToast.getInstance().showToast(R.string.enter_email, getApplicationContext());
                return;
            }

            if (TextUtils.isEmpty(password)) {
                MyToast.getInstance().showToast(R.string.enter_password, getApplicationContext());
                return;
            }

            if (TextUtils.isEmpty(name)) {
                MyToast.getInstance().showToast(R.string.register_enter_name, getApplicationContext());
            }

            if (password.length() < 6) {
                MyToast.getInstance().showToast(R.string.register_minimum_password, getApplicationContext());
                return;
            }

            register_BAR_progress.setVisibility(View.VISIBLE);
            createUser(email, password, name);
        }
    };

    // Creating the user inside the firebase auth
    private void createUser(String email, String password, final String name) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                register_BAR_progress.setVisibility(View.GONE);

                //Alert the user if he failed to register
                if (!task.isSuccessful()) {
                    MyToast.getInstance().showToast(R.string.register_auth_failed, getApplicationContext());
                } else {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(DEFAULT_IMAGE_URL)).build();
                    auth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("oof", "User name added.");
                            }
                        }
                    });

                    MyToast.getInstance().showToast(R.string.register_successful_registration, getApplicationContext());

                    //Force a 1 second wait so the user may see the toast
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    };

                    handler.postDelayed(runnable,1000);
                    handler.removeCallbacksAndMessages(runnable);
                }
            }
        });

    }

    // Cancelling the toast onPause
    @Override
    protected void onPause() {
        super.onPause();

        MyToast.getInstance().cancelToast();
    }
}
