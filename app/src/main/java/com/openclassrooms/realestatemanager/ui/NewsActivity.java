package com.openclassrooms.realestatemanager.ui;


import android.os.Bundle;

import com.openclassrooms.realestatemanager.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        webView = (WebView) findViewById(R.id.webview);
        // Activer JavaScript (si le site web en a besoin)
        webView.getSettings().setJavaScriptEnabled(true);
        // S'assurer que les redirections sont ouvertes dans la WebView et non dans un navigateur externe
        webView.setWebViewClient(new WebViewClient());
        // Charger l'URL
        webView.loadUrl("https://www.lemonde.fr/immobilier/");
    }
}
