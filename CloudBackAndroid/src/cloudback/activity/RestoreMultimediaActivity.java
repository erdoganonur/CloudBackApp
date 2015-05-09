package cloudback.activity;

import java.io.File;

import cloudback.common.ClientOperations;
import cloudback.common.TCP.TCPException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;

public class RestoreMultimediaActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore_multimedia);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restore_multimedia, menu);
		return true;
	}

	public void doRestore(View v) throws TCPException
	{
		switch (v.getId())
		{
		case R.id.BUTTON_RESTORE_DOWNLOADS:

			MainActivity.getTCPClient().SendInt(
					ClientOperations.DOWNLOADS_RESTORE);
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());
			break;

		default:
			break;
		}
	}

	public void getFilesFromServer(String filePath) throws TCPException
	{
		boolean isContinue = true;

		File directory = Environment
				.getExternalStoragePublicDirectory(filePath);
		File dir;
		String fileName;

		while (isContinue)
		{
			isContinue = MainActivity.getTCPClient().ReceiveBoolean();

			String workingDir = "";

			if (!isContinue)
			{
				System.out.println("###### Dosyalar bitti... #####");
				break;
			}

			workingDir = directory
					+ MainActivity.getTCPClient().ReceiveString();
			// System.out.println(workingDir);
			dir = new File(workingDir);

			fileName = MainActivity.getTCPClient().ReceiveString();

			System.out.println(dir.getAbsolutePath());
			// if the directory does not exist, create it

			System.out.println(workingDir + "/" + fileName
					+ "\t file copying...");
			MainActivity.getTCPClient().ReceiveFile(
					workingDir + "/" + fileName, 2048);

		}
	}

	public class FileUploadTask extends AsyncTask<Integer, String, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(RestoreMultimediaActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog
					.setMessage("Files is copying to cloud.\nThis can take long time depend on your files' size... ");
			progressDialog.show();

		}

		@Override
		protected Boolean doInBackground(Integer... params)
		{
			switch (params[0])
			{
			case R.id.BUTTON_BACKUP_DOWNLOADS:

				try
				{
					RestoreMultimediaActivity.this
							.getFilesFromServer(Environment.DIRECTORY_DOWNLOADS);
				} catch (TCPException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			progressDialog.dismiss();
		}

	}

	private ProgressDialog progressDialog;
}
