package com.nenok.mawn;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nenok.db.DBHelper;
//import com.nenok.db.DBHelperLoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class ReceiverService extends BroadcastReceiver{
	 public SQLiteDatabase myDataBase; 
	 SmsManager smsManager;
	 String sender = "";
	 String msgBody = "";
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		
        String str = "";
//        String sender;
        if (bundle != null) {
        	Object[] pdus = (Object[]) bundle.get("pdus");
        	msgs = new SmsMessage[pdus.length];
        	for (int i=0; i<msgs.length; i++){
        		
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                msgBody = msgs[i].getMessageBody().toString();
                sender = msgs[i].getOriginatingAddress();
                str += "SMS sds " + msgs[i].getOriginatingAddress();                     
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";  
        	}
        }
//        senderNumber = sender;
        if ("Please give me back my phone! Please! Please!".equals(msgBody)){
        	smsManager = SmsManager.getDefault();
            //---display the new SMS message---
            Toast.makeText(context, str + " ", Toast.LENGTH_LONG).show();
            Log.v("body:", str);
            //--on GPS
            setGPSOn(context);
            
            LocationListener mlocListener = new MyLocationListener(context);
            Log.v("mlocListener:", mlocListener.toString());
        	
        }
	}
        
    private void setGPSOn(Context context) {
    	String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps"))
	     { 
	        //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        context.sendBroadcast(poke);
	    }
	}
    
    
    public class MyLocationListener implements LocationListener {
//    	private DBHelperLoc dhl;
    	private DBHelper dh;
    	
    	Context context;
    	protected LocationManager locationManager;
    	protected LocationListener locationListener;
    	String lat;
    	String provider;
    	protected String latitude,longitude; 
    	protected boolean gps_enabled,network_enabled;
		
    	private boolean haveNetworkConnection() {
    	    boolean haveConnectedWifi = false;
    	    boolean haveConnectedMobile = false;

    	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
    	    for (NetworkInfo ni : netInfo) {
    	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
    	            if (ni.isConnected())
    	                haveConnectedWifi = true;
    	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
    	            if (ni.isConnected())
    	                haveConnectedMobile = true;
    	    }
    	    return haveConnectedWifi || haveConnectedMobile;
    	}
    	
		public MyLocationListener(Context context) {
			this.context = context;
			dh = new DBHelper(context.getApplicationContext());
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 0, this);
            if (haveNetworkConnection()) {
            	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 0, this);
            }else{
            	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 0, this);
            }
            
            Log.v("network:", Boolean.toString(haveNetworkConnection()));
            Log.v("asdsds : ", "lalalal!");
		}

		@Override
		public void onLocationChanged(Location location) {
			
			Log.v("changed : ", "changed!");
//			dh.deleteAll();
//			location.getLatitude();
//			location.getLongitude();
			
//			String[] email = dh.getEmail();
//			Log.v("email from db:", dh.selectAll().get(0).toString());
			String myEmail=dh.selectAll().get(0).toString();
			String myToken=dh.selectAll().get(1).toString();
			int i = (int) (new Date().getTime()/1000L);
			int a = (int) location.getTime();
//			Log.v("email : ", myEmail);
//			Log.v("long : ", Double.toString(location.getLongitude()));
//			Log.v("lat : ", Double.toString(location.getLatitude()));
//			Log.v("token : ", myToken);
//			Log.v("date:", Integer.toString(i));
//			dh = new DBHelper(context.getApplicationContext());
//			dh.insertToLocations(
//					myEmail,
//					Double.toString(location.getLongitude()),
//					Double.toString(location.getLatitude()),
//					myToken,
//					i
//					);
			
			dh.insert2(Double.toString(location.getLongitude()) + "-" + Double.toString(location.getLatitude()) + "-" + a);
			SmsManager smsManager = SmsManager.getDefault();
//			Log.v("sender : ", sender);
			smsManager.sendTextMessage(sender, null, "Last Location \n " + "Longitude:\n" + location.getLongitude() + "\n" + "Latitude:\n" +  Double.toString(location.getLatitude()), null, null);
			if (haveNetworkConnection()) {
				TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				
				String mPhoneNumber = tMgr.getLine1Number();
				Log.v("number : ", mPhoneNumber);
				
				String jsonNumber;
			    JSONObject jsonObjectNumber = new JSONObject();
			    try {
			    	jsonObjectNumber.accumulate("value", mPhoneNumber);
			    	jsonObjectNumber.accumulate("timestamp", a);
				  } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
			    
			    jsonNumber = jsonObjectNumber.toString();
			    HttpClient clientNumber = new DefaultHttpClient();
			    HttpPost postNumber = new HttpPost("http://nenok.herokuapp.com/api/number");
			    List<NameValuePair> numbers = new ArrayList<NameValuePair>();
			    numbers.add(new BasicNameValuePair("email", myEmail));
			    numbers.add(new BasicNameValuePair("token", myToken));
			    numbers.add(new BasicNameValuePair("numbers", "[" + jsonNumber +"]"));
			    
			    try {
					postNumber.setEntity(new UrlEncodedFormEntity(numbers));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch blocksds
					e.printStackTrace();
				}
			    
			    HttpResponse numberResponse = null;
			    try {
					numberResponse = clientNumber.execute(postNumber);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Log.v("response number : ", numberResponse.getStatusLine().toString());
			    
				List<String> locations = this.dh.selectAllLocations();
				int size = locations.size();
			    for (int l=0; l<size; l++)
			    {
			      if (haveNetworkConnection()) {
				      String json;
				      String[] locationSplit = locations.get(l).split("-");
				      JSONObject jsonObject = new JSONObject();
			          try {
						jsonObject.accumulate("longitude", locationSplit[0]);
						jsonObject.accumulate("latitude", locationSplit[1]);
				        jsonObject.accumulate("timestamp", locationSplit[2]);
					  } catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					  }
			          
			 
			            // 4. convert JSONObject to JSON to String
			          json = jsonObject.toString();
			          HttpClient client = new DefaultHttpClient();
	//		          HttpPost post = new HttpPost("http://nenok.herokuapp.com/api/gps");
			          HttpPost post = new HttpPost("http://nenok.herokuapp.com/api/gps");
			          
			          List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			          pairs.add(new BasicNameValuePair("email", myEmail));
			          pairs.add(new BasicNameValuePair("token", myToken));
			          pairs.add(new BasicNameValuePair("gps", "[" + json +"]"));
			          try {
						post.setEntity(new UrlEncodedFormEntity(pairs));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			          
			          HttpResponse response = null;
			          try {
						response = client.execute(post);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			          Log.v("response : ", response.getStatusLine().toString());
	//		          {email: рс, token: рс, gps: JSON({longitude: 1, latitude: 1, timestamp: Unix time in integer})}
			    } else {
			    	Log.v("no network : ", "");
			    }
			  }
			    
			}
			
//			myDataBase.close();
//			String Text = "My current location is: " + "Latitud = " + location.getLatitude() + "Longitud = " + location.getLongitude();
//			Toast.makeText( context.getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
//			Log.v("mlocListener:", Text.toString());
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( context.getApplicationContext(), "Provider Disabled", Toast.LENGTH_SHORT).show();
			Log.v("mlocListener:", "disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( context.getApplicationContext(), "Provider Enabled", Toast.LENGTH_SHORT).show();
			Log.v("mlocListener:", "enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Toast.makeText( context.getApplicationContext(), "Status", Toast.LENGTH_SHORT).show();
			Log.v("mlocListener:", "status");
		}
		
	}

}

