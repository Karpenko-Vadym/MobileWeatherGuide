package com.vk.android.mobileweatherguide;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import java.util.Date;
import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

public class InitialActivity extends AppCompatActivity
{
    private static final int ACCESS_LOCATION_PERMISSION = 100001; // Identifier for access precise location (ACCESS_FINE_LOCATION) permission.

    private CityInfo cityInfo;
    private int selectedLocationIndex = -1;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;
    private boolean isExitConfirmed;
    private Location location;

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

    public void setCurrentLocation(View view) // setCurrentLocation method is triggered by USE DEVICE LOCATION button in activity_location.xml file.
    {
        // First check if application has a permission to access precise location (ACCESS_FINE_LOCATION).
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // If application has permission to access precise location (ACCESS_FINE_LOCATION), obtain LocationManager using getSystemService(Context.LOCATION_SERVICE)
            // and check if GPS provider is enabled or if Network provider is enabled.
            if(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                // If GPS provider is enabled, retrieve last known location and set it to location property.
                this.setLocation(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            else if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                // If GPS provider is not enabled, check if Network provider is enabled, if so, retrieve last known location and set it to location property.
                this.setLocation(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
            else
            {
                // If none of the providers are enabled, inform the user.

                // TODO: Decide on displaying the error message in ErrorActivity activity.
                this.startErrorActivity();
            }

            // Check whether location was set.
            if(this.getLocation() != null)
            {
                // If location is set, proceed with update location flow.
                this.processLocation();
            }
            else
            {
                // Otherwise, inform the user that no previously set location was detected.

                // TODO: Decide on displaying the error message in ErrorActivity activity.
                this.startErrorActivity();
            }
        }
        else
        {
            // If not, request permission from user. Flow will continue in onRequestPermissionsResult() listener.
            // Use ACCESS_LOCATION_PERMISSION value for determining right permission in onRequestPermissionsResult() listener.
            this.requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, ACCESS_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        // Once user responded to permission request, proceed on flow execution depending on user choice.
        switch (requestCode)
        {
            // Ensure that permission to access precise location (Defined by ACCESS_LOCATION_PERMISSION value) was addressed.
            case ACCESS_LOCATION_PERMISSION:
            {
                // Check grant results for corresponding permission. If grant results is PERMISSION_GRANTED (0), proceed with retrieval of last known location,
                // otherwise, do nothing (Allow user to select location manually).
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Double check if application has a permission to access precise location (ACCESS_FINE_LOCATION).
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        // If application has permission to access precise location (ACCESS_FINE_LOCATION), obtain LocationManager using getSystemService(Context.LOCATION_SERVICE)
                        // and check if GPS provider is enabled or if Network provider is enabled.
                        if(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
                        {
                            // If GPS provider is enabled, retrieve last known location and set it to location property.
                            this.setLocation(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER));
                        }
                        else if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                        {
                            // If GPS provider is not enabled, check if Network provider is enabled, if so, retrieve last known location and set it to location property.
                            this.setLocation(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                        }
                        else
                        {
                            // If none of the providers are enabled, inform the user.

                            // TODO: Decide on displaying the error message in ErrorActivity activity.
                            this.startErrorActivity();
                        }

                        // Check whether location was set.
                        if(this.getLocation() != null)
                        {
                            // If location is set, proceed with update location flow.
                            this.processLocation();
                        }
                        else
                        {
                            // Otherwise, inform the user that no previously set location was detected.

                            // TODO: Decide on displaying the error message in ErrorActivity activity.
                            this.startErrorActivity();
                        }
                    }
                }
            }
        }
    }

    private void processLocation()
    {
        // Once last known location is identified, application will make a request to retrieve current weather for the new location using last known latitude and longitude.
        String url = this.getString(R.string.current_weather_url_lat_lon) + this.getLocation().getLatitude() + "&lon=" + this.getLocation().getLongitude() + "&" + this.getString(R.string.url_api_key);

        JsonObjectRequest jsonCurrentHourlyWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonObject)
                    {
                        Gson gson = new Gson();

                        WeatherInfo weather = WeatherInfo.getInstance();

                        JsonObject result = gson.fromJson(jsonObject.toString(), JsonObject.class);

                        if(result.get("weather") != null)
                        {
                            // Upon successful response, set current weather object, and write location id and location display name to shared preferences.
                            weather.setCurrentWeather(gson.fromJson(jsonObject.toString(), WeatherInfo.CurrentWeather.class)); // Deserialize JSON representation into Weather.CurrentWeather class.

                            if (result.get("rain") != null) // Check if "rain" key exists.
                            {
                                if (result.getAsJsonObject("rain").get("3h") != null) {
                                    weather.getCurrentWeather().getRain().setLast3h(result.getAsJsonObject("rain").get("3h").getAsDouble()); // Map the value of "3h" key to Rain's "last3h" property.
                                }

                                if (result.getAsJsonObject("rain").get("1h") != null) {
                                    weather.getCurrentWeather().getRain().setLast1h(result.getAsJsonObject("rain").get("1h").getAsDouble()); // Map the value of "1h" key to Rain's "last1h" property.
                                }
                            }

                            if (result.get("snow") != null) // Check if "snow" key exists.
                            {
                                if (result.getAsJsonObject("snow").get("3h") != null) {
                                    weather.getCurrentWeather().getSnow().setLast3h(result.getAsJsonObject("snow").get("3h").getAsDouble()); // Map the value of "3h" key to Snow's "last3h" property.
                                }

                                if (result.getAsJsonObject("snow").get("1h") != null) {
                                    weather.getCurrentWeather().getSnow().setLast1h(result.getAsJsonObject("snow").get("1h").getAsDouble()); // Map the value of "1h" key to Snow's "last1h" property.
                                }
                            }

                            weather.getCurrentWeather().setTimestamp(new Date()); // Record when current object was last updated.

                            if(WeatherInfo.getInstance().getCurrentWeather().getId() != null && WeatherInfo.getInstance().getCurrentWeather().getName() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getCountry() != null) {
                                // Write location id of the city to shared preferences.
                                InitialActivity.this.getApplicationSharedPreferenceManager().setLocationId(WeatherInfo.getInstance().getCurrentWeather().getId());

                                // Write location display name to shared preferences.
                                InitialActivity.this.getApplicationSharedPreferenceManager().setLocationDisplayName(WeatherInfo.getInstance().getCurrentWeather().getName().concat(", " + WeatherInfo.getInstance().getCurrentWeather().getSys().getCountry()));

                                // Once preferences (Location id and location display name) are set, start main activity.
                                Intent explicitIntent = new Intent(InitialActivity.this, MainActivity.class);

                                startActivity(explicitIntent);

                                finish(); // Shut down current activity.
                            }
                            else
                            {
                                // If some of the required properties were not set properly, inform the user.

                                // TODO: Decide on displaying the error message in ErrorActivity activity.
                                InitialActivity.this.startErrorActivity();
                            }
                        }
                        else
                        {
                            // If no weather object was delivered with the response, inform the user.

                            // TODO: Decide on displaying the error message in ErrorActivity activity.
                            InitialActivity.this.startErrorActivity();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // TODO: Decide on displaying the error message in ErrorActivity activity.
                        InitialActivity.this.startErrorActivity();

                    }
                });

        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentHourlyWeatherRequest);
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

    public Location getLocation()
    {
        return this.location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
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
