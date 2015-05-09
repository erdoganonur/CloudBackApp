package cloudback.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import cloudback.common.ClientOperations;
import cloudback.common.TCP.TCPException;

public class BackupMultimediaActivity extends Activity
{

	Uri currImageURI;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup_multimedia);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup_multimedia, menu);
		return true;
	}

	@SuppressLint("NewApi")
	public void doBackup(View v)
	{
		this.backupNameDialog(v.getId());
	}

	public void sendFiles2Server(String filePath, int clientOperation)
	{
		directory = Environment.getExternalStoragePublicDirectory(filePath);

		try
		{
			// Sending type of multi media file to server
			MainActivity.getTCPClient().SendInt(clientOperation);

			// Sending user name of current user who saved on this device.
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());

			copyFile(directory);

			MainActivity.getTCPClient().SendBoolean(false);
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyFile(File workDir) throws TCPException
	{

		if (workDir.isDirectory())
		{
			File[] fileList = workDir.listFiles();

			for (int i = 0; i < fileList.length; i++)
			{
				if (fileList[i].isDirectory())
				{
					System.out.println(fileList[i].getAbsolutePath()
							+ " is directory");

					File dir = new File(fileList[i].getAbsolutePath());
					copyFile(dir);

				} else if (fileList[i].isFile())
				{
					// Sending a flag to server to say file process continue. We
					// don't have any about when it will complete because it
					// runs recursively.
					MainActivity.getTCPClient().SendBoolean(true);
					System.out
							.println("The file path is : "
									+ fileList[i].getParent()
									+ "\n File is copying...");
					System.out.println(workDir.getPath());

					// Sending file's parent path
					MainActivity.getTCPClient().SendString(
							fileList[i].getParent().replaceFirst(
									directory.getPath(), ""));
					// Sending file name
					MainActivity.getTCPClient().SendString(
							fileList[i].getName());

					// Sending file
					MainActivity.getTCPClient().SendFile(fileList[i].getPath(),
							2048);
				}
			}
		}

	}

	public void backupNameDialog(final int buttonId)
	{
		backupName = "";
		
		/* Alert Dialog Code Start */
		AlertDialog.Builder alertContacts = new AlertDialog.Builder(
				BackupMultimediaActivity.this);
		alertContacts.setTitle("CONTACTS Backup Name"); // Set Alert dialog
														// title here
		alertContacts.setMessage("Enter Your Backup Name"); // Message here

		// Set an EditText view to get user input
		final EditText inputBackupName = new EditText(
				BackupMultimediaActivity.this);
		alertContacts.setView(inputBackupName);

		alertContacts.setPositiveButton("OK",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// You will get as string input data in this
						// variable.
						// here we convert the input to a string and show in
						// a toast.
						backupName = inputBackupName.getEditableText()
								.toString();
						
						new FileUploadTask().execute(buttonId);

					} // End of onClick(DialogInterface dialog, int
						// whichButton)
				}); // End of alert.setPositiveButton

		AlertDialog alertDialogContacts = alertContacts.create();
		alertDialogContacts.show();
		/* Alert Dialog Code End */
	}

	public class FileUploadTask extends AsyncTask<Integer, String, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(BackupMultimediaActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Files is copying to cloud.\nThis can take long time depend on your files' size... ");
			progressDialog.show();

		}
		@Override
		protected Boolean doInBackground(Integer... params)
		{
			switch (params[0])
			{
			case R.id.BUTTON_BACKUP_DCIM:

				BackupMultimediaActivity.this.sendFiles2Server(
						Environment.DIRECTORY_DCIM,
						ClientOperations.DCIM_BACKUP);

				break;
			case R.id.BUTTON_BACKUP_PICTURES:

				BackupMultimediaActivity.this.sendFiles2Server(
						Environment.DIRECTORY_PICTURES,
						ClientOperations.PICTURES_BACKUP);
				break;

			case R.id.BUTTON_BACKUP_MUSIC:

				BackupMultimediaActivity.this.sendFiles2Server(
						Environment.DIRECTORY_MUSIC,
						ClientOperations.MUSIC_BACKUP);
				break;

			case R.id.BUTTON_BACKUP_MOVIES:

				BackupMultimediaActivity.this.sendFiles2Server(
						Environment.DIRECTORY_MOVIES,
						ClientOperations.MOVIES_BACKUP);
				break;
			case R.id.BUTTON_BACKUP_DOWNLOADS:

				BackupMultimediaActivity.this.sendFiles2Server(
						Environment.DIRECTORY_DOWNLOADS,
						ClientOperations.DOWNLOADS_BACKUP);

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
	@SuppressWarnings("unused")
	private String backupName;
	private File directory;

}
