package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView titleView = findViewById(R.id.text_detail_title);
        TextView ingredientsView = findViewById(R.id.text_detail_ingredients);
        TextView instructionsView = findViewById(R.id.text_detail_instructions);

        if (getIntent().hasExtra("EXTRA_RECIPE")) {
            Recipe recipe = (Recipe) getIntent().getSerializableExtra("EXTRA_RECIPE");
            if (recipe != null) {
                setTitle(recipe.getTitle());
                titleView.setText(recipe.getTitle());

                StringBuilder ingSb = new StringBuilder();
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    ingSb.append("• ")
                            .append(ri.getQuantity()).append(" ")
                            .append(ri.getUnit()).append(" ")
                            .append(ri.getName())
                            .append("\n");
                }
                ingredientsView.setText(ingSb.toString());
                instructionsView.setText(recipe.getInstructions());
            }
        }
    }
}
