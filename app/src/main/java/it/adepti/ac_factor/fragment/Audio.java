package it.adepti.ac_factor.fragment;

import android.media.MediaPlayer;
import android.os.Handler;
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

public class Audio extends Fragment implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "Audio";

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set up the Media Player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        // Set up the Media Controller
        mediaController = new MediaController(getActivity());

        try{
            mediaPlayer.setDataSource(getActivity().getAssets().openFd("mfx/Science.mp3").getFileDescriptor());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"No audio");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.audio_layout, container, false);

        v.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mediaController.show();
                return false;
            }
        });

        return v;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getActivity().findViewById(R.id.audio_view));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });

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
