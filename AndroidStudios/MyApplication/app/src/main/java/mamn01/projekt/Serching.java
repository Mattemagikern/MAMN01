package mamn01.projekt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class Serching extends AppCompatActivity {
    private ImageView spinner;
    private TextView text;
    private Thread t;

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Serching.this, Connect.class);
                startActivity(i);
                finish();
            }
        }, 5000);
    }


}
