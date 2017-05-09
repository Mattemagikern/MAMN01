package mamn01.projekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class ConnectActivity extends AppCompatActivity {

    private TextView connectText;
    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);

        connectText = (TextView) findViewById(R.id.connecttext);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mymatch = sharedPref.getString("mymatch", "NO MATCH FOUND");
        try {
            JSONObject match = new JSONObject(mymatch);
            String name = match.getString("name");
            connectText.setText(connectText.getText() + "\n: " + name);
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(ConnectActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        h.removeCallbacksAndMessages(null);
    }
}
