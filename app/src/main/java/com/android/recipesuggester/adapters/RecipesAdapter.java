package com.android.recipesuggester.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.android.recipesuggester.data.Recipe;
import com.bumptech.glide.Glide;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder> {

    private ArrayList<Recipe> recipes;
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    public RecipesAdapter(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void updateRecipes(ArrayList<Recipe> newRecipes) {
        if (recipes != null) {
            this.recipes.clear();
        }
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        if (newRecipes != null) {
            this.recipes.addAll(newRecipes);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (recipes == null) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else if (recipes.size() == 0) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = 0;
        switch (viewType) {
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                layout = R.layout.recipes_empty;
                break;
            case VIEW_TYPE_OBJECT_VIEW:
                layout = R.layout.recipes_row;
                break;
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout, parent, false);
        return new RecipesAdapter.RecipesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecipesAdapter.RecipesViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_EMPTY_LIST_PLACEHOLDER) {
            //nothing
        } else {
            Glide.with(holder.itemView).load(recipes.get(position).getImageURL()).into(holder.recipe_IMG_img);
            holder.recipe_LBL_text.setText(recipes.get(position).getTitle());
            holder.recipe_TXT_expandedReq.setText("Required Ingredients: " + recipes.get(position).getMissing().replace(".", ", ") + recipes.get(position).getUsed());
            holder.recipe_TXT_expandedSteps.setText("Cooking steps: \n" + recipes.get(position).getSteps());
            Glide.with(holder.itemView).load(R.drawable.arrow_down_sign_to_navigate).into(holder.recipe_IMG_expand);

            holder.recipe_IMG_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(holder.recipe_expanded.isExpanded())) {
                        holder.recipe_expanded.expand();
                    } else {
                        holder.recipe_expanded.collapse();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 0;
        }
        if (recipes.size() == 0) {
            return 1;
        } else {
            return recipes.size();
        }
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder {

        private ExpandableLayout recipe_expanded;
        private TextView recipe_LBL_text;
        private ImageView recipe_IMG_img;
        private ImageView recipe_IMG_expand;
        private TextView recipe_TXT_expandedReq;
        private TextView recipe_TXT_expandedSteps;

        public RecipesViewHolder(@NonNull final View itemView) {
            super(itemView);

            if (recipes.size() != 0) {
                final Context context = itemView.getContext();
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipe_expanded = itemView.findViewById(R.id.recipe_expanded);
                        recipe_LBL_text = itemView.findViewById(R.id.recipe_LBL_text);
                        recipe_IMG_img = itemView.findViewById(R.id.recipe_IMG_img);
                        recipe_IMG_expand = itemView.findViewById(R.id.recipe_IMG_expand);
                        recipe_TXT_expandedReq = itemView.findViewById(R.id.recipe_TXT_expandedReq);
                        recipe_TXT_expandedSteps = itemView.findViewById(R.id.recipe_TXT_expandedSteps);
                    }
                });
            }
        }
    }
}
