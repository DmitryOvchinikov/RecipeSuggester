package com.android.recipesuggester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//TODO: Confirmation email when registering
//TODO: EditText borders possibly

public class LoginActivity extends AppCompatActivity {

    private ImageView login_IMG_topBG;
    private ImageView login_IMG_botBG;
    private ImageView login_IMG_logo;

    private EditText login_EDT_email, login_EDT_password;
    private Button login_BTN_login, login_BTN_resetpass, login_BTN_registerscreen;
    private ProgressBar login_BAR_progress;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                finish();
        }

        setContentView(R.layout.activity_login);

        findViews();
        glideIMGS();
        bindButtonListeners();
    }

    private void bindButtonListeners() {
        login_BTN_login.setOnClickListener(loginListener);
        login_BTN_registerscreen.setOnClickListener(registerListener);
        login_BTN_resetpass.setOnClickListener(resetListener);
    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.wave).into(login_IMG_botBG);
        Glide.with(this).load(R.drawable.wave).into(login_IMG_topBG);
        Glide.with(this).load(R.mipmap.ic_launcher).into(login_IMG_logo);

        login_IMG_topBG.setRotation(180);
    }

    private void findViews() {
        login_IMG_topBG = findViewById(R.id.login_IMG_topBG);
        login_IMG_botBG = findViewById(R.id.login_IMG_botBG);
        login_IMG_logo = findViewById(R.id.login_IMG_logo);

        login_EDT_email = findViewById(R.id.login_EDT_email);
        login_EDT_password = findViewById(R.id.login_EDT_password);

        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_resetpass = findViewById(R.id.login_BTN_resetpass);
        login_BTN_registerscreen = findViewById(R.id.login_BTN_registerscreen);

        login_BAR_progress = findViewById(R.id.login_BAR_progress);
    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }
    };

    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ResetPasswordDialog resetPasswordDialog = new ResetPasswordDialog(LoginActivity.this);
            resetPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resetPasswordDialog.getWindow().setDimAmount(0.8f);
            resetPasswordDialog.show();
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = login_EDT_email.getText().toString();
            final String password = login_EDT_password.getText().toString();

            if (TextUtils.isEmpty(email)) {
                MyToast.getInstance().showToast(R.string.enter_email, getApplicationContext());
                return;
            }

            if (TextUtils.isEmpty(password)) {
                MyToast.getInstance().showToast(R.string.enter_password, getApplicationContext());
                return;
            }

            login_BAR_progress.setVisibility(View.VISIBLE);

            authenticateUser(email, password);
        }
    };

    private void authenticateUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                login_BAR_progress.setVisibility(View.GONE);

                if (!task.isSuccessful()) {
                    if (password.length() < 6) {
                        login_EDT_password.setError(getString(R.string.minimum_password));
                    } else {
                        MyToast.getInstance().showToast(R.string.auth_failed, getApplicationContext());
                    }
                } else {
                    startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                    finish();
                }
            }
        });
    }
}