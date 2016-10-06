package com.vk.android.mobileweatherguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        if(WeatherInfo.getInstance().getCurrentWeather() == null || WeatherInfo.getInstance().getCurrentWeather().getUvIndex() == null)
        {
            // If any of required information is missing, send a request to fetch required information.
            String url = this.getString(R.string.current_weather_url) + this.getApplicationSharedPreferenceManager().getLocationId() + "&" + this.getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, this.getResponseListener(), this.getErrorListener());

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentWeatherRequest);
        }
        else
        {
            MainActivity.this.populateContent(); // If required information is present, display it.
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

    private Response.Listener<JSONObject> getResponseListener()
    {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) // This event listener listens for success response.
            {
                Gson gson = new Gson();

                WeatherInfo weather = WeatherInfo.getInstance();

                // Since Open Weather Map Api returns rain and snow in the last 3 hours with "3h" key and "3h" naming is not supported
                // in Java, code below handles the mapping of rain and snow properties as well as UV Index.
                JsonObject result = gson.fromJson(jsonObject.toString(), JsonObject.class);

                if(result.get("weather") != null)
                {
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

                    // Send a request to fetch UV Index data.
                    String url = MainActivity.this.getString(R.string.current_uvindex_url) + Math.round(WeatherInfo.getInstance().getCurrentWeather().getCoord().getLat()) + "," + Math.round(WeatherInfo.getInstance().getCurrentWeather().getCoord().getLon()) + "/current.json?" + MainActivity.this.getString(R.string.url_api_key);

                    JsonObjectRequest jsonCurrentUVIndexRequest = new JsonObjectRequest(Request.Method.GET, url, null, MainActivity.this.getResponseListener(), MainActivity.this.getErrorListener());

                    RequestQueueSingleton.getInstance(MainActivity.this.getApplicationContext()).addToRequestQueue(jsonCurrentUVIndexRequest);
                }
                else if(result.get("data") != null)
                {
                    weather.getCurrentWeather().setUvIndex(gson.fromJson(jsonObject.toString(), WeatherInfo.CurrentWeather.UVI.class)); // Deserialize JSON representation into Weather.CurrentWeather.UVI class.

                    // TODO: Add forecast api call here. URI: http://api.openweathermap.org/data/2.5/forecast/daily?id=6167865&appid=d9d8bec8ab1806f5096d7734167da78e

                    MainActivity.this.populateContent();
                }
                else
                {
                    MainActivity.this.startErrorActivity();
                }
            }
        };
    }

    private Response.ErrorListener getErrorListener()
    {
        return new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError) // This event listener listens for error response.
            {
                // TODO: Decide on displaying the error message in ErrorActivity activity.

                displayToast(volleyError.getMessage());

                MainActivity.this.startErrorActivity();
            }
        };
    }

    private void populateContent() // This method populates all the fields.
    {
        if(WeatherInfo.getInstance().getCurrentWeather() != null) // Validate that CurrentWeather object has been set, if so, proceed. Otherwise, display an error.
        {
            // Validate value for each field and set each field.

            // Set current location.
            if (this.getApplicationSharedPreferenceManager().getLocationDisplayName() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_current_location)).setText(this.getApplicationSharedPreferenceManager().getLocationDisplayName());
            }

            // Set current temperature value.
            if (WeatherInfo.getInstance().getCurrentWeather().getMain() != null && WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp())), (char) 0x00B0));
            }

            // Set high temperature value.
            if(WeatherInfo.getInstance().getCurrentWeather().getMain() != null && WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp_max() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_high_temperature)).setText(String.format(Locale.CANADA, "HI: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp_max())), (char) 0x00B0));
            }

            // Set low temperature value.
            if(WeatherInfo.getInstance().getCurrentWeather().getMain() != null && WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp_min() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_low_temperature)).setText(String.format(Locale.CANADA, "LO: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp_min())), (char) 0x00B0));
            }

            // Set date and time of the weather update result.
            if(WeatherInfo.getInstance().getCurrentWeather().getDt() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_date)).setText(new SimpleDateFormat("EEE, d MMM h:mm a", Locale.CANADA).format(new Date(Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getDt()) * 1000)));
            }

            // Set weather description value.
            if(WeatherInfo.getInstance().getCurrentWeather().getWeather().length > 0 && WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getDescription() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_description)).setText(WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getDescription().toUpperCase());
            }

            // Set UV Index value.
            if(WeatherInfo.getInstance().getCurrentWeather().getUvIndex() != null)
            {
                if (WeatherInfo.getInstance().getCurrentWeather().getUvIndex().getData() >= 11)
                {
                    ((TextView) this.findViewById(R.id.current_weather_uv_index)).setText(R.string.uvindex_level_extreme);
                }
                else if (WeatherInfo.getInstance().getCurrentWeather().getUvIndex().getData() >= 8)
                {
                    ((TextView) this.findViewById(R.id.current_weather_uv_index)).setText(R.string.uvindex_level_very_high);
                }
                else if (WeatherInfo.getInstance().getCurrentWeather().getUvIndex().getData() >= 6)
                {
                    ((TextView) this.findViewById(R.id.current_weather_uv_index)).setText(R.string.uvindex_level_high);
                }
                else if (WeatherInfo.getInstance().getCurrentWeather().getUvIndex().getData() >= 3)
                {
                    ((TextView) this.findViewById(R.id.current_weather_uv_index)).setText(R.string.uvindex_level_moderate);
                }
                else if (WeatherInfo.getInstance().getCurrentWeather().getUvIndex().getData() >= 0)
                {
                    ((TextView) this.findViewById(R.id.current_weather_uv_index)).setText(R.string.uvindex_level_low);
                }
            }

            // Set humidity value.
            if(WeatherInfo.getInstance().getCurrentWeather().getMain()!= null && WeatherInfo.getInstance().getCurrentWeather().getMain().getHumidity() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_humidity)).setText(String.format(Locale.CANADA, "%d%%", WeatherInfo.getInstance().getCurrentWeather().getMain().getHumidity()));
            }

            // Set wind value.
            if(WeatherInfo.getInstance().getCurrentWeather().getWind() != null && WeatherInfo.getInstance().getCurrentWeather().getWind().getSpeed() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_wind)).setText(String.format(Locale.CANADA, "%s %.2f m/s", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getCurrentWeather().getWind().getDeg()), WeatherInfo.getInstance().getCurrentWeather().getWind().getSpeed()));
            }

            // Set pressure value.
            if(WeatherInfo.getInstance().getCurrentWeather().getMain() != null && WeatherInfo.getInstance().getCurrentWeather().getMain().getPressure() > 0)
            {
                ((TextView) this.findViewById(R.id.current_weather_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f kPa", WeatherInfo.getInstance().getCurrentWeather().getMain().getPressureKPA(WeatherInfo.getInstance().getCurrentWeather().getMain().getPressure())));
            }

            // Set sunset value.
            if(WeatherInfo.getInstance().getCurrentWeather().getSys() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getSunset() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_sunset)).setText(String.format(Locale.CANADA, "Sunset: %s", new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getSys().getSunset()) * 1000))));
            }

            // Set sunrise value.
            if(WeatherInfo.getInstance().getCurrentWeather().getSys() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getSunrise() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_sunrise)).setText(String.format(Locale.CANADA, "Sunrise: %s", new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getSys().getSunrise()) * 1000))));
            }

            // Set cloudiness value.
            if(WeatherInfo.getInstance().getCurrentWeather().getClouds() != null && WeatherInfo.getInstance().getCurrentWeather().getClouds().getAll() != null)
            {
                if(Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getClouds().getAll()) == 0)
                {
                    ((TextView) this.findViewById(R.id.current_weather_cloudiness)).setText(String.format(Locale.CANADA, "Cloudiness: %s", this.getString(R.string.cloudiness_clear_sky)));
                }
                else
                {
                    ((TextView) this.findViewById(R.id.current_weather_cloudiness)).setText(String.format(Locale.CANADA, "Cloudiness: %s%%", WeatherInfo.getInstance().getCurrentWeather().getClouds().getAll()));
                }
            }
        }
        else
        {
            this.startErrorActivity();
        }
    }
}
