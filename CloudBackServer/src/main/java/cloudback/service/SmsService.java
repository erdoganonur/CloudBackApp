package cloudback.service;

import java.sql.SQLException;
import java.util.ArrayList;

import cloudback.DAO.BackupNameDAO;
import cloudback.DAO.SmsDAO;
import cloudback.common.BackupTypes;
import cloudback.common.StatusCodes;
import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;
import cloudback.db.DbConnection;
import cloudback.entities.Sms;

public class SmsService
{
	public SmsService(TCP clientTCP, DbConnection dbConn)
	{
		this.m_clientTCP = clientTCP;
		this.m_dbConn = dbConn;
	}

	public DbConnection getDbConn()
	{
		return m_dbConn;
	}
	
	public void setDbConn(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}
	
	public TCP getClientTCP()
	{
		return m_clientTCP;
	}

	public void setClientTCP(TCP clientTCP)
	{
		this.m_clientTCP = clientTCP;
	}

	public Sms getSms()
	{
		return m_sms;
	}

	public void setSms(Sms sms)
	{
		this.m_sms = sms;
	}
	
	public int insertBulkSms() throws SQLException, TCPException
	{
		ArrayList<Sms> smsList = new ArrayList<Sms>();
		
		//Getting backup name
		String backupName = m_clientTCP.ReceiveString();
		System.out.println("Backup Name :"+backupName);
		//Getting username from client
		String username = m_clientTCP.ReceiveString();
		System.out.println(username);
		//Getting size of sms list which will sent from client.
		int listSize = m_clientTCP.ReceiveInt();
		System.out.println("Has been gotten from client '"+username+"', size is :"+listSize);
		
		for(int i = 0; i < listSize; i++)
		{
			String address = m_clientTCP.ReceiveString();
			long date = m_clientTCP.ReceiveLong();
			int protocol = m_clientTCP.ReceiveInt();
			int read = m_clientTCP.ReceiveInt();
			int status = m_clientTCP.ReceiveInt();
			int type = m_clientTCP.ReceiveInt();
			String body = m_clientTCP.ReceiveString();
			String serviceCenter = m_clientTCP.ReceiveString();
			int locked = m_clientTCP.ReceiveInt();
			
			smsList.add(new Sms(address, date, protocol, read, status, type, body, serviceCenter, locked));
		}
		
		m_userService = new UserService(m_clientTCP, m_dbConn);
		m_smsDAO = new SmsDAO(m_dbConn);
		long userId = m_userService.registeredUserId(username);
		int lastRevisionNum = m_smsDAO.lastRevisionNum(userId);
		
		System.out.println("User Id : "+ userId+" || Last Revision Number : "+lastRevisionNum);
		int statusCode = m_smsDAO.insertSmsList(smsList, userId, (lastRevisionNum + 1));
		
		BackupNameDAO backupNameDAO = new BackupNameDAO(m_dbConn);
		backupNameDAO.insertBackupName(userId, BackupTypes.SMS, (lastRevisionNum + 1),backupName);
		
		return statusCode;
		
	}
	
	public int sendSms2Client() throws TCPException, SQLException
	{
		ArrayList<Sms> smsList = null;
		
		m_smsDAO = new SmsDAO(m_dbConn);
		
		String username = m_clientTCP.ReceiveString();
		System.out.println("Username :"+username );
		int revNumber = m_clientTCP.ReceiveInt();
		System.out.println("Revision Number : "+revNumber);
		
		m_userService = new UserService(m_clientTCP, m_dbConn);
		
		long userId = m_userService.registeredUserId(username);
		System.out.println("User ID :"+userId);
		
		smsList = m_smsDAO.selectSmsByUserRev(userId, revNumber);
		
		System.out.println(smsList.size());
		
		if(smsList.isEmpty())
			return StatusCodes.FAILD;
		
		m_clientTCP.SendInt(smsList.size());
		
		for(int i = 0; i < smsList.size(); i++)
		{
			m_clientTCP.SendString(smsList.get(i).getAddress());
			m_clientTCP.SendLong(smsList.get(i).getDate());
			m_clientTCP.SendInt(smsList.get(i).getProtocol());
			m_clientTCP.SendInt(smsList.get(i).getRead());
			m_clientTCP.SendInt(smsList.get(i).getStatus());
			m_clientTCP.SendInt(smsList.get(i).getType());
			m_clientTCP.SendString(smsList.get(i).getBody());
			m_clientTCP.SendString(smsList.get(i).getService_center());
			m_clientTCP.SendInt(smsList.get(i).getLocked());
		}
		
		return StatusCodes.SUCCESFUL;
	}
	
	private SmsDAO m_smsDAO;
	private UserService m_userService; 
	private Sms m_sms;
	private TCP m_clientTCP;
	private DbConnection m_dbConn;

}
