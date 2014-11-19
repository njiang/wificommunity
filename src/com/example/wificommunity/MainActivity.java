package com.example.wificommunity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import com.example.wificommunity.DisplayMessageActivity.WifiReceiver;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
    private HashMap<String, ScanResult> scanResults = new HashMap<String, ScanResult>();
    
    private Socket dataSocket;
    private String serverIP = "54.69.117.206";
    private int serverPort = 5999;
    private DataInputStream instream;
    private DataOutputStream outstream;
    
    class SocketCommunicationTask extends AsyncTask<Object, Void, String> {

        private Exception exception;
        String ssid;
        public SocketCommunicationTask(String ssid) {
        	this.ssid = ssid;
        }
        
        protected String doInBackground(Object... params) {
            try {
            	String command = (String)params[0];
            	DataInputStream ins = (DataInputStream)params[1];
            	DataOutputStream os = (DataOutputStream)params[2];
            	os.writeUTF(command);
            	os.flush();
            	String response = ins.readUTF();
                return response;
            } catch (Exception e) {
                this.exception = e;
                dataSocket = null;
                instream = null;
                outstream = null;
                SocketConnectionTask sct = new SocketConnectionTask();
                sct.execute(serverIP, serverPort);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            // TODO: check this.exception 
            // TODO: do something with the feed
        	if (response != null) {
	             WifiConfiguration conf = new WifiConfiguration();
	             conf.SSID = "\"" + ssid + "\"";
	             conf.preSharedKey = "\""+ response +"\"";
	             wifi.addNetwork(conf);
	             
	             List<WifiConfiguration> list = wifi.getConfiguredNetworks();
	             for( WifiConfiguration i : list ) {
	                 if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
	                      wifi.disconnect();
	                      wifi.enableNetwork(i.networkId, true);
	                      wifi.reconnect();               
	                      break;
	                 }           
	              }
            }
        }
    }
    
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            //StringBuilder sb = new StringBuilder();
            wifiList = wifi.getScanResults();
            String[] values = new String[wifiList.size()];
            for(int i = 0; i < wifiList.size(); i++){
            	ScanResult network = wifiList.get(i);
            	values[i] = new String(network.BSSID + ";;;" + network.SSID);
            	scanResults.put(network.BSSID, network);
            	
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
                	 int itemPosition = position;
             
		             // ListView Clicked item value
		             String  itemValue = (String) listView.getItemAtPosition(position);
		                
		             // Show Alert 
		             Toast.makeText(getApplicationContext(),
		                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
		                .show();
		             
		             try {
		            	 // Get BSSID
		            	 String bssid = itemValue.split(";;;")[0];
		            	 String ssid = itemValue.split(";;;")[1];
			             // Contact server to get password
			             String command = "GetPassword;;;" + bssid;
			             //outstream.writeChars(command);
			             if (dataSocket != null) {
				             SocketCommunicationTask sct = new SocketCommunicationTask(ssid);
				             sct.execute(command, instream, outstream);
			             }
			             else {
			            	 SocketConnectionTask sct = new SocketConnectionTask();
			                 sct.execute(serverIP, serverPort);
			             }
			             
		             }
		             catch (Exception e) {
		            	 System.out.println("Failed to connect to Wifi " + e.getMessage());
		            	 e.printStackTrace();
		             }
		           }
  
            }); 
        }
    }
    
    class SocketConnectionTask extends AsyncTask<Object, Void, Socket> {

        private Exception exception;

        protected Socket doInBackground(Object... params) {
            try {
            	String url = (String)params[0];
            	int port = ((Integer)params[1]).intValue();
                return new Socket(url, port);
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Socket socket) {
            // TODO: check this.exception 
            // TODO: do something with the feed
        	dataSocket = socket;
        	try {
	        	instream = new DataInputStream(dataSocket.getInputStream());
	        	outstream = new DataOutputStream(dataSocket.getOutputStream());
        	}
        	catch (Exception e) {
        		System.out.println("Failed to get streams of socket " + e.getMessage());
        		socket = null;
        	}
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        
        SocketConnectionTask sct = new SocketConnectionTask();
        sct.execute(this.serverIP, this.serverPort);
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
