package com.android.recipesuggester;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    private LottieAnimationView loading_ANIM_animation;

    private ImageView loading_IMG_logo;
    private TextView loading_LBL_title;

    private String[] ingredients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        findViews();
        glideIMGS();
        setAnimations();


    }

    private void setAnimations() {
        Animation top_animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bot_animation = AnimationUtils.loadAnimation(this, R.anim.bot_animation);
        loading_IMG_logo.setAnimation(top_animation);
        loading_LBL_title.setAnimation(top_animation);
        loading_ANIM_animation.setAnimation(bot_animation);
    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.ic_launcher).into(loading_IMG_logo);
    }

    private void findViews() {
        loading_IMG_logo = findViewById(R.id.loading_IMG_logo);
        loading_LBL_title = findViewById(R.id.loading_LBL_title);
        loading_ANIM_animation = findViewById(R.id.loading_ANIM_animation);
    }
}
