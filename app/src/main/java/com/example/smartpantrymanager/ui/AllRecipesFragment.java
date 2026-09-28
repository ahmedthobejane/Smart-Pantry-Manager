package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.RecipeDetailActivity;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDbHelper;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

public class AllRecipesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_recipes, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_all_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PantryDbHelper dbHelper = new PantryDbHelper(getContext());
        List<Recipe> recipes = dbHelper.getAllRecipesWithIngredients();

        RecipeAdapter adapter = new RecipeAdapter(recipes, recipe -> {
            Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
            intent.putExtra("EXTRA_RECIPE", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        return v;
    }
}