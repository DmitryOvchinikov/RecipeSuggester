package com.android.recipesuggester.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.recipesuggester.R;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//TODO: going out of the app with back and going back in goes into loading screen again
//TODO: logout from drawer causes the app to go into loading again

public class MainActivity extends AppCompatActivity {

    //TEXT
    private TextView menu_LBL_user;

    //DRAWER MENU
    private DrawerLayout main_drawer;
    private Toolbar main_BAR_toolbar;
    private NavigationView main_navigation;

    //IMGS
    private ImageView menu_IMG_logo;
    private ImageView menu_IMG_bg;
    private ImageView main_IMG_navigationBG;

    //DB
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        findViews();
        initDrawer();
        glideIMGS();

    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.ic_launcher).into(menu_IMG_logo);
        Glide.with(this).load(R.drawable.drawer_header_background).into(menu_IMG_bg);
        Glide.with(this).load(R.drawable.drawer_list_background).into(main_IMG_navigationBG);
    }

    private void initDrawer() {
        main_navigation.bringToFront();
        setSupportActionBar(main_BAR_toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, main_drawer, main_BAR_toolbar, R.string.drawer_open, R.string.drawer_close);
        main_drawer.addDrawerListener(toggle);
        toggle.syncState();

        main_navigation.setNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    private void findViews() {
        main_drawer = findViewById(R.id.main_drawer);
        main_navigation = findViewById(R.id.main_navigation);
        main_BAR_toolbar = findViewById(R.id.main_BAR_toolbar);
        menu_LBL_user = findViewById(R.id.menu_LBL_user);
        main_IMG_navigationBG = findViewById(R.id.main_IMG_navigationBG);

        //Creating a custom view to interact with the header of the navigation view
        View header_view = main_navigation.getHeaderView(0);
        menu_IMG_logo = header_view.findViewById(R.id.menu_IMG_logo);
        menu_LBL_user = header_view.findViewById(R.id.menu_LBL_user);
        menu_IMG_bg = header_view.findViewById(R.id.menu_IMG_bg);

        menu_LBL_user.setText("" + firebaseUser.getDisplayName());
        main_BAR_toolbar.setTitle("Hello " + firebaseUser.getDisplayName() + "!");
    }

    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.drawer_home:
                    break;
                case R.id.drawer_logout:
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("logout", 0);
                    startActivity(intent);
                    finish();
            }
            main_drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (main_drawer.isDrawerOpen(GravityCompat.START)) {
            main_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
