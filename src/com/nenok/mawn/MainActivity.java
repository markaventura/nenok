package com.nenok.mawn;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	Button buttonStart;
	EditText usernameFld, passwordFld;
	String username, password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		usernameFld = (EditText) findViewById(R.id.uname);
		
		
		passwordFld = (EditText) findViewById(R.id.passw);
		
		buttonStart = (Button) findViewById(R.id.login);
		buttonStart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		username = usernameFld.getText().toString();
		password = passwordFld.getText().toString();

		new LoginService(username, password).execute("http://nenok.herokuapp.com/api/users");
	}


}
