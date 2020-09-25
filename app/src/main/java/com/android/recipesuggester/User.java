package com.android.recipesuggester;

import java.util.ArrayList;

public class User {
    private String email;
    private ArrayList<String> ingredients;
    private String ingredientsString;

    public User() {
    }

    public User(String email, ArrayList<String> ingredients) {
        this.email = email;
        this.ingredients = ingredients;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    //Getting a string for the http request
    public String getIngredientsString() {
        String str = "&ingredients=";
        for (int i = 0; i < ingredients.size(); i++) {
            str = str.concat(ingredients.get(i).replaceAll(" ", "-"));
            if ( i != ingredients.size() - 1) {
                str = str.concat(",+");
            }
        }
        return str;
    }

    public void setIngredientsString(String ingredientsString) {
        this.ingredientsString = ingredientsString;
    }

    public boolean addIngredient(String ingredient) {
        if (this.ingredients.contains(ingredient)) {
            return false;
        } else {
            this.ingredients.add(ingredient);
        }
        return true;
    }

}
