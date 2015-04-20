package it.adepti.ac_factor.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Date;
import java.util.GregorianCalendar;

import it.adepti.ac_factor.R;

public class Concorso_new extends Fragment {
    private WebView youtube;
    private String url = "https://www.youtube.com/watch?v=RgKAFK5djSk&list=PL55713C70BA91BD6E";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreate");
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreateView");
        View v = inflater.inflate(R.layout.concorso_layout, container, false);
        youtube = (WebView) v.findViewById(R.id.youtubeView);
        youtube.loadUrl(url);
        WebSettings webSettings = youtube.getSettings();
        webSettings.setJavaScriptEnabled(true);
        return v;
    }

}
