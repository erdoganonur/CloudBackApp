package cloudback.service;

import java.sql.SQLException;
import java.util.ArrayList;

import cloudback.DAO.BackupNameDAO;
import cloudback.DAO.ContactDAO;
import cloudback.common.BackupTypes;
import cloudback.common.StatusCodes;
import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;
import cloudback.db.DbConnection;
import cloudback.entities.Contact;

public class ContactService
{
	
	public ContactService(TCP clientTCP, DbConnection dbConn)
	{
		this.m_clientTCP = clientTCP;
		this.m_dbConn = dbConn;
	}
	public ContactDAO getContactDAO()
	{
		return m_contactDAO;
	}
	public void setContactDAO(ContactDAO contactDAO)
	{
		this.m_contactDAO = contactDAO;
	}
	public Contact getContact()
	{
		return m_contact;
	}
	public void setContact(Contact contact)
	{
		this.m_contact = contact;
	}
	public TCP getClientTCP()
	{
		return m_clientTCP;
	}
	public void setClientTCP(TCP clientTCP)
	{
		this.m_clientTCP = clientTCP;
	}
	public DbConnection getDbConn()
	{
		return m_dbConn;
	}
	public void setDbConn(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}
	
	public int insertBulkContact() throws TCPException, SQLException
	{

		ArrayList<Contact> contactList = new ArrayList<Contact>();
		
		//Getting backup name 
		String backupName = m_clientTCP.ReceiveString();
		//Getting username from client
		String username = m_clientTCP.ReceiveString();
		System.out.println(username);
		//Getting size of sms list which will sent from client.
		int listSize = m_clientTCP.ReceiveInt();
		
		for(int i = 0; i < listSize; i++)
		{
			String displayName = m_clientTCP.ReceiveString();
			String nickName = m_clientTCP.ReceiveString();
			String homePhone = m_clientTCP.ReceiveString();
			String mobilePhone = m_clientTCP.ReceiveString();
			String workPhone = m_clientTCP.ReceiveString();
			String homeEmail = m_clientTCP.ReceiveString();
			String workEmail = m_clientTCP.ReceiveString();
			String companyName = m_clientTCP.ReceiveString();
			String title = m_clientTCP.ReceiveString();
			
			contactList.add(new Contact(displayName, nickName, homePhone, mobilePhone, workPhone, homeEmail, workEmail, companyName, title));
		}
		
		m_userService = new UserService(m_clientTCP, m_dbConn);
		m_contactDAO = new ContactDAO(m_dbConn);
		long userId = m_userService.registeredUserId(username);
		int lastRevisionNum = m_contactDAO.lastRevisionNum(userId) + 1;
		
		m_contactDAO.insertContactList(contactList, userId, lastRevisionNum);
		BackupNameDAO backupNameDAO = new BackupNameDAO(m_dbConn);
		backupNameDAO.insertBackupName(userId, BackupTypes.CONTACTS, (lastRevisionNum+1), backupName);
		return lastRevisionNum;
	}
	
	public int sendContacts2Client() throws TCPException, SQLException
	{
		ArrayList<Contact> contactList = null;
		
		m_contactDAO = new ContactDAO(m_dbConn);
		
		String username = m_clientTCP.ReceiveString();
		System.out.println("username : "+username);
		int revNumber = m_clientTCP.ReceiveInt();
		System.out.println("Revision Number : "+revNumber );
		
		m_userService = new UserService(m_clientTCP, m_dbConn);
		
		long userId = m_userService.registeredUserId(username);
		
		System.out.println("User ID :"+userId);
		contactList = m_contactDAO.selectContactByUserRev(userId, revNumber);
		
		System.out.println(contactList.size());
		
		if(contactList.isEmpty())
			return StatusCodes.FAILD;
		
		m_clientTCP.SendInt(contactList.size());
		
		for(int i = 0; i < contactList.size(); i++)
		{
			m_clientTCP.SendString(contactList.get(i).getDisplayName());
			m_clientTCP.SendString(contactList.get(i).getNickName());
			m_clientTCP.SendString(contactList.get(i).getHomePhone());
			m_clientTCP.SendString(contactList.get(i).getMobilePhone());
			m_clientTCP.SendString(contactList.get(i).getWorkPhone());
			m_clientTCP.SendString(contactList.get(i).getHomeEmail());
			m_clientTCP.SendString(contactList.get(i).getWorkEmail());
			m_clientTCP.SendString(contactList.get(i).getCompanyName());
			m_clientTCP.SendString(contactList.get(i).getTitle());
		}
		
		return StatusCodes.SUCCESFUL;
		
	}
	private UserService m_userService;
	private ContactDAO m_contactDAO;
	private Contact m_contact;
	private TCP m_clientTCP;
	private DbConnection m_dbConn;
}
