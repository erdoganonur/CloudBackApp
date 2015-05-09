package cloudback.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cloudback.common.ClientOperations;
import cloudback.common.StatusCodes;
import cloudback.common.TCP.TCPException;

public class LoginActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void init()
	{
		m_editTextUsername = (EditText) findViewById(R.id.EDITTEXT_LOGIN_USERNAME);
		m_editTextPassword = (EditText) findViewById(R.id.EDITTEXT_LOGIN_PASSWORD);
	}

	@SuppressLint("ShowToast")
	public void login(View v)
	{
		try
		{
			String username = m_editTextUsername.getText().toString().trim();
			String password = m_editTextPassword.getText().toString().trim();

			MainActivity.getTCPClient().SendInt(ClientOperations.LOGIN);
			MainActivity.getTCPClient().SendString(username);
			MainActivity.getTCPClient().SendString(password);

			int status = MainActivity.getTCPClient().ReceiveInt();

			if (status == StatusCodes.SUCCESFUL)
			{
				FileOutputStream fos = openFileOutput("login.info",
						Context.MODE_PRIVATE);
				fos.write((username + "\n" + password).getBytes());
				fos.close();

				Toast.makeText(LoginActivity.this,
						"You have successfully logged", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);

			} else
			{
				Toast.makeText(LoginActivity.this,
						"You have not logged! Please try again", Toast.LENGTH_SHORT).show();
			}
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private EditText m_editTextUsername, m_editTextPassword;
}
