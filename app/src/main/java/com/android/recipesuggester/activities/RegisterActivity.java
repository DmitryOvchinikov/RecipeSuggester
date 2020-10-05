package com.android.recipesuggester.activities;

import android.content.Intent;
import android.os.Bundle;
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

//TODO: add name to register

public class RegisterActivity extends AppCompatActivity {

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

    private View.OnClickListener loginscreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    };

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
                MyToast.getInstance().showToast(R.string.enter_name, getApplicationContext());
            }

            if (password.length() < 6) {
                MyToast.getInstance().showToast(R.string.minimum_password, getApplicationContext());
                return;
            }

            register_BAR_progress.setVisibility(View.VISIBLE);
            createUser(email, password, name);
        }
    };

    private void createUser(String email, String password, final String name) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                register_BAR_progress.setVisibility(View.GONE);

                //Alert the user if he failed to register
                if (!task.isSuccessful()) {
                    MyToast.getInstance().showToast(R.string.auth_failed, getApplicationContext());
                } else {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    auth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("oof", "User name added.");
                            }
                        }
                    });
                    MyToast.getInstance().showToast(R.string.successful_registration, getApplicationContext());
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        MyToast.getInstance().cancelToast();
    }
}
