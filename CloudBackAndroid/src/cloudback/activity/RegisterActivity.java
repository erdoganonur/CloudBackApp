package cloudback.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cloudback.common.ClientOperations;
import cloudback.common.StatusCodes;
import cloudback.common.TCP.TCPException;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		init();
	}
	
	public void init()
	{
		m_editTextUsername = (EditText) findViewById(R.id.EDITTEXT_REGISTER_USERNAME);
		m_editTextPassword = (EditText) findViewById(R.id.EDITTEXT_REGISTER_PASSWORD);
		m_editTextRePassword = (EditText) findViewById(R.id.EDITTEXT_REGISTER_RETYPE_PASSWORD);
		m_editTextEmail = (EditText) findViewById(R.id.EDITTEXT_REGISTER_EMAIL);
	}
	
	public void doRegister(View v) throws TCPException
	{
		Toast.makeText(RegisterActivity.this, m_editTextPassword.getText().toString()+" || "+m_editTextRePassword.getText().toString(), Toast.LENGTH_LONG).show();
		if(m_editTextPassword.getText().toString().compareTo(m_editTextRePassword.getText().toString()) == 0)
		{
			MainActivity.getTCPClient().SendInt(ClientOperations.REGISTER);
			MainActivity.getTCPClient().SendString(m_editTextUsername.getText().toString().trim());
			MainActivity.getTCPClient().SendString(m_editTextPassword.getText().toString().trim());
			MainActivity.getTCPClient().SendString(m_editTextEmail.getText().toString().trim());
			
			int status = MainActivity.getTCPClient().ReceiveInt();
			if(status == StatusCodes.SUCCESFUL) {
				Toast.makeText(RegisterActivity.this, "Register Successfull", Toast.LENGTH_LONG).show();
				
			} else if(status == StatusCodes.FAILD)
				Toast.makeText(RegisterActivity.this, "Register FAILED", Toast.LENGTH_LONG).show();
			else if(status == StatusCodes.USER_ALREADY_EXIST)
				Toast.makeText(RegisterActivity.this, "User Already Exist", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(RegisterActivity.this, "Passwords don't match. Please Retype", Toast.LENGTH_LONG).show();
	}
	
	private EditText m_editTextUsername, m_editTextPassword, m_editTextRePassword, m_editTextEmail;  
}
