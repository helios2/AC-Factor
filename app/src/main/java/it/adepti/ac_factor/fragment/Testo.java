package it.adepti.ac_factor.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.ftp.FTPManager;

public class Testo extends Fragment {

    // FTP Manager
    private FTPManager myManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        AsyncTask connectTask = new AsyncTask(){
//            @Override
//            protected Object doInBackground(Object[] params) {
//                Log.d("FTPManager", "doInBackground");
//                myManager = new FTPManager();
//                myManager.connectWithFTP("ftp.androidprova.altervista.org",
//                        "androidprova",
//                        "dukcivosne70",
//                        FTPManager.PASSIVE_MODE);
//                myManager.setWorkingDirectory("/080415/");
//                myManager.downloadFile("Text_080415", "txt");
//                return null;
//            }
//        };
//
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
//            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else
//            connectTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.text_layout, container, false);
        TextView tv = (TextView) v.findViewById(R.id.text);
        //tv.setText(this.getTag() + " Content");
        return v;
    }
}
