package cloudback.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class LoginOrRegisterActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_or_register);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_or_register, menu);
		return true;
	}

	public void doSelect(View v)
	{
		switch (v.getId()) 
		{
		
		case R.id.BUTTON_SELECT_REGISTER:
			
			Intent register = new Intent(LoginOrRegisterActivity.this, RegisterActivity.class);
			startActivity(register);
			
			break;
			
		case R.id.BUTTON_SELECT_LOGIN:
			
			Intent login = new Intent(LoginOrRegisterActivity.this, LoginActivity.class);
			startActivity(login);
			
			break;

		default:
			break;
		}
	}
	

}
