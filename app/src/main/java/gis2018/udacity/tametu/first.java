package gis2018.udacity.tametu;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class first extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        TextView textv = (TextView) findViewById(R.id.tametu);
        textv.setShadowLayer(85, 0, 0, Color.WHITE);
        /** Called when the activity is first created. */


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(first.this, MainActivity.class);
                first.this.startActivity(mainIntent);
                first.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}