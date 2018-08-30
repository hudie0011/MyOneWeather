package com.myoneweathertest.android.gson;

public  class DailyForecastBean {
public String date;
public tmpBean tmp;
public condBean cond;

public class tmpBean{
    public String max;
    public String min;
}

    public class condBean{
        public String txt_d;
        }

}
