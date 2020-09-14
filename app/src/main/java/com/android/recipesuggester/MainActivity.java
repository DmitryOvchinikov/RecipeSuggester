package com.android.recipesuggester;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.SearchAdapter;
import com.opencsv.CSVReader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TODO: do something with delay when application is opening without login

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView main_BAR_search;
    private Toolbar main_BAR_toolbar;
    private String[] ingredients;

    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        findViews();


        try {
            readIngredients();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initMaterialSearchBar();

        //String url = "";
        //String type = "";
        //requestHTTP(url, type);
    }

    private void initMaterialSearchBar() {

        main_BAR_search.setVoiceSearch(false);
        main_BAR_search.setEllipsize(true);
        main_BAR_search.setSuggestions(ingredients);
        //main_BAR_search.setAdapter( new SearchAdapter(this, ingredients));

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
                Log.d("oof", "" + adapterView.getItemAtPosition(i).toString());
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
        Log.d("oof", "" + Arrays.toString(ingredients));
    }

    private void requestHTTP(String url, String type) {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("oof", "onResponse: Request successful");
                Log.d("oof", "CALL: " + call);
                Log.d("oof", "RESPONSE: " + response);
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

        setSupportActionBar(main_BAR_toolbar);
    }

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
        } else {
            super.onBackPressed();
        }
    }
}
