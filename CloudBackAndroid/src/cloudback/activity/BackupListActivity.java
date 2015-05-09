package cloudback.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cloudback.common.ClientOperations;
import cloudback.common.TCP.TCPException;
import cloudback.entities.Contact;
import cloudback.entities.Sms;
import cloudback.service.ContactService;
import cloudback.service.SmsService;
import cloudback.util.Backup;
import cloudback.util.BackupListArrayAdapter;

public class BackupListActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup_list);

		Intent intent = getIntent();
		op = intent.getIntExtra("op", 0);
		System.out.println("Operation :"+op);
		switch (op)
		{
		case R.id.BUTTON_RESTORE_SMS:
			try
			{
				System.out.println("Retore Service");
				MainActivity.getTCPClient().SendInt(ClientOperations.SMS_BACKUP_LIST);
				backupList = this.getBackupListFromServer();
				
			} catch (TCPException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.BUTTON_RESTORE_CONTACT:
			System.out.println("Contact Service");
			
			try
			{
				MainActivity.getTCPClient().SendInt(ClientOperations.CONTACT_BACKUP_LIST);
				backupList = this.getBackupListFromServer();
			} catch (TCPException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			break;
		default:
			break;
		}
		

		BackupListArrayAdapter adapter = new BackupListArrayAdapter(this,
				R.layout.rowlayout, backupList);
		ListView list = (ListView) findViewById(R.id.LIST_VIEW_BACKUP);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				switch (op)
				{
				case R.id.BUTTON_RESTORE_SMS:
					System.out.println("SMS RESTORE");
					SmsService smsService = new SmsService();
					ArrayList<Sms> smsList = smsService.smsListFromServer(1);
					smsService.addSms(BackupListActivity.this, smsList);
					break;
				case R.id.BUTTON_RESTORE_CONTACT:
					System.out.println("CONTACT RESTORE");
					ContactService contactService = new ContactService();
					ArrayList<Contact> contactList = contactService
							.contactListFromServer(0);
					contactService.addContact2(BackupListActivity.this, contactList);
					break;
				default:
					break;
				}
			}
		});

	}

	public ArrayList<Backup> getBackupListFromServer() throws TCPException
	{
		MainActivity.getTCPClient().SendString(MainActivity.getUsername());
		int listSize = MainActivity.getTCPClient().ReceiveInt();
		
		ArrayList<Backup> backupList = new ArrayList<Backup>();
		
		System.out.println(listSize);
		for(int i = 0; i < listSize; i++)
		{
			String backupName = MainActivity.getTCPClient().ReceiveString();
			System.out.println(backupName);
			long userId = MainActivity.getTCPClient().ReceiveLong();
			int revNum = MainActivity.getTCPClient().ReceiveInt();
			int backupType = MainActivity.getTCPClient().ReceiveInt();
			
			backupList.add(new Backup(backupName, revNum, userId, backupType));
		}
		
		return backupList;
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup_list, menu);
		return true;
	}

	int op;
	ArrayList<Backup> backupList;
}
