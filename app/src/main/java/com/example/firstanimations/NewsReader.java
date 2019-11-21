package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewsReader extends AppCompatActivity {

    private static final String TAG = "NewsReader";
    private static final String MAIN_URL = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";

    final static String SAVED_INSTANCE_KEY = "savedInstanceKey";


    ListView listView;
    ProgressBar progressBar;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();

    SQLiteDatabase articlesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);

        //initialize DB==============================
        articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, title VARCHAR, url VARCHAR)");
        //===========================================

        listView = (ListView) findViewById(R.id.listBasicNewsReader);
        progressBar = (ProgressBar)findViewById(R.id.progressBarNewsReader);

        if(savedInstanceState != null){ //if screen was ROTATED
            startAdapter();//with existing data
        } else {
            new NewsReader.MyAsyncTaskJSON().execute(MAIN_URL);
        }

    }

    //SAVED INSTANCE( ON phone rotated)================================
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void startAdapter(){

        getAndInitTitlesAndUrls();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startNewIntent(urls.get(position));
            }
        });
    }

    public void getAndInitTitlesAndUrls(){
        Cursor c = articlesDB.rawQuery("SELECT * FROM articles", null);

        int titleIndex = c.getColumnIndex("title");
        int urlIndex = c.getColumnIndex("url");

        boolean isThereFirst = c.moveToFirst();

        if(isThereFirst){
            do {
                initTitlesAndUrls(c.getString(titleIndex), c.getString(urlIndex));
            } while (c.moveToNext());//While there's a next item, keep executing
        }
    }

    private void initTitlesAndUrls(String title, String content){
        titles.add(title);
        urls.add(content);
    }

    private void insertTitlesAndUrls(String articleId, String articleTitle, String articleURL){
        String sql = "INSERT INTO articles (articleId, title, url) VALUES (?, ?, ?)";
        SQLiteStatement statement = articlesDB.compileStatement(sql);
        statement.bindString(1,articleId);
        statement.bindString(2,articleTitle);
        statement.bindString(3,articleURL);
        statement.execute();
    }

    private void startNewIntent(String url){
        Intent ni = new Intent(NewsReader.this, NewsReaderWebView.class);
        ni.putExtra("url", url);
        startActivity(ni);
    }

    private void removeOldRecords(){
        articlesDB.execSQL("DELETE FROM articles");
    }

    //Background thread:======================================================================
    public class MyAsyncTaskJSON extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            Log.d("MyAsyncTaskJSON", "doInBackground: GOT HERE");
            String baseUrl = urls[0];
            try{
                //Remove all elements in DB, as we want NEWS
                removeOldRecords();

                URL urlToGetArrayOfIDs = new URL(baseUrl);
                String arrayOfIDs = getResponseInputStreamReader(urlToGetArrayOfIDs);
                Log.d("MyAsyncTaskJSON", "doInBackground: RESPONSE:" + arrayOfIDs);

                JSONArray jsonArray = new JSONArray(arrayOfIDs);//Result will be like: [23,34] so need to create a jsonarray
                int maxItems = 10;
                if (jsonArray.length() < 10) {
                    maxItems = jsonArray.length();
                }
                for (int i = 0; i < maxItems ; i++) {
                    String articleId = jsonArray.getString(i);

                    URL specificURL = new URL ("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                    String articleJSONString = getResponseInputStreamReader(specificURL);
                    JSONObject jsonObject = new JSONObject(articleJSONString);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        insertTitlesAndUrls(articleId, jsonObject.getString("title"), jsonObject.getString("url"));
                    }
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progressBar.setVisibility(View.GONE);

            if(success){
                startAdapter();
            } else {
                Toast.makeText(NewsReader.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
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
    //====================================================================================

}
