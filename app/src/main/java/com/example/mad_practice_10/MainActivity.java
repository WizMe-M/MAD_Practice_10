package com.example.mad_practice_10;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageOutput);
        Button btnJokeLoad = findViewById(R.id.btnGetImage);
        btnJokeLoad.setOnClickListener(view -> new JokeLoader().execute());
    }

    private class JokeLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String json = getJsonRandomImage();

            try {
                int id = new Random().nextInt(50);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(id);
                imageUrl = jsonObject.getString("thumbnailUrl") + ".png";

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageUrl = null;
            imageView.setImageResource(R.mipmap.ic_launcher);

            Toast.makeText(getApplicationContext(),
                    R.string.image_is_loading,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (imageUrl != null) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round);

                Glide.with(imageView).load(imageUrl).apply(options).into(imageView);
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.image_load_fail,
                        Toast.LENGTH_SHORT).show();
            }
        }

        private String getJsonRandomImage() {
            String data = "";
            try {
                URL url = new URL(getString(R.string.api_random_image_link));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), StandardCharsets.UTF_8));

                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    data = sb.toString();
                    r.close();
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}