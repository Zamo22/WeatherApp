package com.dvt.zaheer.weatherapp;

import android.icu.util.Calendar;
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

public class fetchForecast extends AsyncTask<Void,Void,Void> {

    public static double min;
    public static double max;

    @Override
    protected Void doInBackground(Void... voids) {

        double lat = MainActivity.lat;
        double lon = MainActivity.lon;


        try {
            //NB I chose to use daily in this case as it provided one reading per day instead of api link given
            JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+lat+"&lon="+lon+"&units=metric&cnt=6&APPID=ea574594b9d36ab688642d5fbeab847e");

            JSONArray obj =  json.getJSONArray("list");

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            JSONObject forecastForToday = (JSONObject) obj.get(0);
            JSONObject tempsForToday = (JSONObject) forecastForToday.get("temp");
            min = Double.parseDouble(tempsForToday.get("min").toString());
            min = Math.round(min);

            max = Double.parseDouble(tempsForToday.get("max").toString());
            max = Math.round(max);


            for (int i = 1; i <= 5; i++)//Ignore first (current day)
            {
                JSONObject forecastForDay = (JSONObject) obj.get(i);
                JSONObject tempsForDay = (JSONObject) forecastForDay.get("temp");
                double temp = Double.parseDouble(tempsForDay.get("day").toString());
                temp = Math.round(temp);

                JSONArray wArr =  forecastForDay.getJSONArray("weather");
                JSONObject obj2 = (JSONObject) wArr.get(0);
                int descriptionID = Integer.parseInt(obj2.get("id").toString());

                if(day == 7)
                {
                    day = 1;
                }
                else{
                    day++;
                }

                MainActivity.modifyForForecast(day, temp, descriptionID, i);
                System.out.print("");

            }





            //JSONObject obj2 = (JSONObject) list.get(0);
            //int descriptionID = Integer.parseInt(obj2.get("id").toString());


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

        MainActivity.degMin.setText(String.format("%.0f", min)+" \u2103 \n min");
        MainActivity.degMax.setText(String.format("%.0f", max)+" \u2103 \n max");

    }

}
