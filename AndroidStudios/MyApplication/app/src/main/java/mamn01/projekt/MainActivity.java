package mamn01.projekt;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button score, search, settings, feedback;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mp = MediaPlayer.create(this, R.raw.wiii);
        setSupportActionBar(toolbar);

        settings = (Button) findViewById(R.id.settings);
        search = (Button) findViewById(R.id.search);
        score = (Button) findViewById(R.id.score);
        feedback = (Button) findViewById(R.id.feedback);

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp.start();
                Intent i = new Intent(MainActivity.this, SearchingActivity.class); //
                startActivity(i);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HugboardActivity.class); //
                startActivity(i);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScOxHPrA1jpefeMmVBQbBrtEVvjpPvkxJ9Y74E6Ypnf8fdgVA/viewform?usp=sf_link");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
