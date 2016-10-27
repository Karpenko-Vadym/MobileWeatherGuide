package com.vk.android.mobileweatherguide;

import android.content.Context;
import android.content.SharedPreferences;

// ApplicationSharedPreferenceManager class represents a manager that manages operations related to preferences.
public class ApplicationSharedPreferenceManager
{
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    public ApplicationSharedPreferenceManager(Context context)
    {
        this.setContext(context);
        // Set shared preferences to access file identified by the resource string R.string.preferences_filename and opens it using private mode so the file is accessible by only your app.
        this.setSharedPreferences(this.getContext().getSharedPreferences(this.getContext().getString(R.string.preferences_filename), Context.MODE_PRIVATE));
    }

    // Setters and getters for ApplicationSharedPreferenceManager properties.
    private Context getContext()
    {
        return this.context;
    }

    private void setContext(Context context)
    {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences()
    {
        return this.sharedPreferences;
    }

    private void setSharedPreferences(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    private SharedPreferences.Editor getSharedPreferencesEditor()
    {
        return this.sharedPreferencesEditor;
    }

    private void setSharedPreferencesEditor(SharedPreferences.Editor sharedPreferencesEditor)
    {
        this.sharedPreferencesEditor = sharedPreferencesEditor;
    }

    public String getLocationId() // Read location id from shared preferences.
    {
        return this.getContext() != null && this.getSharedPreferences() != null ? this.getSharedPreferences().getString(this.getContext().getString(R.string.location_id), null) : null;
    }

    public void setLocationId(String locationId) // Write location id to shared preferences.
    {
        // Ensure that location id can be written to shared preferences.
        if(this.getContext() != null && this.getSharedPreferences() != null)
        {
            if(this.getSharedPreferencesEditor() == null)
            {
                this.setSharedPreferencesEditor(this.getSharedPreferences().edit());
            }

            // Write location id to shared preferences.
            this.getSharedPreferencesEditor().putString(this.getContext().getString(R.string.location_id), locationId);
            this.getSharedPreferencesEditor().commit();
        }
    }

    public String getLocationDisplayName() // Read location display name from shared preferences.
    {
        return this.getContext() != null && this.getSharedPreferences() != null ? this.getSharedPreferences().getString(this.getContext().getString(R.string.location_display_name), null) : null;
    }

    public void setLocationDisplayName(String locationDisplayName) // Write location display name to shared preferences.
    {
        // Ensure that location display name can be written to shared preferences.
        if(this.validateContext())
        {
            if(this.getSharedPreferencesEditor() == null)
            {
                this.setSharedPreferencesEditor(this.getSharedPreferences().edit());
            }

            // Write location display name to shared preferences.
            this.getSharedPreferencesEditor().putString(this.getContext().getString(R.string.location_display_name), locationDisplayName);
            this.getSharedPreferencesEditor().commit();
        }
    }

    // validateContext method validates that current context and shared preferences are not null. If so, returns true, false otherwise.
    public boolean validateContext()
    {
        return this.getContext() != null && this.getSharedPreferences() != null;
    }

    // validatePreferences method validated that preferences (Location id and location display name) are set. If so, returns true, false otherwise.
    public boolean validatePreferences()
    {
        return this.getSharedPreferences().contains(this.getContext().getString(R.string.location_id)) && this.getSharedPreferences().contains(this.getContext().getString(R.string.location_display_name));
    }
}
