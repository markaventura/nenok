//package com.nenok.mawn;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.nenok.db.DBHelper;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//public class LoginService extends AsyncTask<String, Void, Void> {
//	HttpClient httpclient = new DefaultHttpClient();
//	HttpPost httppost = new HttpPost("http://nenok.herokuapp.com/api/session");
//	String username = "";
//	String password = "";
//	
//	Context context;
//	
//	private DBHelper dh;
//	
//	public LoginService(String username, String password, Context context) {
//		this.username = username;
//		this.password = password;
//		this.context = context;
//	}
//
//	@Override
//	protected Void doInBackground(String... arg0) {
//		try {
//			this.dh = new DBHelper(context);
//			
//			Log.v("email", this.username);
//			Log.v("password", this.password);
//		
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//			nameValuePairs.add(new BasicNameValuePair("email", this.username));
//		    nameValuePairs.add(new BasicNameValuePair("password", this.password));
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			HttpResponse response = httpclient.execute(httppost);
//			
//			String responseBody = EntityUtils.toString(response.getEntity());
//			JSONObject jsonResponse=new JSONObject(responseBody);
//			Log.v("Response:", response.getStatusLine().toString());
//			Log.v("body:", responseBody);
//			Log.v("token:", jsonResponse.getString("token").toString());
//		} catch (IOException e) {
//			Log.v("Error:", e.toString());      
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		return null;
//	}
//
//}
