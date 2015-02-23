package com.example.kyle.assignment1;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private int BATTERY_CUTOFF;

    Thread thread = new Thread(){
        @Override
        public void run(){
            int count = 0;
            while(true){
                System.out.println("Hello");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final float originalLevel = getBatteryLevel();

        final EditText numberInput = (EditText)findViewById(R.id.edit);
        final Button shakeButton = (Button)findViewById(R.id.button);
        final TextView topMsg = (TextView)findViewById(R.id.textView);
        final TextView batteryMsg = (TextView)findViewById(R.id.textView2);
        ImageView img = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        String path = Environment.getExternalStorageDirectory() + "";

        shakeButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
                        PowerManager.WakeLock wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
                        float level = getBatteryLevel();
                        if(originalLevel - level >= BATTERY_CUTOFF){
                            wakelock.acquire();
                            //TODO print something to screen
                            wakelock.release();
                        }

                    }
                });

        numberInput.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            String input = numberInput.getText().toString();
                            int inputNum = Integer.parseInt(input);
                            setBatteryLvl(inputNum);
                            handled = true;
                        }
                        return handled;
                    }
                });

        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBatteryLvl(int lvl){
        BATTERY_CUTOFF = lvl;
    }

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

}
