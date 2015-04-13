package it.adepti.ac_factor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import it.adepti.ac_factor.fragment.Audio;
import it.adepti.ac_factor.fragment.Testo;
import it.adepti.ac_factor.fragment.Video;
import it.adepti.ac_factor.push_notification.BootNotificationReceiver;
import it.adepti.ac_factor.push_notification.NotificationService;
import it.adepti.ac_factor.utils.FilesSupport;

public class MainActivity extends FragmentActivity {

    // Constants
    private final String TAG = "MainActivity";
    private static final int SETTINGS_RESULT = 101;

    // Fragment per i tab
    private FragmentTabHost mTabHost;


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
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab_txt").setIndicator(buildTabLayout(getResources().getString(R.string.tab_testo))), Testo.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_video").setIndicator(buildTabLayout(getResources().getString(R.string.tab_video))), Video.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_audio").setIndicator(buildTabLayout(getResources().getString(R.string.tab_audio))), Audio.class, null);

        /** CODICE DEBUG */ //TODO DA ELMINARE
        Intent startServiceIntent = new Intent(this, NotificationService.class);
        this.startService(startServiceIntent);
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

    private View buildTabLayout(String tag) {
        View tab = getLayoutInflater().inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) tab.findViewById(R.id.tab_layout_tv);
        tv.setText(tag);
        return tab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_RESULT){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Log.d(TAG, "Notifiche: " + sharedPreferences.getBoolean("prefPushNotify", true));
        }

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

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://events/369142586623334/"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/369142586623334/"));
        }
    }

}
