package com.myoneweathertest.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myoneweathertest.android.activity.WeatherActivity;
import com.myoneweathertest.android.gson.WeatherBean;
import com.myoneweathertest.android.util.HttpUtil;
import com.myoneweathertest.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AutoUpdateService extends Service {



    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anhour=8*60*60*1000;
        long triaggerAtTime= SystemClock.elapsedRealtime()+anhour;
        Intent intent1=new Intent(this,AutoUpdateService.class);
        PendingIntent  pendingIntent=PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triaggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            WeatherBean weatherBean= Utility.handleWeatherResponse(weatherString);
            String weatherId=weatherBean.basic.id;
            String weatherUrl="http://guolin.tech/api/weather?city="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new okhttp3.Callback() {


                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        String responseText=response.body().string();
                        WeatherBean weatherBean1=Utility.handleWeatherResponse(responseText);
                    if (weatherBean1 != null && "ok".equals(weatherBean1.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                   }
                }


                @Override
                public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                }
            });
        }
    }

    private void updateBingPic() {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new okhttp3.Callback() {


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic= response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }
}
