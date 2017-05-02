package mamn01.projekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PugActivity extends AppCompatActivity {


    private String matchId;
    private Button pugButton;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pug);
        mp = MediaPlayer.create(this, R.raw.sadtrombone);
        pugButton = (Button) findViewById(R.id.pugButton);
        pugButton.setEnabled(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mymatch = sharedPref.getString("mymatch", "NO MATCH FOUND");
        try {
            JSONObject match = new JSONObject(mymatch);
            matchId = match.getString("id");
            setHugFailed();
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }
    }

    /*
    *  Fetch from the backend the list of hugboard.
    *  Add the response to the ListAdapter for updating the ListView.
   */
    private void setHugFailed() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=hugfailed&device=" + deviceId + "&other=" + matchId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mp.start();
                        pugButton.setEnabled(true);
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
    @Override
    public void onPause(){
        super.onPause();
        mp.stop();
    }

    public void BackToMain(View v){
        this.finish();
    }
}
