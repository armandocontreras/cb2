package com.br.cb2;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mUserView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	public Context co;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		co = this;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);
		mUserView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		/*mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});*/

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}
	protected void attemptRegister() {
		ParseUser user = new ParseUser();
		user.setUsername(mUserView.getText().toString());
		user.setPassword(mPasswordView.getText().toString());
		user.setEmail(mEmailView.getText().toString());
		 
		// other fields can be set just like with ParseObject
		//user.put("phone", "650-253-0000");
		 
		user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(com.parse.ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						Intent intent5 = new Intent(co, LoginActivity.class); 
						startActivity(intent5);
					} else {
					      Log.e("ParseException", e.getMessage());
					}	
				}
			});
	}
}
