package com.android.recipesuggester;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.opencsv.CSVReader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class MainActivity extends AppCompatActivity {

    private final static String FIND_BY_INGREDIENTS_GET = "https://api.spoonacular.com/recipes/findByIngredients";
    private final static String API_KEY = "?apiKey=841a45035b8f4ada971513a952601c6b";

    private ImageView main_IMG_botBG;

    private SearchBar main_BAR_search;
    private Toolbar main_BAR_toolbar;
    private MyRecyclerView main_recycler;
    private Button main_BTN_findRecipe;

    private String[] ingredients;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private User user;
    private IngredientsAdapter ingredientsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViews();
        glideIMGs();
        bindButtonListeners();
        ingredientsAdapter = new IngredientsAdapter(null);

        try {
            readIngredients();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initMaterialSearchBar();
        initDB();
        initUser();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(main_recycler);
    }

    private void bindButtonListeners() {
        main_BTN_findRecipe.setOnClickListener(findRecipeListener);
    }

    private void initIngredientList(User user1) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        main_recycler.setLayoutManager(linearLayoutManager);
        main_recycler.setHasFixedSize(true);
        main_recycler.setAdapter(ingredientsAdapter);
        ingredientsAdapter.updateIngredients(user1.getIngredients());
        main_recycler.smoothScrollToPosition(0);
        Log.d("oof", "INGREDIENTS: " + user1.getIngredients());
    }

    private void initDB() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void initUser() {
        user = new User();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                            saveUserToDB();
                            Log.d("oof", "User created successfully!");
                        }

                        initIngredientList(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("oof", "Database read cancelled while fetching a user!");
                    }
                });
            }
        });
    }

    private void glideIMGs() {
        //Glide.with(this).load(R.drawable.wave).into(main_IMG_botBG);
    }

    private void initMaterialSearchBar() {

        main_BAR_search.setVoiceSearch(false);
        main_BAR_search.setEllipsize(true);
        main_BAR_search.setSuggestions(ingredients);

        main_BAR_search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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
        main_BAR_search.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.d("oof","onSearchViewShown");

            }

            @Override
            public void onSearchViewClosed() {
                Log.d("oof","onSearchViewClosed");
            }
        });

        main_BAR_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                main_BAR_search.dismissSuggestions();
                main_BAR_search.closeSearch();
                if (user.addIngredient(adapterView.getItemAtPosition(i).toString())) {
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
                    Snackbar.make(view, "Added " + adapterView.getItemAtPosition(i).toString() + " to your ingredients list.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, adapterView.getItemAtPosition(i).toString() + " already exists in your ingredients list!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void readIngredients() throws IOException {
            InputStream is = this.getResources().openRawResource(R.raw.ingredients);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            List<String[]> ingredientList = new CSVReader(reader).readAll();
            listToStringArray(ingredientList);
    }

    private void listToStringArray(List<String[]> ingredientList) {
        ingredients = new String[ingredientList.size()];

        for (int i = 0; i < ingredientList.size(); i++) {
            ingredients[i] = Arrays.toString(ingredientList.get(i));
            ingredients[i] = ingredients[i].replace("[", "");
            ingredients[i] = ingredients[i].replace("]", "");
        }
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
        main_BAR_search = findViewById(R.id.main_BAR_search);
        main_BAR_toolbar = findViewById(R.id.main_BAR_toolbar);
        //main_IMG_botBG = findViewById(R.id.main_IMG_botBG);
        main_recycler = findViewById(R.id.main_recycler);
        main_BTN_findRecipe = findViewById(R.id.main_BTN_findRecipe);

        setSupportActionBar(main_BAR_toolbar);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to delete " + user.getIngredients().get(position) + " from the ingredients list?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Snackbar.make(viewHolder.itemView, "Removed " + user.getIngredients().get(position) + " from your ingredients list.", Snackbar.LENGTH_LONG).show();
                    ingredientsAdapter.notifyItemRemoved(position);
                    user.getIngredients().remove(position);
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
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
        main_BAR_search.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (main_BAR_search.isSearchOpen()) {
            main_BAR_search.closeSearch();
        }
        else {
            super.onBackPressed();
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
