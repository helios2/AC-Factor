package it.adepti.ac_factor.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import it.adepti.ac_factor.R;

public class FB_Concorso extends Fragment {

    // Layout Object
    private WebView FB_View;

    // WebPage URL
    private String pageURL = "http://androidprova.altervista.org/Concorso/facebook_posts.html";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fb_concorso_layout, container, false);

        FB_View = (WebView) v.findViewById(R.id.fb_webView);
        FB_View.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings webSettings = FB_View.getSettings();
        FB_View.setWebChromeClient(new WebChromeClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        FB_View.loadUrl(pageURL);

        return v;
    }

}
