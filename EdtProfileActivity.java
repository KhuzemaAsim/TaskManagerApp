package com.example.taskmanager;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EdtProfileActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputPhone;
    private Spinner spinnerGender;
    private Button btnDobSelect, btnSaveProfile, btnGoBack;
    private SharedPreferences userPrefs;
    private Calendar birthDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        inputFirstName = findViewById(R.id.edit_first_name_input);
        inputLastName = findViewById(R.id.edit_last_name_input);
        inputPhone = findViewById(R.id.edit_phone_input);
        spinnerGender = findViewById(R.id.edit_gender_spinner);
        btnDobSelect = findViewById(R.id.edit_dob_button);
        btnSaveProfile = findViewById(R.id.save_profile_button);
        btnGoBack = findViewById(R.id.back_button);

        birthDateCalendar = Calendar.getInstance();

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        String savedFirstName = userPrefs.getString("first_name", "Not set");
        String savedLastName = userPrefs.getString("last_name", "Not set");
        String savedPhone = userPrefs.getString("phone", "Not set");
        String savedGender = userPrefs.getString("gender", "Not set");
        String savedDob = userPrefs.getString("dob", "Not set");

        inputFirstName.setText("Not set".equals(savedFirstName) ? "" : savedFirstName);
        inputLastName.setText("Not set".equals(savedLastName) ? "" : savedLastName);
        inputPhone.setText("Not set".equals(savedPhone) ? "" : savedPhone);

        if (!"Not set".equals(savedGender)) {
            int selectedPos = genderAdapter.getPosition(savedGender);
            spinnerGender.setSelection(selectedPos);
        }

        btnDobSelect.setText("Not set".equals(savedDob) ? "What is your date of birth?" : savedDob);

        btnDobSelect.setOnClickListener(view -> {
            DatePickerDialog picker = new DatePickerDialog(
                    this,
                    (dateView, year, month, day) -> {
                        birthDateCalendar.set(year, month, day);
                        String dobFormatted = String.format("%02d/%02d/%d", day, month + 1, year);
                        btnDobSelect.setText(dobFormatted);
                    },
                    birthDateCalendar.get(Calendar.YEAR),
                    birthDateCalendar.get(Calendar.MONTH),
                    birthDateCalendar.get(Calendar.DAY_OF_MONTH)
            );

            picker.show();
        });

        btnSaveProfile.setOnClickListener(view -> {
            String fName = inputFirstName.getText().toString().trim();
            String lName = inputLastName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            String dob = btnDobSelect.getText().toString();

            if (fName.isEmpty()) {
                Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("What is your date of birth?".equals(dob)) {
                Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString("first_name", fName);
            editor.putString("last_name", lName);
            editor.putString("phone", phone);
            editor.putString("gender", gender);
            editor.putString("dob", dob);
            editor.apply();

            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();

            finish();
        });

        btnGoBack.setOnClickListener(view -> finish());
    }
}
