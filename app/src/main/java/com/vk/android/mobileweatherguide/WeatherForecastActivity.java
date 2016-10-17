package com.vk.android.mobileweatherguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.Date;

public class WeatherForecastActivity extends AppCompatActivity
{
    private ApplicationDrawerNavigationManager applicationDrawerNavigationManager;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private boolean isExitConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**** SETUP ****/

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        this.setContentView(R.layout.activity_weather_forecast);

        this.setTitle(this.getResources().getString(R.string.drawer_weather_forecast)); // Set activity title.

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

        if(WeatherInfo.getInstance().getWeatherForecast() == null || WeatherInfo.getInstance().getWeatherForecast().getList() == null || WeatherInfo.getInstance().getWeatherForecast().getList().length < Integer.parseInt(this.getString(R.string.weather_forecast_count_url)))
        {
            // If any of required information is missing, send a request to fetch required information.
            String url = this.getString(R.string.weather_forecast_url) + this.getString(R.string.weather_forecast_count_url) + "&id=" + this.getApplicationSharedPreferenceManager().getLocationId() + "&" + this.getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, getResponseListener(), getErrorListener());

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentWeatherRequest);
        }
        else if(WeatherInfo.getInstance().getWeatherForecast() != null && !this.getApplicationSharedPreferenceManager().getLocationId().equals(WeatherInfo.getInstance().getWeatherForecast().getCity().getId()))
        {
            // If location id stored in weather forecast object does not match the location id stored in shared preferences, fetch required information for correct location id.
            String url = this.getString(R.string.weather_forecast_url) + this.getString(R.string.weather_forecast_count_url) + "&id=" + this.getApplicationSharedPreferenceManager().getLocationId() + "&" + this.getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, getResponseListener(), getErrorListener());

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonCurrentWeatherRequest);
        }
        else
        {
            if(WeatherForecastActivity.this.getViewPager() == null) // If viewPager property is not set, set it.
            {
                WeatherForecastActivity.this.setViewPager((ViewPager) findViewById(R.id.weather_forecast_view_pager));
            }

            if(WeatherForecastActivity.this.getPagerAdapter() == null) // If pagerAdapter property is not set, set it.
            {
                WeatherForecastActivity.this.setPagerAdapter(new WeatherForecastPagerAdapter(WeatherForecastActivity.this.getSupportFragmentManager()));
            }

            WeatherForecastActivity.this.getViewPager().setAdapter(WeatherForecastActivity.this.getPagerAdapter()); // Set PagerAdapter.

            WeatherForecastActivity.this.getViewPager().setPageTransformer(true, new ZoomOutPageTransformer()); // Set page transformer for slides.
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

        if(this.getPagerAdapter() == null)
        {
            this.setPagerAdapter(null); // Unset PagerAdapter adapter property.
        }

        if(this.getViewPager().getAdapter() != null)
        {
            this.getViewPager().setAdapter(null); // Unset ViewPager adapter property.
        }

        if(this.getViewPager() != null)
        {
            this.setViewPager(null); // Unset ViewPager property.
        }

        if(RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue() != null)
        {
            RequestQueueSingleton.getInstance(this.getApplicationContext()).setRequestQueue(null); // Unset request queue property.
        }
    }

    // WeatherForecastPagerAdapter adapter is used by ViewPager as a supply for new pages (Instances of WeatherForecastSlideFragment) to display.
    private class WeatherForecastPagerAdapter extends FragmentStatePagerAdapter
    {
        public WeatherForecastPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        // getItem method is used for supplying instances of WeatherForecastSlideFragment as new pages.
        @Override
        public Fragment getItem(int position)
        {
            return WeatherForecastSlideFragment.getInstance(position);
        }

        // getCount method returns the amount of pages that adapter will create.
        @Override
        public int getCount()
        {
            return Integer.parseInt(WeatherForecastActivity.this.getString(R.string.weather_forecast_count_url));
        }
    }

    // setCurrentItem method allows to change slides to specified position.
    public void setCurrentItem(int position, boolean smoothScroll)
    {
        if(this.getViewPager() != null)
        {
            this.getViewPager().setCurrentItem(position, smoothScroll);
        }
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        // If current ViewPager is at first slide, exit the application, otherwise, slide down (position - 1).
        if(this.getViewPager().getCurrentItem() == 0)
        {
            // When user presses the back button on their device, display confirmation dialog. Once user confirms that he/she wants to exit, exit the application.
            if(!this.isExitConfirmed())
            {
                // Setup AlertDialog and show it.
                new AlertDialog.Builder(WeatherForecastActivity.this).setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                WeatherForecastActivity.this.setExitConfirmed(true);

                                WeatherForecastActivity.this.onBackPressed(); // Invoke onBackPressed() again to exit the application.
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
        else
        {
            this.getViewPager().setCurrentItem(this.getViewPager().getCurrentItem() - 1);
        }
    }

    // Setter and getter for applicationSharedPreferenceManager property.
    private ApplicationSharedPreferenceManager getApplicationSharedPreferenceManager()
    {
        return this.applicationSharedPreferenceManager;
    }

    private void setApplicationSharedPreferenceManager(ApplicationSharedPreferenceManager applicationSharedPreferenceManager)
    {
        this.applicationSharedPreferenceManager = applicationSharedPreferenceManager;
    }

    // Setter and getter for viewPager property.
    public ViewPager getViewPager()
    {
        return this.viewPager;
    }

    public void setViewPager(ViewPager viewPager)
    {
        this.viewPager = viewPager;
    }

    // Setter and getter for pagerAdapter property.
    public PagerAdapter getPagerAdapter()
    {
        return this.pagerAdapter;
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter)
    {
        this.pagerAdapter = pagerAdapter;
    }

    // Setter and getter for isExitConfirmed property.
    public boolean isExitConfirmed()
    {
        return this.isExitConfirmed;
    }

    public void setExitConfirmed(boolean isExitConfirmed)
    {
        this.isExitConfirmed = isExitConfirmed;
    }

    private Response.Listener<JSONObject> getResponseListener()
    {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) // This event listener listens for success response.
            {
                Gson gson = new Gson();

                WeatherInfo weather = WeatherInfo.getInstance();

                if(jsonObject != null)
                {
                    weather.setWeatherForecast(gson.fromJson(jsonObject.toString(), WeatherInfo.WeatherForecast.class)); // Deserialize JSON representation into Weather.HourlyForecast class.

                    if (weather.getHourlyForecast() !=  null && weather.getHourlyForecast().getList() != null && weather.getWeatherForecast().getList().length >= (Integer.parseInt(WeatherForecastActivity.this.getString(R.string.weather_forecast_count_url)) - 1) && weather.getHourlyForecast().getCity() != null)
                    {
                        if(WeatherForecastActivity.this.getViewPager() == null) // If viewPager property is not set, set it.
                        {
                            WeatherForecastActivity.this.setViewPager((ViewPager) findViewById(R.id.weather_forecast_view_pager));
                        }

                        if(WeatherForecastActivity.this.getPagerAdapter() == null) // If pagerAdapter property is not set, set it.
                        {
                            WeatherForecastActivity.this.setPagerAdapter(new WeatherForecastPagerAdapter(WeatherForecastActivity.this.getSupportFragmentManager()));
                        }

                        weather.getWeatherForecast().setTimestamp(new Date()); // Record when current object was last updated.

                        WeatherForecastActivity.this.getViewPager().setAdapter(WeatherForecastActivity.this.getPagerAdapter()); // Set PagerAdapter

                        WeatherForecastActivity.this.getViewPager().setPageTransformer(true, new ZoomOutPageTransformer()); // Set page transformer for slides.
                    }
                    else
                    {
                        // TODO: Decide on displaying the error message in ErrorActivity activity.

                        WeatherForecastActivity.this.startErrorActivity();
                    }
                }
                else
                {
                    // TODO: Decide on displaying the error message in ErrorActivity activity.

                    WeatherForecastActivity.this.startErrorActivity();
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

                WeatherForecastActivity.this.startErrorActivity();
            }
        };
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
}
