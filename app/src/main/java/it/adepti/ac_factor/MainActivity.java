package it.adepti.ac_factor;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

import it.adepti.ac_factor.fragment.Audio;
import it.adepti.ac_factor.fragment.Testo;
import it.adepti.ac_factor.fragment.Video;
import it.adepti.ac_factor.push_notification.BootNotificationReceiver;
import it.adepti.ac_factor.push_notification.NotificationService;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.FilesSupport;

public class MainActivity extends FragmentActivity {

    // Constants
    private final String TAG = "MainActivity";
    private static final int SETTINGS_RESULT = 101;

    // Check Connectivity
    private CheckConnectivity checkConnectivity = new CheckConnectivity(this);


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Create Root Directory */
        File rootDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/acfactor");
        if(!rootDirectory.exists()) rootDirectory.mkdirs();
        /** Create Today Directory */
        File todayDirectory = new File(rootDirectory.toString() + "/" + FilesSupport.dateTodayToString());
        if(!todayDirectory.exists()) todayDirectory.mkdirs();

        /** Tab settings */
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter());
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(1);

        // Connectivity Status
        if(!checkConnectivity.isConnected()){
            Toast.makeText(this, getResources().getString(R.string.text_noConnection), Toast.LENGTH_SHORT).show();
        }
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
        private Audio audio = new Audio();

        // Titles
        private List<String> titles = new ArrayList<>();
        private List<Fragment> fragments = new ArrayList<>();

        // Page Count
        final int PAGE_COUNT = 3;


        public SampleFragmentPagerAdapter() {
            super(getSupportFragmentManager());

            // Titles list setting
            titles.add(getResources().getString(R.string.tab_video).toUpperCase());
            titles.add(getResources().getString(R.string.tab_testo).toUpperCase());
            titles.add(getResources().getString(R.string.tab_audio).toUpperCase());

            // Fragment list setting
            fragments.add(video);
            fragments.add(testo);
            fragments.add(audio);

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


