package com.example.smartpantrymanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.ui.AllRecipesFragment;
import com.example.smartpantrymanager.ui.PantryFragment;
import com.example.smartpantrymanager.ui.SettingsFragment;
import com.example.smartpantrymanager.ui.SuggestedRecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_pantry) {
                selected = new PantryFragment();
            } else if (itemId == R.id.nav_suggestions) {
                selected = new SuggestedRecipesFragment();
            } else if (itemId == R.id.nav_all_recipes) {
                selected = new AllRecipesFragment();
            } else if (itemId == R.id.nav_settings) {
                selected = new SettingsFragment();
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PantryFragment())
                    .commit();
        }
    }
}