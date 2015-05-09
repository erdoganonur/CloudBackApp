package cloudback.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cloudback.entities.Contact;
import cloudback.entities.Sms;
import cloudback.service.ContactService;
import cloudback.service.SmsService;

public class BackupActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup, menu);
		return true;
	}

	public void backup(View v)
	{
		switch (v.getId())
		{
		case R.id.BUTTON_BACKUP_SMS:

			backupName = "";
			/* Alert Dialog Code Start */
			AlertDialog.Builder alert = new AlertDialog.Builder(
					BackupActivity.this);
			alert.setTitle("SMS Backup Name"); // Set Alert dialog title here
			alert.setMessage("Enter Your Backup Name"); // Message here

			// Set an EditText view to get user input
			final EditText input = new EditText(BackupActivity.this);
			alert.setView(input);

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// You will get as string input data in this variable.
					// here we convert the input to a string and show in a
					// toast.
					backupName = input.getEditableText().toString();
					// Toast.makeText(BackupActivity.this, backupName,
					// Toast.LENGTH_LONG).show();

					new SmsUploadTask().execute(backupName);
					// LongTask.this.doInBackground();

				} // End of onClick(DialogInterface dialog, int whichButton)
			}); // End of alert.setPositiveButton

			AlertDialog alertDialog = alert.create();
			alertDialog.show();
			/* Alert Dialog Code End */

			break;

		case R.id.BUTTON_BACKUP_CONTACTS:

			backupName = "";
			/* Alert Dialog Code Start */
			AlertDialog.Builder alertContacts = new AlertDialog.Builder(
					BackupActivity.this);
			alertContacts.setTitle("CONTACTS Backup Name"); // Set Alert dialog title
														// here
			alertContacts.setMessage("Enter Your Backup Name"); // Message here

			// Set an EditText view to get user input
			final EditText inputContacts = new EditText(BackupActivity.this);
			alertContacts.setView(inputContacts);

			alertContacts.setPositiveButton("OK",
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,
								int whichButton)
						{
							// You will get as string input data in this
							// variable.
							// here we convert the input to a string and show in
							// a
							// toast.
							backupName = inputContacts.getEditableText()
									.toString();
							// Toast.makeText(BackupActivity.this, backupName,
							// Toast.LENGTH_LONG).show();

							new ContactsUploadTask().execute(backupName);
							// LongTask.this.doInBackground();

						} // End of onClick(DialogInterface dialog, int
							// whichButton)
					}); // End of alert.setPositiveButton

			AlertDialog alertDialogContacts = alertContacts.create();
			alertDialogContacts.show();
			/* Alert Dialog Code End */

			break;

		case R.id.BUTTON_BACKUP_MULTIMEDIA:

			Intent intent = new Intent(BackupActivity.this,
					BackupMultimediaActivity.class);
			startActivity(intent);

			break;
		default:
			break;
		}

	}

	public String backupDialog(String title)
	{
		backupName = "";
		/* Alert Dialog Code Start */
		AlertDialog.Builder alert = new AlertDialog.Builder(BackupActivity.this);
		alert.setTitle(title); // Set Alert dialog title here
		alert.setMessage("Enter Your Backup Name"); // Message here

		// Set an EditText view to get user input
		final EditText input = new EditText(BackupActivity.this);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// You will get as string input data in this variable.
				// here we convert the input to a string and show in a toast.
				backupName = input.getEditableText().toString();
				Toast.makeText(BackupActivity.this, backupName,
						Toast.LENGTH_LONG).show();

			} // End of onClick(DialogInterface dialog, int whichButton)
		}); // End of alert.setPositiveButton

		AlertDialog alertDialog = alert.create();
		alertDialog.show();
		/* Alert Dialog Code End */

		return backupName;
	}

	public class SmsUploadTask extends AsyncTask<String, Integer, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(BackupActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("SMS is uploading to cloud ");
			progressDialog.show();

		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			// TODO Auto-generated method stub
			// Toast.makeText(BackupActivity.this, "Sms collecting...",
			// Toast.LENGTH_SHORT).show();
			String backupName = params[0];
			
			SmsService smsService = new SmsService();
			ArrayList<Sms> smsList = smsService.getAllSms(BackupActivity.this);
			smsService.sendSmsList2Server(smsList, backupName);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			progressDialog.dismiss();
		}

	}

	public class ContactsUploadTask extends AsyncTask<String, Integer, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog(BackupActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("SMS is uploading to cloud ");
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			// Toast.makeText(BackupActivity.this, "Yoksaaa contact......",
			// Toast.LENGTH_SHORT).show();
			String backupName = params[0];
			
			ContactService contactService = new ContactService();
			ArrayList<Contact> contactList = contactService
					.getAllContacts(BackupActivity.this);
			contactService.contactList2Server(contactList, backupName);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			progressDialog.dismiss();
		}
	}

	private ProgressDialog progressDialog;
	private String backupName;
}
