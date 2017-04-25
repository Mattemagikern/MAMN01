package mamn01.projekt;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {
    private GoogleMap gMap;
    private GoogleApiClient gCli;
    private Marker Counterpart;
    private LocationRequest locReq;
    private Location loc;
    private String matchId;
    private MarkerOptions markerOptions;
    private double inc = 0.0004;
    private double myLat = 0.0;
    private double myLong = 0.0;
    private double dLat = 0.0;
    private double dLong = 0.0;
    private int x = 0;
    private String deviceId;
    private Vibrator vibrator;
    boolean ifVibrate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        checkLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("onCreate","Done!");
        deviceId = Settings.Secure.getString(MapsActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mymatch = sharedPref.getString("mymatch", "NO MATCH FOUND");
        Log.d("Get Match ", mymatch);
        try {
            JSONObject match = new JSONObject(mymatch);

            matchId = match.getString("id");
            double lat = match.getDouble("lat");
            double lng = match.getDouble("lng");
            markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(lat, lng));
            markerOptions.title("Hugger");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(final GoogleMap gMap) {
        this.gMap = gMap;
        Counterpart = gMap.addMarker(markerOptions);
        gMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.mapis2)));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }else{
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }
        Log.d("OnMapReady","Done!");
        final Timer t =  new Timer();
        t.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                String url = "http://shapeapp.se/mamn01/?action=getCoordinate&device=" + deviceId + "&id=" + matchId;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject data;
                                    try {
                                        String dataStr = (String) response.get("data");
                                        data = new JSONObject(dataStr);
                                        double lat = data.getDouble("lat");
                                        double lng = data.getDouble("lng");

                                        // Make other go towards us.
                                        double steps = 3;
                                        if(myLat != 0.0) {
                                            dLat = dLat + (myLat - lat) / steps;
                                            dLong = dLong + (myLong - lng) / steps;
                                            lat = lat + (dLat);
                                            lng = lng + (dLong);
                                            x++;
                                        }
                                        // Update position
                                        if(x <= steps) {
                                            Counterpart.setPosition(new LatLng(lat, lng));
                                        }

                                        if (x == steps){
                                            ifVibrate = true;
                                        }

                                        if(ifVibrate){
                                            long[] pattern = {0, 500, 1000, 500};
                                            vibrator.vibrate(pattern,-1);
                                            ifVibrate =  false;
                                        }

                                        // Checks distance
//                                        if ((myLat - lat < 0.000001) && (myLong - lng < 0.000001)){
//                                            ifVibrate = true;
//                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                MySingleton.getInstance(MapsActivity.this).addToRequestQueue(jsObjRequest);
            }
        },0,3000);

    }

    protected synchronized void buildGoogleApiClient() {
        gCli = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gCli.connect();
    }

    public void StarActivity(View v){
        Intent i = new Intent(this, StarActivity.class);
        startActivity(i);
        this.finish();
    }
    public void PugActivity(View v){
        Intent i = new Intent(this,PugActivity.class);
        startActivity(i);
        this.finish();
    }
    @Override
    public void onLocationChanged(Location location) {
        loc = location;
        myLat = location.getLatitude();
        myLong = location.getLongitude();
        LatLng latLng = new LatLng(myLat, myLong);
        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //send to database
        String url = "http://shapeapp.se/mamn01/?action=updateCoordinate&device=" + deviceId + "&lat=" + myLat + "&lng=" + myLong;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(jsObjRequest);
        //stop location updates
        if (gCli != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(gCli, this);
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        locReq = new LocationRequest();
        locReq.setInterval(1000);
        locReq.setFastestInterval(1000);
        locReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(gCli, locReq, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (gCli == null) {
                            buildGoogleApiClient();
                        }
                        gMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
