package it.adepti.ac_factor.fragment;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import it.adepti.ac_factor.R;

public class Concorso extends Fragment {
    private TextView mTextView;
    private CountDownTimer countDownTimer;
    private GregorianCalendar toDate = new GregorianCalendar(2015,4,10);
    private GregorianCalendar today;
    private Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreate");
        super.onCreate(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/clock.ttf");

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
        //final SimpleDateFormat dataFormat = new SimpleDateFormat("dd Giorni\nhh Ore\nmm Minuti\nss Secondi");
        countDownTimer = new CountDownTimer(countDownUtil, 1000) {

            public void onTick(long millisUntilFinished) {
                Date date = new Date(millisUntilFinished);
                String countdown = date.getDate() + " d " + date.getHours() + " h " + date.getMinutes() + " m " + date.getSeconds() + " s";
                mTextView.setText("\nCountdown\n" + countdown);
                mTextView.setTypeface(typeface);
            }

            public void onFinish() {
                mTextView.setText("Aggiorna l'app per vedere i video!");
            }
        }.start();
        return v;
    }

}
