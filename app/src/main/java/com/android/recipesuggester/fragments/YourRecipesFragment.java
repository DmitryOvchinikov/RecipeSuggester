package com.android.recipesuggester.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.android.recipesuggester.adapters.RecipesAdapter;
import com.android.recipesuggester.custom.MyRecyclerView;
import com.android.recipesuggester.data.Recipe;
import com.android.recipesuggester.data.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class YourRecipesFragment extends Fragment {

    //CALLBACKS
    YourRecipesFragment.OnSwitchFragmentListener callback;

    //RECIPES LIST
    private MyRecyclerView recipes_recycler;

    //BUTTONS
    private Button recipes_BTN_clear;

    //DATA
    private User user;
    private RecipesAdapter recipesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipesAdapter = new RecipesAdapter(null);

        findViews(view);
        getUser();
        initRecipesList();
        bindButtonListeners();
        initItemTouch();
    }

    // Initialize the item touch on the ingredients list
    private void initItemTouch() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(recipes_recycler);
    }

    private void bindButtonListeners() {
        recipes_BTN_clear.setOnClickListener(clearListListener);
    }

    // Initialize the list of recipes
    private void initRecipesList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recipes_recycler.setLayoutManager(linearLayoutManager);
        recipes_recycler.setHasFixedSize(true);
        recipes_recycler.setAdapter(recipesAdapter);
        recipesAdapter.updateRecipes(user.getRecipes());
        recipes_recycler.smoothScrollToPosition(0);
    }

    // Get the user from the activity
    private void getUser() {
        MainActivity mainActivity = (MainActivity) getActivity();
        user = mainActivity.getUser();
    }

    private void findViews(View view) {
        recipes_recycler = view.findViewById(R.id.recipes_recycler);
        recipes_BTN_clear = view.findViewById(R.id.recipes_BTN_clear);
    }

    // A listener to clear the recipes list entirely
    private View.OnClickListener clearListListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<Recipe> emptyList = new ArrayList<Recipe>();
            user.setRecipes(emptyList);
            recipesAdapter.setRecipes(emptyList);
            recipesAdapter.notifyDataSetChanged();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.saveUserToDB();
        }
    };

    // Item touch callback, prompt a dialog to ask for the recipe removal from the list on swipe
    private ItemTouchHelper.SimpleCallback itemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); //Getting the position of the item swiped

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete " + user.getRecipes().get(position).getTitle() + " from the recipes list?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Snackbar.make(viewHolder.itemView, "Removed " + user.getRecipes().get(position).getTitle() + " from your ingredients list.", Snackbar.LENGTH_SHORT).show();
                    recipesAdapter.notifyItemRemoved(position);
                    user.getRecipes().remove(position);
                    recipesAdapter.updateRecipes(user.getRecipes());
                    MainActivity activity = (MainActivity) getActivity();
                    activity.saveUserToDB();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    recipesAdapter.notifyDataSetChanged();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    recipesAdapter.notifyDataSetChanged();
                }
            }).show();
        }
    };

    public void setOnSwitchFragmentListener(YourRecipesFragment.OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onRecipesFragmentSwitch(User user);
    }

}
