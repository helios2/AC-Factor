package it.adepti.ac_factor.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;

public class Audio extends Fragment implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "Audio";

    // Media Player
    private MediaPlayer mediaPlayer;
    // Media Controller
    private MediaController mediaController;
    // Audio URL
    private String streamingAudioURL;
    // String for today
    private String todayString;
    // Image View
    private ImageView audioIcon;
    private ImageView wifiIcon;
    // Check Connectivity
    private CheckConnectivity checkConnectivity;
    // Util Constants
    public static final String CURRENT_POSITION = "curr_pos";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Audio onCreate");
        super.onCreate(savedInstanceState);

        //-----------------------------------------------------
        // INITIALIZE VARIABLES
        //-----------------------------------------------------

        // Set up the Media Player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        // Set up the Media Controller
        mediaController = new MediaController(getActivity());

        // Set up connectivity
        checkConnectivity = new CheckConnectivity(getActivity());

        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();

        // Initialize directory for download the file. It depends from todayString
        streamingAudioURL = new String(Constants.DOMAIN +
                                        todayString +
                                        Constants.AUDIO_RESOURCE +
                                        todayString +
                                        Constants.AUDIO_EXTENSION);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save audio position
        Log.d("LifeCycle", "Audio onSavedInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mediaPlayer.getCurrentPosition());
        Log.d("VideoBug","curr_audio_pos " + mediaPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        Log.d("LifeCycle", "Audio onPause");
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LifeCycle", "Audio onCreateView");
        View v = inflater.inflate(R.layout.audio_layout, container, false);

        // Audio Icon findById
        audioIcon = (ImageView) v.findViewById(R.id.audio_image_view);
        //TODO Qualcosa è andato storto con questa vista!
        //wifiIcon = (ImageView) v.findViewById(R.id.wifi_audio_image_view);

        if(!checkConnectivity.isConnected()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_noConnection), Toast.LENGTH_LONG).show();
        } else {
            // Anchor mediaController to the Fragment's view
            mediaController.setAnchorView(v);

            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mediaController.show();
                    return false;
                }
            });

            // Media Player Settings
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(streamingAudioURL);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Media Player prepare and start
            try {
                mediaPlayer.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (savedInstanceState != null)
                mediaPlayer.seekTo(savedInstanceState.getInt(CURRENT_POSITION));

            mediaPlayer.start();
        }

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
