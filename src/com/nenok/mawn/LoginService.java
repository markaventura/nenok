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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoginService extends AsyncTask<String, Void, Void> {
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost("http://nenok.herokuapp.com/api/session");

	@Override
	protected Void doInBackground(String... arg0) {
		try {
			Log.v("Background:", "in-progress");
		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", "marka@sourcepad.com"));
		    nameValuePairs.add(new BasicNameValuePair("password", "password"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			Log.v("Response:", response.getStatusLine().toString());
		} catch (IOException e) {
			Log.v("Error:", e.toString());      
		} 
		return null;
	}

}
