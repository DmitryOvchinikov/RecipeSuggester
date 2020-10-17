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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonIOException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchRecipesFragment extends Fragment {

    //CALLBACKS
    OnSwitchFragmentListener callback;

    //FINAL STRINGS
    private final static String FIND_BY_INGREDIENTS_GET = "https://api.spoonacular.com/recipes/findByIngredients";
    private final static String GET_ANALYZED_RECIPE_INFO = "https://api.spoonacular.com/recipes/";
    private final static String API_KEY = "";

    //IMGS
    private ImageView search_IMG_recipe;

    //TXT
    private TextView search_TXT_title;
    private TextView search_TXT_missing;
    private TextView search_TXT_used;

    //BUTTONS
    private Button search_TXT_search;
    private Button search_BTN_back;
    private Button search_BTN_next;
    private Button search_BTN_add;

    //DATA
    private User user;
    private Recipe[] recipes;
    private int currentRecipePosition;
    private String currentSummaryString;

    //ANIMATIONS
    private LottieAnimationView search_ANIM_animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        getUserFromActivity();
        bindButtonListeners();

        search_ANIM_animation.setVisibility(View.INVISIBLE);
        setButtonsVisibility(View.INVISIBLE);
    }

    // Change buttons visibility
    private void setButtonsVisibility(int visibility) {
        search_BTN_back.setVisibility(visibility);
        search_BTN_next.setVisibility(visibility);
        search_BTN_add.setVisibility(visibility);
    }

    private void bindButtonListeners() {
        search_TXT_search.setOnClickListener(findRecipeListener);
        search_BTN_back.setOnClickListener(switchRecipeListener);
        search_BTN_next.setOnClickListener(switchRecipeListener);
        search_BTN_add.setOnClickListener(addRecipeListener);
    }

    private void findViews(View view) {
        search_IMG_recipe = view.findViewById(R.id.search_IMG_recipe);
        search_TXT_title = view.findViewById(R.id.search_TXT_title);
        search_TXT_missing = view.findViewById(R.id.search_TXT_missing);
        search_TXT_search = view.findViewById(R.id.search_TXT_search);
        search_TXT_used = view.findViewById(R.id.search_TXT_used);
        search_BTN_back = view.findViewById(R.id.search_BTN_back);
        search_BTN_next = view.findViewById(R.id.search_BTN_next);
        search_BTN_add = view.findViewById(R.id.search_BTN_add);
        search_ANIM_animation = view.findViewById(R.id.search_ANIM_animation);

    }

    // Get the user from the main activity
    private void getUserFromActivity() {
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
    }

    // Request an HTTP GET from the API to receive recipes according to the ingredients from the user
    private void requestHttpRecipes(final String url, String type) {
        search_ANIM_animation.setVisibility(View.VISIBLE);

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
                        updateButtons();
                    } else {
                        search_ANIM_animation.setVisibility(View.INVISIBLE);
                        Snackbar.make(getView(), "We didn't find any recipes, please add more ingredients!", Snackbar.LENGTH_SHORT).show();
                    }
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

    // Update the button states
    private void updateButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentRecipePosition == 0) {
                    search_BTN_back.setEnabled(false);
                    search_BTN_next.setEnabled(true);
                } else if (currentRecipePosition == recipes.length - 1) {
                    search_BTN_next.setEnabled(false);
                    search_BTN_back.setEnabled(true);
                } else if (currentRecipePosition > 0 && currentRecipePosition < recipes.length - 1) {
                    search_BTN_back.setEnabled(true);
                    search_BTN_next.setEnabled(true);
                }
            }
        });
    }

    // Load the recipes to the recipes array, and then load them into the ui
    private void loadRecipes(final JsonObject[] jsonObjectArray) throws JsonIOException {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recipes = new Recipe[jsonObjectArray.length];
                for (int i = 0; i < jsonObjectArray.length; i++) {
                    final int id = jsonObjectArray[i].get("id").getAsInt();
                    String title = jsonObjectArray[i].get("title").getAsString();
                    String imageURL = jsonObjectArray[i].get("image").getAsString();
                    int amountOfMissing = jsonObjectArray[i].get("missedIngredientCount").getAsInt();
                    int amountOfUsed = jsonObjectArray[i].get("usedIngredientCount").getAsInt();

                    String missing = "";
                    String used = "";
                    String steps = "";
                    JsonArray jsonIngredients = jsonObjectArray[i].get("missedIngredients").getAsJsonArray();
                    missing = loadIngredients(amountOfMissing, jsonIngredients);
                    jsonIngredients = jsonObjectArray[i].get("usedIngredients").getAsJsonArray();
                    used = loadIngredients(amountOfUsed, jsonIngredients);
                    Log.d("oof", "MISSING: " + missing);
                    Log.d("oof", "USED: " + used);
                    recipes[i] = new Recipe(id, title, imageURL, missing, used, steps);
                }
                loadRecipeToUi(0);
            }
        });
    }

    // Load the ingredients from the JsonArray, used for the missing / used ingredients
    private String loadIngredients(int amount, JsonArray missingIngredients) {
        String ingredients = "";
        Gson gson = new Gson();
        JsonObject[] jsonObjectArrayMissing;
        jsonObjectArrayMissing = gson.fromJson(missingIngredients, JsonObject[].class);
        for (int j = 0; j < amount; j++) {
            ingredients += jsonObjectArrayMissing[j].get("name").getAsString();
            if (j != amount - 1) {
                ingredients += ", ";
            }
        }
        ingredients += ".";
        return ingredients;
    }

    // Load the recipe texts to the ui
    private void loadRecipeToUi(final int position) {
        Log.d("oof", "loadRecipeToUi: loading recipe number " + position + " to UI.");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                search_TXT_title.setText(recipes[position].getTitle());
                Glide.with(getActivity()).load(recipes[position].getImageURL()).into(search_IMG_recipe);
                search_TXT_missing.setText("Missing Ingredients: " + recipes[position].getMissing());
                search_TXT_used.setText("Used Ingredients: " + recipes[position].getUsed());
                currentRecipePosition = position;
                updateButtons();

                updateLoadingAnimation(false);
                setButtonsVisibility(View.VISIBLE);
            }
        });
    }

    // Listener for the HTTP GET recipes request
    private View.OnClickListener findRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (user.getIngredients().size() != 0) {
                requestHttpRecipes(FIND_BY_INGREDIENTS_GET + API_KEY + user.getIngredientsString() + "&number=10" + "&ignorePantry=false", "application/json");
            } else {
                search_ANIM_animation.setVisibility(View.INVISIBLE);
                Snackbar.make(view, "You have no ingredients to search with!", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    // Listener to add a recipe to the recipes list inside the user, requests the recipe steps from the API
    private View.OnClickListener addRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe currentRecipe = recipes[currentRecipePosition];

            if (!(user.getRecipes().contains(currentRecipe))) {
                Log.d("oof", GET_ANALYZED_RECIPE_INFO + recipes[currentRecipePosition].getID() + "/analyzedInstructions" + API_KEY);
                requestHttpSteps(GET_ANALYZED_RECIPE_INFO + recipes[currentRecipePosition].getID() + "/analyzedInstructions" + API_KEY, "application/json");
                Snackbar.make(view, "Added " + currentRecipe.getTitle() + " to your recipe list!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(view, currentRecipe.getTitle() + " already exists in your recipe list!", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    // Request an HTTP GET from the API to receive the recipe steps
    private void requestHttpSteps(final String url, String type) {
        Log.d("oof", "Requesting Recipe Steps:");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).header(type, "application/json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("oof", "  onFailure: Request unsuccessful");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    Log.d("oof", "  onResponse: Request successful");

                    Gson gson = new Gson();
                    JsonObject[] jsonStepsArray = null;

                    String jsonResponse = response.body().string();
                    Log.d("oof", "       Response: " + jsonResponse.toString());
                    JsonObject[] jsonObjectArray;
                    jsonObjectArray = gson.fromJson(jsonResponse, JsonObject[].class);

                    if (jsonResponse.length() > 5) {
                        jsonStepsArray = gson.fromJson(jsonObjectArray[0].get("steps"), JsonObject[].class);
                    }
                    loadSteps(jsonStepsArray);
                } catch (JsonIOException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Load the recipe steps into the recipe
    private void loadSteps(final JsonObject[] jsonStepsArray) {
        Log.d("oof", "      loadSteps: loading steps");
        if (jsonStepsArray != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder steps = new StringBuilder();
                    int amountOfSteps = jsonStepsArray.length;
                    Log.d("oof", "      AMOUNT OF STEPS:" + amountOfSteps);
                    for (int i = 0; i < amountOfSteps; i++) {
                        steps.append((i+1) + ". ");
                        steps.append(jsonStepsArray[i].get("step").getAsString());
                        if (i != amountOfSteps - 1) {
                            steps.append("\n");
                        }
                    }
                    Log.d("oof", "steps: " + steps.toString());
                    recipes[currentRecipePosition].setSteps(steps.toString());

                    user.addRecipe(recipes[currentRecipePosition]);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.saveUserToDB();
                }
            });
        } else {
            recipes[currentRecipePosition].setSteps("No cooking steps found!");
            user.addRecipe(recipes[currentRecipePosition]);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.saveUserToDB();
        }
    }

    // Switch the recipe onClick, back / next recipe
    private View.OnClickListener switchRecipeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.search_BTN_back:
                    currentRecipePosition -= 1;
                    break;
                case R.id.search_BTN_next:
                    currentRecipePosition += 1;
                    break;
            }
            updateLoadingAnimation(true);
            loadRecipeToUi(currentRecipePosition);
        }
    };

    // update the loading animation and make the buttons visible
    private void updateLoadingAnimation(boolean visibility) {
        if (visibility) {
            search_ANIM_animation.setVisibility(View.VISIBLE);
            search_TXT_missing.setVisibility(View.INVISIBLE);
            search_TXT_title.setVisibility(View.INVISIBLE);
            search_IMG_recipe.setVisibility(View.INVISIBLE);
            search_TXT_used.setVisibility(View.INVISIBLE);
        } else {
            search_ANIM_animation.setVisibility(View.INVISIBLE);
            search_TXT_used.setVisibility(View.VISIBLE);
            search_TXT_missing.setVisibility(View.VISIBLE);
            search_TXT_title.setVisibility(View.VISIBLE);
            search_IMG_recipe.setVisibility(View.VISIBLE);
        }
    }

    public void setOnSwitchFragmentListener(SearchRecipesFragment.OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onSearchFragmentSwitch(User user);
    }
}