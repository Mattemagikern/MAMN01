package mamn01.projekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    /*
     * Redirect user to WantActivity
     */
    public void UpdateName(View v){
        //TODO: Implement
    }


    /*
     * Redirect user to HugboardActivity
     */
    public void Cancel(View v){
        finish();
    }
}
