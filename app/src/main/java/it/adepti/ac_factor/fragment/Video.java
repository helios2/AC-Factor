package it.adepti.ac_factor.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;

public class Video extends Fragment implements MediaController.MediaPlayerControl {

    private final String TAG = "Video";

    // Video View
    private VideoView vidView;

    // Video address
    private String vidAddress;
    private Uri vidUri;

    // Media Controller
    private MediaController mediaController;

    // Check connectivity
    private CheckConnectivity checkConnectivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the video view on the activity
        vidView = new VideoView(getActivity());

        // Set up the Media Controller
        mediaController = new MediaController(getActivity());

        // Check Connectivity
        checkConnectivity = new CheckConnectivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_layout, container, false);

        if(!checkConnectivity.isConnected()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_noConnection), Toast.LENGTH_LONG).show();
        }

        // Anchor mediaController View
        mediaController.setAnchorView(v);

        // Set Up the Video View
        vidView = (VideoView) v.findViewById(R.id.video_view);

        // Address URI Parsing and Setting
        vidAddress = "http://androidprova.altervista.org/100415/Video_100415.mp4";
        vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);

        // Video start
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
        // Salva l'ultimo punto in cui si stava visualizzando il video
        super.onSaveInstanceState(outState);
        outState.putInt("video_pos", vidView.getCurrentPosition());
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        // Restore dell'ultimo punto in cui si stava visualizzando il video
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            vidView.seekTo(savedInstanceState.getInt("video_pos"));
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
