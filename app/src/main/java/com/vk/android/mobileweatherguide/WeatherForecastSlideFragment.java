package com.vk.android.mobileweatherguide;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherForecastSlideFragment extends Fragment
{
    private int pageIndex;

    public static WeatherForecastSlideFragment getInstance(int pageIndex)
    {
        WeatherForecastSlideFragment weatherForecastSlideFragment = new WeatherForecastSlideFragment();

        weatherForecastSlideFragment.setPageIndex(pageIndex);

        return weatherForecastSlideFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_weather_forecast_slide, container, false);

        if(WeatherInfo.getInstance().getWeatherForecast() != null && WeatherInfo.getInstance().getWeatherForecast().getList() != null && WeatherInfo.getInstance().getWeatherForecast().getList().length >= Integer.parseInt(this.getString(R.string.weather_forecast_count_url)))
        {
            // If required information is not missing, set layout for temperature, slide buttons and static TextView.
            rootView.findViewById(R.id.weather_forecast_temperature_layout).setBackgroundResource(R.drawable.dark_grey_rectangle_rounded_corners);

            if(this.getPageIndex() != 0)
            {
                Button slideLeftButton = (Button) rootView.findViewById(R.id.weather_forecast_slide_left);

                slideLeftButton.setVisibility(View.VISIBLE);

                slideLeftButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        if(WeatherForecastSlideFragment.this.getPageIndex() != 0)
                        {
                            ((WeatherForecastActivity) WeatherForecastSlideFragment.this.getActivity()).setCurrentItem(WeatherForecastSlideFragment.this.getPageIndex() - 1, true);
                        }
                    }
                });
            }

            if(this.getPageIndex() != (Integer.parseInt(this.getString(R.string.weather_forecast_count_url)) - 1))
            {
                rootView.findViewById(R.id.weather_forecast_slide_right).setVisibility(View.VISIBLE);

                Button slideRightButton = (Button) rootView.findViewById(R.id.weather_forecast_slide_right);

                slideRightButton.setVisibility(View.VISIBLE);

                slideRightButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        if(WeatherForecastSlideFragment.this.getPageIndex() != (Integer.parseInt(WeatherForecastSlideFragment.this.getString(R.string.weather_forecast_count_url)) - 1))
                        {
                            ((WeatherForecastActivity) WeatherForecastSlideFragment.this.getActivity()).setCurrentItem(WeatherForecastSlideFragment.this.getPageIndex() + 1, true);
                        }
                    }
                });
            }

            ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_title)).setText(this.getString(R.string.weather_forecast_temperature));

            ((TextView) rootView.findViewById(R.id.weather_forecast_navigation_info)).setText(this.getString(R.string.weather_forecast_navigation_info));

            // Set current location.
            if (WeatherInfo.getInstance().getWeatherForecast().getCity() != null && WeatherInfo.getInstance().getWeatherForecast().getCity().getName() != null && WeatherInfo.getInstance().getWeatherForecast().getCity().getCounrty() != null)
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_current_location)).setText(String.format(Locale.CANADA, "%s, %s", WeatherInfo.getInstance().getWeatherForecast().getCity().getName(), WeatherInfo.getInstance().getWeatherForecast().getCity().getCounrty()));
            }

            // Set weather description value.
            if (WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather() != null && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather().length > 0 && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather()[0].getDescription() != null)
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_description)).setText(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather()[0].getDescription().toUpperCase());
            }

            // Set humidity value.
            ((TextView) rootView.findViewById(R.id.weather_forecast_humidity)).setText(String.format(Locale.CANADA, "Humidity: %d%%", WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getHumidity()));

            // Set wind value.
            String windPreference = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(this.getString(R.string.preferences_wind_speed_units), null);

            if(windPreference != null)
            {
                if (windPreference.equals(this.getResources().getStringArray(R.array.preferences_wind_speed_units_values)[0]))
                {
                    ((TextView) rootView.findViewById(R.id.weather_forecast_wind)).setText(String.format(Locale.CANADA, "Wind: %s %.2f km/h", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getDeg()), WeatherInfo.getInstance().getKilometersPerHour(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getSpeed())));
                }
                else if (windPreference.equals(this.getResources().getStringArray(R.array.preferences_wind_speed_units_values)[1]))
                {
                    ((TextView) rootView.findViewById(R.id.weather_forecast_wind)).setText(String.format(Locale.CANADA, "Wind: %s %.2f m/s", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getDeg()), WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getSpeed()));
                }
            }
            else
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_wind)).setText(String.format(Locale.CANADA, "Wind: %s %.2f m/s", WeatherInfo.getInstance().getWindDirection(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getDeg()), WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getSpeed()));
            }

            // Set pressure value.
            String pressurePreference = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(this.getString(R.string.preferences_pressure_units), null);

            if(pressurePreference != null)
            {
                if (pressurePreference.equals(this.getResources().getStringArray(R.array.preferences_pressure_units_values)[0]))
                {
                    ((TextView) rootView.findViewById(R.id.weather_forecast_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f mbar", WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getPressure()));
                }
                else if (pressurePreference.equals(this.getResources().getStringArray(R.array.preferences_pressure_units_values)[1]))
                {
                    ((TextView) rootView.findViewById(R.id.weather_forecast_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f kPa", WeatherInfo.getInstance().getPressureKPA(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getPressure())));
                }
            }
            else
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_pressure)).setText(String.format(Locale.CANADA, "Pressure: %.1f kPa", WeatherInfo.getInstance().getPressureKPA(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getPressure())));
            }

            // Set cloudiness value.
            if(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getClouds() != null)
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_cloudiness)).setText(String.format(Locale.CANADA, "Cloudiness: %s%%", WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getClouds()));
            }

            // Set date value.
            if (WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getDt() != null)
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_date)).setText(new SimpleDateFormat("EEE, d MMM yyyy", Locale.CANADA).format(new Date(Integer.parseInt(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getDt()) * (long) 1000)));
            }

            // Set weather icon.
            if (WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather() != null && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather().length > 0 && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather()[0].getIcon() != null)
            {
                (rootView.findViewById(R.id.weather_forecast_weather_icon)).setBackgroundResource(this.getIconResource(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather()[0].getIcon()));
            }

            // Set morning temperature value.
            if(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp() != null)
            {
                String temperaturePreference = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(this.getString(R.string.preferences_temperature_units), null);

                if(temperaturePreference != null)
                {
                    if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[0]))
                    {
                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_morning)).setText(String.format(Locale.CANADA, "Morning: %d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getMorn())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_day)).setText(String.format(Locale.CANADA, "Day: %d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getDay())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_evening)).setText(String.format(Locale.CANADA, "Evening: %d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getEve())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_night)).setText(String.format(Locale.CANADA, "Night: %d%sF", Math.round(WeatherInfo.getInstance().getFahrenheit(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getNight())), (char) 0x00B0));
                    }
                    else if (temperaturePreference.equals(this.getResources().getStringArray(R.array.preferences_temperature_units_values)[1]))
                    {
                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_morning)).setText(String.format(Locale.CANADA, "Morning: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getMorn())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_day)).setText(String.format(Locale.CANADA, "Day: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getDay())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_evening)).setText(String.format(Locale.CANADA, "Evening: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getEve())), (char) 0x00B0));

                        ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_night)).setText(String.format(Locale.CANADA, "Night: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getNight())), (char) 0x00B0));
                    }
                }
                else
                {
                    ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_morning)).setText(String.format(Locale.CANADA, "Morning: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getMorn())), (char) 0x00B0));

                    ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_day)).setText(String.format(Locale.CANADA, "Day: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getDay())), (char) 0x00B0));

                    ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_evening)).setText(String.format(Locale.CANADA, "Evening: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getEve())), (char) 0x00B0));

                    ((TextView) rootView.findViewById(R.id.weather_forecast_temperature_night)).setText(String.format(Locale.CANADA, "Night: %d%sC", Math.round(WeatherInfo.getInstance().getCelsius(WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getTemp().getNight())), (char) 0x00B0));
                }
            }

            return rootView;
        }
        else
        {
            // Set weather description value.
            if (WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather() != null && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather().length > 0 && WeatherInfo.getInstance().getWeatherForecast().getList()[this.getPageIndex()].getWeather()[0].getDescription() != null)
            {
                ((TextView) rootView.findViewById(R.id.weather_forecast_description)).setText(this.getString(R.string.data_not_available));
            }

            return rootView;
        }
    }

    // Setter and getter for pageIndex property.
    private void setPageIndex(int pageIndex)
    {
        this.pageIndex = pageIndex;
    }

    private int getPageIndex()
    {
        return this.pageIndex;
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
