package com.android.recipesuggester;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyToast {
    private static MyToast instance;
    private Toast toast;

    public static MyToast getInstance() {
        return instance;
    }

    public static MyToast initHelper(Context context) {
        if (instance == null) {
            instance = new MyToast(context);
        }
        return instance;
    }

    public MyToast(Context context) {
        toast = new Toast(context);
    }

    public void showToast(int R_STRING, Context context) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, R_STRING, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
