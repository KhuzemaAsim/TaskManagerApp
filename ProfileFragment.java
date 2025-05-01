package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView fNameText, lNameText, phoneText, genderText, dobText;
    private Switch themeToggle;
    private ImageView profilePic;
    private Button editProfileBtn;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // SharedPreferences setup
        prefs = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);

        // UI references
        fNameText = root.findViewById(R.id.first_name_display);
        lNameText = root.findViewById(R.id.last_name_display);
        phoneText = root.findViewById(R.id.phone_display);
        genderText = root.findViewById(R.id.gender_display);
        dobText = root.findViewById(R.id.dob_display);
        themeToggle = root.findViewById(R.id.dark_mode_switch);
        editProfileBtn = root.findViewById(R.id.update_profile_button);

        refreshUserInfo();

        applyTheme(prefs.getBoolean("dark_mode", false));

        themeToggle.setOnCheckedChangeListener((CompoundButton btn, boolean isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme(isChecked);
        });

        editProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EdtProfileActivity.class));
        });

        return root;
    }

    private void refreshUserInfo() {
        String fname = prefs.getString("first_name", "N/A");
        String lname = prefs.getString("last_name", "N/A");
        String phone = prefs.getString("phone", "N/A");
        String gender = prefs.getString("gender", "N/A");
        String dob = prefs.getString("dob", "N/A");

        fNameText.setText("First Name: " + fname);
        lNameText.setText("Last Name: " + lname);
        phoneText.setText("Phone: " + phone);
        genderText.setText("Gender: " + gender);
        dobText.setText("Date of Birth: " + dob);

        themeToggle.setChecked(prefs.getBoolean("dark_mode", false));
    }

    private void applyTheme(boolean darkEnabled) {
        if (darkEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserInfo();
    }
}
