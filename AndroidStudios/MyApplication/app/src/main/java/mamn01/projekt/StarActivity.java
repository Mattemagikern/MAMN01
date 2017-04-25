package mamn01.projekt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StarActivity extends Activity {

    private String matchId;
    private Button splendidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.star_activity);

        splendidButton = (Button) findViewById(R.id.splendid);
        splendidButton.setEnabled(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mymatch = sharedPref.getString("mymatch", "NO MATCH FOUND");
        try {
            JSONObject match = new JSONObject(mymatch);
            matchId = match.getString("id");
            setHugccess();
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }
    }

    /*
     *  Fetch from the backend the list of hugboard.
     *  Add the response to the ListAdapter for updating the ListView.
    */
    private void setHugccess() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=hugccess&device=" + deviceId + "&other=" + matchId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO: Nothing to do, nothing to see.
                        splendidButton.setEnabled(true);

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

    /**
     * Redirect user to mainActivity
     * @param v
     */
    public void MainActivity(View v){
        finish();
    }
}
