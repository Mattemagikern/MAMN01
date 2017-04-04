package mamn01.projekt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class Serching extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Timer Time;
    private GoogleApiClient Gapi;
    private Location location;
    private String lat,lgn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serching);

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

      final Timer t =  new Timer();
        t.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                String deviceId = Settings.Secure.getString(Serching.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                String url = "http://shapeapp.se/mamn01/?action=matchMeUp&device=" + deviceId;
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

                                    Intent i = new Intent(Serching.this, Connect.class);
                                    i.putExtra("mymatch", data.toString());
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
                MySingleton.getInstance(Serching.this).addToRequestQueue(jsObjRequest);
            }
        },0,2000);
    }
    @Override
    protected void onStart() {
        Gapi.connect();
        super.onStart();
    }
    @Override
    protected void onStop(){
        Gapi.disconnect();
        super.onStop();
    }
    public void CancelPressed(View v){
        finish();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                Gapi);
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lgn = String.valueOf(location.getLongitude());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
