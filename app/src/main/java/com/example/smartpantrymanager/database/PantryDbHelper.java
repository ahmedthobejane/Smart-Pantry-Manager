package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.Pantryitem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class PantryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common Columns
    public static final String COLUMN_ID = "_id";

    // Pantry Columns
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipe Columns
    public static final String COLUMN_RECIPE_NAME = "title";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe Ingredient Junction Columns
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_ING_NAME = "ingredient_name";
    public static final String COLUMN_RI_QTY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public PantryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QTY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT" + ");";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL" + ");";

        String CREATE_RECIPE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_RI_ING_NAME + " TEXT NOT NULL, " +
                COLUMN_RI_QTY + " REAL NOT NULL, " +
                COLUMN_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- PANTRY CRUD OPERATIONS ---

    public long addPantryItem(Pantryitem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, item.getName());
        values.put(COLUMN_PANTRY_QTY, item.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, item.getUnit());
        values.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    public List<Pantryitem> getAllPantryItems() {
        List<Pantryitem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COLUMN_PANTRY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY));

                list.add(new Pantryitem(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int updatePantryItem(Pantryitem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, item.getName());
        values.put(COLUMN_PANTRY_QTY, item.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, item.getUnit());
        values.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());

        return db.update(TABLE_PANTRY, values, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // --- RECIPE RETRIEVAL ---

    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COLUMN_RECIPE_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long recipeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

                List<RecipeIngredient> ingredients = getIngredientsForRecipe(db, recipeId);
                recipes.add(new Recipe(recipeId, title, instructions, ingredients));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COLUMN_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_ING_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT));

                ingredients.add(new RecipeIngredient(name, qty, unit));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ingredients;
    }

    // --- PRE-SEED DATABASE ---

    private void seedRecipes(SQLiteDatabase db) {
        // Seed Recipe 1
        long r1 = insertRecipe(db, "Classic Scrambled Eggs", "1. Whisk eggs, milk, salt, and pepper.\n2. Melt butter in a pan over medium-low heat.\n3. Pour in eggs and gently stir until cooked through.");
        insertIngredient(db, r1, "Egg", 2, "pcs");
        insertIngredient(db, r1, "Milk", 50, "ml");
        insertIngredient(db, r1, "Butter", 10, "g");
        insertIngredient(db, r1, "Salt", 1, "tsp");

        // Seed Recipe 2
        long r2 = insertRecipe(db, "Garlic Butter Pasta", "1. Boil pasta in salted water according to package directions.\n2. Melt butter in a skillet and saute minced garlic for 1 minute.\n3. Toss pasta with garlic butter, parmesan, and salt.");
        insertIngredient(db, r2, "Pasta", 200, "g");
        insertIngredient(db, r2, "Butter", 30, "g");
        insertIngredient(db, r2, "Garlic", 2, "cloves");
        insertIngredient(db, r2, "Parmesan Cheese", 25, "g");
        insertIngredient(db, r2, "Salt", 1, "tsp");

        // Seed Recipe 3
        long r3 = insertRecipe(db, "Tomato Basil Omelette", "1. Beat eggs with salt.\n2. Pour into oiled skillet.\n3. Top with chopped tomatoes and fresh basil, then fold and serve.");
        insertIngredient(db, r3, "Egg", 3, "pcs");
        insertIngredient(db, r3, "Tomato", 1, "pcs");
        insertIngredient(db, r3, "Basil", 5, "g");
        insertIngredient(db, r3, "Cooking Oil", 10, "ml");
        insertIngredient(db, r3, "Salt", 1, "tsp");

        // Seed Recipe 4
        long r4 = insertRecipe(db, "Simple Grilled Cheese", "1. Butter one side of each bread slice.\n2. Place cheddar between bread slices with butter facing out.\n3. Grill on medium heat until golden brown and cheese melts.");
        insertIngredient(db, r4, "Bread", 2, "slices");
        insertIngredient(db, r4, "Cheddar Cheese", 2, "slices");
        insertIngredient(db, r4, "Butter", 15, "g");

        // Seed Recipe 5
        long r5 = insertRecipe(db, "Pancake Stacks", "1. Whisk flour, sugar, and baking powder.\n2. Mix in egg, milk, and melted butter.\n3. Cook ladlefuls on a greased skillet until bubbly, flip and finish.");
        insertIngredient(db, r5, "Flour", 150, "g");
        insertIngredient(db, r5, "Milk", 200, "ml");
        insertIngredient(db, r5, "Egg", 1, "pcs");
        insertIngredient(db, r5, "Sugar", 20, "g");
        insertIngredient(db, r5, "Butter", 20, "g");

        // Seed Recipe 6
        long r6 = insertRecipe(db, "Rice and Fried Egg", "1. Heat cooked rice in a pan.\n2. Fry egg in cooking oil to desired crispiness.\n3. Drizzle soy sauce over rice and top with fried egg.");
        insertIngredient(db, r6, "Rice", 200, "g");
        insertIngredient(db, r6, "Egg", 1, "pcs");
        insertIngredient(db, r6, "Soy Sauce", 15, "ml");
        insertIngredient(db, r6, "Cooking Oil", 10, "ml");

        // Seed Recipe 7
        long r7 = insertRecipe(db, "Caprese Salad", "1. Slice tomatoes and mozzarella.\n2. Arrange alternating slices on a plate with fresh basil leaves.\n3. Drizzle with olive oil and season with salt.");
        insertIngredient(db, r7, "Tomato", 2, "pcs");
        insertIngredient(db, r7, "Mozzarella", 125, "g");
        insertIngredient(db, r7, "Basil", 10, "g");
        insertIngredient(db, r7, "Olive Oil", 15, "ml");
        insertIngredient(db, r7, "Salt", 1, "tsp");

        // Seed Recipe 8
        long r8 = insertRecipe(db, "Guacamole dip", "1. Mash avocados in a bowl.\n2. Stir in chopped onion, minced garlic, lemon juice, and salt until combined.");
        insertIngredient(db, r8, "Avocado", 2, "pcs");
        insertIngredient(db, r8, "Onion", 0.5, "pcs");
        insertIngredient(db, r8, "Garlic", 1, "cloves");
        insertIngredient(db, r8, "Lemon", 0.5, "pcs");
        insertIngredient(db, r8, "Salt", 1, "tsp");

        // Seed Recipe 9
        long r9 = insertRecipe(db, "French Toast", "1. Whisk egg, milk, and sugar together.\n2. Dip bread slices into mixture.\n3. Fry in buttered pan until golden on both sides.");
        insertIngredient(db, r9, "Bread", 3, "slices");
        insertIngredient(db, r9, "Egg", 1, "pcs");
        insertIngredient(db, r9, "Milk", 60, "ml");
        insertIngredient(db, r9, "Sugar", 10, "g");
        insertIngredient(db, r9, "Butter", 15, "g");

        // Seed Recipe 10
        long r10 = insertRecipe(db, "Garlic Fried Rice", "1. Saute minced garlic in cooking oil until golden.\n2. Add cooked rice and stir-fry over high heat.\n3. Season with soy sauce and salt.");
        insertIngredient(db, r10, "Rice", 250, "g");
        insertIngredient(db, r10, "Garlic", 3, "cloves");
        insertIngredient(db, r10, "Cooking Oil", 15, "ml");
        insertIngredient(db, r10, "Soy Sauce", 10, "ml");

        // Seed Recipe 11
        long r11 = insertRecipe(db, "Sauted Spinach and Garlic", "1. Heat olive oil in a pan.\n2. Add minced garlic and cook for 30 seconds.\n3. Add fresh spinach and cook until wilted. Season with salt.");
        insertIngredient(db, r11, "Spinach", 200, "g");
        insertIngredient(db, r11, "Garlic", 2, "cloves");
        insertIngredient(db, r11, "Olive Oil", 10, "ml");
        insertIngredient(db, r11, "Salt", 1, "tsp");

        // Seed Recipe 12
        long r12 = insertRecipe(db, "Butter Toast", "1. Toast bread slices until crisp.\n2. Spread butter evenly while warm.");
        insertIngredient(db, r12, "Bread", 2, "slices");
        insertIngredient(db, r12, "Butter", 10, "g");

        // Seed Recipe 13
        long r13 = insertRecipe(db, "Mashed Potatoes", "1. Boil chopped potatoes in salted water until tender.\n2. Drain water, add butter and milk.\n3. Mash until smooth and creamy.");
        insertIngredient(db, r13, "Potato", 3, "pcs");
        insertIngredient(db, r13, "Butter", 20, "g");
        insertIngredient(db, r13, "Milk", 50, "ml");
        insertIngredient(db, r13, "Salt", 1, "tsp");

        // Seed Recipe 14
        long r14 = insertRecipe(db, "Boiled Eggs", "1. Submerge eggs in boiling water.\n2. Cook for 7 minutes for soft-boiled or 10 minutes for hard-boiled.\n3. Transfer to ice water and peel.");
        insertIngredient(db, r14, "Egg", 2, "pcs");

        // Seed Recipe 15
        long r15 = insertRecipe(db, "Lemon Water Refresher", "1. Squeeze lemon juice into cold water.\n2. Add sugar and stir until fully dissolved.");
        insertIngredient(db, r15, "Lemon", 1, "pcs");
        insertIngredient(db, r15, "Water", 300, "ml");
        insertIngredient(db, r15, "Sugar", 15, "g");
    }

    private long insertRecipe(SQLiteDatabase db, String name, String instructions) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECIPE_NAME, name);
        cv.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);
        return db.insert(TABLE_RECIPES, null, cv);
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RI_RECIPE_ID, recipeId);
        cv.put(COLUMN_RI_ING_NAME, name);
        cv.put(COLUMN_RI_QTY, qty);
        cv.put(COLUMN_RI_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, cv);
    }
}