package com.example.chatapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;

public class SettingsFragment extends Fragment {

    private ToggleButton toggleButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "prefs";
    private static final String DARK_MODE_KEY = "dark_mode";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        toggleButton = view.findViewById(R.id.themeToggleButton);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);

        // Load the saved state and set the mode accordingly
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        setDarkMode(isDarkMode);
        toggleButton.setChecked(isDarkMode);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked);
            saveDarkModeState(isChecked);
        });

        return view;
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void saveDarkModeState(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();
    }
}
