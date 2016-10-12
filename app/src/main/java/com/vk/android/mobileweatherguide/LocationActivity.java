package com.vk.android.mobileweatherguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.TextView;
import java.util.Arrays;

public class LocationActivity extends AppCompatActivity
{
    private CityInfo cityInfo;
    private int selectedLocationIndex = -1;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;
    private ApplicationDrawerNavigationManager applicationDrawerNavigationManager;
    private boolean isExitConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**** SETUP ****/

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        this.setContentView(R.layout.activity_location);

        this.setTitle(this.getResources().getString(R.string.menu_location)); // Set activity title.

        this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar()));  // Set drawer navigation.

        this.setCityInfo(new CityInfo(this)); // Instantiate cityInfo property.

        this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this)); // Instantiate applicationSharedPreferenceManager property.

        /**** END OF SETUP ****/

        if(this.getApplicationSharedPreferenceManager().validatePreferences()) // Validate that preferences (Location id and location display name) are set.
        {
            if(this.getCityInfo().validateData()) // Ensure that data from cities.xml string arrays is fetched properly.
            {
                // Get reference of TextView with the id of location_current_location.
                TextView textView = (TextView) this.findViewById(R.id.location_current_location);

                // Set text of textView to "Current Location: <Location Display Name>", so that current location is reflected.
                textView.setText(String.format("Current Location: %s", this.getApplicationSharedPreferenceManager().getLocationDisplayName()));

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
                        LocationActivity.this.setSelectedLocationIndex(Arrays.asList(LocationActivity.this.getCityInfo().getCityDisplayNames()).indexOf(parent.getItemAtPosition(position).toString()));

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationActivity.this); // Create new AlertDialog builder.

                        // Setup AlertDialog and show it.
                        alertDialogBuilder.setMessage("You selected '" + parent.getItemAtPosition(position).toString() + "' as your location. Please confirm your selection.")
                                .setPositiveButton("SELECT", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        // When user clicks on positive button, store selected city in shared preferences and start main activity.
                                        if(LocationActivity.this.getSelectedLocationIndex() == -1)
                                        {
                                            // TODO: Decide on displaying the error message in ErrorActivity activity.

                                            LocationActivity.this.startErrorActivity();
                                        }

                                        // Write location id of selected city to shared preferences.
                                        LocationActivity.this.getApplicationSharedPreferenceManager().setLocationId(getCityInfo().getCityIds()[LocationActivity.this.getSelectedLocationIndex()]);

                                        // Write location display name to shared preferences.
                                        LocationActivity.this.getApplicationSharedPreferenceManager().setLocationDisplayName(getCityInfo().getCityDisplayNames()[LocationActivity.this.getSelectedLocationIndex()]);

                                        // Once preferences (Location id and location display name) are set, start main activity.
                                        Intent explicitIntent = new Intent(LocationActivity.this, MainActivity.class);

                                        startActivity(explicitIntent);

                                        finish(); // Shut down current activity.

                                    }
                                })
                                .setNegativeButton("CHOOSE ANOTHER CITY", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        // When user clicks on negative button, put focus on AutoCompleteTextView and display soft keyboard, so user can enter another city.
                                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) LocationActivity.this.findViewById(R.id.citiesAutoCompleteField);

                                        // Put focus on AutoCompleteTextView.
                                        autoCompleteTextView.requestFocus();

                                        InputMethodManager inputMethodManager = (InputMethodManager) LocationActivity.this.getSystemService(LocationActivity.INPUT_METHOD_SERVICE);

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
        else
        {
            // If not set start InitialActivity activity.
            Intent explicitIntent = new Intent(this, InitialActivity.class);

            this.startActivity(explicitIntent);

            this.finish(); // Shut down current activity.
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

        if(this.getApplicationDrawerNavigationManager() == null) // If applicationDrawerNavigationManager property is not set, set it.
        {
            this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar()));
        }

        if(this.isExitConfirmed())
        {
            this.setExitConfirmed(false);
        }
    }

    @Override
    protected void onStop() // This method is called when the activity is no longer visible.
    {
        super.onStop();

        this.setCityInfo(null); // Unset cityInfo property.

        this.setApplicationSharedPreferenceManager(null); // Unset applicationSharedPreferenceManager property.

        this.setApplicationDrawerNavigationManager(null); // Unset applicationDrawerNavigationManager property.
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        // When user presses the back button on their device, display confirmation dialog. Once user confirms that he/she wants to exit, exit the application.
        if(!this.isExitConfirmed())
        {
            // Setup AlertDialog and show it.
            new AlertDialog.Builder(LocationActivity.this).setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            LocationActivity.this.setExitConfirmed(true);

                            LocationActivity.this.onBackPressed(); // Invoke onBackPressed() again to exit the application.
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

    /*********************************** NAVIGATION METHODS ***********************************/
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Synchronize the indicator with the state of DrawerLayout by calling syncState() from onPostCreate method.
        this.getApplicationDrawerNavigationManager().getActionBarDrawerToggle().syncState();
    }

    // Setter and getter for applicationDrawerNavigationManager property.
    private void setApplicationDrawerNavigationManager(ApplicationDrawerNavigationManager applicationDrawerNavigationManager)
    {
        this.applicationDrawerNavigationManager = applicationDrawerNavigationManager;
    }

    private ApplicationDrawerNavigationManager getApplicationDrawerNavigationManager()
    {
        return this.applicationDrawerNavigationManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // This listener listens for menu selection on action bar.
    {
        Intent explicitIntent;

        switch (item.getItemId()) // Determine which menu and menu item is selected.
        {
            case R.id.menu_item_notification:
                explicitIntent = new Intent(this, NotificationActivity.class); // If right side menu Notifications menu item is selected, start NotificationActivity activity.

                this.startActivity(explicitIntent);

                this.finish();

                return true;

            default:
                // When action bar hamburger menu item is selected (Left side), pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event.
                return (this.getApplicationDrawerNavigationManager().getActionBarDrawerToggle().onOptionsItemSelected(item) && super.onOptionsItemSelected(item));
        }
    }

    /*********************************** END OF NAVIGATION METHODS ***********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) // This event listener only called once, when the menu is first displayed, and it is used to initialize Activity's standard options menu.
    {
        // Inflate menu defined in app_menu.xml
        this.getMenuInflater().inflate(R.menu.app_menu_location, menu);

        return true;
    }

    // displayToast method displays a toast with a message provided as a parameter. Toast is mostly used for testing purposes.
    private void displayToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
