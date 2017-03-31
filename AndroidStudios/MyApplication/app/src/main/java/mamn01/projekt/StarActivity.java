package mamn01.projekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
    }

    /**
     * Redirect user to mainActivity
     * @param v
     */
    public void MainActivity(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
