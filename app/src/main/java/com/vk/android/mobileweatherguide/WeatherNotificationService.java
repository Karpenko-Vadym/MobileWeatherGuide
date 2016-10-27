package com.vk.android.mobileweatherguide;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.os.Process;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WeatherNotificationService extends Service
{
    // Class used to run a message loop for a thread.
    private Looper looper;

    // Handler that will be attached to a background thread to execute a unit of work.
    private WeatherNotificationServiceHandler weatherNotificationServiceHandler;
    private ApplicationSharedPreferenceManager applicationSharedPreferenceManager;

    // WeatherNotificationServiceHandler class receives a message from a thread.
    private final class WeatherNotificationServiceHandler extends Handler
    {
        public WeatherNotificationServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message message)
        {
            // Once message is received, send a request to fetch hourly data and pass thread id to the response listener so it can shut down the service.
            String url = getContext().getString(R.string.hourly_weather_url) + getApplicationSharedPreferenceManager().getLocationId() + "&cnt=" + Integer.parseInt(getContext().getString(R.string.hourly_forecast_analysis_count)) + "&" + getContext().getString(R.string.url_api_key);

            JsonObjectRequest jsonCurrentHourlyWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, getResponseListener(message.arg1), getErrorListener(message.arg1));

            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonCurrentHourlyWeatherRequest);
        }
    }

    @Override
    public void onCreate()
    {
        // Start up new thread that will run on a background and pass it's looper to the handler (WeatherNotificationServiceHandler), so that service will run on a background.
        HandlerThread handlerThread = new HandlerThread("WeatherNotificationHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);

        handlerThread.start();

        this.setLooper(handlerThread.getLooper());

        this.setWeatherNotificationServiceHandler(new WeatherNotificationServiceHandler(this.getLooper()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(this.getApplicationSharedPreferenceManager() == null) // If applicationSharedPreferenceManager property is not set, set it.
        {
            this.setApplicationSharedPreferenceManager(new ApplicationSharedPreferenceManager(this));
        }

        // When start request is invoked, send a message to the handler (With the id of the tread) to start the job.
        Message message = this.getWeatherNotificationServiceHandler().obtainMessage();

        message.arg1 = startId;

        this.getWeatherNotificationServiceHandler().sendMessage(message);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if(this.getApplicationSharedPreferenceManager() != null)
        {
            this.setApplicationSharedPreferenceManager(null); // Unset applicationSharedPreferenceManager property.
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public Looper getLooper()
    {
        return this.looper;
    }

    public void setLooper(Looper looper)
    {
        this.looper = looper;
    }

    public WeatherNotificationServiceHandler getWeatherNotificationServiceHandler()
    {
        return this.weatherNotificationServiceHandler;
    }

    public void setWeatherNotificationServiceHandler(WeatherNotificationServiceHandler weatherNotificationServicehandler)
    {
        this.weatherNotificationServiceHandler = weatherNotificationServicehandler;
    }

    private Response.Listener<JSONObject> getResponseListener(final int startId)
    {
        return new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject jsonObject) // This event listener listens for success response.
            {
                Gson gson = new Gson();

                WeatherAnalysis weatherAnalysis = WeatherAnalysis.getInstance();

                JsonObject result = gson.fromJson(jsonObject.toString(), JsonObject.class);

                if(result.get("cnt").getAsInt() == Integer.parseInt(getContext().getString(R.string.hourly_forecast_analysis_count)))
                {
                    // When response with required information is received, set WeatherAnalysis.HourlyForecast object and proceed with building notification(s).
                    weatherAnalysis.setHourlyForecast(gson.fromJson(jsonObject.toString(), WeatherAnalysis.HourlyForecast.class)); // Deserialize JSON representation into WeatherAnalysis.WeatherForecast class.

                    String notificationTitle;
                    String notificationText;

                    // Get temperature preference value.
                    String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getContext().getString(R.string.preferences_temperature_units), null);

                    // First, build notificationTitle string.

                    // Check if required data is present.
                    if(weatherAnalysis.getHourlyForecast() != null && weatherAnalysis.getHourlyForecast().getList() != null && weatherAnalysis.getHourlyForecast().getList().length == Integer.parseInt(getString(R.string.hourly_forecast_analysis_count))
                            && weatherAnalysis.getHourlyForecast().getList()[0].getDt() != null)
                    {
                        // Get closest hourly date value and calculate how many hours between current hour value and closest hourly weather snapshot hour.
                        long nextHours = TimeUnit.MILLISECONDS.toHours(new Date((long) Integer.parseInt(weatherAnalysis.getHourlyForecast().getList()[0].getDt()) * (long) 1000).getTime() - new Date().getTime());

                        // If result is greater then 1 hour, build string with temperature in x amount of hours.
                        if (nextHours > 1)
                        {
                            // Build string according to user preference on temperature units.
                            if (temperaturePreference != null)
                            {
                                if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sF in the next %d hours", Math.round(WeatherAnalysis.getInstance().getFahrenheit(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0, nextHours);
                                }
                                else if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC in the next %d hours", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0, nextHours);
                                } else
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC in the next %d hours", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0, nextHours);
                                }
                            }
                            else
                            {
                                notificationTitle = String.format(Locale.CANADA, "%d%sC in the next %d hours", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0, nextHours);
                            }
                        }
                        else if (nextHours > 0) // If 1, build string with temperature in one hour.
                        {
                            // Build string according to user preference on temperature units.
                            if (temperaturePreference != null)
                            {
                                if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sF in the next hour", Math.round(WeatherAnalysis.getInstance().getFahrenheit(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                                else if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC in the next hour", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                                else
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC in the next hour", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                            }
                            else
                            {
                                notificationTitle = String.format(Locale.CANADA, "%d%sC in the next hour", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                            }
                        }
                        else // Otherwise, build string with temperature now.
                        {
                            // Build string according to user preference on temperature units.
                            if (temperaturePreference != null)
                            {
                                if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sF now", Math.round(WeatherAnalysis.getInstance().getFahrenheit(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                                else if (temperaturePreference.equals(getContext().getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC now", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                                else
                                {
                                    notificationTitle = String.format(Locale.CANADA, "%d%sC now", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                                }
                            }
                            else
                            {
                                notificationTitle = String.format(Locale.CANADA, "%d%sC now", Math.round(WeatherAnalysis.getInstance().getCelsius(weatherAnalysis.getHourlyForecast().getList()[0].getMain().getTemp())), (char) 0x00B0);
                            }
                        }
                    }
                    else
                    {
                        // If data is not present, set title to application name.
                        notificationTitle = getContext().getString(R.string.app_name);
                    }

                    // Next, build notificationText string.

                    // Check if required data is present.
                    if(weatherAnalysis.getHourlyForecast() != null && weatherAnalysis.getHourlyForecast().getList() != null && weatherAnalysis.getHourlyForecast().getList().length == Integer.parseInt(getContext().getString(R.string.hourly_forecast_analysis_count))
                            && weatherAnalysis.getHourlyForecast().getList()[0].getWeather() != null && weatherAnalysis.getHourlyForecast().getList()[0].getWeather().length > 0
                            && weatherAnalysis.getHourlyForecast().getList()[0].getWeather()[0].getDescription() != null)
                    {
                        notificationText = weatherAnalysis.getHourlyForecast().getList()[0].getWeather()[0].getDescription().toUpperCase();
                    }
                    else // If data is not present, set text to error condition.
                    {
                        notificationText = getContext().getString(R.string.data_not_available);
                    }

                    // Finally, setup and issue notification.
                    int notificationId = 100001;

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(notificationTitle).setContentText(notificationText);

                    // Intent that will allow to open an activity when user tap on the notification.
                    Intent intent = new Intent(getContext(), MainActivity.class);

                    // TaskStackBuilder ensures that navigation backwards from the activity leads out of the application to the Home screen (Could be useful if activity stack would be preserved in the future).
                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getContext()).addParentStack(MainActivity.class).addNextIntent(intent);

                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationBuilder.setContentIntent(pendingIntent);

                    // Issue notification.
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notificationBuilder.build());

                    // If in the next 24 hours there are alerting weather conditions, build an alert notification.

                    StringBuilder stringBuilder = new StringBuilder(getContext().getString(R.string.alertText));

                    boolean isAlertRequired = false;

                    // Check if required data is present.
                    if(weatherAnalysis.getHourlyForecast() != null && weatherAnalysis.getHourlyForecast().getList() != null && weatherAnalysis.getHourlyForecast().getList().length == Integer.parseInt(getContext().getString(R.string.hourly_forecast_analysis_count)))
                    {
                        // Iterate through each weather snapshot and check whether each snapshot's condition matches to any of the alert conditions.
                        for(int i = 0; i < weatherAnalysis.getHourlyForecast().getList().length; i++)
                        {
                            // Check if required data is present.
                            if(weatherAnalysis.getHourlyForecast().getList()[i].getDt() != null && weatherAnalysis.getHourlyForecast().getList()[i].getWeather() != null
                                    && weatherAnalysis.getHourlyForecast().getList()[i].getWeather().length > 0 && weatherAnalysis.getHourlyForecast().getList()[i].getWeather()[0].getId() != null)
                            {
                                // If match is found, add alert condition to the alert stringBuilder and set isAlertRequired to true, so that an alert notification will be issued.
                                if(Arrays.asList(getContext().getResources().getStringArray(R.array.alert_codes)).contains(weatherAnalysis.getHourlyForecast().getList()[i].getWeather()[0].getId()))
                                {
                                    stringBuilder.append(String.format(Locale.CANADA, "\n\r%s - %s", new SimpleDateFormat("h:mm a", Locale.CANADA).format(new Date((long) Integer.parseInt(weatherAnalysis.getHourlyForecast().getList()[i].getDt()) * (long) 1000)),
                                            weatherAnalysis.getHourlyForecast().getList()[i].getWeather()[0].getDescription()));

                                    isAlertRequired = true;
                                }
                            }
                        }
                    }

                    // If there alert is required, setup and issue an alert notification.
                    if(isAlertRequired)
                    {
                        int alertId = 100002;

                        NotificationCompat.Builder alertBuilder = new NotificationCompat.Builder(getContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getContext().getString(R.string.alertTitle))
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(stringBuilder.toString()));

                        notificationBuilder.setContentIntent(pendingIntent);

                        // Issue notification.
                        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(alertId, alertBuilder.build());
                    }

                    // Shut down the service.
                    stopSelf(startId);
                }
                else
                {
                    // TODO: Decide on error scenario.
                    // Shut down the service.
                    stopSelf(startId);
                }
            }
        };
    }

    private Response.ErrorListener getErrorListener(final int startId)
    {
        return new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError) // This event listener listens for error response.
            {
                // TODO: Decide on error scenario.
                // Shut down the service.
                stopSelf(startId);
            }
        };
    }

    private ApplicationSharedPreferenceManager getApplicationSharedPreferenceManager()
    {
        return this.applicationSharedPreferenceManager;
    }

    private void setApplicationSharedPreferenceManager(ApplicationSharedPreferenceManager applicationSharedPreferenceManager)
    {
        this.applicationSharedPreferenceManager = applicationSharedPreferenceManager;
    }

    private Service getContext()
    {
        return this;
    }
}