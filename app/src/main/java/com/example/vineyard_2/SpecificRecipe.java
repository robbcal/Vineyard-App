package com.example.vineyard_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class SpecificRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_recipe);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl(url);
    }
}
