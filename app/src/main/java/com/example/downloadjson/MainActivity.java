package com.example.downloadjson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private EditText editText;

    String address = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=3a2e19b21998a72c3c370f2c9c3b3ec5&lang=ru&units=metric";
    String city = "";
    String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView);
        editText = findViewById(R.id.editTextTextPersonName);


    }

    public void onClickGetWeather(View view) {
        String city =  editText.getText().toString().trim();
        if(!city.isEmpty()) {
            DownloadTask task = new DownloadTask();
            String url = String.format(address, city);
            task.execute(url);
        }


    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null){
                    result.append(line);
                    line = br.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                String weater = String.format("%s\nTemp: %s\nIn street: %s", city, temp, description);
                tv.setText(weater);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}