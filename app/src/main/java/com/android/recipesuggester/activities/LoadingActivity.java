package com.android.recipesuggester.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.recipesuggester.R;
import com.android.recipesuggester.data.Recipe;
import com.android.recipesuggester.data.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    //ANIMATIONS
    private LottieAnimationView loading_ANIM_animation;

    //IMGS
    private ImageView loading_IMG_logo;

    //TXT
    private TextView loading_LBL_title;

    //DATA
    private String[] ingredients;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        findViews();
        glideIMGS();
        setAnimations();

        try {
            readIngredients();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initUser();
    }

    // Initializing a User either by creating it or from the firebase auth.
    private void initUser() {
        user = new User();

        LoadingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            user = snapshot.getValue(User.class);
                            if (user.getIngredients() == null) {
                                user.setIngredients(new ArrayList<String>());
                            }
                            Log.d("oof", "User loaded successfully from the database!");
                        } else {
                            user.setEmail(firebaseUser.getEmail());
                            user.setIngredients(new ArrayList<String>());
                            user.setIngredientsString("");
                            user.setRecipes(new ArrayList<Recipe>());
                            Log.d("oof", "User created successfully!");
                        }

                        //Waiting for a second after the user is fetched
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                continueToMain();
                            }
                        };

                        handler.postDelayed(runnable, 1000);
                        handler.removeCallbacksAndMessages(runnable);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("oof", "Database read cancelled while fetching a user!");
                        continueToMain();
                    }
                });
            }
        });
    }

    private void continueToMain() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        Gson gson = new Gson();

        intent.putExtra("user", gson.toJson(user));
        intent.putExtra("ingredients", ingredients);
        intent.putExtra("user_name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        startActivity(intent);
        finish();
    }

    // Reading ingredients from a CSV file
    private void readIngredients() throws IOException {
        InputStream is = this.getResources().openRawResource(R.raw.ingredients);
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        List<String[]> ingredientList = new CSVReader(reader).readAll();
        listToStringArray(ingredientList);
    }

    // Transforming a list to a string array
    private void listToStringArray(List<String[]> ingredientList) {
        ingredients = new String[ingredientList.size()];

        for (int i = 0; i < ingredientList.size(); i++) {
            ingredients[i] = Arrays.toString(ingredientList.get(i));
            ingredients[i] = ingredients[i].replace("[", "");
            ingredients[i] = ingredients[i].replace("]", "");
        }
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
