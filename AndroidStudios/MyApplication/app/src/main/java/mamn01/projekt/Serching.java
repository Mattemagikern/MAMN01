package mamn01.projekt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class Serching extends AppCompatActivity {
    private ImageView spinner;
    private TextView text;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serching);
        spinner = (ImageView) findViewById(R.id.spinner);
        // a nice text animation maybe?
        text = (TextView) findViewById(R.id.text);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        spinner.startAnimation(animation);
        //Let's it spin for 5s, befor starting new activity
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                String deviceId = Settings.Secure.getString(Serching.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                String url = "http://shapeapp.se/mamn01/?action=matchMeUp&device=" + deviceId;

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject data = (JSONObject) response.get("data");


                                    Intent i = new Intent(Serching.this, Connect.class);
                                    i.putExtra("mymatch", data.toString());
                                    startActivity(i);
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
            }
        },0,500);
    }
    public void CancelPressed(View v){
        finish();
    }


    private void matchMeUp() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=matchMeUp&device=" + deviceId;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = (JSONObject) response.get("data");


                            Intent i = new Intent(Serching.this, Connect.class);
                            i.putExtra("mymatch", data.toString());
                            startActivity(i);
                            finish();

                        } catch (Exception e) {
                            matchMeUp();
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
