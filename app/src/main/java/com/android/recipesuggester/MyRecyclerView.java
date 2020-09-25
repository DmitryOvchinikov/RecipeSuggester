package com.android.recipesuggester;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerView extends RecyclerView {
    public MyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Overriding the onMeasure method to force the recycler view maximum height
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightSpec = MeasureSpec.makeMeasureSpec((int) (size.y*0.8),MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
