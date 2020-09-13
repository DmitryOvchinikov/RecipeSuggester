package com.android.recipesuggester;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoginActivity extends AppCompatActivity {

    private ImageView login_IMG_topBG;
    private ImageView login_IMG_botBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViews();
        glideIMGS();
    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.wave).into(login_IMG_botBG);
        Glide.with(this).load(R.drawable.wave).into(login_IMG_topBG);
        login_IMG_topBG.setRotation(180);
    }

    private void findViews() {
        login_IMG_topBG = findViewById(R.id.login_IMG_topBG);
        login_IMG_botBG = findViewById(R.id.login_IMG_botBG);
    }
}