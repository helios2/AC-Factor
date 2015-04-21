package it.adepti.ac_factor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.youtube.DeveloperKey;

public class Concorso_YouTube extends Fragment {

    YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
    private String playListUrl = "insertHere"; //TODO playlist url
    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer yTubePlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Concorso_YouTube onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LifeCycle", "Concorso_YouTube onCreateView");
        View v = inflater.inflate(R.layout.fragments_youtube, container, false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                yTubePlayer = youTubePlayer;
                yTubePlayer.loadPlaylist("PL55713C70BA91BD6E");
                yTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        return v;
    }
}
