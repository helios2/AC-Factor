package it.adepti.ac_factor;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;

import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.adepti.ac_factor.fragment.Audio;
import it.adepti.ac_factor.fragment.Concorso;
import it.adepti.ac_factor.fragment.Concorso_new;
import it.adepti.ac_factor.fragment.Testo;
import it.adepti.ac_factor.fragment.Video;
import it.adepti.ac_factor.push_notification.BootNotificationReceiver;
import it.adepti.ac_factor.push_notification.NotificationService;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;

public class MainActivity extends FragmentActivity {

    // Constants
    private final String TAG = "MainActivity";
    private static final int SETTINGS_RESULT = 101;

    // Files existence
    private boolean testoVisible;
    private boolean videoVisible;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Files existence */
        Intent intent = getIntent();
        testoVisible = intent.getBooleanExtra(SplashScreen.EXTRA_TEXT, testoVisible);
        Log.d(TAG, "testoVisible = " + testoVisible);
        videoVisible = intent.getBooleanExtra(SplashScreen.EXTRA_VIDEO, videoVisible);
        Log.d(TAG, "videoVisible = " + videoVisible);

        /** Create Root Directory */
        File rootDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/acfactor");
        if(!rootDirectory.exists()) rootDirectory.mkdirs();
        /** Create Today Directory */
        File todayDirectory = new File(rootDirectory.toString() + "/" + FilesSupport.dateTodayToString());
        if(!todayDirectory.exists()) todayDirectory.mkdirs();

        /** Tab settings */
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter());
        //viewPager.setCurrentItem(1);
        //viewPager.setOffscreenPageLimit(1);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setTabIndicatorColorResource(R.color.background_tab_pressed);

    }

    @Override
    protected void onPause() {
        Log.d("LifeCycle", "MainActivity onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.d("LifeCycle", "MainActivity onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("LifeCycle", "MainActivity onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("LifeCycle", "MainActivity onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LifeCycle", "MainActivity onActivityResult");

        if (requestCode == SETTINGS_RESULT){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Log.d(TAG, "Notifiche: " + sharedPreferences.getBoolean("prefPushNotify", true));
        }
    }

    // ---------------------------------------------
    // Menù
    // ---------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG,"Action Settings");
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(intent, SETTINGS_RESULT);
                return true;
            case R.id.action_facebook:
                Intent fb_intent = getOpenFacebookIntent(this);
                startActivity(fb_intent);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ---------------------------------------------
    // Facebook Intent
    // ---------------------------------------------
    public static Intent getOpenFacebookIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/369142586623334/"));
    }

    // ---------------------------------------------
    // Fragment Pager Adapter Settings
    // ---------------------------------------------
    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        // Fragment
        private Testo testo = new Testo();
        private Video video = new Video();
        private Concorso concorso = new Concorso();

        // Titles
        private List<String> titles = new ArrayList<>();
        private List<Fragment> fragments = new ArrayList<>();

        // Page Count
        private int PAGE_COUNT = 1;


        public SampleFragmentPagerAdapter() {
            super(getSupportFragmentManager());

            if (testoVisible){
                PAGE_COUNT++;
                titles.add(getResources().getString(R.string.tab_testo).toUpperCase());
                fragments.add(testo);
            }

            if (videoVisible){
                PAGE_COUNT++;
                titles.add(getResources().getString(R.string.tab_video).toUpperCase());
                fragments.add(video);
            }

            titles.add(getResources().getString(R.string.tab_concorso).toUpperCase());
            fragments.add(concorso);

        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        // Mostra i fragment visibili nella pager_header
        @Override
        public Fragment getItem(int position) {
                return fragments.get(position);
        }

        // Setta i titoli nella pager_header
        @Override
        public CharSequence getPageTitle(int position) {
                return titles.get(position);
        }

    }
}


