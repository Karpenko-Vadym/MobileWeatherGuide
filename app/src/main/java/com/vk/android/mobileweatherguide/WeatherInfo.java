package com.vk.android.mobileweatherguide;

import java.util.Date;
import java.util.List;

public class WeatherInfo
{
    private static WeatherInfo weatherInfo;
    private CurrentWeather currentWeather;
    private HourlyForecast hourlyForecast;
    private WeatherForecast weatherForecast;

    private WeatherInfo() { }

    public synchronized static WeatherInfo getInstance() // Singleton pattern.
    {
        if(weatherInfo == null)
        {
            weatherInfo = new WeatherInfo();
        }

        return weatherInfo;
    }

    public CurrentWeather getCurrentWeather()
    {
        return this.currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather)
    {
        this.currentWeather = currentWeather;
    }

    protected class CurrentWeather
    {
        private String base;             // Internal parameter.
        private Clouds clouds;
        private String cod;              // Internal parameter.
        private Coordinates coord;
        private String dt;               // Time of data calculation, unix, UTC.
        private String id;               // City ID.
        private Main main;
        private String name;             // City name.
        private Rain rain;
        private Snow snow;
        private System sys;
        private Weather[] weather;
        private Wind wind;
        private UVI uvIndex;
        private Date timestamp;

        public String getBase()
        {
            return this.base;
        }

        public void setBase(String base)
        {
            this.base = base;
        }

        public Clouds getClouds()
        {
            return this.clouds;
        }

        public void setClouds(Clouds clouds)
        {
            this.clouds = clouds;
        }

        public String getCod()
        {
            return this.cod;
        }

        public void setCod(String cod)
        {
            this.cod = cod;
        }

        public Coordinates getCoord()
        {
            return this.coord;
        }

        public void setCoord(Coordinates coord)
        {
            this.coord = coord;
        }

        public String getDt()
        {
            return this.dt;
        }

        public void setDt(String dt)
        {
            this.dt = dt;
        }

