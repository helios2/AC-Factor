package it.adepti.ac_factor.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.adepti.ac_factor.MainActivity;
import it.adepti.ac_factor.R;
import it.adepti.ac_factor.ftp.FTPManager;

public class Testo extends Fragment {

    private FTPManager myManager;
    private StringBuilder text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity)getActivity();
        myManager = activity.getMyManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.text_layout, container, false);
        TextView tv = (TextView) v.findViewById(R.id.text);
        MainActivity activity = (MainActivity)getActivity();
        text = activity.getStringBuilderText();
        tv.setText(text);
        return v;
    }
}
