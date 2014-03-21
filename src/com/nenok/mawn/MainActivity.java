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
		new LoginService(respResult, this.dh, username, password, getApplicationContext()).execute("http://nenok.herokuapp.com/api/users");
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
