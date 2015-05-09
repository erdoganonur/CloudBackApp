package cloudback.service;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import cloudback.activity.MainActivity;
import cloudback.common.ClientOperations;
import cloudback.common.TCP.TCPException;
import cloudback.entities.Contact;

public class ContactService
{

	public void contactList2Server(ArrayList<Contact> contactList, String backupName)
	{
		String displayName = "";
		String nickName = "";
		String homePhone = "";
		String mobilePhone = "";
		String workPhone = "";
		String homeEmail = "";
		String workEmail = "";
		String companyName = "";
		String title = "";

		try
		{
			MainActivity.getTCPClient()
					.SendInt(ClientOperations.CONTACT_BACKUP);

			MainActivity.getTCPClient().SendString(backupName);
			System.out.println(MainActivity.getUsername());
			// Sending user name to server
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());
			// Sending size of sms to server
			MainActivity.getTCPClient().SendInt(contactList.size());

			for (int i = 0; i < contactList.size(); i++)
			{

				displayName = contactList.get(i).getDisplayName();
				nickName = contactList.get(i).getNickName();
				homePhone = contactList.get(i).getHomePhone();
				mobilePhone = contactList.get(i).getMobilePhone();
				workPhone = contactList.get(i).getWorkPhone();
				homeEmail = contactList.get(i).getHomeEmail();
				workEmail = contactList.get(i).getWorkEmail();
				companyName = contactList.get(i).getCompanyName();
				title = contactList.get(i).getTitle();

				String[] contactAtt = { displayName, nickName, homePhone,
						mobilePhone, workPhone, homeEmail, workEmail,
						companyName, title };

				for (int j = 0; j < contactAtt.length; j++)
				{
					if (contactAtt[j] == null)
					{
						System.out.print("String from " + contactAtt[j]);
						contactAtt[j] = "";
						System.out.println(" to " + contactAtt[j]);
					}
				}

				MainActivity.getTCPClient().SendString(contactAtt[0]);
				MainActivity.getTCPClient().SendString(contactAtt[1]);
				MainActivity.getTCPClient().SendString(contactAtt[2]);
				MainActivity.getTCPClient().SendString(contactAtt[3]);
				MainActivity.getTCPClient().SendString(contactAtt[4]);
				MainActivity.getTCPClient().SendString(contactAtt[5]);
				MainActivity.getTCPClient().SendString(contactAtt[6]);
				MainActivity.getTCPClient().SendString(contactAtt[7]);
				MainActivity.getTCPClient().SendString(contactAtt[8]);

				System.out.println("Contact sending ... " + i + "/"
						+ contactList.size());
			}

		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<Contact> contactListFromServer(int revNum)
	{

		String displayName = "";
		String nickName = "";
		String homePhone = "";
		String mobilePhone = "";
		String workPhone = "";
		String homeEmail = "";
		String workEmail = "";
		String companyName = "";
		String title = "";

		int listSize;

		ArrayList<Contact> contactList = new ArrayList<Contact>();

		try
		{
			MainActivity.getTCPClient().SendInt(
					ClientOperations.CONTACT_RESTORE);
			MainActivity.getTCPClient().SendString(MainActivity.getUsername());
			MainActivity.getTCPClient().SendInt(revNum);

			listSize = MainActivity.getTCPClient().ReceiveInt();

			Contact contact;
			for (int i = 0; i < listSize; i++)
			{

				displayName = MainActivity.getTCPClient().ReceiveString();
				nickName = MainActivity.getTCPClient().ReceiveString();
				homePhone = MainActivity.getTCPClient().ReceiveString();
				mobilePhone = MainActivity.getTCPClient().ReceiveString();
				workPhone = MainActivity.getTCPClient().ReceiveString();
				homeEmail = MainActivity.getTCPClient().ReceiveString();
				workEmail = MainActivity.getTCPClient().ReceiveString();
				companyName = MainActivity.getTCPClient().ReceiveString();
				title = MainActivity.getTCPClient().ReceiveString();
				

				contact = new Contact(displayName, nickName, homePhone,
						mobilePhone, workPhone, homeEmail, workEmail,
						companyName, title);
				
				contactList.add(contact);
			}
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contactList;
	}

	public void addContact2(Context context, ArrayList<Contact> contactList)
	{
		ArrayList<ContentProviderOperation> opList = null;

		for (int i = 0; i < contactList.size(); i++)
		{
			System.out.println(contactList.get(i).toString());
			opList = new ArrayList<ContentProviderOperation>();
			
			System.out.println("Number of contact : "+opList.size());

			int rawId = opList.size();
			opList.add(ContentProviderOperation
					.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
					// .withValue(RawContacts.AGGREGATION_MODE,
					// RawContacts.AGGREGATION_MODE_DEFAULT)
					.build());

			// first and last names
			opList.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME,
							contactList.get(i).getDisplayName()).build());

				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
						.withValue(Nickname.NAME, contactList.get(i).getNickName())
						.build());

				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
										contactList.get(i).getHomePhone())
										.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
												Phone.TYPE_HOME).build());

				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
										contactList.get(i).getMobilePhone())
										.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
												Phone.TYPE_MOBILE).build());
	
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
										contactList.get(i).getWorkPhone())
										.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
												Phone.TYPE_WORK).build());

				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Email.DATA,
										contactList.get(i).getHomeEmail())
										.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
												Email.TYPE_HOME).build());

				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Email.DATA,
										contactList.get(i).getWorkEmail())
										.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
												Email.TYPE_WORK).build());
			
				
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
								.withValue(
										ContactsContract.CommonDataKinds.Organization.DATA,
										contactList.get(i).getCompanyName())
										.withValue(
												ContactsContract.CommonDataKinds.Organization.TYPE,
												Organization.COMPANY).build());
			
			
						
				opList.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, rawId)
						
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
								.withValue(
										ContactsContract.CommonDataKinds.Organization.DATA,
										contactList.get(i).getTitle())
										.withValue(
												ContactsContract.CommonDataKinds.Organization.TYPE,
												Organization.TITLE).build());
			



			try
			{
				//ContentProviderResult[] results = 
				context.getContentResolver()
				.applyBatch(ContactsContract.AUTHORITY, opList);
				
				System.out.println("Saved successfully");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void addContacts(Context context, ArrayList<Contact> contactList)
	{

		ArrayList<ContentProviderOperation> ops = null;

		for (int i = 0; i < contactList.size(); i++)
		{

			ops = new ArrayList<ContentProviderOperation>();

			int rawContactID = ops.size();

			// Adding insert operation to operations list
			// to insert a new raw contact in the table
			// ContactsContract.RawContacts
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
					.withValue(RawContacts.ACCOUNT_NAME, null).build());

			// Adding insert operation to operations list
			// to insert display name in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(ContactsContract.Data.MIMETYPE,
							StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME,
							contactList.get(i).getDisplayName()).build());

			// Adding insert operation to operations list
			// to insert nickname in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(ContactsContract.Data.MIMETYPE,
							Nickname.CONTENT_ITEM_TYPE)
					.withValue(Nickname.NAME, contactList.get(i).getNickName())
					.build());

			// Adding insert operation to operations list
			// to insert Mobile Number in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(ContactsContract.Data.MIMETYPE,
							Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER,
							contactList.get(i).getMobilePhone())
					.withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
					.build());

			// Adding insert operation to operations list
			// to insert Home Phone Number in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(ContactsContract.Data.MIMETYPE,
							Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, contactList.get(i).getHomePhone())
					.withValue(Phone.TYPE, Phone.TYPE_HOME).build());

			// Adding insert operation to operations list
			// to insert Home Phone Number in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(ContactsContract.Data.MIMETYPE,
							Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, contactList.get(i).getWorkPhone())
					.withValue(Phone.TYPE, Phone.TYPE_WORK).build());

			// Adding insert operation to operations list
			// to insert Home Email in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							contactList.get(i).getWorkEmail())
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							Email.TYPE_HOME).build());

			// Adding insert operation to operations list
			// to insert Work Email in the table ContactsContract.Data
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							contactList.get(i).getWorkEmail())
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							Email.TYPE_WORK).build());

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.DATA,
							contactList.get(i).getCompanyName())
					.withValue(
							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
							Organization.COMPANY).build());

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.DATA,
							contactList.get(i).getTitle())
					.withValue(
							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
							Organization.TITLE).build());
		}
		try
		{
			// Executing all the insert operations as a single database
			// transaction
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
			// Toast.makeText(.getBaseContext(),
			// "Contact is successfully added", Toast.LENGTH_SHORT).show();
		} catch (RemoteException e)
		{
			e.printStackTrace();
		} catch (OperationApplicationException e)
		{
			e.printStackTrace();
		}

	}

	public ArrayList<Contact> getAllContacts(Context context)
	{
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

		// Querying the table ContactsContract.Contacts to retrieve all the
		// contacts
		Cursor contactsCursor = context.getContentResolver().query(contactsUri,
				null, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

		if (contactsCursor.moveToFirst())
		{
			do
			{
				long contactId = contactsCursor.getLong(contactsCursor
						.getColumnIndex("_ID"));

				Uri dataUri = ContactsContract.Data.CONTENT_URI;

				// Querying the table ContactsContract.Data to retrieve
				// individual items like
				// home phone, mobile phone, work email etc corresponding to
				// each contact
				Cursor dataCursor = context.getContentResolver().query(dataUri,
						null,
						ContactsContract.Data.CONTACT_ID + "=" + contactId,
						null, null);

				String displayName = "";
				String nickName = "";
				String homePhone = "";
				String mobilePhone = "";
				String workPhone = "";
				String homeEmail = "";
				String workEmail = "";
				String companyName = "";
				String title = "";

				if (dataCursor.moveToFirst())
				{
					// Getting Display Name
					displayName = dataCursor
							.getString(dataCursor
									.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
					do
					{

						// Getting NickName
						if (dataCursor
								.getString(
										dataCursor.getColumnIndex("mimetype"))
								.equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
							nickName = dataCursor.getString(dataCursor
									.getColumnIndex("data1"));

						// Getting Phone numbers
						if (dataCursor
								.getString(
										dataCursor.getColumnIndex("mimetype"))
								.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
						{
							switch (dataCursor.getInt(dataCursor
									.getColumnIndex("data2")))
							{
							case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
								homePhone = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								break;
							case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
								mobilePhone = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								break;
							case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
								workPhone = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								break;
							}
						}

						// Getting EMails
						if (dataCursor
								.getString(
										dataCursor.getColumnIndex("mimetype"))
								.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))
						{
							switch (dataCursor.getInt(dataCursor
									.getColumnIndex("data2")))
							{
							case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
								homeEmail = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								break;
							case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
								workEmail = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								break;
							}
						}

						// Getting Organization details
						if (dataCursor
								.getString(
										dataCursor.getColumnIndex("mimetype"))
								.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE))
						{
							companyName = dataCursor.getString(dataCursor
									.getColumnIndex("data1"));
							title = dataCursor.getString(dataCursor
									.getColumnIndex("data4"));
						}

					} while (dataCursor.moveToNext());
				}

				Contact contact = new Contact(displayName, nickName, homePhone,
						mobilePhone, workPhone, homeEmail, workEmail,
						companyName, title);
				System.out.println(contact.toString());
				contactList.add(contact);
			} while (contactsCursor.moveToNext());
		}
		return contactList;
	}
}
