package cloudback.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class RestoreActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restore, menu);
		return true;
	}

	public void doOperation(View v)
	{
		Intent intent;
		
		if(v.getId() == R.id.BUTTON_RESTORE_MULTIMEDIA)
		{
			intent = new Intent(RestoreActivity.this,
				RestoreMultimediaActivity.class);
			startActivity(intent);
		}
		
		intent = new Intent(RestoreActivity.this,
				BackupListActivity.class);
		
		System.out.println("Button Id :"+v.getId());
		intent.putExtra("op", v.getId());
		startActivity(intent);
	
	}

}
