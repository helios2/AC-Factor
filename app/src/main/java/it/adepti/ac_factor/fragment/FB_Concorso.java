package it.adepti.ac_factor.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.youtube.DeveloperKey;

public class FB_Concorso extends Fragment {

    // Layout Object
    private WebView FB_View;

    // WebPage URL
    private String pageURL = "http://androidprova.altervista.org/Concorso/facebook_posts.html";

    // YouTubePlayer Fragment
    private YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
    private YouTubePlayer yTubePlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= 15){
            try {
                WebView.class.getMethod("onResume").invoke(FB_View);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT >= 15) {
            try {
                WebView.class.getMethod("onPause").invoke(FB_View);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;

        if(Build.VERSION.SDK_INT >= 15) {
            v = inflater.inflate(R.layout.fb_concorso_layout, container, false);
            FB_View = (WebView) v.findViewById(R.id.fb_webView);
            FB_View.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            WebSettings webSettings = FB_View.getSettings();
            FB_View.setWebChromeClient(new WebChromeClient());
            webSettings.setJavaScriptEnabled(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setAllowFileAccess(true);
            if (Build.VERSION.SDK_INT >= 21) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            FB_View.loadUrl("https://m.youtube.com/watch?list=PLNuxVPAQYaGVGCt_b8ZFiI7G1zPTwFnKe&v=33z2N58HWXQ");
        } else {
            v = inflater.inflate(R.layout.fragment_youtube, container, false);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

            youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener(){
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    yTubePlayer = youTubePlayer;
                    yTubePlayer.loadPlaylist("PL55713C70BA91BD6E");
                    yTubePlayer.play();
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }

//        String html = "";
//        html += "<html><body>";
//        html += "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/videoseries?list=PLNuxVPAQYaGVGCt_b8ZFiI7G1zPTwFnKe\" frameborder=\"0\" allowfullscreen></iframe>";
//        html += "</body></html>";
//        FB_View.loadUrl(pageURL);
//        FB_View.loadData(html, "text/html", null);

        return v;
    }

}
