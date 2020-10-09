package com.android.recipesuggester.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    //CALLBACKS
    OnSwitchFragmentListener callback;

    //TXT
    private TextView home_TXT_hello;
    private TextView home_TXT_ingredients;

    //IMGS
    private ImageView home_IMG_bg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        setTexts();
        glideIMGS();
    }

    private void glideIMGS() {
        Glide.with(getActivity()).load(R.drawable.drawer_list_background).into(home_IMG_bg);
    }

    private void setTexts() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name = getActivity().getIntent().getExtras().getString("user_name");
                home_TXT_hello.setText("Hello " + name + "!");

                updateIngredientAmount();
            }
        });
    }

    private void updateIngredientAmount() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity.getUser().getIngredients() != null) {
            int amount = activity.getUser().getIngredients().size();
            home_TXT_ingredients.setText("Amount of Ingredients: " + amount);
        }
    }

    private void findViews(View view) {
        home_TXT_hello = view.findViewById(R.id.home_TXT_hello);
        home_IMG_bg = view.findViewById(R.id.home_IMG_bg);
        home_TXT_ingredients = view.findViewById(R.id.home_TXT_ingredients);
    }

    public void setOnSwitchFragmentListener(OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onHomeFragmentSwitch(int amount);
    }
}
