package com.android.recipesuggester.fragments;

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

import com.airbnb.lottie.LottieAnimationView;
import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.android.recipesuggester.data.Recipe;
import com.android.recipesuggester.data.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonIOException;

import java.io.IOException;

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
    private final static String SUMMARIZE_RECIPE = "https://api.spoonacular.com/recipes/";
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
    private Recipe[] recipes;
    private int currentRecipePosition;
    private String currentSummaryString;

    //ANIMATIONS
    private LottieAnimationView recipes_ANIM_animation;

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

        recipes_ANIM_animation.setVisibility(View.INVISIBLE);
        setButtonsVisibility(View.INVISIBLE);
    }

    private void setButtonsVisibility(int visibility) {
        recipes_BTN_back.setVisibility(visibility);
        recipes_BTN_next.setVisibility(visibility);
    }

    private void bindButtonListeners() {
        recipes_TXT_search.setOnClickListener(findRecipeListener);
        recipes_BTN_back.setOnClickListener(switchRecipeListener);
        recipes_BTN_next.setOnClickListener(switchRecipeListener);
    }

    private void findViews(View view) {
        recipes_IMG_recipe = view.findViewById(R.id.recipes_IMG_recipe);
        recipes_TXT_title = view.findViewById(R.id.recipes_TXT_title);
        recipes_TXT_recipe = view.findViewById(R.id.recipes_TXT_recipe);

        recipes_TXT_search = view.findViewById(R.id.recipes_TXT_search);
        recipes_BTN_back = view.findViewById(R.id.recipes_BTN_back);
        recipes_BTN_next = view.findViewById(R.id.recipes_BTN_next);
        recipes_ANIM_animation = view.findViewById(R.id.recipes_ANIM_animation);
    }

    private void getUserFromActivity() {
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
    }

    private void requestHttpRecipes(final String url, String type) {
        recipes_ANIM_animation.setVisibility(View.VISIBLE);

        Log.d("oof", "Requesting Recipes:");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    Log.d("oof", "  onResponse: Request successful");

                    Gson gson = new Gson();
                    String jsonResponse = response.body().string();

                    JsonObject[] jsonObjectArray;
                    jsonObjectArray = gson.fromJson(jsonResponse, JsonObject[].class);
                    if (jsonObjectArray.length != 0) {
                        loadRecipes(jsonObjectArray);
                    }
                    updateButtons();
                } catch (JsonIOException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("oof", "  onFailure: Request failed:" + e.getMessage());
            }
        });

    }

    private void updateButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentRecipePosition == 0) {
                    recipes_BTN_back.setEnabled(false);
                    recipes_BTN_next.setEnabled(true);
                } else if (currentRecipePosition == recipes.length - 1) {
                    recipes_BTN_next.setEnabled(false);
                    recipes_BTN_back.setEnabled(true);
                } else if (currentRecipePosition > 0 && currentRecipePosition < recipes.length - 1) {
                    recipes_BTN_back.setEnabled(true);
                    recipes_BTN_next.setEnabled(true);
                }
            }
        });
    }

    private void requestHttpSummary(String url, String type, final int position) {
        Log.d("oof", "Requesting Recipe Summary:");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();
        final String[] currentRecipeText = {""};

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("oof", "  onFailure: Request failed:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("oof", "  onResponse: Request successful");
                Gson gson = new Gson();
                String jsonResponse = response.body().string();
                JsonObject jsonObject;
                jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                if (jsonObject.size() != 0) {
                    loadRecipeText(jsonObject, position);
                    loadRecipeToUi(position);
                }
            }
        });
    }

    private void cleanSummaryString() {
        String cleanString = "";
        cleanString = currentSummaryString.replace("<b>", "");
        cleanString = cleanString.replace("</b>", "");
        int iend = cleanString.indexOf("%");
        cleanString = cleanString.substring(0, iend+1);
        cleanString = cleanString + ".";
        currentSummaryString = cleanString;
    }

    private void loadRecipes(final JsonObject[] jsonObjectArray) throws JsonIOException {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipes = new Recipe[jsonObjectArray.length];
                for (int i = 0; i < jsonObjectArray.length; i++) {
                    final int id = jsonObjectArray[i].get("id").getAsInt();
                    String title = jsonObjectArray[i].get("title").getAsString();
                    String imageURL = jsonObjectArray[i].get("image").getAsString();
                    String recipeText = "";
                    recipes[i] = new Recipe(id, title, imageURL, recipeText);
                }
                requestHttpSummary(SUMMARIZE_RECIPE + recipes[0].getID() + "/summary" + API_KEY + "&includeNutrition=false", "application/json", 0);
            }
        });
    }

    private void loadRecipeText(final JsonObject jsonObject, final int position) {
        Log.d("oof", "      Loading Recipe Text");
        currentSummaryString = jsonObject.get("summary").getAsString();
        Log.d("oof", "      TEXT:" + currentSummaryString);
        cleanSummaryString();
        recipes[position].setSummary(currentSummaryString);
    }

    private void loadRecipeToUi(final int position) {
        Log.d("oof", "loadRecipeToUi: loading recipe number " + position + " to UI.");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipes_TXT_title.setText(recipes[position].getTitle());
                Glide.with(getActivity()).load(recipes[position].getImageURL()).into(recipes_IMG_recipe);
                recipes_TXT_recipe.setText(recipes[position].getSummary());
                currentRecipePosition = position;
                updateButtons();

                updateLoadingAnimation(false);
                setButtonsVisibility(View.VISIBLE);
            }
        });
    }

    private View.OnClickListener findRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requestHttpRecipes(FIND_BY_INGREDIENTS_GET + API_KEY + user.getIngredientsString() + "&number=2", "application/json");
        }
    };

    private View.OnClickListener switchRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.recipes_BTN_back:
                    currentRecipePosition -= 1;
                    break;
                case R.id.recipes_BTN_next:
                    currentRecipePosition += 1;
                    break;
            }
            updateLoadingAnimation(true);
            requestHttpSummary(SUMMARIZE_RECIPE + recipes[currentRecipePosition].getID() + "/summary" + API_KEY + "&includeNutrition=false", "application/json", currentRecipePosition);
        }
    };

    private void updateLoadingAnimation(boolean visibility) {
        if (visibility) {
            recipes_ANIM_animation.setVisibility(View.VISIBLE);
            recipes_TXT_recipe.setVisibility(View.INVISIBLE);
            recipes_TXT_title.setVisibility(View.INVISIBLE);
            recipes_IMG_recipe.setVisibility(View.INVISIBLE);
        } else {
            recipes_ANIM_animation.setVisibility(View.INVISIBLE);
            recipes_TXT_recipe.setVisibility(View.VISIBLE);
            recipes_TXT_title.setVisibility(View.VISIBLE);
            recipes_IMG_recipe.setVisibility(View.VISIBLE);
        }
    }

    public void setOnSwitchFragmentListener(RecipesFragment.OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onRecipesFragmentSwitch(int position);
    }
}