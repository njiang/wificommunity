package com.example.wificommunity;

import java.util.List;

import com.example.wificommunity.DisplayMessageActivity.WifiReceiver;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.example.wificommunity.MESSAGE";
	private WifiManager wifi;
    private List<ScanResult> wifiList;
    private TextView textView;
    ListView listView ;
    private WifiReceiver receiverWifi;
    
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            //StringBuilder sb = new StringBuilder();
            wifiList = wifi.getScanResults();
            String[] values = new String[wifiList.size()];
            for(int i = 0; i < wifiList.size(); i++){
            	ScanResult network = wifiList.get(i);
            	values[i] = new String(network.SSID + " " + network.capabilities);
                //sb.append(Integer.valueOf(i+1).toString() + ".");
                //sb.append(network.SSID + " " + network.capabilities);
                //sb.append("\n");
            }
            //textView.setText(sb);
            
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, android.R.id.text1, values);
   
  
            // Assign adapter to ListView
            listView.setAdapter(adapter); 
          
            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
   
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                   int position, long id) {
                      
                     // ListView Clicked item index
                	 int itemPosition     = position;
             
		             // ListView Clicked item value
		             String  itemValue    = (String) listView.getItemAtPosition(position);
		                
		             // Show Alert 
		             Toast.makeText(getApplicationContext(),
		                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
		                .show();
		           
		           }
  
            }); 
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
    
    /** Called when the user clicks the Send button */
    public void scanWiFi(View view) {
        // Do something in response to button
    	/*Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent); */
    	
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
    }
}
