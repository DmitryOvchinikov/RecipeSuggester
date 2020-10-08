package com.android.recipesuggester.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import androidx.fragment.app.Fragment;

import com.android.recipesuggester.R;
import com.android.recipesuggester.custom.MySearchBar;
import com.android.recipesuggester.data.User;
import com.android.recipesuggester.fragments.HomeFragment;
import com.android.recipesuggester.fragments.IngredientsListFragment;
import com.android.recipesuggester.fragments.RecipesFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

//TODO: going out of the app with back and going back in goes into loading screen again
//TODO: drawer highlights the last used, remove highlight

public class MainActivity extends AppCompatActivity implements HomeFragment.OnSwitchFragmentListener, IngredientsListFragment.OnSwitchFragmentListener, RecipesFragment.OnSwitchFragmentListener {

    //TEXT
    private TextView menu_LBL_user;
    private TextView menu_LBL_email;

    //DRAWER MENU
    private DrawerLayout main_drawer;
    private Toolbar main_BAR_toolbar;
    private NavigationView main_navigation;

    //BOTTOM CHIP BAR
    private ChipNavigationBar main_BAR_fragments;

    //IMGS
    private ImageView menu_IMG_logo;
    private ImageView menu_IMG_bg;

    //DATA
    private FirebaseUser firebaseUser;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        findViews();
        setTextViews();
        initDrawer();
        glideIMGS();
        loadUser();
        initBottomBar();

    }

    private void initBottomBar() {
        main_BAR_fragments.setItemSelected(R.id.fragments_home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragments_container, new HomeFragment()).commit();
        main_BAR_fragments.setOnItemSelectedListener(fragmentSelectListener);
    }

    private void loadUser() {
        Gson gson = new Gson();
        String userString = "";
        userString = getIntent().getStringExtra("user");

        user = gson.fromJson(userString, User.class);
    }

    private void setTextViews() {
        menu_LBL_user.setText("" + firebaseUser.getDisplayName());
        menu_LBL_email.setText("" + firebaseUser.getEmail());
        main_BAR_toolbar.setTitle("");
    }

    private void glideIMGS() {
        Glide.with(this).load(R.drawable.ic_launcher).into(menu_IMG_logo);
        Glide.with(this).load(R.drawable.drawer_header_background).into(menu_IMG_bg);
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
        main_BAR_fragments = findViewById(R.id.main_BAR_fragments);

        //Creating a custom view to interact with the header of the navigation view
        View header_view = main_navigation.getHeaderView(0);
        menu_IMG_logo = header_view.findViewById(R.id.menu_IMG_logo);
        menu_LBL_user = header_view.findViewById(R.id.menu_LBL_user);
        menu_IMG_bg = header_view.findViewById(R.id.menu_IMG_bg);
        menu_LBL_email = header_view.findViewById(R.id.menu_LBL_email);
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

    private ChipNavigationBar.OnItemSelectedListener fragmentSelectListener = new ChipNavigationBar.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int i) {
            Fragment fragment = null;
            switch (i) {
                case R.id.fragments_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.fragments_list:
                    fragment = new IngredientsListFragment();
                    break;
                case R.id.fragments_recipe:
                    fragment = new RecipesFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragments_container, fragment).commit();
        }
    };

    public User getUser() {
        return user;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Handling the onBackPressed inside the activity rather than in the fragments
    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragments_container);
        Log.d("oof", "OnBackPressed:");
        if (main_drawer.isDrawerOpen(GravityCompat.START)) {
            main_drawer.closeDrawer(GravityCompat.START);
        } else if (f != null && f.getClass().equals(IngredientsListFragment.class)) {
            Log.d("oof", "  IngredientsListFragment:");
            MySearchBar mySearchBar = f.getView().findViewById(R.id.list_BAR_search);
            if (mySearchBar.isSearchOpen()) {
                Log.d("oof", "      Dismissing suggestions and closing search.");
                mySearchBar.dismissSuggestions();
                mySearchBar.closeSearch();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onHomeFragmentSwitch(int amount) {

    }

    @Override
    public void onIngredientsListFragmentSwitch(User updated_user) {
        user = updated_user;
    }

    @Override
    public void onRecipesFragmentSwitch(int position) {

    }
}
