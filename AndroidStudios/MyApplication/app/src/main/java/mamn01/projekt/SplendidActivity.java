package mamn01.projekt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class SplendidActivity extends Activity {

    private String matchId;
    private Button splendidButton;
    private Button feedbackButton;
    private TextView hugpoint;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splendid_activity);
        mp = MediaPlayer.create(this, R.raw.wiii);
        splendidButton = (Button) findViewById(R.id.splendid);
        feedbackButton = (Button) findViewById(R.id.sendfeed);
        feedbackButton.setVisibility(View.INVISIBLE);
        splendidButton.setEnabled(false);
        hugpoint= (TextView) findViewById(R.id.hugpoint1);
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
                        mp.start();
                        hugpoint.setText("You just got a hugpoint!");
                        splendidButton.setEnabled(true);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        feedbackButton.setVisibility(View.VISIBLE);
                        splendidButton.setText("No Thanks, Go back to Start");
                        hugpoint.setText("Something went wrong due to connection issues");
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
