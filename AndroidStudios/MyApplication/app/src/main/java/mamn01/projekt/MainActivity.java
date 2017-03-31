package mamn01.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
        Intent i = new Intent(this, MainActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }

    /*
     * Redirect user to SettingsActivity
     */
    public void StartSettingsActivity(View v){
        Intent i = new Intent(this, MainActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }


    /*
     * Redirect user to WantActivity
     */
    public void StartWantActivity(View v){
        Intent i = new Intent(this, MainActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }


    /*
     * Redirect user to HugboardActivity
     */
    public void StartHugboardActivity(View v){
        Intent i = new Intent(this, MainActivity.class); // TODO: Send to correct Activity class.
        startActivity(i);
    }


}
