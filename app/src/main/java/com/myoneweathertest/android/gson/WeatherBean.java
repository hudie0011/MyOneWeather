package com.myoneweathertest.android.gson;

import android.text.style.SuggestionSpan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherBean {
    public String status;
    public BasicBean basic;
    public AqiBean aqi;
    public NowBean now;
    public SuggestionBean suggestion;

  /*  @SerializedName("daily_forecast")
    public List<DailyForecastBean> forecastList;
    */
    public List<DailyForecastBean> daily_forecast;

}
