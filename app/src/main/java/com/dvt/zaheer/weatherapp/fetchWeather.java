package com.dvt.zaheer.weatherapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class fetchWeather extends AsyncTask<Void,Void,Void>
{

    public static String degrees = "";
    public static String description = "";
    public static String min = "";
    public static String max = "";
    public static int weathercode = 0;

    @Override
    protected Void doInBackground(Void... voids) {

        double lat = MainActivity.lat;
        double lon = MainActivity.lon;


        try {
            JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=ea574594b9d36ab688642d5fbeab847e");
            JSONObject obj = (JSONObject) json.get("main");
            double kelvin =  Double.parseDouble(obj.get("temp").toString());
            double tempMinK = Double.parseDouble(obj.get("temp_min").toString());
            double tempMaxK = Double.parseDouble(obj.get("temp_max").toString());

            //Converting to Celcius from kelvin is not repeated again
            double tempMin = tempMinK - 273.15;
            double tempMax = tempMaxK - 273.15;
            double celcius = kelvin - 273.15;

            JSONArray list =  json.getJSONArray("weather");
            JSONObject obj2 = (JSONObject) list.get(0);
            int descriptionID = Integer.parseInt(obj2.get("id").toString());

            if(descriptionID >= 500 && descriptionID <= 531)
            {
                description = "Rainy";
                weathercode = 1;
            }
            else if(descriptionID >= 801 && descriptionID <= 804)
            {
                description = "Cloudy";
                weathercode = 2;
            }
            else//Ignore all other codes
            {
                description = "Sunny";
            }

            degrees = String.format("%.0f", celcius)+" \u2103";
            min = String.format("%.0f", tempMin)+" \u2103";
            max = String.format("%.0f", tempMax)+" \u2103";


        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity.forecastDegrees.setText(degrees);
        MainActivity.forecast.setText(description.toUpperCase());
        MainActivity.degCurrent.setText(degrees+ "\n Current");
        MainActivity.degMin.setText(min + "\n min");
        MainActivity.degMax.setText(max + "\n max");

        MainActivity.modifyForWeather(weathercode);
    }
}
