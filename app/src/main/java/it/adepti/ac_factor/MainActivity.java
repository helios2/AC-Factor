package it.adepti.ac_factor;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    // Fragment per i tab
    private FragmentTabHost mTabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Tab settings */
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab_txt").setIndicator(buildTabLayout(getResources().getString(R.string.tab_testo))), Text.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_multi").setIndicator(buildTabLayout(getResources().getString(R.string.tab_multi))), Multimedia.class, null);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
