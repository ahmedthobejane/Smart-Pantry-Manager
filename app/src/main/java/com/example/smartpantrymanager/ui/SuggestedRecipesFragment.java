package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.RecipeDetailActivity;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDbHelper;
import com.example.smartpantrymanager.logic.PantryMatcher;
import com.example.smartpantrymanager.model.Pantryitem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

public class SuggestedRecipesFragment extends Fragment {

    private RecyclerView recyclerStrict, recyclerAlmost;
    private TextView textEmpty, textAlmostHeader;
    private PantryDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        recyclerStrict = v.findViewById(R.id.recycler_strict_recipes);
        recyclerAlmost = v.findViewById(R.id.recycler_almost_recipes);
        textEmpty = v.findViewById(R.id.text_no_suggestions);
        textAlmostHeader = v.findViewById(R.id.text_almost_header);

        dbHelper = new PantryDbHelper(getContext());

        recyclerStrict.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        evaluateSuggestions();
    }

    private void evaluateSuggestions() {
        List<Pantryitem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipesWithIngredients();

        PantryMatcher.MatchResult result = PantryMatcher.evaluateRecipes(pantry, allRecipes);

        if (result.strictMatches.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            recyclerStrict.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerStrict.setVisibility(View.VISIBLE);

            RecipeAdapter strictAdapter = new RecipeAdapter(result.strictMatches, recipe -> {
                Intent i = new Intent(getActivity(), RecipeDetailActivity.class);
                i.putExtra("EXTRA_RECIPE", recipe);
                startActivity(i);
            });
            recyclerStrict.setAdapter(strictAdapter);
        }

        // Optional Stretch: Display "Almost There" recipes missing exactly 1 ingredient separately
        if (!result.almostThereMatches.isEmpty()) {
            textAlmostHeader.setVisibility(View.VISIBLE);
            recyclerAlmost.setVisibility(View.VISIBLE);

            RecipeAdapter almostAdapter = new RecipeAdapter(result.almostThereMatches, recipe -> {
                Intent i = new Intent(getActivity(), RecipeDetailActivity.class);
                i.putExtra("EXTRA_RECIPE", recipe);
                startActivity(i);
            });
            recyclerAlmost.setAdapter(almostAdapter);
        } else {
            textAlmostHeader.setVisibility(View.GONE);
            recyclerAlmost.setVisibility(View.GONE);
        }
    }
}