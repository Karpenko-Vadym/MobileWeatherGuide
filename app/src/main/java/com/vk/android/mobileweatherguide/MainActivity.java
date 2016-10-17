package com.vk.android.mobileweatherguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
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
    private boolean isExitConfirmed;

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

        if(WeatherInfo.getInstance().getCurrentWeather() == null || WeatherInfo.getInstance().getCurrentWeather().getUvIndex() == null || WeatherInfo.getInstance().getHourlyForecast() == null || WeatherInfo.getInstance().getHourlyForecast().getList() == null || WeatherInfo.getInstance().getHourlyForecast().getList().length < Integer.parseInt(this.getString(R.string.hourly_weather_count_url)))
        {
            // If any of required information is missing, send a request to fetch required information.
            String url = this.getString(R.string.current_weather_url) + this.getApplicationSharedPreferenceManager().getLocationId() + "&" + this.getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, this.getResponseListener(), this.getErrorListener());

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentWeatherRequest);
        }
        else if(WeatherInfo.getInstance().getCurrentWeather() != null && !this.getApplicationSharedPreferenceManager().getLocationId().equals(WeatherInfo.getInstance().getCurrentWeather().getId()))
        {
            // If location id stored in current object object does not match the location id stored in shared preferences, fetch required information for correct location id.
            String url = this.getString(R.string.current_weather_url) + this.getApplicationSharedPreferenceManager().getLocationId() + "&" + this.getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, this.getResponseListener(), this.getErrorListener());

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentWeatherRequest);
        }
        else
        {
            this.populateContent(); // If required information is present, display it.
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
        // When user presses the back button on their device, display confirmation dialog. Once user confirms that he/she wants to exit, exit the application.
        if(!this.isExitConfirmed())
        {
            // Setup AlertDialog and show it.
            new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            MainActivity.this.setExitConfirmed(true);

                            MainActivity.this.onBackPressed(); // Invoke onBackPressed() again to exit the application.
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

    public void setExitConfirmed(boolean isExitConfirmed) {
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
            case R.id.menu_item_location:
                explicitIntent = new Intent(this, LocationActivity.class); // If right side menu Location menu item is selected, start LocationActivity activity.

                this.startActivity(explicitIntent);

                this.finish();

                return true;

            case R.id.menu_item_notification:
                explicitIntent = new Intent(this, NotificationActivity.class); // If right side menu Notifications menu item is selected, start NotificationActivity activity.

                this.startActivity(explicitIntent);

                this.finish();

                return true;

            case R.id.menu_item_preferences:
                explicitIntent = new Intent(this, UserPreferenceActivity.class); // If right side menu Preferences menu item is selected, start UserPreferenceActivity activity.

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
        // TODO: Decide on displaying the error message in ErrorActivity activity.

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

                    // Send a request to fetch hourly data.
                    String url = MainActivity.this.getString(R.string.hourly_weather_url) + MainActivity.this.getApplicationSharedPreferenceManager().getLocationId() + "&cnt=" + MainActivity.this.getString(R.string.hourly_weather_count_url) + "&" + MainActivity.this.getString(R.string.url_api_key);

                    JsonObjectRequest jsonCurrentHourlyWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, MainActivity.this.getResponseListener(), MainActivity.this.getErrorListener());

                    RequestQueueSingleton.getInstance(MainActivity.this.getApplicationContext()).addToRequestQueue(jsonCurrentHourlyWeatherRequest);
                }
                else if(result.get("list") != null)
                {
                    weather.setHourlyForecast(gson.fromJson(jsonObject.toString(), WeatherInfo.HourlyForecast.class)); // Deserialize JSON representation into Weather.HourlyWeather class.

                    MainActivity.this.populateContent();
                }
                else
                {
                    // TODO: Decide on displaying the error message in ErrorActivity activity.

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
            // Set icons.
            this.findViewById(R.id.current_weather_uv_index_icon).setBackgroundResource(R.drawable.uv_index);
            this.findViewById(R.id.current_weather_humidity_icon).setBackgroundResource(R.drawable.humidity_index);
            this.findViewById(R.id.current_weather_wind_icon).setBackgroundResource(R.drawable.wind_index);

            // Set next 12 hours text and background resource for each of 3 hour step.
            ((TextView) this.findViewById(R.id.hourly_forecast_title)).setText(this.getString(R.string.current_weather_next_12_hours));
            this.findViewById(R.id.hourly_forecast_three_layout).setBackgroundResource(R.drawable.dark_grey_rectangle_rounded_corners);
            this.findViewById(R.id.hourly_forecast_six_layout).setBackgroundResource(R.drawable.dark_grey_rectangle_rounded_corners);
            this.findViewById(R.id.hourly_forecast_nine_layout).setBackgroundResource(R.drawable.dark_grey_rectangle_rounded_corners);
            this.findViewById(R.id.hourly_forecast_twelve_layout).setBackgroundResource(R.drawable.dark_grey_rectangle_rounded_corners);

            // Validate value for each field and set each field.

            // Set current location.
            if (WeatherInfo.getInstance().getCurrentWeather().getName() != null  && WeatherInfo.getInstance().getCurrentWeather().getSys() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getCountry() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_current_location)).setText(String.format(Locale.CANADA, "%s, %s", WeatherInfo.getInstance().getCurrentWeather().getName(), WeatherInfo.getInstance().getCurrentWeather().getSys().getCountry()));
            }

            // Set current temperature value.
            if (WeatherInfo.getInstance().getCurrentWeather().getMain() != null)
            {
                String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_temperature_units), null);

                if(temperaturePreference != null)
                {
                    if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_temperature)).setText(String.format(Locale.CANADA, "%d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp())), (char) 0x00B0));
                    }
                    else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp())), (char) 0x00B0));
                    }
                }
                else
                {
                    ((TextView) this.findViewById(R.id.current_weather_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getCurrentWeather().getMain().getTemp())), (char) 0x00B0));
                }
            }

            // Set date and time of the weather update result.
            if(WeatherInfo.getInstance().getCurrentWeather().getDt() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_date)).setText(new SimpleDateFormat("EEE, d MMM h:mm a", Locale.CANADA).format(new Date(Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getDt()) * (long) 1000)));
            }

            // Set weather description value.
            if(WeatherInfo.getInstance().getCurrentWeather().getWeather().length > 0 && WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getDescription() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_description)).setText(WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getDescription().toUpperCase());
            }

            // Set weather icon.
            if(WeatherInfo.getInstance().getCurrentWeather().getWeather().length > 0 && WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getIcon() != null)
            {
                (this.findViewById(R.id.current_weather_weather_icon)).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getCurrentWeather().getWeather()[0].getIcon()));
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
            if(WeatherInfo.getInstance().getCurrentWeather().getMain()!= null)
            {
                ((TextView) this.findViewById(R.id.current_weather_humidity)).setText(String.format(Locale.CANADA, "%d%%", WeatherInfo.getInstance().getCurrentWeather().getMain().getHumidity()));
            }

            // Set wind value.
            if(WeatherInfo.getInstance().getCurrentWeather().getWind() != null)
            {
                String windPreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_wind_speed_units), null);

                if(windPreference != null)
                {
                    if (windPreference.equals(this.getResources().getStringArray(R.array.preferences_wind_speed_units_values)[0]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_wind)).setText(String.format(Locale.CANADA, "%s %.2f km/h", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getCurrentWeather().getWind().getDeg()), WeatherInfo.getInstance().getKilometersPerHour(WeatherInfo.getInstance().getCurrentWeather().getWind().getSpeed())));
                    }
                    else if (windPreference.equals(this.getResources().getStringArray(R.array.preferences_wind_speed_units_values)[1]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_wind)).setText(String.format(Locale.CANADA, "%s %.2f m/s", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getCurrentWeather().getWind().getDeg()), WeatherInfo.getInstance().getCurrentWeather().getWind().getSpeed()));
                    }
                }
                else
                {
                    ((TextView) this.findViewById(R.id.current_weather_wind)).setText(String.format(Locale.CANADA, "%s %.2f m/s", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getCurrentWeather().getWind().getDeg()), WeatherInfo.getInstance().getCurrentWeather().getWind().getSpeed()));
                }
            }

            // Set pressure value.
            if(WeatherInfo.getInstance().getCurrentWeather().getMain() != null)
            {
                String pressurePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_pressure_units), null);

                if(pressurePreference != null)
                {
                    if (pressurePreference.equals(this.getResources().getStringArray(R.array.preferences_pressure_units_values)[0]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f mbar", WeatherInfo.getInstance().getCurrentWeather().getMain().getPressure()));
                    }
                    else if (pressurePreference.equals(this.getResources().getStringArray(R.array.preferences_pressure_units_values)[1]))
                    {
                        ((TextView) this.findViewById(R.id.current_weather_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f kPa", WeatherInfo.getInstance().getPressureKPA(WeatherInfo.getInstance().getCurrentWeather().getMain().getPressure())));
                    }
                }
                else
                {
                    ((TextView) this.findViewById(R.id.current_weather_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f kPa", WeatherInfo.getInstance().getPressureKPA(WeatherInfo.getInstance().getCurrentWeather().getMain().getPressure())));
                }
            }

            // Set sunset value.
            if(WeatherInfo.getInstance().getCurrentWeather().getSys() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getSunset() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_sunset)).setText(String.format(Locale.CANADA, "Sunset: %s", new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getSys().getSunset()) * (long) 1000))));
            }

            // Set sunrise value.
            if(WeatherInfo.getInstance().getCurrentWeather().getSys() != null && WeatherInfo.getInstance().getCurrentWeather().getSys().getSunrise() != null)
            {
                ((TextView) this.findViewById(R.id.current_weather_sunrise)).setText(String.format(Locale.CANADA, "Sunrise: %s", new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getCurrentWeather().getSys().getSunrise()) * (long) 1000))));
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

            // Set hourly forecasting properties.
            if(WeatherInfo.getInstance().getHourlyForecast().getList() != null && WeatherInfo.getInstance().getHourlyForecast().getList().length >= Integer.parseInt(this.getString(R.string.hourly_weather_count_url)))
            {
                // Set in three hours icon property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getWeather() != null && WeatherInfo.getInstance().getHourlyForecast().getList()[0].getWeather().length > 0 && WeatherInfo.getInstance().getHourlyForecast().getList()[0].getWeather()[0].getIcon() != null)
                {
                    this.findViewById(R.id.hourly_forecast_three_icon).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getWeather()[0].getIcon()));
                }

                // Set in three hours temperature property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getMain() != null)
                {
                    String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_temperature_units), null);

                    if(temperaturePreference != null)
                    {
                        if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_three_temperature)).setText(String.format(Locale.CANADA, "%d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0));
                        }
                        else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_three_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0));
                        }
                    }
                    else
                    {
                        ((TextView) this.findViewById(R.id.hourly_forecast_three_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0));
                    }
                }

                // Set in three hours time property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getDt() != null)
                {
                    ((TextView) this.findViewById(R.id.hourly_forecast_three_time)).setText(new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getHourlyForecast().getList()[0].getDt()) * (long) 1000)));
                }

                // Set in six hours icon property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getWeather() != null && WeatherInfo.getInstance().getHourlyForecast().getList()[1].getWeather().length > 0 && WeatherInfo.getInstance().getHourlyForecast().getList()[1].getWeather()[0].getIcon() != null)
                {
                    this.findViewById(R.id.hourly_forecast_six_icon).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getWeather()[0].getIcon()));
                }

                // Set in six hours temperature property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getMain() != null)
                {
                    String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_temperature_units), null);

                    if(temperaturePreference != null)
                    {
                        if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_six_temperature)).setText(String.format(Locale.CANADA, "%d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getMain().getTemp())), (char) 0x00B0));
                        }
                        else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_six_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getMain().getTemp())), (char) 0x00B0));
                        }
                    }
                    else
                    {
                        ((TextView) this.findViewById(R.id.hourly_forecast_six_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getMain().getTemp())), (char) 0x00B0));
                    }
                }

                // Set in six hours time property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getDt() != null)
                {
                    ((TextView) this.findViewById(R.id.hourly_forecast_six_time)).setText(new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getHourlyForecast().getList()[1].getDt()) * (long) 1000)));
                }

                // Set in nine hours icon property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getWeather() != null && WeatherInfo.getInstance().getHourlyForecast().getList()[2].getWeather().length > 0 && WeatherInfo.getInstance().getHourlyForecast().getList()[2].getWeather()[0].getIcon() != null)
                {
                    this.findViewById(R.id.hourly_forecast_nine_icon).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getWeather()[0].getIcon()));
                }

                // Set in nine hours temperature property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getMain() != null)
                {
                    String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_temperature_units), null);

                    if(temperaturePreference != null)
                    {
                        if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_nine_temperature)).setText(String.format(Locale.CANADA, "%d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getMain().getTemp())), (char) 0x00B0));
                        }
                        else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_nine_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getMain().getTemp())), (char) 0x00B0));
                        }
                    }
                    else
                    {
                        ((TextView) this.findViewById(R.id.hourly_forecast_nine_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getMain().getTemp())), (char) 0x00B0));
                    }
                }

                // Set in nine hours time property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getDt() != null)
                {
                    ((TextView) this.findViewById(R.id.hourly_forecast_nine_time)).setText(new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getHourlyForecast().getList()[2].getDt()) * (long) 1000)));
                }

                // Set in twelve hours icon property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getWeather() != null && WeatherInfo.getInstance().getHourlyForecast().getList()[3].getWeather().length > 0 && WeatherInfo.getInstance().getHourlyForecast().getList()[3].getWeather()[0].getIcon() != null)
                {
                    this.findViewById(R.id.hourly_forecast_twelve_icon).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getWeather()[0].getIcon()));
                }

                // Set in twelve hours temperature property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getMain() != null)
                {
                    String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.preferences_temperature_units), null);

                    if(temperaturePreference != null)
                    {
                        if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_twelve_temperature)).setText(String.format(Locale.CANADA, "%d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getMain().getTemp())), (char) 0x00B0));
                        }
                        else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                        {
                            ((TextView) this.findViewById(R.id.hourly_forecast_twelve_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getMain().getTemp())), (char) 0x00B0));
                        }
                    }
                    else
                    {
                        ((TextView) this.findViewById(R.id.hourly_forecast_twelve_temperature)).setText(String.format(Locale.CANADA, "%d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getMain().getTemp())), (char) 0x00B0));
                    }
                }

                // Set in twelve hours time property.
                if(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getDt() != null)
                {
                    ((TextView) this.findViewById(R.id.hourly_forecast_twelve_time)).setText(new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(WeatherInfo.getInstance().getHourlyForecast().getList()[3].getDt()) * (long) 1000)));
                }

            }
        }
        else
        {
            // TODO: Decide on displaying the error message in ErrorActivity activity.

            this.startErrorActivity();
        }
    }

    // getIconResource method accepts an id of the icon specified by OpenWeatherMap api and translates is to the resource id of the corresponding icon.
    private int getIconResource(String icon)
    {
        int iconId;

        switch(icon)
        {
            case "01d":
                iconId = R.drawable.sunny_day;

                break;
            case "01n":
                iconId = R.drawable.clear_night;

                break;
            case "02d":
                iconId = R.drawable.few_clouds_day;

                break;
            case "02n":
                iconId = R.drawable.few_clouds_night;

                break;
            case "03d":
                iconId = R.drawable.scattered_clouds_day;

                break;
            case "03n":
                iconId = R.drawable.scattered_clouds_night;

                break;
            case "04d":
                iconId = R.drawable.broken_cloud_day;

                break;
            case "04n":
                iconId = R.drawable.broken_cloud_night;

                break;
            case "09d":
                iconId = R.drawable.shower_rain_day;

                break;
            case "09n":
                iconId = R.drawable.shower_rain_night;

                break;
            case "10d":
                iconId = R.drawable.rain_day;

                break;
            case "10n":
                iconId = R.drawable.rain_night;

                break;
            case "11d":
                iconId = R.drawable.thurderstorm_day;

                break;
            case "11n":
                iconId = R.drawable.thurderstorm_night;

                break;
            case "13d":
                iconId = R.drawable.snow_day;

                break;
            case "13n":
                iconId = R.drawable.snow_night;

                break;
            case "50d":
                iconId = R.drawable.mist_day;

                break;
            case "50n":
                iconId = R.drawable.mist_night;

                break;

            default:
                iconId = 0;
        }

        return iconId;
    }
}
