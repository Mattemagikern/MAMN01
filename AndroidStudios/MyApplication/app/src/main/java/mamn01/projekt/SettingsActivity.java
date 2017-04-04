package mamn01.projekt;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar rangeBar;
    private TextView rangeText;
    private EditText nameText;

    private int minRange = 100;
    private int currentRange = 2000;
    private String currentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        rangeBar = (SeekBar) findViewById(R.id.rangeBar);
        rangeText = (TextView) findViewById(R.id.rangeText);
        nameText = (EditText) findViewById(R.id.nameText);
    }

    @Override
    public void onResume(){
        super.onResume();

        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(fromUser){
                    currentRange = 100 + minRange * progress;
                    setRange(currentRange);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        getProfile();
    }

    public void setRange(int range){
        rangeText.setText("Range: " + (range) + "m");
        rangeBar.setProgress((currentRange - 100) / minRange);
    }


    /*
     * Redirect user to WantActivity
     */
    public void UpdateProfile(View v){
        String name = nameText.getText().toString();
        String range = String.valueOf(currentRange);
        if(name.length() < 1){
            Toast.makeText(this, "Please give a name", Toast.LENGTH_LONG).show();
            return;
        }
        updateProfileOnBackend(name, range);
    }


    /*
     * Redirect user to HugboardActivity
     */
    public void Cancel(View v){
        finish();
    }

    private void updateProfileOnBackend(String name, String range) {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=updateName&device=" + deviceId + "&name=" + name + "&range=" + range;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SettingsActivity.this.finish();
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


    private void getProfile() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=getByDevice&device=" + deviceId;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = (JSONObject) response.get("data");
                            String nme = data.getString("name");
                            int rng = Integer.parseInt(data.getString("hugrange"));
                            SettingsActivity.this.nameText.setText(nme);
                            SettingsActivity.this.setRange(rng);
                        } catch (JSONException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
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
