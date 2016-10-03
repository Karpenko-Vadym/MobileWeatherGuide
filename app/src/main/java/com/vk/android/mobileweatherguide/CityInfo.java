package com.vk.android.mobileweatherguide;

import android.content.Context;

// CityInfo class represents a container for storing the data about locations that user may select.
public class CityInfo
{
    private String [] cityIds;
    private String [] cityNames;
    private String [] countryCodes;
    private String [] cityDisplayNames; // Combination of city names and city codes concatenated using ", " delimiter (Example: Toronto, CA).

    public CityInfo(Context context) // Construct the class with a context that is used to retrieve application resources.
    {
        // Extract data from cities.xml string arrays and set each corresponding property.
        this.setCityIds(context.getResources().getStringArray(R.array.city_ids));
        this.setCityNames(context.getResources().getStringArray(R.array.city_names));
        this.setCountryCodes(context.getResources().getStringArray(R.array.country_codes));
        this.setCityDisplayNames(new String[cityIds.length]);

        if(this.validateData()) // Validate that data is set properly
        {
            for (int i = 0; i < this.getCityIds().length; i++)
            {
                // Trim the values of each property and set cityDisplayNames property.
                this.getCityIds()[i] = this.getCityIds()[i].trim();
                this.getCityNames()[i] = this.getCityNames()[i].trim();
                this.getCountryCodes()[i] = this.getCountryCodes()[i].trim();
                this.getCityDisplayNames()[i] = this.getCityNames()[i].concat(", " + this.getCountryCodes()[i]);
            }
        }
        else // If data is not valid, set all properties to null
        {
            this.setCityIds(null);
            this.setCityNames(null);
            this.setCountryCodes(null);
            this.setCityDisplayNames(null);
        }
    }

    // Setters and getters for each property
    private void setCityIds(String[] cityIds)
    {
        this.cityIds = cityIds;
    }

    public String[] getCityIds()
    {
        return this.cityIds;
    }

    private void setCityNames(String[] cityNames)
    {
        this.cityNames = cityNames;
    }

    public String[] getCityNames()
    {
        return this.cityNames;
    }

    private void setCountryCodes(String[] countryCodes)
    {
        this.countryCodes = countryCodes;
    }

    public String[] getCountryCodes()
    {
        return this.countryCodes;
    }

    private void setCityDisplayNames(String[] displayCities)
    {
        this.cityDisplayNames = displayCities;
    }

    public String[] getCityDisplayNames()
    {
        return this.cityDisplayNames;
    }

    // validateData method validates that each property has at least one element in it and that all properties are of the same size (Each array is of the same size).
    public boolean validateData()
    {
        return (this.getCityIds().length == this.getCityNames().length && this.getCityNames().length == this.getCountryCodes().length && this.getCountryCodes().length == this.getCityDisplayNames().length && this.getCityIds().length > 0);
    }
}