        public String getId()
        {
            return this.id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public Main getMain()
        {
            return this.main;
        }

        public void setMain(Main main)
        {
            this.main = main;
        }

        public String getName()
        {
            return this.name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Rain getRain()
        {
            return this.rain;
        }

        public void setRain(Rain rain)
        {
            this.rain = rain;
        }

        public Snow getSnow()
        {
            return this.snow;
        }

        public void setSnow(Snow snow)
        {
            this.snow = snow;
        }

        public System getSys()
        {
            return this.sys;
        }

        public void setSys(System sys)
        {
            this.sys = sys;
        }

        public Weather[] getWeather()
        {
            return this.weather;
        }

        public void setWeather(Weather[] weather)
        {
            this.weather = weather;
        }

        public Wind getWind()
        {
            return this.wind;
        }

        public void setWind(Wind wind)
        {
            this.wind = wind;
        }

        public UVI getUvIndex()
        {
            return this.uvIndex;
        }

        public void setUvIndex(UVI uvIndex)
        {
            this.uvIndex = uvIndex;
        }

        public Date getTimestamp()
        {
            return this.timestamp;
        }

        public void setTimestamp(Date timestamp)
        {
            this.timestamp = timestamp;
        }

        protected class Clouds
        {
            private String all;          // Cloudiness, %.

            public String getAll()
            {
                return this.all;
            }

            public void setAll(String all)
            {
                this.all = all;
            }
        }

        protected class Coordinates
        {
            private double lat;                 // City geo location, latitude.
            private double lon;                 // City geo location, longitude.

            public double getLat()
            {
                return this.lat;
            }

            public void setLat(double lat)
            {
                this.lat = lat;
            }

            public double getLon()
            {
                return this.lon;
            }

            public void setLon(double lon)
            {
                this.lon = lon;
            }
        }

        protected class Main
        {
            private int humidity;        // Humidity, %.
            private double pressure;     // Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa.
            private double sea_level;    // Atmospheric pressure on the sea level, hPa
            private double grnd_level;   // Atmospheric pressure on the ground level, hPa
            private double temp;         // Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double temp_max;     // Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double temp_min;     // Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.

            public double getTemp_min()
            {
                return temp_min;
            }

            public void setTemp_min(double temp_min)
            {
                this.temp_min = temp_min;
            }

            public int getHumidity()
            {
                return this.humidity;
            }

            public void setHumidity(int humidity)
            {
                this.humidity = humidity;
            }

            public double getPressure()
            {
                return this.pressure;
            }

            public double getSea_level()
            {
                return this.sea_level;
            }

            public void setSea_level(double sea_level)
            {
                this.sea_level = sea_level;
            }

            public double getGrnd_level()
            {
                return this.grnd_level;
            }

            public void setGrnd_level(double grnd_level)
            {
                this.grnd_level = grnd_level;
            }

            public void setPressure(int pressure)
            {
                this.pressure = pressure;
            }

            public double getTemp()
            {
                return this.temp;
            }

            public void setTemp(double temp)
            {
                this.temp = temp;
            }

            public double getTemp_max()
            {
                return this.temp_max;
            }

            public void setTemp_max(double temp_max)
            {
                this.temp_max = temp_max;
            }
        }

        protected class Rain
        {
            private double last3h;       // Rain volume for the last 3 hours.
            private double last1h;       // Rain volume for the last 3 hours.

            public double getLast3h()
            {
                return this.last3h;
            }

            public void setLast3h(double last3h)
            {
                this.last3h = last3h;
            }

            public double getLast1h()
            {
                return this.last1h;
            }

            public void setLast1h(double last1h)
            {
                this.last1h = last1h;
            }
        }

        protected class Snow
        {
            private double last3h;       // Snow volume for the last 3 hours.
            private double last1h;       // Snow volume for the last 3 hours.

            public double getLast3h()
            {
                return this.last3h;
            }

            public void setLast3h(double last3h)
            {
                this.last3h = last3h;
            }

            public double getLast1h()
            {
                return this.last1h;
            }

            public void setLast1h(double last1h)
            {
                this.last1h = last1h;
            }
        }

        protected class System
        {
            private String country;      // Country code (GB, JP etc.)
            private String id;           // Internal parameter.
            private double message;      // Internal parameter.
            private String sunrise;      // Sunrise time, unix, UTC.
            private String sunset;       // Sunset time, unix, UTC.
            private int type;            // Internal parameter.

            public String getCountry()
            {
                return this.country;
            }

            public void setCountry(String country)
            {
                this.country = country;
            }

            public String getId()
            {
                return this.id;
            }

            public void setId(String id)
            {
                this.id = id;
            }

            public double getMessage()
            {
                return this.message;
            }

            public void setMessage(double message)
            {
                this.message = message;
            }

            public String getSunrise()
            {
                return this.sunrise;
            }

            public void setSunrise(String sunrise)
            {
                this.sunrise = sunrise;
            }

            public String getSunset()
            {
                return this.sunset;
            }

            public void setSunset(String sunset)
            {
                this.sunset = sunset;
            }

            public int getType()
            {
                return this.type;
            }

            public void setType(int type)
            {
                this.type = type;
            }
        }

        protected class Weather
        {
            private String description;  // Weather condition within the group.
            private String icon;         // Weather icon id.
            private String id;           // Weather condition id.
            private String main;         // Group of weather parameters (Rain, Snow, Extreme etc.).

            public String getDescription()
            {
                return this.description;
            }

            public void setDescription(String description)
            {
                this.description = description;
            }

            public String getIcon()
            {
                return this.icon;
            }

            public void setIcon(String icon)
            {
                this.icon = icon;
            }

            public String getId()
            {
                return this.id;
            }

            public void setId(String id)
            {
                this.id = id;
            }

            public String getMain()
            {
                return this.main;
            }

            public void setMain(String main)
            {
                this.main = main;
            }
        }

        protected class Wind
        {
            private double deg;          // Wind direction, degrees (meteorological).
            private double speed;        // Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.

            public double getDeg()
            {
                return this.deg;
            }

            public void setDeg(double deg)
            {
                this.deg = deg;
            }

            public double getSpeed()
            {
                return this.speed;
            }

            public void setSpeed(double speed)
            {
                this.speed = speed;
            }
        }

        protected class UVI
        {
            private double data;        // UV Index value.
            private Location location;

            public double getData()
            {
                return this.data;
            }

            public void setData(double data)
            {
                this.data = data;
            }

            protected class Location
            {
                private double latitude;
                private double longitude;

                public double getLongitude()
                {
                    return this.longitude;
                }

                public void setLongitude(double longitude)
                {
                    this.longitude = longitude;
                }

                public double getLatitude()
                {
                    return this.latitude;
                }

                public void setLatitude(double latitude)
                {
                    this.latitude = latitude;
                }
            }
        }
    }

    public HourlyForecast getHourlyForecast()
    {
        return this.hourlyForecast;
    }

    public void setHourlyForecast(HourlyForecast hourlyForecast)
    {
        this.hourlyForecast = hourlyForecast;
    }

    protected class HourlyForecast
    {
        private City city;
        private int cnt;
        private String cod;                     // Internal parameter.
        private HourlyWeather[] list;
        private String message;                 // Internal parameter.

        public City getCity()
        {
            return this.city;
        }

        public void setCity(City city)
        {
            this.city = city;
        }

        public int getCnt()
        {
            return this.cnt;
        }

        public void setCnt(int cnt)
        {
            this.cnt = cnt;
        }

        public String getCod()
        {
            return this.cod;
        }

        public void setCod(String cod)
        {
            this.cod = cod;
        }

        public HourlyWeather[] getList()
        {
            return this.list;
        }

        public void setList(HourlyWeather[] list)
        {
            this.list = list;
        }

        public String getMessage()
        {
            return this.message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        protected class City
        {
            private Coordinates coord;
            private String country;                 // Country code (GB, JP etc.).
            private String id;                      // City ID.
            private String name;                    // City name.
            private String population;
            private System sys;

            public Coordinates getCoord()
            {
                return this.coord;
            }

            public void setCoord(Coordinates coord)
            {
                this.coord = coord;
            }

            public String getCounrty()
            {
                return this.country;
            }

            public void setCounrty(String country)
            {
                this.country = country;
            }

            public String getId()
            {
                return this.id;
            }

            public void setId(String id)
            {
                this.id = id;
            }

            public String getName()
            {
                return this.name;
            }

            public void setName(String name)
            {
                this.name = name;
            }

            public String getPopulation()
            {
                return this.population;
            }

            public void setPopulation(String population)
            {
                this.population = population;
            }

            public System getSys()
            {
                return this.sys;
            }

            public void setSys(System sys)
            {
                this.sys = sys;
            }

            protected class Coordinates
            {
                private double lat;                 // City geo location, latitude.
                private double lon;                 // City geo location, longitude.

                public double getLat()
                {
                    return this.lat;
                }

                public void setLat(double lat)
                {
                    this.lat = lat;
                }

                public double getLon()
                {
                    return this.lon;
                }

                public void setLon(double lon)
                {
                    this.lon = lon;
                }
            }

            protected class System
            {
                private String population;

                public String getPopulation()
                {
                    return this.population;
                }

                public void setPopulation(String population)
                {
                    this.population = population;
                }
            }
        }

        protected class HourlyWeather
        {
            private Clouds clouds;
            private String dt;                  // Time of data forecasted, unix, UTC.
            private String dt_txt;              // Data/time of caluclation, UTC.
            private Main main;
            private System sys;
            private Weather[] weather;
            private Wind wind;
            private Rain rain;
            private Snow snow;

            public Clouds getClouds()
            {
                return this.clouds;
            }

            public void setClouds(Clouds clouds)
            {
                this.clouds = clouds;
            }

            public String getDt()
            {
                return this.dt;
            }

            public void setDt(String dt)
            {
                this.dt = dt;
            }

            public String getDt_txt()
            {
                return this.dt_txt;
            }

            public void setDt_txt(String dt_txt)
            {
                this.dt_txt = dt_txt;
            }

            public Main getMain()
            {
                return this.main;
            }

            public void setMain(Main main)
            {
                this.main = main;
            }

            public System getSys()
            {
                return this.sys;
            }

            public void setSys(System sys)
            {
                this.sys = sys;
            }

            public Weather[] getWeather()
            {
                return this.weather;
            }

            public void setWeather(Weather[] weather)
            {
                this.weather = weather;
            }

            public Wind getWind()
            {
                return this.wind;
            }

            public void setWind(Wind wind)
            {
                this.wind = wind;
            }

            public Rain getRain()
            {
                return this.rain;
            }

            public void setRain(Rain rain)
            {
                this.rain = rain;
            }

            public Snow getSnow()
            {
                return this.snow;
            }

            public void setSnow(Snow snow)
            {
                this.snow = snow;
            }

            protected class Clouds
            {
                private String all;          // Cloudiness, %.

                public String getAll()
                {
                    return this.all;
                }

                public void setAll(String all)
                {
                    this.all = all;
                }
            }

            protected class Main
            {
                private int humidity;        // Humidity, %.
                private double pressure;     // Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa.
                private double sea_level;    // Atmospheric pressure on the sea level, hPa
                private double grnd_level;   // Atmospheric pressure on the ground level, hPa
                private double temp;         // Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                private double temp_kf;      // Internal parameter.
                private double temp_max;     // Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
                private double temp_min;     // Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.

                public double getTemp_min()
                {
                    return temp_min;
                }

                public void setTemp_min(double temp_min)
                {
                    this.temp_min = temp_min;
                }

                public int getHumidity()
                {
                    return this.humidity;
                }

                public void setHumidity(int humidity)
                {
                    this.humidity = humidity;
                }

                public double getPressure()
                {
                    return this.pressure;
                }

                public double getSea_level()
                {
                    return this.sea_level;
                }

                public void setSea_level(double sea_level)
                {
                    this.sea_level = sea_level;
                }

                public double getGrnd_level()
                {
                    return this.grnd_level;
                }

                public void setGrnd_level(double grnd_level)
                {
                    this.grnd_level = grnd_level;
                }

                public double getPressureKPA(double pressure)
                {
                    return pressure * 0.1;
                }

                public void setPressure(int pressure)
                {
                    this.pressure = pressure;
                }

                public double getTemp()
                {
                    return this.temp;
                }

                public void setTemp(double temp)
                {
                    this.temp = temp;
                }

                public double getTemp_max()
                {
                    return this.temp_max;
                }

                public void setTemp_max(double temp_max)
                {
                    this.temp_max = temp_max;
                }
            }

            protected class System
            {
                private String n;

                public String getN()
                {
                    return this.n;
                }

                public void setN(String n)
                {
                    this.n = n;
                }
            }

            protected class Weather
            {
                private String description;  // Weather condition within the group.
                private String icon;         // Weather icon id.
                private String id;           // Weather condition id.
                private String main;         // Group of weather parameters (Rain, Snow, Extreme etc.).

                public String getDescription()
                {
                    return this.description;
                }

                public void setDescription(String description)
                {
                    this.description = description;
                }

                public String getIcon()
                {
                    return this.icon;
                }

                public void setIcon(String icon)
                {
                    this.icon = icon;
                }

                public String getId()
                {
                    return this.id;
                }

                public void setId(String id)
                {
                    this.id = id;
                }

                public String getMain()
                {
                    return this.main;
                }

                public void setMain(String main)
                {
                    this.main = main;
                }
            }

            protected class Wind
            {
                private double deg;          // Wind direction, degrees (meteorological).
                private double speed;        // Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.

                public double getDeg()
                {
                    return this.deg;
                }

                public void setDeg(double deg)
                {
                    this.deg = deg;
                }

                public double getSpeed()
                {
                    return this.speed;
                }

                public void setSpeed(double speed)
                {
                    this.speed = speed;
                }
            }

            protected class Rain
            {
                private double last3h;       // Rain volume for the last 3 hours.
                private double last1h;       // Rain volume for the last 1 hours.

                public double getLast3h()
                {
                    return this.last3h;
                }

                public void setLast3h(double last3h)
                {
                    this.last3h = last3h;
                }

                public double getLast1h()
                {
                    return this.last1h;
                }

                public void setLast1h(double last1h)
                {
                    this.last1h = last1h;
                }
            }

            protected class Snow
            {
                private double last3h;       // Snow volume for the last 3 hours.
                private double last1h;       // Snow volume for the last 1 hour.

                public double getLast3h()
                {
                    return this.last3h;
                }

                public void setLast3h(double last3h)
                {
                    this.last3h = last3h;
                }

                public double getLast1h()
                {
                    return this.last1h;
                }

                public void setLast1h(double last1h)
                {
                    this.last1h = last1h;
                }
            }
        }
    }

    public WeatherForecast getWeatherForecast()
    {
        return this.weatherForecast;
    }

    public void setWeatherForecast(WeatherForecast weatherForecast)
    {
        this.weatherForecast = weatherForecast;
    }

    protected class WeatherForecast
    {
        private int cnt;
        private String cod;
        private String message;
        private City city;
        private DailyForecast[] list;
        private Date timestamp;

        public int getCnt()
        {
            return this.cnt;
        }

        public void setCnt(int cnt)
        {
            this.cnt = cnt;
        }

        public String getCod()
        {
            return this.cod;
        }

        public void setCod(String cod)
        {
            this.cod = cod;
        }

        public String getMessage()
        {
            return this.message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public City getCity()
        {
            return this.city;
        }

        public void setCity(City city)
        {
            this.city = city;
        }

        public DailyForecast[] getList()
        {
            return this.list;
        }

        public void setList(DailyForecast[] list)
        {
            this.list = list;
        }

        public Date getTimestamp()
        {
            return this.timestamp;
        }

        public void setTimestamp(Date timestamp)
        {
            this.timestamp = timestamp;
        }

        protected class City
        {
            private Coordinates coord;
            private String country;                 // Country code (GB, JP etc.).
            private String id;                      // City ID.
            private String name;                    // City name.
            private String population;

            public Coordinates getCoord()
            {
                return this.coord;
            }

            public void setCoord(Coordinates coord)
            {
                this.coord = coord;
            }

            public String getCounrty()
            {
                return this.country;
            }

            public void setCounrty(String country)
            {
                this.country = country;
            }

            public String getId()
            {
                return this.id;
            }

            public void setId(String id)
            {
                this.id = id;
            }

            public String getName()
            {
                return this.name;
            }

            public void setName(String name)
            {
                this.name = name;
            }

            public String getPopulation()
            {
                return this.population;
            }

            public void setPopulation(String population)
            {
                this.population = population;
            }

            protected class Coordinates
            {
                private double lat;                 // City geo location, latitude.
                private double lon;                 // City geo location, longitude.

                public double getLat()
                {
                    return this.lat;
                }

                public void setLat(double lat)
                {
                    this.lat = lat;
                }

                public double getLon()
                {
                    return this.lon;
                }

                public void setLon(double lon)
                {
                    this.lon = lon;
                }
            }
        }

        protected class DailyForecast
        {
            private int humidity;        // Humidity, %.
            private double deg;          // Wind direction, degrees (meteorological).
            private double speed;        // Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
            private double pressure;     // Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa.
            private double snow;         // Snow amount.
            private double rain;         // Rain amount.
            private String dt;           // Time of data forecasted, unix, UTC.
            private String clouds;       // Cloudiness %.
            private Weather[] weather;
            private Temperature temp;

            public int getHumidity()
            {
                return this.humidity;
            }

            public void setHumidity(int humidity)
            {
                this.humidity = humidity;
            }

            public double getDeg()
            {
                return this.deg;
            }

            public void setDeg(double deg)
            {
                this.deg = deg;
            }

            public double getSpeed()
            {
                return this.speed;
            }

            public void setSpeed(double speed)
            {
                this.speed = speed;
            }

            public double getPressure()
            {
                return this.pressure;
            }

            public void setPressure(double pressure)
            {
                this.pressure = pressure;
            }

            public double getSnow()
            {
                return this.snow;
            }

            public void setSnow(double snow)
            {
                this.snow = snow;
            }

            public double getRain()
            {
                return this.rain;
            }

            public void setRain(double rain)
            {
                this.rain = rain;
            }

            public String getDt()
            {
                return this.dt;
            }

            public void setDt(String dt)
            {
                this.dt = dt;
            }

            public String getClouds()
            {
                return this.clouds;
            }

            public void setClouds(String clouds)
            {
                this.clouds = clouds;
            }

            public Weather[] getWeather()
            {
                return this.weather;
            }

            public void setWeather(Weather[] weather)
            {
                this.weather = weather;
            }

            public Temperature getTemp()
            {
                return this.temp;
            }

            public void setTemp(Temperature temp)
            {
                this.temp = temp;
            }

            protected class Weather
            {
                private String description;  // Weather condition within the group.
                private String icon;         // Weather icon id.
                private String id;           // Weather condition id.
                private String main;         // Group of weather parameters (Rain, Snow, Extreme etc.).

                public String getDescription()
                {
                    return this.description;
                }

                public void setDescription(String description)
                {
                    this.description = description;
                }

                public String getIcon()
                {
                    return this.icon;
                }

                public void setIcon(String icon)
                {
                    this.icon = icon;
                }

                public String getId()
                {
                    return this.id;
                }

                public void setId(String id)
                {
                    this.id = id;
                }

                public String getMain()
                {
                    return this.main;
                }

                public void setMain(String main)
                {
                    this.main = main;
                }
            }
        }

        protected class Temperature
        {
            private double day;         // Day temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double eve;         // Evening temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double max;         // Max daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double min;         // Min daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double morn;        // Morning temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
            private double night;       // Night temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.

            public double getDay()
            {
                return this.day;
            }

            public void setDay(double day)
            {
                this.day = day;
            }

            public double getEve()
            {
                return this.eve;
            }

            public void setEve(double eve)
            {
                this.eve = eve;
            }

            public double getMax()
            {
                return this.max;
            }

            public void setMax(double max)
            {
                this.max = max;
            }

            public double getMin()
            {
                return this.min;
            }

            public void setMin(double min)
            {
                this.min = min;
            }

            public double getMorn()
            {
                return this.morn;
            }

            public void setMorn(double morn)
            {
                this.morn = morn;
            }

            public double getNight()
            {
                return this.night;
            }

            public void setNight(double night)
            {
                this.night = night;
            }
        }
    }

    // Convert temperature from kelvin to Fahrenheit.
    protected double getFahrenheit(double kelvinTemperature)
    {
        return kelvinTemperature * 9 / 5 - 459.67;
    }

    // Convert temperature from kelvin to Celsius.
    protected double getCelsius(double kelvinTemperature)
    {
        return kelvinTemperature - 273.15;
    }

    // Convert degrees to direction.
    protected String getWindDirection(double degree)
    {
        String[] directions = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

        return directions[(int)((degree / 45) % 8)];
    }

    protected double getKilometersPerHour(double metersPerSecond)
    {
        return metersPerSecond * 3.6;
    }

    protected double getPressureKPA(double pressure)
    {
        return pressure * 0.1;
    }
}

