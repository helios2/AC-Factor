package it.adepti.ac_factor.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

import it.adepti.ac_factor.R;

public class Video extends Fragment implements MediaController.MediaPlayerControl {

    private VideoView vidView;
    private String vidAddress;
    private Uri vidUri;
    private MediaController mediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the Media Controller
        mediaController = new MediaController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_layout, container, false);

        // Anchor mediaController View
        mediaController.setAnchorView(v);

        // Set Up the Video View
        vidView = (VideoView) v.findViewById(R.id.video_view);

        vidAddress = "http://androidprova.altervista.org/100415/Video_100415.mp4";
        vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        if(savedInstanceState != null)
            vidView.seekTo(savedInstanceState.getInt("curr_pos"));
        try{
          vidView.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // Set the media controller for the Video View
        vidView.setMediaController(mediaController);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curr_pos", vidView.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        vidView.pause();
    }

    @Override
    public void start() {
        vidView.start();
    }

    @Override
    public void pause() {
        vidView.pause();
    }

    @Override
    public int getDuration() {
        return vidView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return vidView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        vidView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return vidView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return vidView.getBufferPercentage();
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
