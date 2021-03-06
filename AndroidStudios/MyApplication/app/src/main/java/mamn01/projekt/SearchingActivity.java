package mamn01.projekt;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class SearchingActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Timer Time;
    private GoogleApiClient Gapi;
    private Location location;
    private String lat, lgn;
    private Timer t;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serching);
        time = System.currentTimeMillis();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean testMode = sharedPref.getBoolean("testmode", true);

        ImageView spinner = (ImageView) findViewById(R.id.spinner);
        // a nice text animation maybe?
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        spinner.startAnimation(animation);
        if (Gapi == null) {
            Gapi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                long current = System.currentTimeMillis();
                if (current - time > 10 * 1000) {
                    setHugFailed();
                    return;
                }
                String deviceId = Settings.Secure.getString(SearchingActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                String url = "http://shapeapp.se/mamn01/?action=matchMeUp&device=" + deviceId + "&testmode=" + testMode;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject data;
                                    try {
                                        String dataStr = (String) response.get("data");
                                        data = new JSONObject(dataStr);
                                    } catch (Exception e) {
                                        data = (JSONObject) response.getJSONObject("data");
                                    }

                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("mymatch", data.toString());
                                    editor.commit();
                                    Log.d("MATCH FOUND: ", data.toString());


                                    Intent i = new Intent(SearchingActivity.this, ConnectActivity.class);
                                    startActivity(i);
                                    t.cancel();
                                    t.purge();
                                    finish();
                                } catch (Exception e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                            }


                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Error: " + error.getMessage());
                            }
                        });
                MySingleton.getInstance(SearchingActivity.this).addToRequestQueue(jsObjRequest);
            }
        }, 2000, 4000);
    }

    @Override
    protected void onStart() {
        Gapi.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Gapi.disconnect();
        super.onStop();
    }

    public void CancelPressed(View v) {
        setHugFailed();
        onPause();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        location = LocationServices.FusedLocationApi.getLastLocation(Gapi);
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lgn = String.valueOf(location.getLongitude());
            Log.d("Lat,Long", lat + " " + lgn);
        }
    }

    //vad ska göras här?
    @Override
    public void onConnectionSuspended(int i) {

    }

    //vad ska göras här?
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setHugFailed() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=hugcancelled&device=" + deviceId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        t.cancel();
                        t.purge();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error: " + error.getMessage());
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
