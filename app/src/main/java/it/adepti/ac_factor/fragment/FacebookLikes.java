package it.adepti.ac_factor.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.VideoItem;
import it.adepti.ac_factor.VideoList;
import it.adepti.ac_factor.utils.CheckConnectivity;

public class FacebookLikes extends Fragment{

    //===================================
    // FACEBOOK UTILS
    //===================================
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private LoginButton loginButton;
    private AccessToken currentToken;
    private Profile currentLogInProfile;

    //===================================
    // ACTIVITY VIEWS
    //===================================
    private TextView textUser;
    private ListView listView;
    private VideoList videoList;

    //===================================
    // UTILS
    //===================================
    private CheckConnectivity checkConnectivity;
    public static final String NOLINK = "nolink";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFacebook();

        checkConnectivity = new CheckConnectivity(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(getActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.facebook_fragment, container, false);
        // Creating Facebook Login Button
        loginButton = (LoginButton)v.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        // Creating Video List Views
        videoList = new VideoList(getActivity(), R.layout.fb_list_item, new ArrayList<VideoItem>());
        videoList.setFragment(this);
        listView = (ListView)v.findViewById(R.id.listViewVideos);
        listView.setAdapter(videoList);
        // Finding TextView
        textUser = (TextView)v.findViewById(R.id.txtUserLogeedIn);
        // Retrieving Facebook User Info
        currentToken = AccessToken.getCurrentAccessToken();
        currentLogInProfile = Profile.getCurrentProfile();
        if(currentLogInProfile != null) {
            textUser.setText(getActivity().getResources().getString(R.string.text_hello) + " " +
                    currentLogInProfile.getName());
        }else{
            textUser.setText(getResources().getString(R.string.text_not_logged_in));
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/acfactornola",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        videosRequest(response);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "posts.since(2015-04-02){name,caption,actions}");
        graphRequest.setParameters(parameters);
        videoList.clear();
        VideoItem noVideos = new VideoItem(getResources().getString(R.string.text_list_not_logged_in), NOLINK);
        videoList.add(noVideos);
        videoList.notifyDataSetChanged();
        if(checkConnectivity.isConnected()) {
            graphRequest.executeAsync();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem item = (VideoItem)listView.getItemAtPosition(position);
                String uri = item.getLikeLink();
                if(uri.compareTo(NOLINK) != 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void initializeFacebook() {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                currentToken = currentAccessToken;
                if (currentAccessToken != null){
                    GraphRequest graphRequest = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/acfactornola",
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    videosRequest(response);
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "posts.since(2015-04-02){name,caption,actions}");
                    graphRequest.setParameters(parameters);
                    videoList.clear();
                    videoList.notifyDataSetChanged();
                    if(checkConnectivity.isConnected()) {
                        graphRequest.executeAsync();
                    }
                }
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                currentLogInProfile = currentProfile;
                if(currentLogInProfile != null) {
                    textUser.setText(getActivity().getResources().getString(R.string.text_hello) + " " +
                            currentLogInProfile.getName());
                }else{
                    textUser.setText("Non sei loggato");
                }
            }
        };
    }

    private void videosRequest(GraphResponse response) {
        Log.d("JSON", response.toString());
        JSONObject object = response.getJSONObject();
        try {
            if(object != null && object.has("posts")) {
                JSONObject post = object.getJSONObject("posts");
                if(post != null && post.has("data")) {
                    JSONArray data = post.getJSONArray("data");
                    for (int j = 0; j < data.length(); j++) {
                        JSONObject current = data.getJSONObject(j);
                        if (current != null && current.has("caption")) {
                            String caption = (String) current.get("caption");
                            if (caption != null && caption.compareTo("youtube.com") == 0) {
                                JSONArray actions = current.getJSONArray("actions");
                                JSONObject linkObject = actions.getJSONObject(1);
                                videoList.add(new VideoItem(current.getString("name"), linkObject.getString("link")));
                                videoList.notifyDataSetChanged();
                                Log.d("JSON", "Name: " + current.getString("name"));
                                Log.d("JSON", "Link: " + linkObject.getString("link"));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
