package com.example.smartpantrymanager.model;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private long id;
    private String title;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe(long id, String title, String instructions, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
