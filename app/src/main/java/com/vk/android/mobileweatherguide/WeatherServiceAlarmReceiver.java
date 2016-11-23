package com.vk.android.mobileweatherguide;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WeatherServiceAlarmReceiver extends WakefulBroadcastReceiver
{
    // AlarmManager provides access to the system alarm services.
    private AlarmManager alarmManager;
    // PendingIntent is triggered when alarm is invoked.
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Start service, keeping the device awake while it is launching.
        startWakefulService(context, new Intent(context, WeatherNotificationService.class));
    }

    public void setAlarm(Context context)
    {
        // Ensure that notifications are enabled.
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.notifications_service_enabled), false))
        {
            // Set alarmManager.
            this.setAlarmManager((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

            // Set pendingIntent.
            this.setPendingIntent(PendingIntent.getBroadcast(context, 0, new Intent(context, WeatherServiceAlarmReceiver.class), 0));

            // Get alarm frequency value from user preference.
            String notificationsServiceFrequency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.notifications_service_frequency), null);

            // Set repeating interval according to user preference.
            if (notificationsServiceFrequency != null)
            {
                if (notificationsServiceFrequency.equals(context.getResources().getStringArray(R.array.notifications_service_frequency_values)[0]))
                {
                    this.getAlarmManager().setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, this.getPendingIntent());
                }
                else if (notificationsServiceFrequency.equals(context.getResources().getStringArray(R.array.notifications_service_frequency_values)[1]))
                {
                    this.getAlarmManager().setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_DAY, this.getPendingIntent());
                }
                else
                {
                    this.getAlarmManager().setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, this.getPendingIntent());
                }
            }
            else
            {
                this.getAlarmManager().setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, this.getPendingIntent());
            }

            // TODO: Remove test code when testing is complete
            // this.getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, this.getPendingIntent());

            // Allow WeatherServiceBootReceiver to restart the alarm when device is restarted.
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, WeatherServiceBootReceiver.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public void cancelAlarm(Context context)
    {
        if(this.getAlarmManager() != null)
        {
            // Cancel the alarm if it has been set.
            this.getAlarmManager().cancel(this.getPendingIntent());
        }

        // Disallow WeatherServiceBootReceiver to restart the alarm when device is restarted.
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, WeatherServiceBootReceiver.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public AlarmManager getAlarmManager()
    {
        return this.alarmManager;
    }

    public void setAlarmManager(AlarmManager alarmManager)
    {
        this.alarmManager = alarmManager;
    }

    public PendingIntent getPendingIntent()
    {
        return this.pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent)
    {
        this.pendingIntent = pendingIntent;
    }
}
