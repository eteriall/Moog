package com.example.moog;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.List;

public class WeatherResponse {
    public class WeatherTemp {
        Double temp;
        Double temp_min;
        Double temp_max;
    }

    public class WeatherDescription {
        String icon;
    }

    @SerializedName("main")
    private WeatherTemp temp;

    @SerializedName("weather")
    private List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long timestamp;

    public WeatherResponse(WeatherTemp temp, List<WeatherDescription> desctiption) {
        this.temp = temp;
        this.desctiption = desctiption;
    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp * 1000);
        return date;
    }

    public String getTemp() {
        return String.valueOf(temp.temp);
    }

    public String getTempMin() {
        return String.valueOf(temp.temp_min);
    }

    public String getTempMax() {
        return String.valueOf(temp.temp_max);
    }

    public String getTempInteger() {
        return String.valueOf(temp.temp.intValue());
    }

    public String getTempWithDegree() {
        return String.valueOf(temp.temp.intValue()) + "\u00B0";
    }

    public String getCity() {
        return city;
    }

    public String getIcon() {
        return desctiption.get(0).icon;
    }

    public String getIconUrl() {
        return "http://openweathermap.org/img/w/" + desctiption.get(0).icon + ".png";
    }
}