package mamn01.projekt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /*
     * Redirect user to GiveActivity
     */
    public void StartGiveActivity(View v){
        Intent i = new Intent(this, MapsActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);

    }

    /*
     * Redirect user to SettingsActivity
     */
    public void StartSettingsActivity(View v){
        Intent i = new Intent(this, SettingsActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }


    /*
     * Redirect user to WantActivity
     */
    public void StartWantActivity(View v){
        Intent i = new Intent(this, SearchingActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }


    /*
     * Redirect user to HugboardActivity
     */
    public void StartHugboardActivity(View v){
        Intent i = new Intent(this, HugboardActivity.class);
        startActivity(i);
    }


}
