package com.android.recipesuggester.data;

public class Recipe {
    private int ID;
    private String title;
    private String imageURL;
    private String missing;
    private String used;
    private String steps;

    public Recipe() {
    }

    public Recipe(int ID, String title, String imageURL, String missing, String used, String steps) {
        this.ID = ID;
        this.title = title;
        this.imageURL = imageURL;
        this.missing = missing;
        this.used = used;
        this.steps = steps;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMissing() {
        return missing;
    }

    public void setMissing(String missing) {
        this.missing = missing;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", missing='" + missing + '\'' +
                '}';
    }
}
