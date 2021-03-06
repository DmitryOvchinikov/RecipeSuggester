package com.android.recipesuggester.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
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
        if (newIngredients != null) {
            this.ingredients.addAll(newIngredients);
        }
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
            //nothing
        } else {
            holder.ingredient_LBL_text.setText(ingredients.get(position));
            Glide.with(holder.itemView).load(INGREDIENTS_IMAGE_URL + ingredients.get(position) + ".jpg").into(holder.ingredient_IMG_img);
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

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredient_LBL_text;
        private ImageView ingredient_IMG_img;

        public IngredientsViewHolder(@NonNull final View itemView) {
            super(itemView);

            if (ingredients.size() != 0 ) {
                final Context context = itemView.getContext();
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ingredient_LBL_text = itemView.findViewById(R.id.ingredient_LBL_text);
                        ingredient_IMG_img = itemView.findViewById(R.id.ingredient_IMG_img);
                    }
                });
            }
        }
    }
}
