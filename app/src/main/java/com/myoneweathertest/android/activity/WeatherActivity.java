package com.myoneweathertest.android.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myoneweathertest.android.R;
import com.myoneweathertest.android.gson.DailyForecastBean;
import com.myoneweathertest.android.gson.WeatherBean;
import com.myoneweathertest.android.util.HttpUtil;
import com.myoneweathertest.android.util.Utility;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private static final String TAG = "WeatherActivity";

    private ImageView bingPicImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ronghetitlt();
       initView();
      initData();
      navButtonsetonclick();
    }



    //背景图和状态栏融合到一起
    private void ronghetitlt() {
      if(Build.VERSION.SDK_INT>=21){
          View decroView=getWindow().getDecorView();
          decroView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
          getWindow().setStatusBarColor(Color.TRANSPARENT);
      }
    }


    //初始化各个控件
    private void initView() {
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity =findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text =findViewById(R.id.pm25_text);
        comfortText =findViewById(R.id.comfort_text);
        carWashText =findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg=findViewById(R.id.bing_pic_img);
        navButton=findViewById(R.id.nav_button);
    }

    private void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        Log.d(TAG, "initData: "+weatherString);
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            WeatherBean weather = Utility.handleWeatherResponse(weatherString);
             showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
          String  mWeatherId = getIntent().getStringExtra("weather_id");
            Log.d(TAG, "initData: "+mWeatherId);
          weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);

       }
    }



    /**
     * 根据天气id请求城市天气信息。
     */
    private void requestWeather(final  String WeatherId) {
        final String weatherUrl = "http://guolin.tech/api/weather?cityid=" + WeatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
       // http://guolin.tech/api/weather?cityid=CN101190407&key=bc0418b57b2d4918819d3974ac1285d9
        Toast.makeText(WeatherActivity.this, WeatherId, Toast.LENGTH_SHORT).show();
        HttpUtil.sendOkHttpRequest(weatherUrl, new okhttp3.Callback() {

       @Override
            public void onResponse(Call call, Response response) throws IOException {
                 final String responseText = response.body().string();
                 Log.d(TAG, "onResponse: "+responseText);
                 final WeatherBean weather = Utility.handleWeatherResponse(responseText);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            Toast.makeText(WeatherActivity.this, "天气信息获取成功，已存放到缓存中", Toast.LENGTH_SHORT).show();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "从缓存获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                }
            });
            }

        });
        loadBingPic();
   }


    /**
     * 处理并展示Weather实体类中的数据。
     */

    private void showWeatherInfo(WeatherBean weather) {
        String cityName = weather.basic.city;
        Log.d(TAG, "showWeatherInfo: "+cityName);
        
        String updateTime = weather.basic.update.loc.split(" ")[1];
        Log.d(TAG, "showWeatherInfo: "+updateTime);

        String degree = weather.now.tmp + "℃";
        Log.d(TAG, "showWeatherInfo: "+degree);

        String weatherInfo = weather.now.cond.txt;
        Log.d(TAG, "showWeatherInfo: "+weatherInfo);

        titleCity.setText(cityName);

        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        Log.d(TAG, "showWeatherInfo: 11111"+weather.daily_forecast);
        for (DailyForecastBean forecast : weather.daily_forecast) {
            Log.d(TAG, "showWeatherInfo: 0000"+weather.daily_forecast);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText =view.findViewById(R.id.date_text);
            TextView infoText =view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText =view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond.txt_d);
            maxText.setText(forecast.tmp.max+"℃");
            minText.setText(forecast.tmp.min+"℃");
            forecastLayout.addView(view);
        }

        Log.d(TAG, "showWeatherInfo: "+weather.aqi);
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            Log.d(TAG, "showWeatherInfo: "+weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            Log.d(TAG, "showWeatherInfo: "+weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comf.txt;
        Log.d(TAG, "showWeatherInfo: "+comfort);

        String carWash = "洗车指数：" + weather.suggestion.cw.txt;
        Log.d(TAG, "showWeatherInfo: "+carWash);

        String sport = "运行建议：" + weather.suggestion.sport.txt;
        Log.d(TAG, "showWeatherInfo: "+sport);

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);


    }


    /**
     * 加载必应每日一图
      */
    private void loadBingPic() {
       String requestBingPic="http://guolin.tech/api/bing_pic";
       HttpUtil.sendOkHttpRequest(requestBingPic, new okhttp3.Callback() {


           @Override
           public void onResponse(Call call, Response response) throws IOException {
               final String bingPic= response.body().string();
               SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
               editor.putString("bing_pic",bingPic);
               editor.apply();
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                   }
               });
           }

           @Override
           public void onFailure(Call call, IOException e) {
                  e.printStackTrace();
           }
       });

    }

    private void navButtonsetonclick() {
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherActivity.this.finish();
            }
        });
    }
}
