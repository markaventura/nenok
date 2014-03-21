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

		new LoginService(username, password, getApplicationContext()).execute("http://nenok.herokuapp.com/api/users");
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
		
		public LoginService(String username, String password, Context context) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
		        
				Log.v("email", this.username);
				Log.v("password", this.password);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("email", this.username));
			    nameValuePairs.add(new BasicNameValuePair("password", this.password));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
			
				String responseBody = EntityUtils.toString(response.getEntity());
				JSONObject jsonResponse=new JSONObject(responseBody);
				Log.v("Response code:", Long.toString(response.getStatusLine().getStatusCode()));
				Log.v("body:", responseBody);
				Log.v("token:", jsonResponse.getString("token").toString());
				responseCode = Long.toString(response.getStatusLine().getStatusCode());
				message = jsonResponse.getString("message").toString();
				token = jsonResponse.getString("token").toString();
				if (responseCode.equals("200")){
					resp = token;
				}else{
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


}
