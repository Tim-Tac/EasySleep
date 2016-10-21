package com.app.timtac.easysleep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UI
    private View root;
    private ImageView start;
    private LinearLayout linearLayout;

    // Variables
    private Context mContext;
    private int brightness_saved;
    private String color_bg = "#D73612";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        linearLayout = (LinearLayout) findViewById(R.id.backgroundButton);
        start = (ImageView) findViewById(R.id.playButton);
        root = linearLayout.getRootView();

        // Get brightness to store it
        brightness_saved = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);


        // Start light when button is touched
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                root.setBackgroundColor(Color.parseColor(color_bg));

                // Hide action bar
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }

                // Hide start button
                start.setVisibility(View.GONE);


                // Set birghtness to maximum
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(mContext)) {
                        Settings.System.putInt(mContext.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, 255
                        );
                    } else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                else
                {
                    Settings.System.putInt(getApplicationContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 255);
                }

                StartBrightnessDown();

            }
        });

    }

    public void StartBrightnessDown()
    {
        new DowngradeBrightness().execute(50);

    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Shows Action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        //set Brightness to same as before when quitting app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (Settings.System.canWrite(mContext))
            {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, brightness_saved);
            }
            else
            {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else
        {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness_saved);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DowngradeBrightness extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... params)
        {
            int duration = params[0];

            for (int i=255 ; i>0 ; i--)
            {
                try
                {
                    Thread.sleep(duration);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, i);
            }
            return null;
        }

        protected void onPostExecute()
        {
            Toast.makeText(mContext,"DONE",Toast.LENGTH_SHORT).show();
        }
    }

}
