package com.vk.android.mobileweatherguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherServiceBootReceiver extends BroadcastReceiver
{
    WeatherServiceAlarmReceiver weatherServiceAlarmReceiver = new WeatherServiceAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) // Triggered when device is rebooted (See AndroidManifest.xml for more info).
    {
        // When device is rebooted, set the alarm.
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            this.getWeatherServiceAlarmReceiver().setAlarm(context);
        }
    }

    public WeatherServiceAlarmReceiver getWeatherServiceAlarmReceiver()
    {
        return this.weatherServiceAlarmReceiver;
    }
}
