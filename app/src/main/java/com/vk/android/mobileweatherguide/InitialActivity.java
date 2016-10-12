package com.vk.android.mobileweatherguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.inputmethod.InputMethodManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Arrays;
import android.content.Intent;

public class InitialActivity extends AppCompatActivity
{
    private CityInfo cityInfo;
    private int selectedLocationIndex = -1;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;
    private boolean isExitConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**** SETUP ****/

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        this.setContentView(R.layout.activity_initial);

        this.setCityInfo(new CityInfo(this)); // Instantiate cityInfo property.

        this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this)); // Instantiate applicationSharedPreferenceManager property.

        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();  // Hide action bar.
        }

        // Set navigation bar (Located at the bottom) color.
        this.getWindow().setNavigationBarColor(this.getResources().getColor(R.color.colorTeal, null));

        /**** END OF SETUP ****/

        if(this.getApplicationSharedPreferenceManager().validatePreferences()) // Check if preferences were previously set.
        {
            // If preferences (Location id and location display name) were previously set, start main activity.
            Intent explicitIntent = new Intent(this, MainActivity.class);

            this.startActivity(explicitIntent);

            this.finish(); // Shut down current activity.
        }

        if(this.getCityInfo().validateData()) // Ensure that data from cities.xml string arrays is fetched properly.
        {
            // Create new ArraysAdapter adapter that will use simple_dropdown_item_1line layout and contain data representing city names with corresponding country codes.
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, this.getCityInfo().getCityDisplayNames());

            // Get reference of AutoCompleteTextView with the id of citiesAutoCompleteField.
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.citiesAutoCompleteField);

            autoCompleteTextView.setThreshold(3); // Show results after at least 3 characters are entered into AutoCompleteTextView.

            autoCompleteTextView.setAdapter(arrayAdapter);

            // Add an onItemClick event listener to AutoCompleteTextView.
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(final AdapterView<?> parent, View view, int position, long id)
                {
                    // Get selected item index and store it in selectedLocationIndex property.
                    setSelectedLocationIndex(Arrays.asList(getCityInfo().getCityDisplayNames()).indexOf(parent.getItemAtPosition(position).toString()));

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InitialActivity.this); // Create new AlertDialog builder.

                    // Setup AlertDialog and show it.
                    alertDialogBuilder.setMessage("You selected '" + parent.getItemAtPosition(position).toString() + "' as your location. Please confirm your selection.")
                            .setPositiveButton("SELECT", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // When user clicks on positive button, store selected city in shared preferences and start main activity.
                                    if(InitialActivity.this.getSelectedLocationIndex() == -1)
                                    {
                                        // TODO: Decide on displaying the error message in ErrorActivity activity.

                                        InitialActivity.this.startErrorActivity();
                                    }

                                    // Write location id of selected city to shared preferences.
                                    getApplicationSharedPreferenceManager().setLocationId(getCityInfo().getCityIds()[InitialActivity.this.getSelectedLocationIndex()]);

                                    // Write location display name to shared preferences.
                                    getApplicationSharedPreferenceManager().setLocationDisplayName(getCityInfo().getCityDisplayNames()[InitialActivity.this.getSelectedLocationIndex()]);

                                    // Once preferences (Location id and location display name) are set, start main activity.
                                    Intent explicitIntent = new Intent(InitialActivity.this, MainActivity.class);

                                    startActivity(explicitIntent);

                                    finish(); // Shut down current activity.

                                }
                            })
                            .setNegativeButton("CHOOSE ANOTHER CITY", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // When user clicks on negative button, put focus on AutoCompleteTextView and display soft keyboard, so user can enter another city.
                                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) InitialActivity.this.findViewById(R.id.citiesAutoCompleteField);

                                    // Put focus on AutoCompleteTextView.
                                    autoCompleteTextView.requestFocus();

                                    InputMethodManager inputMethodManager = (InputMethodManager) InitialActivity.this.getSystemService(InitialActivity.INPUT_METHOD_SERVICE);

                                    // Display soft keyboard.
                                    inputMethodManager.showSoftInput(autoCompleteTextView, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }).create().show();
                }
            });
        }
        else // If data from cities.xml string arrays is NOT fetched properly, display an error.
        {
            // TODO: Decide on displaying the error message in ErrorActivity activity.

            this.startErrorActivity();
        }
    }

    @Override
    protected void onStart() // This method is called when the activity is about to become visible.
    {
        super.onStart();

        if(this.getCityInfo() == null) // If cityInfo property is not set, set it.
        {
            this.setCityInfo(new CityInfo(this));
        }

        if(this.getApplicationSharedPreferenceManager() == null) // If applicationSharedPreferenceManager property is not set, set it.
        {
            this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this));
        }
    }

    @Override
    protected void onStop() // This method is called when the activity is no longer visible.
    {
        super.onStop();

        this.setCityInfo(null); // Unset cityInfo property.

        this.setApplicationSharedPreferenceManager(null); // Unset applicationSharedPreferenceManager property.
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        // When user presses the back button on their device, display confirmation dialog. Once user confirms that he/she wants to exit, exit the application.
        if(!this.isExitConfirmed())
        {
            // Setup AlertDialog and show it.
            new AlertDialog.Builder(InitialActivity.this).setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            InitialActivity.this.setExitConfirmed(true);

                            InitialActivity.this.onBackPressed(); // Invoke onBackPressed() again to exit the application.
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) { }
                    }).create().show();
        }
        else
        {
            super.onBackPressed();

            // Apply exit animation.
            this.overridePendingTransition(R.anim.activity_exit_animation_in, R.anim.activity_exit_animation_out);
        }
    }

    // Getters and setters for each property of InitialActivity class.
    private void setCityInfo(CityInfo cityInfo)
    {
        this.cityInfo = cityInfo;
    }

    private CityInfo getCityInfo()
    {
        return this.cityInfo;
    }

    private int getSelectedLocationIndex()
    {
        return this.selectedLocationIndex;
    }

    private void setSelectedLocationIndex(int index)
    {
        this.selectedLocationIndex = index;
    }

    private ApplicationSharedPreferenceManager getApplicationSharedPreferenceManager()
    {
        return this.applicationSharedPreferenceManager;
    }

    private void setApplicationSharedPreferenceManager(ApplicationSharedPreferenceManager applicationSharedPreferenceManager)
    {
        this.applicationSharedPreferenceManager = applicationSharedPreferenceManager;
    }

    public boolean isExitConfirmed()
    {
        return this.isExitConfirmed;
    }

    public void setExitConfirmed(boolean isExitConfirmed)
    {
        this.isExitConfirmed = isExitConfirmed;
    }

    // startErrorActivity method start ErrorActivity activity.
    private void startErrorActivity()
    {
        // TODO: Decide on displaying the error message in ErrorActivity activity.

        Intent explicitIntent = new Intent(this, ErrorActivity.class);

        this.startActivity(explicitIntent);

        this.finish(); // Shut down current activity.
    }
}
