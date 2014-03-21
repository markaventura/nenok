package com.nenok.mawn;

import java.io.IOException;
import java.util.ArrayList;
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

import com.nenok.db.DBHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	Button buttonStart;
	EditText usernameFld, passwordFld;
	TextView respResult;
	String username, password;
	private DBHelper dh;
	protected SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		respResult = (TextView) findViewById(R.id.resp);
		usernameFld = (EditText) findViewById(R.id.uname);
		passwordFld = (EditText) findViewById(R.id.passw);
		
		buttonStart = (Button) findViewById(R.id.login);
		buttonStart.setOnClickListener(this);
		
		this.dh = new DBHelper(getApplicationContext());
		
		 List<String> names = this.dh.selectAll();
		 Toast.makeText(getApplicationContext(), names.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		username = usernameFld.getText().toString();
		password = passwordFld.getText().toString();
		this.dh = new DBHelper(getApplicationContext());
		new LoginService(this.dh, username, password, getApplicationContext()).execute("http://nenok.herokuapp.com/api/users");
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
				resp = "Slept for  milliseconds";
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
					resp = "Login successfully";
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
