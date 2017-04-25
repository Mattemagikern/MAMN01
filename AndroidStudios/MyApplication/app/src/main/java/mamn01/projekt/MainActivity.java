package mamn01.projekt;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button score , search, settings;
    private AudioManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(SoundEffectConstants.CLICK);
        setSupportActionBar(toolbar);

        settings = (Button) findViewById(R.id.settings);
        search = (Button) findViewById(R.id.search);
        score = (Button) findViewById(R.id.score);

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.playSoundEffect(SoundEffectConstants.CLICK);
                Intent i = new Intent(MainActivity.this, SettingsActivity.class); //
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setSoundEffectsEnabled(true);
                v.playSoundEffect(SoundEffectConstants.CLICK);
                Intent i = new Intent(MainActivity.this, SearchingActivity.class); //
                startActivity(i);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setSoundEffectsEnabled(true);
                v.playSoundEffect(SoundEffectConstants.CLICK);
                Intent i = new Intent(MainActivity.this, HugboardActivity.class); //
                startActivity(i);
            }
        });
    }
}
