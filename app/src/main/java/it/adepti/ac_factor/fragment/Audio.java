package it.adepti.ac_factor.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;

public class Audio extends Fragment implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "Audio";

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private String audioAddress;
    private boolean isInternetOn = true; // TODO settarlo ad un valore vero


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set up the Media Player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        // Set up the Media Controller
        mediaController = new MediaController(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curr_pos", mediaPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.audio_layout, container, false);

        mediaController.setAnchorView(v);

        if(isInternetOn) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mediaController.show();
                    return false;
                }
            });

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioAddress = "http://androidprova.altervista.org/100415/Audio_100415.mp3";
            try {
                mediaPlayer.setDataSource(audioAddress);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mediaPlayer.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (savedInstanceState != null)
                mediaPlayer.seekTo(savedInstanceState.getInt("curr_pos"));
            mediaPlayer.start();
        }
        else Log.d(TAG,"Turn on internet");


        return v;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController.setMediaPlayer(this);
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
