package com.myoneweathertest.android.util;

import android.text.TextUtils;

import com.myoneweathertest.android.db.City;
import com.myoneweathertest.android.db.County;
import com.myoneweathertest.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    //1解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray=new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    int id=jsonObject.getInt("id");
                   String name =jsonObject.getString("name");
                    Province province=new Province();//表：省份表
                    province.setProvinceCode(id);//设定省份代号的值
                    province.setProvinceName(name);//设定省份名称的值
                    province.save();//保存表中的信息
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }


    //2解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray=new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    int id=jsonObject.getInt("id");
                    String name =jsonObject.getString("name");
                    City city=new City();//表：省份表
                    city.setCityCode(id);//设定市代号的值
                    city.setCityName(name);//设定市名称的值
                    city.setProvinceId(provinceId);//======设定所属的省级代号
                    city.save();//保存表中的信息
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }


    //3解析和处理服务器返回的县级数据

    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray=new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    int id=jsonObject.getInt("id");
                    String name =jsonObject.getString("name");
                    String weather_id =jsonObject.getString("weather_id");

                    County county=new County();//表：县级表

                    county.setCountyName(name);//设定县名称的值
                    county.setWeatherId(weather_id);//设定县的天气的id值
                    county.setCityId(cityId);        //======设定所属的市级代号

                    county.save();//保存表中的信息
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
