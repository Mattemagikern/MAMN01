package mamn01.projekt;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private double x = 0.0;
    private String deviceId;
    private Vibrator vibrator;
    boolean ifVibrate = false;
    private Button hugccessButton;
    private boolean hasGottenClose = false;
    boolean test_mode_wrong_direction = true;
    private double dKm = -1.0;
    private double tmp_dLat = 0, tmp_dLong = 0, tmp_lat = 0, tmp_lng = 0, tmp_dKm = 0;
    private Timer t;
    private boolean testMode;
    private SensorManager sm;
    private Sensor am;
    private long lastTime = System.currentTimeMillis();
    private float last_x, last_y, last_z = 0;
    private float curr_x, curr_y, curr_z;
    private boolean drawMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        checkLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("onCreate", "Done!");
        deviceId = Settings.Secure.getString(MapsActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        hugccessButton = (Button) findViewById(R.id.acc);
        hugccessButton.setEnabled(false);
        shakefield = (TextView) findViewById(R.id.shake);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        am = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mymatch = sharedPref.getString("mymatch", "NO MATCH FOUND");
        testMode = sharedPref.getBoolean("testmode", true);
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
        } else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                positionFetched();
            }
        }, 0, 3000);
    }

    private void doRoute(LatLng myPos, LatLng otherPos) {
        //gMap.clear();

        // Creating MarkerOptions
        //MarkerOptions options = new MarkerOptions();
        //options.position(otherPos);
        //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //gMap.addMarker(options);

        // Getting URL to the Google Directions API
        String url = getUrl(myPos, otherPos);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&sensor=false&mode=walking";
        // Output format
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            ;
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
            }
        }
    }


    private void positionFetched() {
        String url = "http://shapeapp.se/mamn01/?action=getCoordinate&device=" + deviceId + "&id=" + matchId + "&testmode=" + testMode;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data;
                            try {
                                String dataStr = (String) response.get("data");
                                data = new JSONObject(dataStr);

                                // Use these to create awesome app.
                                boolean hugfailed = 1 == data.getInt("hugfailed");
                                boolean hugccess = 1 == data.getInt("hugccess");
                                boolean lost = 1 == data.getInt("lost");

                                if (hugccess) {
                                    finish();
                                }
                                if (hugfailed) {
                                    PugActivity(null);
                                }
                                if (lost) {
                                    otherIsLost();
                                }

                                double lat = data.getDouble("lat");
                                double lng = data.getDouble("lng");

                                LatLng otherPos = new LatLng(lat, lng);
                                LatLng myPos = new LatLng(myLat, myLong);

                                double prev_dKm = dKm;
                                double dKm = CalculationByDistance(otherPos, myPos);

                                // Make other go towards us if in testmode.
                                double steps = 4.0;
                                if (testMode && myLat != 0.0) {
                                    if (x < steps) {
                                        dLat = dLat + (myLat - lat) / steps;
                                        dLong = dLong + (myLong - lng) / steps;
                                    }
                                    lat = lat + (dLat);
                                    lng = lng + (dLong);
                                    prev_dKm = dKm;
                                    double newDistance = (dKm - (x * dKm / steps));
                                    dKm = newDistance;
                                    if (x == 1 && test_mode_wrong_direction) {
                                        tmp_dLat = dLat;
                                        tmp_dLong = dLong;
                                        tmp_lat = lat;
                                        tmp_lng = lng;
                                        tmp_dKm = dKm;
                                    } else if (x == 3 && test_mode_wrong_direction) {
                                        test_mode_wrong_direction = false;
                                        prev_dKm = dKm;
                                        dKm = dKm + (x * dKm / steps);
                                        dLat = tmp_dLat;
                                        dLong = tmp_dLong;
                                        lat = tmp_lat;
                                        lng = tmp_lng;
                                        x = 1;
                                    }
                                    if (x < steps) {
                                        x++;
                                    }
                                }

                                if (prev_dKm > dKm && dKm > 0.3 && dKm < 1.0) {
                                    MediaPlayer getCloser = MediaPlayer.create(MapsActivity.this, R.raw.hugsie);
                                    getCloser.start();
                                }

                                if (prev_dKm < dKm && dKm > 0.3 && dKm < 2.0) {
                                    MediaPlayer getFurther = MediaPlayer.create(MapsActivity.this, R.raw.getting_away);
                                    getFurther.start();
                                }

                                // clear map
                                gMap.clear();

                                // Update position
                                LatLng counterPartPos = new LatLng(lat, lng);
                                Counterpart = gMap.addMarker(markerOptions);
                                Counterpart.setPosition(counterPartPos);
                                // Do a route
                                if(drawMap) {
                                    Toast.makeText(MapsActivity.this, "Shake detected, We'll help you find your way!", Toast.LENGTH_LONG).show();
                                    doRoute(myPos, counterPartPos);
                                }


                                if (hasGottenClose == false) {
                                    ifVibrate = true;
                                    hasGottenClose = true;
                                }
                                if (ifVibrate) {
                                    long[] pattern = {0, 500, 1000, 500};
                                    vibrator.vibrate(pattern, -1);
                                    ifVibrate = false;
                                }

                                // Enable hugccess if close
                                hugButtonEnabler(dKm);
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

    private void otherIsLost() {
        //TODO: Implement something cool. Like flash or vibration.

    }

    private void hugButtonEnabler(double dKm) {
        double rangeLimit = 0.3;
        boolean hugEnable = dKm < rangeLimit;
        hugccessButton.setEnabled(hugEnable);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    protected synchronized void buildGoogleApiClient() {
        gCli = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gCli.connect();
    }

    public void StarActivity(View v) {
        Intent i = new Intent(this, SplendidActivity.class);
        startActivity(i);
        this.finish();
    }

    public void PugActivity(View v) {
        Intent i = new Intent(this, PugActivity.class);
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

    public boolean checkLocationPermission() {
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
        curr_x= event.values[0];
        curr_y= event.values[1];
        curr_z = event.values[2];
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        long diffTime = curTime - lastTime;
    if(diffTime > 100) {
        lastTime = curTime;
        float speed = Math.abs(curr_x + curr_y + curr_z  - last_x
                - last_y - last_z) / diffTime * 10000;

        if (speed > 500) {
            Log.d("sensor", "shake detected w/ speed: " + speed);
            drawMap = true;
        }
    }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, am, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        LocationServices.FusedLocationApi.removeLocationUpdates(gCli, this);
        t.cancel();
    }
}
