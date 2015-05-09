package cloudback.activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;

@SuppressLint("NewApi")
public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Intent intent = null;
		FileInputStream fis = null;

		if (this.connectServer())
		{
			Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG)
					.show();

			try
			{
				fis = openFileInput("login.info");

				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis, "UTF8"));

				String line;
				if ((line = br.readLine()) != null)
				{
					setUsername(line);
					// if((line = br.readLine()) != null)

				}

				Toast.makeText(MainActivity.this,
						"Login user : " + getUsername(), Toast.LENGTH_LONG)
						.show();

			} catch (FileNotFoundException e)
			{
				intent = new Intent(MainActivity.this,
						LoginOrRegisterActivity.class);
				startActivity(intent);
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else
		{
			Toast.makeText(MainActivity.this,
					"An error occured when connecting server",
					Toast.LENGTH_LONG).show();

			finish();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void selectOperation(View v)
	{
		Intent intent = null;

		switch (v.getId())
		{
		case R.id.BUTTON_SELECT_BACKUP:
			intent = new Intent(MainActivity.this, BackupActivity.class);
			startActivity(intent);
			break;

		case R.id.BUTTON_SELECT_RESTORE:
			Toast.makeText(MainActivity.this,
					"Hayaldi gercek oldu ;)",
					Toast.LENGTH_SHORT).show();

			intent = new Intent(MainActivity.this, RestoreActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	public boolean connectServer()
	{
		try
		{
			TCPClient = new TCP(TCP.SocketType.CLIENT, "192.168.2.32", 7070);
			TCPClient.Connect();

			return true;
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static TCP getTCPClient()
	{
		return TCPClient;
	}

	public static void setTCPClient(TCP tCPClient)
	{
		TCPClient = tCPClient;
	}

	public static String getUsername()
	{
		return username;
	}

	public static void setUsername(String username)
	{
		MainActivity.username = username;
	}

	private static String username;
	private static TCP TCPClient;

}
