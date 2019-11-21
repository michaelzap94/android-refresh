package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class NewsReaderWebView extends AppCompatActivity {
    private WebView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader_web_view);

        mContentView = (WebView) findViewById(R.id.fullscreen_webview_news_reader);
        mContentView.getSettings().setJavaScriptEnabled(true);
        mContentView.setWebViewClient(new WebViewClient());
        Intent fromNewsReader = getIntent();
        if(fromNewsReader.hasExtra("url")){
            mContentView.loadUrl(fromNewsReader.getStringExtra("url"));
        } else {
            mContentView.loadData("<html><body>404 NO CONTENT WAS FOUND IN THE URL</body></html>", "text/html", "UTF-8");
        }
    }
}
