package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerPantry;
    private PantryAdapter pantryAdapter;
    private DatabaseHelper databaseHelper;

    private LinearLayout layoutExpiryWarning;
    private TextView textExpiryWarningItems;

    private ArrayList<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedRecipesIfNeeded();

        recyclerPantry = findViewById(R.id.recyclerPantry);
        layoutExpiryWarning = findViewById(R.id.layoutExpiryWarning);
        textExpiryWarningItems = findViewById(R.id.textExpiryWarningItems);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        pantryItems = databaseHelper.getAllPantryItems();
        pantryAdapter = new PantryAdapter(pantryItems);
        recyclerPantry.setAdapter(pantryAdapter);

        Button buttonAddIngredient =
                findViewById(R.id.buttonAddIngredient);

        Button buttonSuggestedRecipes =
                findViewById(R.id.buttonSuggestedRecipes);

        Button buttonSettings =
                findViewById(R.id.buttonSettings);

        buttonAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });

        buttonSuggestedRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && pantryAdapter != null) {
            ArrayList<PantryItem> updatedItems =
                    databaseHelper.getAllPantryItems();

            pantryAdapter.updateData(updatedItems);
            updateExpiryWarning(updatedItems);
        }
    }

    private void updateExpiryWarning(ArrayList<PantryItem> items) {

        // Hide the warning if expiry alerts are disabled.
        if (!databaseHelper.areExpiryAlertsEnabled()) {
            layoutExpiryWarning.setVisibility(View.GONE);
            return;
        }

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar warningLimit = (Calendar) today.clone();
        warningLimit.add(Calendar.DAY_OF_YEAR, 3);

        SimpleDateFormat inputFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        inputFormat.setLenient(false);

        SimpleDateFormat displayFormat =
                new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        StringBuilder warningText = new StringBuilder();
        int warningCount = 0;

        for (PantryItem item : items) {

            String expiryDateText = item.getExpiryDate();

            if (expiryDateText == null || expiryDateText.trim().isEmpty()) {
                continue;
            }

            try {
                Date parsedDate = inputFormat.parse(expiryDateText.trim());

                if (parsedDate == null) {
                    continue;
                }

                Calendar expiryDate = Calendar.getInstance();
                expiryDate.setTime(parsedDate);
                clearTime(expiryDate);

                // Ignore ingredients expiring more than three days from now.
                if (expiryDate.after(warningLimit)) {
                    continue;
                }

                long differenceMillis =
                        expiryDate.getTimeInMillis() - today.getTimeInMillis();

                long daysLeft = Math.round(
                        differenceMillis / (1000.0 * 60 * 60 * 24)
                );

                if (warningCount > 0) {
                    warningText.append("\n\n");
                }

                warningText.append("🌸 ")
                        .append(item.getName())
                        .append("\n");

                warningText.append("Expires: ")
                        .append(displayFormat.format(expiryDate.getTime()))
                        .append("\n");

                if (daysLeft < 0) {
                    long daysExpired = Math.abs(daysLeft);

                    warningText.append("💗 Expired ")
                            .append(daysExpired)
                            .append(daysExpired == 1
                                    ? " day ago"
                                    : " days ago");

                } else if (daysLeft == 0) {
                    warningText.append("💕 Expires today!");

                } else {
                    warningText.append("✨ ")
                            .append(daysLeft)
                            .append(daysLeft == 1
                                    ? " day left"
                                    : " days left");
                }

                warningCount++;

            } catch (ParseException e) {
                // Ignore dates that are not in yyyy-MM-dd format.
            }
        }

        if (warningCount > 0) {
            textExpiryWarningItems.setText(warningText.toString());
            layoutExpiryWarning.setVisibility(View.VISIBLE);
        } else {
            layoutExpiryWarning.setVisibility(View.GONE);
        }
    }

    // Reset the time to midnight so we compare calendar dates only.
    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

