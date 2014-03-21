package com.nenok.mawn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost("http://nenok.herokuapp.com/login");
	Button buttonStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buttonStart = (Button) findViewById(R.id.button1);
		buttonStart.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", "marka@sourcepad.com"));
		    nameValuePairs.add(new BasicNameValuePair("password", "passsword"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			Toast.makeText( getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
		        
		} catch (IOException e) {
			Toast.makeText( getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		      
		} 
	}


}
