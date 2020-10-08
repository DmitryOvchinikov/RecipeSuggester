package com.android.recipesuggester.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.recipesuggester.R;
import com.android.recipesuggester.activities.MainActivity;
import com.android.recipesuggester.adapters.IngredientsAdapter;
import com.android.recipesuggester.custom.MyRecyclerView;
import com.android.recipesuggester.custom.MySearchBar;
import com.android.recipesuggester.data.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TODO: No items is shown when items exist in the list.

public class IngredientsListFragment extends Fragment {

    //CALLBACKS
    OnSwitchFragmentListener callback;

    //IMGS

    //INGREDIENT LIST + SEARCH BAR
    private MySearchBar list_BAR_search;
    private Toolbar list_BAR_toolbar;
    private MyRecyclerView list_recycler;

    //DATA
    private String[] ingredients;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private User user;
    private IngredientsAdapter ingredientsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ingredientsAdapter = new IngredientsAdapter(null);
        ingredients = getActivity().getIntent().getStringArrayExtra("ingredients");

        findViews(view);
        glideIMGs();
        initMaterialSearchBar();
        initDB();
        getUserFromActivity();
        initIngredientsList();
        initItemTouch();
    }

    private void initItemTouch() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(list_recycler);
    }

    private void getUserFromActivity() {
        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUser();
    }

    private void initIngredientsList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list_recycler.setLayoutManager(linearLayoutManager);
        list_recycler.setHasFixedSize(true);
        list_recycler.setAdapter(ingredientsAdapter);
        ingredientsAdapter.updateIngredients(user.getIngredients());
        list_recycler.smoothScrollToPosition(0);
        Log.d("oof", "INGREDIENTS: " + user.getIngredients());

    }

    private void initDB() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void glideIMGs() {
        //Glide.with(this).load(R.drawable.wave).into(main_IMG_botBG);
    }

    private void initMaterialSearchBar() {

        list_BAR_search.setVoiceSearch(false);
        list_BAR_search.setEllipsize(true);
        list_BAR_search.setSuggestions(ingredients);

        list_BAR_search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("oof", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("oof", "onQueryTextChange");
                return false;
            }
        });
        list_BAR_search.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.d("oof", "onSearchViewShown");
            }

            @Override
            public void onSearchViewClosed() {
                Log.d("oof", "onSearchViewClosed");
            }
        });

        list_BAR_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                list_BAR_search.dismissSuggestions();
                list_BAR_search.closeSearch();
                if (user.addIngredient(adapterView.getItemAtPosition(i).toString())) {
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
                    Snackbar.make(view, "Added " + adapterView.getItemAtPosition(i).toString() + " to your ingredients list.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, adapterView.getItemAtPosition(i).toString() + " already exists in your ingredients list!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findViews(View view) {
        list_BAR_search = view.findViewById(R.id.list_BAR_search);
        list_BAR_toolbar = view.findViewById(R.id.list_BAR_toolbar);
        list_recycler = view.findViewById(R.id.list_recycler);

        ((AppCompatActivity) getActivity()).setSupportActionBar(list_BAR_toolbar);
    }

    private void saveUserToDB() {
        databaseReference.child(firebaseUser.getUid()).setValue(user);
        Log.d("oof", "Updating user information at the Realtime Database!");
    }

    private ItemTouchHelper.SimpleCallback itemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); //Getting the position of the item swiped

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete " + user.getIngredients().get(position) + " from the ingredients list?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Snackbar.make(viewHolder.itemView, "Removed " + user.getIngredients().get(position) + " from your ingredients list.", Snackbar.LENGTH_LONG).show();
                    ingredientsAdapter.notifyItemRemoved(position);
                    user.getIngredients().remove(position);
                    ingredientsAdapter.updateIngredients(user.getIngredients());
                    saveUserToDB();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ingredientsAdapter.notifyDataSetChanged();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    ingredientsAdapter.notifyDataSetChanged();
                }
            }).show();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ingredients, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        list_BAR_search.setMenuItem(item);
    }


    public void setOnSwitchFragmentListener(IngredientsListFragment.OnSwitchFragmentListener callback) {
        this.callback = callback;
    }

    public interface OnSwitchFragmentListener {
        public void onIngredientsListFragmentSwitch(User updated_user);
    }

}