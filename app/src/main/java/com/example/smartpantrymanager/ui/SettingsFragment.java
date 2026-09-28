package com.example.smartpantrymanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.R;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchAlerts;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        switchAlerts = v.findViewById(R.id.switch_expiry_alerts);

        prefs = getActivity().getSharedPreferences("PantryPrefs", Context.MODE_PRIVATE);
        switchAlerts.setChecked(prefs.getBoolean("KEY_EXPIRY_ALERTS", true));

        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("KEY_EXPIRY_ALERTS", isChecked).apply();
        });

        return v;
    }
}