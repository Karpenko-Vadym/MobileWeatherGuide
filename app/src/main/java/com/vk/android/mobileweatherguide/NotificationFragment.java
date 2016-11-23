package com.vk.android.mobileweatherguide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

// NotificationFragment fragment shows user notification preferences described in notifications.xml.
public class NotificationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    WeatherServiceAlarmReceiver weatherServiceAlarmReceiver = new WeatherServiceAlarmReceiver();

    public static NotificationFragment getInstance()
    {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load notification preferences from XML resource.
        this.addPreferencesFromResource(R.xml.notifications);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(key.equals(this.getActivity().getString(R.string.notifications_service_enabled)))
        {
            if(sharedPreferences.getBoolean(key, false))
            {
                // Set alarm that starts a service.
                this.getWeatherServiceAlarmReceiver().setAlarm(this.getActivity());
            }
            else
            {
                // Cancel alarm that starts a service.
                this.getWeatherServiceAlarmReceiver().cancelAlarm(this.getActivity());
            }
        }
        else if(key.equals(this.getActivity().getString(R.string.notifications_service_frequency)))
        {
            if(sharedPreferences.getBoolean(this.getActivity().getString(R.string.notifications_service_enabled), false) && sharedPreferences.getBoolean(this.getActivity().getString(R.string.notifications_service_enabled), false))
            {
                // Modify alarm that starts a service.
                this.getWeatherServiceAlarmReceiver().cancelAlarm(this.getActivity());
                this.getWeatherServiceAlarmReceiver().setAlarm(this.getActivity());
            }
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public WeatherServiceAlarmReceiver getWeatherServiceAlarmReceiver()
    {
        return this.weatherServiceAlarmReceiver;
    }

    public void setWeatherServiceAlarmReceiver(WeatherServiceAlarmReceiver weatherServiceAlarmReceiver)
    {
        this.weatherServiceAlarmReceiver = weatherServiceAlarmReceiver;
    }
}