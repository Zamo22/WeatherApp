package com.dvt.zaheer.weatherapp;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    JSONObject data = null;
    public static  double lat;
    public static double lon;

    public static ImageView wallpaper;
    public static TextView forecastDegrees;
    public static TextView forecast;
    public static TextView degCurrent;
    public  static TextView degMin;
    public static TextView degMax;
    public TextView city;
    public static View backG;
    public static TextView d1Day;
    public static ImageView d1Pic;
    public static TextView d1Temp;
    public static TextView d2Day;
    public static ImageView d2Pic;
    public static TextView d2Temp;
    public static TextView d3Day;
    public static ImageView d3Pic;
    public static TextView d3Temp;
    public static TextView d4Day;
    public static ImageView d4Pic;
    public static TextView d4Temp;
    public static TextView d5Day;
    public static ImageView d5Pic;
    public static TextView d5Temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wallpaper = (ImageView) findViewById(R.id.pic);
        forecast = (TextView) findViewById(R.id.forecastCurrent);
        forecastDegrees = (TextView) findViewById(R.id.degrees);
        degCurrent = (TextView) findViewById(R.id.weatherCurrent);
        degMax = (TextView) findViewById(R.id.weatherMax);
        degMin = (TextView) findViewById(R.id.weatherMin);
        city = (TextView) findViewById(R.id.city);
        backG = (View) findViewById(R.id.back);
        d1Day = (TextView) findViewById(R.id.dayOneDay);
        d1Pic = (ImageView) findViewById(R.id.dayOneSymbol);
        d1Temp = (TextView) findViewById(R.id.dayOneTemp);
        d2Day = (TextView) findViewById(R.id.dayTwoDay);
        d2Pic = (ImageView) findViewById(R.id.dayTwoSymbol);
        d2Temp = (TextView) findViewById(R.id.dayTwoTemp);
        d3Day = (TextView) findViewById(R.id.dayThreeDay);
        d3Pic = (ImageView) findViewById(R.id.dayThreeSymbol);
        d3Temp = (TextView) findViewById(R.id.dayThreeTemp);
        d4Day = (TextView) findViewById(R.id.dayFourDay);
        d4Pic = (ImageView) findViewById(R.id.dayFourSymbol);
        d4Temp = (TextView) findViewById(R.id.dayFourTemp);
        d5Day = (TextView) findViewById(R.id.dayFiveDay);
        d5Pic = (ImageView) findViewById(R.id.dayFiveSymbol);
        d5Temp = (TextView) findViewById(R.id.dayFiveTemp);


        //Avoid errors when assigning a pic later on
        d1Pic.setImageResource(R.drawable.clear);
        d2Pic.setImageResource(R.drawable.clear);
        d3Pic.setImageResource(R.drawable.clear);
        d4Pic.setImageResource(R.drawable.clear);
        d5Pic.setImageResource(R.drawable.clear);



        GPSTracker gps = new GPSTracker(this);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MainActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MainActivity.MY_PERMISSION_ACCESS_FINE_LOCATION );
        }

        if (gps.canGetLocation())
        {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        }
        else
        {
            gps.showSettingsAlert();
        }

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            city.setText(addresses.get(0).getLocality());
            fetchWeather process = new fetchWeather();
            process.execute();


        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Error finding location, try enabling gps", Toast.LENGTH_SHORT); toast.show();
        }


    }

    public static void modifyForWeather(int code)
    {
        if(code == 0)
        {
            degCurrent.setBackgroundResource(R.color.Sunny);
            degMin.setBackgroundResource(R.color.Sunny);
            degMax.setBackgroundResource(R.color.Sunny);
            wallpaper.setImageResource(R.drawable.forest_sunny);
            backG.setBackgroundResource(R.color.Sunny);
        }
        else if(code == 1)
        {
            degCurrent.setBackgroundResource(R.color.Rainy);
            degMin.setBackgroundResource(R.color.Rainy);
            degMax.setBackgroundResource(R.color.Rainy);
            wallpaper.setImageResource(R.drawable.forest_rainy);
            backG.setBackgroundResource(R.color.Rainy);
        }
        else if(code == 2)
        {
            degCurrent.setBackgroundResource(R.color.Cloudy);
            degMin.setBackgroundResource(R.color.Cloudy);
            degMax.setBackgroundResource(R.color.Cloudy);
            wallpaper.setImageResource(R.drawable.forest_cloudy);
            backG.setBackgroundResource(R.color.Cloudy);
        }

        fetchForecast process2 = new fetchForecast();
        process2.execute();
    }

    public static void modifyForForecast(int dayCode, double weather, int descrCode, int dayPos)
    {
        String day = "";
        switch (dayCode)
        {
            case 1: day = "Sunday";
              break;

            case 2: day = "Monday";
            break;

            case 3: day = "Tuesday";
            break;

            case 4: day = "Wednesday";
            break;

            case 5: day = "Thursday";
            break;

            case 6: day = "Friday";
            break;

            case 7: day = "Saturday";
            break;
        }

        switch(dayPos)
        {
            case 1:
                d1Day.setText(day);
                d1Temp.setText(String.format("%.0f", weather)+ " \u2103");
                if(descrCode >= 500 && descrCode <= 531)
                {
                    d1Pic.setImageResource(R.drawable.rain);

                }
                else if(descrCode >= 801 && descrCode <= 804)
                {
                    d1Pic.setImageResource(R.drawable.partlysunny);
                }
                else {
                    d1Pic.setImageResource(R.drawable.clear);
                }

                break;
            case 2:
                d2Day.setText(day);
                d2Temp.setText(String.format("%.0f", weather)+ " \u2103");
                if(descrCode >= 500 && descrCode <= 531)
                {
                    d2Pic.setImageResource(R.drawable.rain);

                }
                else if(descrCode >= 801 && descrCode <= 804)
                {
                    d2Pic.setImageResource(R.drawable.partlysunny);
                }
                else {
                    d2Pic.setImageResource(R.drawable.clear);
                }
                break;
            case 3:
                d3Day.setText(day);
                d3Temp.setText(String.format("%.0f", weather)+ " \u2103");
                if(descrCode >= 500 && descrCode <= 531)
                {
                    d3Pic.setImageResource(R.drawable.rain);

                }
                else if(descrCode >= 801 && descrCode <= 804)
                {
                    d3Pic.setImageResource(R.drawable.partlysunny);
                }
                else {
                    d3Pic.setImageResource(R.drawable.clear);
                }
                break;
            case 4:
                d4Day.setText(day);
                d4Temp.setText(String.format("%.0f", weather)+ " \u2103");
                if(descrCode >= 500 && descrCode <= 531)
                {
                    d4Pic.setImageResource(R.drawable.rain);

                }
                else if(descrCode >= 801 && descrCode <= 804)
                {
                    d4Pic.setImageResource(R.drawable.partlysunny);
                }
                else {
                    d4Pic.setImageResource(R.drawable.clear);
                }
                break;
            case 5:
                d5Day.setText(day);
                d5Temp.setText(String.format("%.0f", weather)+ " \u2103");
                if(descrCode >= 500 && descrCode <= 531)
                {
                    d5Pic.setImageResource(R.drawable.rain);

                }
                else if(descrCode >= 801 && descrCode <= 804)
                {
                    d5Pic.setImageResource(R.drawable.partlysunny);
                }
                else {
                    d5Pic.setImageResource(R.drawable.clear);
                }
                break;
        }
    }
}

