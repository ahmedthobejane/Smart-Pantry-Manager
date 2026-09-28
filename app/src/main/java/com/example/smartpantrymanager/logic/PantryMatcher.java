package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.model.Pantryitem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class PantryMatcher {

    public static class MatchResult {
        public List<Recipe> strictMatches = new ArrayList<>();
        public List<Recipe> almostThereMatches = new ArrayList<>(); // Missing exactly 1 ingredient
    }

    public static MatchResult evaluateRecipes(List<Pantryitem> pantry, List<Recipe> allRecipes) {
        MatchResult result = new MatchResult();

        for (Recipe recipe : allRecipes) {
            int missingCount = 0;
            boolean satisfiesQuantities = true;

            for (RecipeIngredient req : recipe.getIngredients()) {
                Pantryitem matchedItem = findMatchingPantryItem(req.getName(), pantry);

                if (matchedItem == null) {
                    missingCount++;
                } else {
                    double convertedPantryQty = convertUnit(matchedItem.getQuantity(), matchedItem.getUnit(), req.getUnit());
                    if (convertedPantryQty < req.getQuantity()) {
                        satisfiesQuantities = false;
                        missingCount++;
                    }
                }
            }

            // Strict Rule: Must miss 0 ingredients AND satisfy all quantities
            if (missingCount == 0 && satisfiesQuantities) {
                result.strictMatches.add(recipe);
            } else if (missingCount == 1) {
                result.almostThereMatches.add(recipe);
            }
        }
        return result;
    }

    private static Pantryitem findMatchingPantryItem(String reqName, List<Pantryitem> pantry) {
        String normReq = normalizeName(reqName);
        for (Pantryitem item : pantry) {
            String normPantry = normalizeName(item.getName());
            if (normReq.equals(normPantry)) {
                return item;
            }
        }
        return null;
    }

    // Normalization: handles lowercasing, stripping punctuation, basic plurals
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase();

        // Simple plural rules
        if (s.endsWith("es") && s.length() > 3) {
            if (s.endsWith("tomatoes")) return "tomato";
            if (s.endsWith("potatoes")) return "potato";
            return s.substring(0, s.length() - 2);
        } else if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 2) {
            if (s.endsWith("cloves")) return "clove";
            if (s.endsWith("slices")) return "slice";
            if (s.endsWith("pcs")) return "pc";
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    // Unit conversion helper
    private static double convertUnit(double val, String fromUnit, String toUnit) {
        String f = fromUnit.trim().toLowerCase();
        String t = toUnit.trim().toLowerCase();

        if (f.equals(t)) return val;

        // Mass: kg to g
        if ((f.equals("kg") || f.equals("kgs")) && t.equals("g")) return val * 1000.0;
        if (f.equals("g") && (t.equals("kg") || t.equals("kgs"))) return val / 1000.0;

        // Volume: l to ml
        if ((f.equals("l") || f.equals("liter") || f.equals("liters")) && t.equals("ml")) return val * 1000.0;
        if (t.equals("l") && f.equals("ml")) return val / 1000.0;

        // Teaspoon / Tablespoon
        if (f.equals("tbsp") && t.equals("tsp")) return val * 3.0;
        if (f.equals("tsp") && t.equals("tbsp")) return val / 3.0;

        // Default: return raw value if units are incompatible or non-standard
        return val;
    }
}