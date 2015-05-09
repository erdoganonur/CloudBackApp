package cloudback.service;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cloudback.activity.MainActivity;
import cloudback.common.ClientOperations;
import cloudback.common.TCP.TCPException;
import cloudback.entities.Sms;

public class SmsService
{
	public void sendSmsList2Server(ArrayList<Sms> smsList, String backupName)
	{

		try
		{

			MainActivity.getTCPClient().SendInt(ClientOperations.SMS_BACKUP);
			
			//Sending backup name
			MainActivity.getTCPClient().SendString(backupName);
			System.out.println(MainActivity.getUsername());
			// Sending username to server
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());
			// Sendind size of sms to server
			MainActivity.getTCPClient().SendInt(smsList.size());

			for (int i = 0; i < smsList.size(); i++)
			{

				MainActivity.getTCPClient().SendString(
						smsList.get(i).getAddress());
				MainActivity.getTCPClient().SendLong(smsList.get(i).getDate());
				MainActivity.getTCPClient().SendInt(
						smsList.get(i).getProtocol());
				MainActivity.getTCPClient().SendInt(smsList.get(i).getRead());
				MainActivity.getTCPClient().SendInt(smsList.get(i).getStatus());
				MainActivity.getTCPClient().SendInt(smsList.get(i).getType());
				MainActivity.getTCPClient()
						.SendString(smsList.get(i).getBody());

				if (smsList.get(i).getService_center() != null)
					MainActivity.getTCPClient().SendString(
							smsList.get(i).getService_center());
				else
					MainActivity.getTCPClient().SendString("0");

				MainActivity.getTCPClient().SendInt(smsList.get(i).getLocked());

				System.out.println("Sms sent " + i + "/" + smsList.size());
			}

			// int statusCode = MainActivity.getTCPClient().ReceiveInt();
			//
			// if (statusCode == StatusCodes.SUCCESFUL)
			// Toast.makeText(BackupActivity.this,
			// "Sms backup has been completed successfuly",
			// Toast.LENGTH_SHORT).show();
			// else
			// Toast.makeText(
			// BackupActivity.this,
			// "We are sory! An eror occured when sms uploading to server",
			// Toast.LENGTH_SHORT).show();
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Sms> getAllSms(Context context)
	{
		ArrayList<Sms> smsList = new ArrayList<Sms>();

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(Uri.parse("content://sms"),
				null, null, null, null);

		m_indexAddress = cursor.getColumnIndex("address");
		m_indexDate = cursor.getColumnIndex("date");
		m_indexProtcol = cursor.getColumnIndex("protocol");
		m_indexRead = cursor.getColumnIndex("read");
		m_indexStatus = cursor.getColumnIndex("status");
		m_indexType = cursor.getColumnIndex("type");
		m_indexBody = cursor.getColumnIndex("body");
		m_indexServiceCenter = cursor.getColumnIndex("service_center");
		m_indexLocked = cursor.getColumnIndex("locked");

		System.out.println("Messages collecting...");

		cursor.moveToFirst();

		if (m_indexBody < 0 || !cursor.moveToFirst())
			return null;

		do
		{
			System.out.println("Message body :"
					+ cursor.getString(m_indexServiceCenter));
			smsList.add(new Sms(cursor.getString(m_indexAddress), cursor
					.getLong(m_indexDate), cursor.getInt(m_indexProtcol),
					cursor.getInt(m_indexRead), cursor.getInt(m_indexStatus),
					cursor.getInt(m_indexType), cursor.getString(m_indexBody),
					cursor.getString(m_indexServiceCenter), cursor
							.getInt(m_indexLocked)));

		} while (cursor.moveToNext());

		return smsList;
	}

	public ArrayList<Sms> smsListFromServer(int revNum)
	{
		String address;
		long date;
		int protocol;
		int read;
		int status;
		int type;
		String body;
		String service_center;
		int locked;

		ArrayList<Sms> smsList = new ArrayList<Sms>();
		int listSize;

		try
		{
			MainActivity.getTCPClient().SendInt(ClientOperations.SMS_RESTORE);
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());
			MainActivity.getTCPClient().SendInt(revNum);

			listSize = MainActivity.getTCPClient().ReceiveInt();

			Sms sms;

			for (int i = 0; i < listSize; i++)
			{
				address = MainActivity.getTCPClient().ReceiveString();
				date = MainActivity.getTCPClient().ReceiveLong();
				protocol = MainActivity.getTCPClient().ReceiveInt();
				read = MainActivity.getTCPClient().ReceiveInt();
				status = MainActivity.getTCPClient().ReceiveInt();
				type = MainActivity.getTCPClient().ReceiveInt();
				body = MainActivity.getTCPClient().ReceiveString();
				service_center = MainActivity.getTCPClient().ReceiveString();
				locked = MainActivity.getTCPClient().ReceiveInt();

				sms = new Sms(address, date, protocol, read, status, type,
						body, service_center, locked);

				smsList.add(sms);
			}

		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return smsList;
	}

	public void addSms(Context context, ArrayList<Sms> smsList)
	{
		for (int i = 0; i < smsList.size(); i++)
		{

			ContentValues values = new ContentValues();
			values.put("address", smsList.get(i).getAddress());
			System.out.println(smsList.get(i).getAddress());
			values.put("date", smsList.get(i).getDate());
			values.put("protocol", smsList.get(i).getProtocol());
			values.put("read", smsList.get(i).getRead());
			values.put("status", smsList.get(i).getStatus());
			values.put("type", smsList.get(i).getType());
			values.put("body", smsList.get(i).getBody());
			values.put("service_center", smsList.get(i).getService_center());
			values.put("locked", smsList.get(i).getLocked());

			// Uri inserted =
			context.getContentResolver().insert(Uri.parse("content://sms"),
					values);
		}
	}

	private int m_indexAddress;
	private int m_indexDate;
	private int m_indexProtcol;
	private int m_indexRead;
	private int m_indexStatus;
	private int m_indexType;
	private int m_indexBody;
	private int m_indexServiceCenter;
	private int m_indexLocked;

}
