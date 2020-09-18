package com.android.recipesuggester;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class SearchBar extends MaterialSearchView {

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchBar(Context context) {
        super(context);
    }

    // Extending the search bar height to the whole screen when searching for suggestions
    @Override
    public void showSuggestions() {
        super.showSuggestions();
        this.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
    }

    // Closing the search bar height entirely when there is no more need for it
    @Override
    public void closeSearch() {
        super.closeSearch();
        this.getLayoutParams().height = 0;
    }
}
