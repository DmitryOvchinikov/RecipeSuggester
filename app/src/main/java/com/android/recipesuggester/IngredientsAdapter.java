package com.android.recipesuggester;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private ArrayList<String> ingredients;
    private final static String INGREDIENTS_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/";

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public IngredientsAdapter(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void updateIngredients(ArrayList<String> newIngredients) {
        if (ingredients != null) {
            this.ingredients.clear();
        }
        if (ingredients == null) {
            ingredients = new ArrayList<String>();
        }
        this.ingredients.addAll(newIngredients);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (ingredients == null) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else if (ingredients.size() == 0) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = 0;
        switch(viewType) {
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                layout = R.layout.ingredients_empty;
                break;
            case VIEW_TYPE_OBJECT_VIEW:
                layout = R.layout.ingredients_row;
                break;
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout, parent, false);
        return new IngredientsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.IngredientsViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_EMPTY_LIST_PLACEHOLDER) {
        } else {
            holder.ingredient_LBL_text.setText(ingredients.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (ingredients == null) {
            return 0;
        }
        if (ingredients.size() == 0) {
            return 1; //for the empty view
        }
        else {
            return ingredients.size();
        }
    }

    public static class IngredientsViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredient_LBL_text;
        private ImageView ingredient_IMG_img;

        public IngredientsViewHolder(@NonNull final View itemView) {
            super(itemView);

            Context context = itemView.getContext();
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ingredient_LBL_text = itemView.findViewById(R.id.ingredient_LBL_text);
                    ingredient_IMG_img = itemView.findViewById(R.id.ingredient_IMG_img);

                    //Log.d("oof", INGREDIENTS_IMAGE_URL +  + ".jpg");
                    //Glide.with(itemView).load(INGREDIENTS_IMAGE_URL + ingredient_LBL_text.getText() + ".jpg").into(ingredient_IMG_img);
                }
            });

        }
    }
}
