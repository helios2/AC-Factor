package it.adepti.ac_factor.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.ftp.FTPManager;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.StringUtils;

public class Testo extends Fragment {

    private FTPManager myManager;
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myManager = FTPManager.getManagerInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.text_layout, container, false);
        mTextView = (TextView) v.findViewById(R.id.text);
        return v;
    }

    @SuppressWarnings("all")
    private final Handler textHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.DOWNLOAD_DONE:
                    switch (msg.arg1){
                        case Constants.DOWNLOAD_TEXT_DONE:
                            mTextView.setText(StringUtils.readFromFile("Text_090415", myManager.getDownloadDirectory()));
                            Log.d("FTP", "Displayng " + myManager.getWorkingDirectory().toString() + "Text_090415");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public Handler getTextHandler() {
        return textHandler;
    }
}
