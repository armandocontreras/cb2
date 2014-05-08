package com.br.cb2;


import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class UserPasswordActivity extends Activity {

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mPasswordView;
	private TextView userName;
	private String sUserName;
	private TextView mLoginStatusMessageView;
	public Context co;
	public ParseUser currentUser;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		co = this;
		currentUser = ParseUser.getCurrentUser();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_user_password);

		mPasswordView = (EditText) findViewById(R.id.password);
		userName = (TextView) findViewById(R.id.username);
		sUserName = currentUser.getUsername();
		//sUserName = "Set your password";
		userName.setText(sUserName);
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 * @throws ParseException 
	 */
	public void attemptLogin() {

		// Reset errors.
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		currentUser.setPassword(mPassword); // attempt to change username
		try {
			currentUser.save();
			Intent intent5 = new Intent(co, RecipeListActivity.class); 
			startActivity(intent5);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			 Toast.makeText(co, "There was an error saving the password, please try again", Toast.LENGTH_SHORT).show();
 		    
		}
	}
	
}
