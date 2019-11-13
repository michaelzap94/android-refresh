package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebDownload extends AppCompatActivity {

    private static final String TAG = "WebDownload";
    TextView myTextView;
    ImageView myImageView;

    //================================================================================================
    //Background thread: Bitmap will return an image.
    public class MyAsyncTaskImageSimple extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d("MyAsyncTaskImageSimple", "doInBackground: GOT HERE");
            String baseUrl = urls[0];
            URL myUrl = myUrlBuilder(baseUrl, null);
            Log.d("MyAsyncTaskImageSimple", "doInBackground: URL:"+myUrl.toString());
            Bitmap response = getResponseBitMap(myUrl);

            return response;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            if(s!=null){
                myImageView.setVisibility(View.VISIBLE);
                myTextView.setVisibility(View.GONE);
                //Sets Bitmap to ImageView
                myImageView.setImageBitmap(s);
            } else {
                myTextView.setVisibility(View.VISIBLE);
                myImageView.setVisibility(View.GONE);
                myTextView.setText("Image could not be loaded/found.");
            }
        }
    }

    //GETS Bitmap data for Images
    public Bitmap getResponseBitMap(URL url){
        Bitmap result = null;
        HttpURLConnection urlConnection = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            //Open connection
            InputStream in = urlConnection.getInputStream();//This method will also do urlConnection.connect();, hence no need to do this.

            //Decodes the image and adds it to a Bitmap object
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            result =  myBitmap;

        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getResponseBitMap: " + e.getMessage());
        }
        return result;
    }

    //================================================================================================

    //Background thread:
    public class MyAsyncTaskJSON extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myTextView.setVisibility(View.VISIBLE);
            myImageView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("MyAsyncTaskJSON", "doInBackground: GOT HERE");
            String baseUrl = urls[0];

            //used to pass query parameters
            HashMap<String, String> queries = new HashMap<String, String>();
            queries.put("q","London,uk");
            queries.put("appid","b6907d289e10d714a6e88b30761fae22");

            URL myUrl = myUrlBuilder(baseUrl, queries);
            Log.d("MyAsyncTaskJSON", "doInBackground: URL:"+myUrl.toString());
            String response = getResponseInputStreamReader(myUrl);
            Log.d("MyAsyncTaskJSON", "doInBackground: RESPONSE:"+response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String result = "";
            try{
                JSONObject json = new JSONObject(s);
                JSONArray jsonArray = json.getJSONArray("weather");
                String main = "";
                String description = "";
                for (int i = 0; i < jsonArray.length() ; i++) {
                    JSONObject objectInsideArr = jsonArray.getJSONObject(i);
                    main = objectInsideArr.getString("main");
                    description = objectInsideArr.getString("description");
                }
                result = String.format("Main: %s, Description: %s", main, description);
            } catch (Exception e){
                result = "Failed. The message is: "+e.getMessage();
            }
            myTextView.setText(result);
        }
    }


    //Background thread:
    public class MyAsyncTaskWebSimple extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myTextView.setVisibility(View.VISIBLE);
            myImageView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("MyAsyncTaskWebSimple", "doInBackground: GOT HERE");
            String baseUrl = urls[0];
            URL myUrl = myUrlBuilder(baseUrl, null);
            Log.d("MyAsyncTaskWebSimple", "doInBackground: URL:"+myUrl.toString());
            String response = getResponseInputStreamReader(myUrl);
            Log.d("MyAsyncTaskWebSimple", "doInBackground: RESPONSE:"+response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myTextView.setText(s);
        }
    }

    //GETS DATA LETTER BY LETTER - TAKES AGES
    public String getResponseInputStreamReader(URL url){
        String result = "";
        HttpURLConnection urlConnection = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            //Get the data to be read
            InputStream in = urlConnection.getInputStream();//This method will also do urlConnection.connect();, hence no need to do this.
            //create an InputStreamReader to read the data
            InputStreamReader reader = new InputStreamReader(in);

            //reader.read() -> gives us each Character, AND removes it from the InputStream.
            int data = reader.read();

            //We're done - We got to the last Character
            while(data != -1){
                char current = (char) data;

                result += current;

                //reader.read() -> gives us each Character, AND removes it from the InputStream.
                data = reader.read();
            }
        } catch (IOException e){
            e.printStackTrace();
            result = "Failed. The message is: "+e.getMessage();
        }
        return result;
    }

    public URL myUrlBuilder(String base, HashMap<String, String> queries){
        Uri buildUri = Uri.parse(base);
        if(queries!=null){
            Uri.Builder myBuilder = buildUri.buildUpon();
            buildUri = queryHandler(myBuilder, queries);
            Log.d(TAG, "queryHandler: URL:"+buildUri.toString());
        }

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException m){
            m.printStackTrace();
        }
        return url;
    }

    public Uri queryHandler(Uri.Builder uriBuilder ,HashMap<String, String> queries ){
        // Getting an iterator
        Iterator hmIterator = queries.entrySet().iterator();

        // Iterate through the hashmap
        // and add some bonus marks for every student
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            uriBuilder.appendQueryParameter(mapElement.getKey().toString(), mapElement.getValue().toString());
        }
        return uriBuilder.build();
    }

    //================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_download);

        myImageView = (ImageView) findViewById(R.id.webDownloadImageView);

        myTextView = (TextView) findViewById(R.id.webDownloadTextView);
        //Make the textview scrollable
        myTextView.setMovementMethod(new ScrollingMovementMethod());

        //WRONG -> Just like threads, AsyncTasks can't be reused. You have to create a new instance every time you want to run one.
//        MyAsyncTaskWebSimple myAsyncTaskWebSimple = new MyAsyncTaskWebSimple();
//        MyAsyncTaskImageSimple myAsyncTaskImageSimple = new MyAsyncTaskImageSimple();
    }


    public void onClickWebHandler(View v){
        switch (v.getId()){
            case R.id.webDownloadButton: new MyAsyncTaskWebSimple().execute("https://www.zappycode.com/");
                break;
            case R.id.imageDownloadButton: new MyAsyncTaskImageSimple().execute("https://upload.wikimedia.org/wikipedia/en/a/aa/Bart_Simpson_200px.png");
                break;
            case R.id.jsonDownloadButton: new MyAsyncTaskJSON().execute("https://samples.openweathermap.org/data/2.5/weather");
                break;
        }
    }
}
