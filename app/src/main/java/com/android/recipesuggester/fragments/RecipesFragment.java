package com.android.recipesuggester.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.android.recipesuggester.data.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TODO: add buttons back / next to the recipes

public class RecipesFragment extends Fragment {

    //CALLBACKS
    OnSwitchFragmentListener callback;

    //FINAL STRINGS
    private final static String FIND_BY_INGREDIENTS_GET = "https://api.spoonacular.com/recipes/findByIngredients";
    private final static String API_KEY = "";

    //IMGS
    private ImageView recipes_IMG_recipe;

    //TXT
    private TextView recipes_TXT_title;
    private TextView recipes_TXT_recipe;

    //BUTTONS
    private Button recipes_TXT_search;
    private Button recipes_BTN_back;
    private Button recipes_BTN_next;

    //DATA
    private User user;
    private String[][] recipes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        getUserFromActivity();
        bindButtonListeners();

    }

    private void bindButtonListeners() {
        recipes_TXT_search.setOnClickListener(findRecipeListener);
    }

    private void setRecipeText() {
        //TODO: add the recipe and title of the recipe
    }

    private void findViews(View view) {
        recipes_IMG_recipe = view.findViewById(R.id.recipes_IMG_recipe);
        recipes_TXT_title = view.findViewById(R.id.recipes_TXT_title);
        recipes_TXT_recipe = view.findViewById(R.id.recipes_TXT_recipe);

        recipes_TXT_search = view.findViewById(R.id.recipes_TXT_search);
        recipes_BTN_back = view.findViewById(R.id.recipes_BTN_back);
        recipes_BTN_next = view.findViewById(R.id.recipes_BTN_next);

    }

    private void getUserFromActivity() {
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
    }

    private void requestHTTP(String url, String type) {
        Log.d("oof", "requestHTTP: Requesting recipe information");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("oof", "  onResponse: Request successful");
                Log.d("oof", "  RESPONSE: " + response.body().string());
                Gson gson = new Gson();
                String jsonRecipes = response.body().string();
                recipes = gson.fromJson(jsonRecipes, String[][].class);
                Log.d("oof", "  RECIPES: " + Arrays.toString(recipes));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("oof", "onFailure: Request failed:" + e.getMessage());
            }
        });

    }

    private View.OnClickListener findRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("oof", FIND_BY_INGREDIENTS_GET + API_KEY + user.getIngredientsString() + "&number=2");
            requestHTTP(FIND_BY_INGREDIENTS_GET + API_KEY + user.getIngredientsString() + "&number=2", "application/json");
            setRecipeText();
        }
    };

    public void setOnSwitchFragmentListener(RecipesFragment.OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onRecipesFragmentSwitch(int position);
    }
}