package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Switch switchExpiryAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Connect to the SQLite database.
        databaseHelper = new DatabaseHelper(this);

        // Find the expiry alerts switch.
        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        // Find the Back to My Pantry button.
        Button buttonBackToPantry =
                findViewById(R.id.buttonBackToPantryFromSettings);

        // Load the saved expiry alert preference.
        switchExpiryAlerts.setChecked(
                databaseHelper.areExpiryAlertsEnabled()
        );

        // Save the preference whenever the switch changes.
        switchExpiryAlerts.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton buttonView,
                            boolean isChecked) {

                        databaseHelper.setExpiryAlertsEnabled(isChecked);
                    }
                }
        );

        // Return to the pantry screen.
        buttonBackToPantry.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the database connection.
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
