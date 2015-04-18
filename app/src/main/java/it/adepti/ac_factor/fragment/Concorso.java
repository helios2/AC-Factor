package it.adepti.ac_factor.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.GregorianCalendar;

import it.adepti.ac_factor.R;

public class Concorso extends Fragment {
    private TextView mTextView;
    private CountDownTimer countDownTimer;
    private GregorianCalendar toDate = new GregorianCalendar(2015,4,10);
    private GregorianCalendar today;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreate");
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreateView");
        View v = inflater.inflate(R.layout.text_layout, container, false);
        mTextView = (TextView) v.findViewById(R.id.text);

        // CountDown Timer
        today = new GregorianCalendar();
        long countDownUtil = toDate.getTimeInMillis() - today.getTimeInMillis();
        countDownTimer = new CountDownTimer(countDownUtil, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextView.setText("done!");
            }
        }.start();
        return v;
    }

}
