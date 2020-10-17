package com.android.recipesuggester.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.recipesuggester.custom.MyToast;
import com.android.recipesuggester.R;
import com.android.recipesuggester.dialogs.ResetPasswordDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //PERMS
    private final static int RC_READ_EXTERNAL_STORAGE = 11111;
    private final static String READ_EXTERNAL_STORAGE_PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;

    //IMGS
    private ImageView login_IMG_topBG;
    private ImageView login_IMG_botBG;
    private ImageView login_IMG_logo;

    //BUTTONS
    private Button login_BTN_login;
    private Button login_BTN_resetpass;
    private Button login_BTN_registerscreen;

    //EDIT TXT
    private EditText login_EDT_email, login_EDT_password;
    private ProgressBar login_BAR_progress;

    //DATA
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        accessPermissions();
        auth = FirebaseAuth.getInstance();

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

    // A listener for moving to the register activity onClick
    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }
    };

    // A listener to open a ResetPasswordDialog onClick
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ResetPasswordDialog resetPasswordDialog = new ResetPasswordDialog(LoginActivity.this);
            resetPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resetPasswordDialog.getWindow().setDimAmount(0.8f);
            resetPasswordDialog.show();
        }
    };

    //A listener to login to the application
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

    // Authenticating the user
    private void authenticateUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                login_BAR_progress.setVisibility(View.GONE);

                if (!task.isSuccessful()) {
                    if (password.length() < 6) {
                        login_EDT_password.setError(getString(R.string.register_minimum_password));
                    } else {
                        MyToast.getInstance().showToast(R.string.register_auth_failed, getApplicationContext());
                    }
                } else {
                    startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                    finish();
                }
            }
        });
    }

    // Checking if the user allowed the external storage permission, which is required for the profile picture
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void accessPermissions() {
        Log.d("oof", "accessPermissions:");
        if (EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE_PERMS)) {
            Log.d("oof", "  External Storage Permission exists!");
        } else {
            Log.d("oof", "  External Storage Permission does not exist!");
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMS)
                            .setRationale(R.string.permissions_rationale)
                            .setPositiveButtonText(R.string.permissions_ok)
                            .setNegativeButtonText(R.string.permissions_cancel)
                            .build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }
}