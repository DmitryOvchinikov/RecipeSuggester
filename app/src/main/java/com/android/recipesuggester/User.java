package com.android.recipesuggester;

public class User {
    private String email;
    private String[] ingredients;

    public User() {
    }

    public User(String email, String[] ingredients) {
        this.email = email;
        this.ingredients = ingredients;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
