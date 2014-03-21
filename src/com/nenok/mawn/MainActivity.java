package com.nenok.mawn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

//import com.example.test.R;
import com.nenok.db.DBHelper;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	Button buttonStart, buttonCancel;
	EditText usernameFld, passwordFld;
	TextView respResult;
	String username, password;
	private DBHelper dh;
	protected SQLiteDatabase db;
	 public SQLiteDatabase myDataBase;
	 ProgressDialog mProgressDialog;
	 AlertDialog.Builder builder;
	 DialogInterface.OnClickListener dialogClickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		deleteSMS(getApplicationContext());
//		SmsManager smsManager = SmsManager.getDefault();
////		Log.v("sender : ", sender);
//		smsManager.sendTextMessage("09166456537", null, "asds", null, null);
		
//		getApplicationContext().deleteDatabase("nenok.db");
//		myDataBase.close();
//		Date date=Calendar.getInstance().getTime();
//		System.out.println(date);
//		Log.v("date", date.toString());
		respResult = (TextView) findViewById(R.id.resp);
		usernameFld = (EditText) findViewById(R.id.uname);
		passwordFld = (EditText) findViewById(R.id.passw);
		
		buttonStart = (Button) findViewById(R.id.login);
		buttonStart.setOnClickListener(this);
		
		buttonCancel = (Button) findViewById(R.id.cancel);
		buttonCancel.setOnClickListener(this);
		
		builder = new AlertDialog.Builder(this);
		this.dh = new DBHelper(getApplicationContext());
		
		dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            finish();
		            break;
		        }
		    }
		};
		builder.setMessage("Your phone is now secured! If you want to active ‘Nenok Mode’ or ‘Stolen Mode’, text this message to this phone’s current number (‘Please give me back my phone! Please!’)").setPositiveButton("Ok, got it!", dialogClickListener);
		
		 List<String> names = this.dh.selectAll();
//		 Toast.makeText(getApplicationContext(), names.toString(), Toast.LENGTH_SHORT).show();
		 
		 List<String> locations = this.dh.selectAllLocations();
//		 Toast.makeText(getApplicationContext(), locations.toString(), Toast.LENGTH_LONG).show();
		 startService(new Intent(this, LocationService.class));
		 
		 if (names.size() > 0) {
			 builder.show();
		 }
	}
	
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.login:
				
				username = usernameFld.getText().toString();
				password = passwordFld.getText().toString();
				this.dh = new DBHelper(getApplicationContext());
				if (haveNetworkConnection()){
					new LoginService(this.dh, username, password, getApplicationContext()).execute("http://nenok.herokuapp.com/api/users");
				} else {
					Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.id.cancel:
				finish();
//				System.exit(0);
				break;
		}
		
		
//        LoginService downloadFile = new DownloadFile();
        
        
			}
	
	public class LoginService extends AsyncTask<String, String, String> {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://nenok.herokuapp.com/api/session");
		String username = "";
		String password = "";
		String responseCode = "";
		String message = "";
		String token = "";
		
		private DBHelper dh;
		
		private String resp;
		
		public LoginService(DBHelper dh, String username, String password, Context context) {
			this.username = username;
			this.password = password;
			this.dh = dh;
		}

		@Override
		protected String doInBackground(String... params) {
			publishProgress("Sleeping...");
			try {
//				resp = "Slept for  milliseconds";
				Log.v("email", this.username);
				Log.v("password", this.password);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("email", this.username));
			    nameValuePairs.add(new BasicNameValuePair("password", this.password));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
			
				String responseBody = EntityUtils.toString(response.getEntity());
				JSONObject jsonResponse=new JSONObject(responseBody);
				
				Log.v("body:", responseBody);
				responseCode = Long.toString(response.getStatusLine().getStatusCode());
				Log.v("Response code:", responseCode);
				Log.v("response", responseCode);
				if ("200".equals(responseCode)){
					token = jsonResponse.getString("token").toString();
					Log.v("asd", "asds");
					this.dh.insert(this.username);
					this.dh.insert(this.token);
					resp = "200";
//					db.close();
//					finish();
					
//					builder.show();
				}else{
					message = jsonResponse.getString("message").toString();
					Log.v("message", message);
//					respResult.setText(message);
					resp = message;
				}
				
			} catch (IOException e) {
				Log.v("Error:", e.toString());      
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return resp;
		}
		
		@Override
		  protected void onPostExecute(String result) {
		   // execution of result of Long time consuming operation
			respResult.setText(result);
			Log.v("login result ", result);
			if ("200".equals(result)){
				builder.show();
//				respResult.setText("Your phone is now secured! If you want to active ‘Nenok Mode’ or ‘Stolen Mode’, text this message to this phone’s current number (‘Please give me back my phone! Please!’)");
			}
			mProgressDialog.dismiss();
		  }
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        mProgressDialog = new ProgressDialog(MainActivity.this);
	        mProgressDialog.setMessage("connecting...");
	        mProgressDialog.setIndeterminate(false);
//	        mProgressDialog.setMax(100);
//	        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        mProgressDialog.show(); // Here eclipse tell that "mProgressDialog cannot be resolved"
	    }
	}
	protected void onDestroy() 
	{
	    super.onDestroy();
	    if (db != null) 
	    {
	        db.close();
	    }
	}
	
}
