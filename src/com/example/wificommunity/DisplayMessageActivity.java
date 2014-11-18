package com.example.wificommunity;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;



public class DisplayMessageActivity extends ActionBarActivity {
	private WifiManager wifi;
    private List<ScanResult> wifiList;
    private TextView textView;
    private WifiReceiver receiverWifi;
    
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            StringBuilder sb = new StringBuilder();
            wifiList = wifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
            	ScanResult network = wifiList.get(i);
            	
                sb.append(Integer.valueOf(i+1).toString() + ".");
                sb.append(network.SSID + " " + network.capabilities);
                sb.append("\n");
            }
            textView.setText(sb);
        }
    }
    
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the message from the intent
	    //Intent intent = getIntent();
	    //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		//setContentView(R.layout.activity_display_message);
		
	    wifi = (WifiManager)getSystemService(this.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {  
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
            Toast.LENGTH_LONG).show();

            wifi.setWifiEnabled(true);
        } 
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        wifi.startScan();
        //mainText.setText("Starting Scan...");
	    
	    // Create the text view
        textView = new TextView(this);
        textView.setTextSize(14);
        
        //textView = (TextView) findViewById(R.id.maintext);
	    //textView = new TextView(this);
	    //textView.setHeight(400);
	    textView.setTextSize(12);
	    textView.setText("Starting scan...");
	    //textView.setWidth(pixels)
	    setContentView(textView);
	    
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	} */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
