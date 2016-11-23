package com.vk.android.mobileweatherguide;

import android.os.Bundle;
import android.preference.PreferenceFragment;

// UserPreferenceFragment fragment shows user preferences described in preferences.xml.
public class UserPreferenceFragment extends PreferenceFragment
{
    public static UserPreferenceFragment getInstance()
    {
        return new UserPreferenceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load preferences from XML resource.
        this.addPreferencesFromResource(R.xml.preferences);
    }
}
