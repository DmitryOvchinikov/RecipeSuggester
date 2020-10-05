package com.android.recipesuggester.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.recipesuggester.adapters.IngredientsAdapter;
import com.android.recipesuggester.custom.MyRecyclerView;
import com.android.recipesuggester.R;
import com.android.recipesuggester.custom.MySearchBar;
import com.android.recipesuggester.data.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TODO: possibly amount of ingredients above MaterialSearchBar
//TODO: possibly jumps to different activities at the bottom of the screen
//TODO: Loading screen to let the RecyclerView to load fully
//TODO: find out how the two-three words ingredients are being sent to the server
//TODO: fix clipping of image with a long text in the ingredients list
//TODO: ask for name in the register screen

public class ListActivity extends AppCompatActivity {

    private final static String FIND_BY_INGREDIENTS_GET = "https://api.spoonacular.com/recipes/findByIngredients";
    private final static String API_KEY = "";

    private ImageView list_IMG_botBG;

    private MySearchBar list_BAR_search;
    private Toolbar list_BAR_toolbar;
    private MyRecyclerView list_recycler;
    private TextView list_LBL_counter;

    private String[] ingredients;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private User user;
    private IngredientsAdapter ingredientsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);

        ingredients = getIntent().getStringArrayExtra("ingredients");

        findViews();
        glideIMGs();
        ingredientsAdapter = new IngredientsAdapter(null);

        initMaterialSearchBar();
        initDB();
        loadUser();
        initIngredientList();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(list_recycler);
    }

    private void initIngredientList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list_recycler.setLayoutManager(linearLayoutManager);
        list_recycler.setHasFixedSize(true);
        list_recycler.setAdapter(ingredientsAdapter);
        ingredientsAdapter.updateIngredients(user.getIngredients());
        list_recycler.smoothScrollToPosition(0);
        Log.d("oof", "INGREDIENTS: " + user.getIngredients());

        updateIngredientCounter();

    }

    private void updateIngredientCounter() {
        list_LBL_counter.setText("You have " + user.getIngredients().size() + " ingredients!");
    }

    private void initDB() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void loadUser() {
        Gson gson = new Gson();
        String userString = "";
        userString = getIntent().getStringExtra("user");
        user = gson.fromJson(userString, User.class);
    }

    private void glideIMGs() {
        //Glide.with(this).load(R.drawable.wave).into(main_IMG_botBG);
    }

    private void initMaterialSearchBar() {

        list_BAR_search.setVoiceSearch(false);
        list_BAR_search.setEllipsize(true);
        list_BAR_search.setSuggestions(ingredients);

        list_BAR_search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("oof","onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("oof","onQueryTextChange");
                return false;
            }
        });
        list_BAR_search.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.d("oof","onSearchViewShown");
            }

            @Override
            public void onSearchViewClosed() {
                Log.d("oof","onSearchViewClosed");
            }
        });

        list_BAR_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                list_BAR_search.dismissSuggestions();
                list_BAR_search.closeSearch();
                if (user.addIngredient(adapterView.getItemAtPosition(i).toString())) {
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
                    Snackbar.make(view, "Added " + adapterView.getItemAtPosition(i).toString() + " to your ingredients list.", Snackbar.LENGTH_LONG).show();
                    updateIngredientCounter();
                } else {
                    Snackbar.make(view, adapterView.getItemAtPosition(i).toString() + " already exists in your ingredients list!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestHTTP(String url, String type) {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("oof", "onResponse: Request successful");
                Log.d("oof", "CALL: " + call);
                Log.d("oof", "RESPONSE: " + response.body().string());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("oof", "onFailure: Request failed:" + e.getMessage());
            }
        });

    }

    private void findViews() {
        list_BAR_search = findViewById(R.id.list_BAR_search);
        list_BAR_toolbar = findViewById(R.id.list_BAR_toolbar);
        //list_IMG_botBG = findViewById(R.id.list_IMG_botBG);
        list_recycler = findViewById(R.id.list_recycler);
        list_LBL_counter = findViewById(R.id.list_LBL_counter);

        setSupportActionBar(list_BAR_toolbar);
    }

    private void saveUserToDB() {
        databaseReference.child(firebaseUser.getUid()).setValue(user);
        Log.d("oof", "Updating user information at the Realtime Database!");
    }

    private View.OnClickListener findRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("oof", FIND_BY_INGREDIENTS_GET + API_KEY  + user.getIngredientsString() + "&number=2");
            requestHTTP(FIND_BY_INGREDIENTS_GET + API_KEY + user.getIngredientsString() + "&number=2","application/json");
        }
    };

    private ItemTouchHelper.SimpleCallback itemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); //Getting the position of the item swiped

            AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
            builder.setMessage("Are you sure you want to delete " + user.getIngredients().get(position) + " from the ingredients list?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Snackbar.make(viewHolder.itemView, "Removed " + user.getIngredients().get(position) + " from your ingredients list.", Snackbar.LENGTH_LONG).show();
                    ingredientsAdapter.notifyItemRemoved(position);
                    user.getIngredients().remove(position);
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
                    updateIngredientCounter();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ingredientsAdapter.notifyDataSetChanged();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    ingredientsAdapter.notifyDataSetChanged();
                }
            }).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredients, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        list_BAR_search.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (list_BAR_search.isSearchOpen()) {
            list_BAR_search.closeSearch();
        }
        else {
            super.onBackPressed();
            startActivity(new Intent(ListActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("oof", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("oof", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("oof", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("oof", "onDestroy");
    }

}
