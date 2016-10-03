package com.vk.android.mobileweatherguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private ApplicationDrawerNavigationManager applicationDrawerNavigationManager;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**** SETUP ****/

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        this.setContentView(R.layout.activity_main);

        this.setTitle(this.getResources().getString(R.string.drawer_current_weather)); // Set activity title.

        this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar())); // Set drawer navigation.

        this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this)); // Instantiate applicationSharedPreferenceManager property.

        /**** END OF SETUP ****/

        if(!this.getApplicationSharedPreferenceManager().validatePreferences()) // Check if preferences were previously set.
        {
            // If preferences (Location id and location display name) were previously set, start initial activity.
            Intent explicitIntent = new Intent(this, InitialActivity.class);

            this.startActivity(explicitIntent);

            this.finish(); // Shut down current activity.
        }
    }

    @Override
    protected void onStart() // This method is called when the activity is about to become visible.
    {
        super.onStart();

        if(this.getApplicationDrawerNavigationManager() == null) // If applicationDrawerNavigationManager property is not set, set it.
        {
            this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar()));
        }

        if(this.getApplicationSharedPreferenceManager() == null) // If applicationSharedPreferenceManager property is not set, set it.
        {
            this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this));
        }

        if(WeatherInfo.getInstance() == null || WeatherInfo.getInstance().getCurrentWeather() == null)
        {
            String url = new StringBuilder().append(this.getString(R.string.current_weather_url)).append(this.getApplicationSharedPreferenceManager().getLocationId()).append(this.getString(R.string.url_api_key)).toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) // This event listener listens for success response of above instantiated JSON request.
                {
                    Gson gson = new Gson(); // Create an instance of Gson class for converting JSON object to Java class.

                    WeatherInfo weather = WeatherInfo.getInstance();

                    weather.setCurrentWeather(gson.fromJson(jsonObject.toString(), WeatherInfo.CurrentWeather.class)); // Deserialize JSON representation into Weather.CurrentWeather class.

                    // Since Open Weather Map Api returns rain and snow in the last 3 hours with "3h" key and "3h" naming is not supported
                    // in Java, code below handles the mapping of rain and snow properties.
                    JsonObject result = gson.fromJson(jsonObject.toString(), JsonObject.class);


                    if (result.get("rain") != null) // Check if "rain" key exists.
                    {
                        if(result.getAsJsonObject("rain").get("3h") != null)
                        {
                            weather.getCurrentWeather().getRain().setLast3h(result.getAsJsonObject("rain").get("3h").getAsDouble()); // Map the value of "3h" key to Rain's "last3h" property.
                        }

                        if(result.getAsJsonObject("rain").get("1h") != null)
                        {
                            weather.getCurrentWeather().getRain().setLast1h(result.getAsJsonObject("rain").get("1h").getAsDouble()); // Map the value of "1h" key to Rain's "last1h" property.
                        }
                    }

                    if (result.get("snow") != null) // Check if "snow" key exists.
                    {
                        if(result.getAsJsonObject("snow").get("3h") != null)
                        {
                            weather.getCurrentWeather().getSnow().setLast3h(result.getAsJsonObject("snow").get("3h").getAsDouble()); // Map the value of "3h" key to Snow's "last3h" property.
                        }

                        if(result.getAsJsonObject("snow").get("1h") != null)
                        {
                            weather.getCurrentWeather().getSnow().setLast1h(result.getAsJsonObject("snow").get("1h").getAsDouble()); // Map the value of "1h" key to Snow's "last1h" property.
                        }
                    }

                    // TODO: Finish weather information delivery logic.

                    String responseText = jsonObject.toString();

                    displayToast(responseText);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) // This event listener listens for error response of above instantiated JSON request.
                {
                    // TODO: Decide on displaying the error message in ErrorActivity activity.

                    displayToast(volleyError.getMessage());

                    MainActivity.this.startErrorActivity();
                }
            });

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
        else
        {
            displayToast(WeatherInfo.getInstance().getCurrentWeather().getName());
        }

    }

    @Override
    protected void onStop() // This method is called when the activity is no longer visible.
    {
        super.onStop();

        if(this.getApplicationDrawerNavigationManager() != null)
        {
            this.setApplicationDrawerNavigationManager(null); // Unset applicationDrawerNavigationManager property.
        }

        if(this.getApplicationSharedPreferenceManager() != null)
        {
            this.setApplicationSharedPreferenceManager(null); // Unset applicationSharedPreferenceManager property.
        }

        if(RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue() != null)
        {
            RequestQueueSingleton.getInstance(this.getApplicationContext()).setRequestQueue(null); // Unset request queue property.
        }
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        super.onBackPressed();

        // Apply exit animation.
        this.overridePendingTransition(R.anim.activity_exit_animation_in, R.anim.activity_exit_animation_out);
    }

    private ApplicationSharedPreferenceManager getApplicationSharedPreferenceManager()
    {
        return this.applicationSharedPreferenceManager;
    }

    private void setApplicationSharedPreferenceManager(ApplicationSharedPreferenceManager applicationSharedPreferenceManager)
    {
        this.applicationSharedPreferenceManager = applicationSharedPreferenceManager;
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
            case R.id.menu_item_location:
                explicitIntent = new Intent(this, LocationActivity.class); // If right side menu Location menu item is selected, start LocationActivity activity.

                this.startActivity(explicitIntent);

                return true;

            case R.id.menu_item_notification:
                explicitIntent = new Intent(this, NotificationActivity.class); // If right side menu Notifications menu item is selected, start NotificationActivity activity.

                this.startActivity(explicitIntent);

                return true;

            default:
                // When action bar hamburger menu item is selected (Left side), pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
                return (this.getApplicationDrawerNavigationManager().getActionBarDrawerToggle().onOptionsItemSelected(item) && super.onOptionsItemSelected(item));
        }

    }

    /*********************************** END OF NAVIGATION METHODS ***********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) // This event listener only called once, when the menu is first displayed, and it is used to initialize Activity's standard options menu.
    {
        // Inflate menu defined in app_menu.xml
        this.getMenuInflater().inflate(R.menu.app_menu, menu);

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
        Intent explicitIntent = new Intent(this, ErrorActivity.class);

        this.startActivity(explicitIntent);

        this.finish(); // Shut down current activity.
    }
}
