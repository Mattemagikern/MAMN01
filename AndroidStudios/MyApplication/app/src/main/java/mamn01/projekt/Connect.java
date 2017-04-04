package mamn01.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class Connect extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Connect.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}
