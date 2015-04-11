package it.adepti.ac_factor;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO sostituire con un metodo non deprecato
        addPreferencesFromResource(R.xml.settings);
    }
}
