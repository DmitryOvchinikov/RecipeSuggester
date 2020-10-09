package com.android.recipesuggester.data;

public class Recipe {
    private int ID;
    private String title;
    private String imageURL;
    private String summary;

    public Recipe() {
    }

    public Recipe(int ID, String title, String imageURL, String recipeText) {
        this.ID = ID;
        this.title = title;
        this.imageURL = imageURL;
        this.summary = recipeText;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
