package com.cloudback.server;

import java.sql.SQLException;

import com.cloudback.common.BackupTypes;
import com.cloudback.common.ClientOperations;
import com.cloudback.common.TCP;
import com.cloudback.db.DbConnection;
import com.cloudback.service.BackupFileService;
import com.cloudback.service.BackupNameService;
import com.cloudback.service.ContactService;
import com.cloudback.service.SmsService;
import com.cloudback.service.UserService;

public class ServerOperationThread extends Thread
{

	public ServerOperationThread(String threadName, TCP clientTCP,
			DbConnection dbConn)
	{

		super(threadName);
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

	public void setClientTCP(TCP m_clientTCP)
	{
		this.m_clientTCP = m_clientTCP;
	}

	public String getThreadName()
	{
		return threadName;
	}

	public void setThreadName(String threadName)
	{
		this.threadName = threadName;
	}

	public void doOperation(int operation, TCP clientTCP) throws TCP.TCPException,
			SQLException
	{

		int statusCode = 0;

		switch (operation)
		{
		case ClientOperations.REGISTER:

			userService = new UserService(m_clientTCP, m_dbConn);
			statusCode = userService.registerUser();
			m_clientTCP.SendInt(statusCode);
			m_lastOperationTime = System.currentTimeMillis();
			break;

		case ClientOperations.LOGIN:

			userService = new UserService(m_clientTCP, m_dbConn);
			System.out.println("A Login process has began...");
			statusCode = userService.userLogin();
			m_clientTCP.SendInt(statusCode);
			m_lastOperationTime = System.currentTimeMillis();
			break;

		case ClientOperations.SMS_BACKUP:

			System.out.println("A SMS Backup process has began...");
			smsService = new SmsService(m_clientTCP, m_dbConn);
			smsService.insertBulkSms();
			m_lastOperationTime = System.currentTimeMillis();
			break;

		case ClientOperations.SMS_BACKUP_LIST:
			System.out.println("getting sms backup list");
			backupNameService = new BackupNameService(m_dbConn, m_clientTCP);
			backupNameService.sendBackupNames2Server(BackupTypes.SMS);
			m_lastOperationTime = System.currentTimeMillis();
			break;
		case ClientOperations.SMS_RESTORE:
			System.out.println("Sms restore ....");
			smsService = new SmsService(m_clientTCP, m_dbConn);
			smsService.sendSms2Client();
			m_lastOperationTime = System.currentTimeMillis();
			break;

		case ClientOperations.CONTACT_BACKUP:

			System.out.println("A contact process has began...");
			contactService = new ContactService(m_clientTCP, m_dbConn);
			contactService.insertBulkContact();
			m_lastOperationTime = System.currentTimeMillis();
			break;
		
		case ClientOperations.CONTACT_BACKUP_LIST:
			backupNameService = new BackupNameService(m_dbConn, m_clientTCP);
			backupNameService.sendBackupNames2Server(BackupTypes.CONTACTS);
			m_lastOperationTime = System.currentTimeMillis();
			
		case ClientOperations.CONTACT_RESTORE:

			System.out.println("Contacts sending to device ");
			contactService = new ContactService(m_clientTCP, m_dbConn);
			contactService.sendContacts2Client();
			m_lastOperationTime = System.currentTimeMillis();

		case ClientOperations.DCIM_BACKUP:

			System.out.println("DCIM backup is started...");
			BackupFileService backupFileServiceDCIM = new BackupFileService(
					m_clientTCP, m_dbConn);
			backupFileServiceDCIM.getFilesFromClient(BackupTypes.DCIM);
			m_lastOperationTime = System.currentTimeMillis();

			break;
		case ClientOperations.PICTURES_BACKUP:

			System.out.println("PICTURES backup is started...");
			BackupFileService backupFileServicePICTURES = new BackupFileService(
					m_clientTCP, m_dbConn);
			backupFileServicePICTURES.getFilesFromClient(BackupTypes.PICTURES);
			m_lastOperationTime = System.currentTimeMillis();

			break;
		case ClientOperations.MUSIC_BACKUP:

			System.out.println("MUSIC backup is started...");
			BackupFileService backupFileServiceMUSIC = new BackupFileService(
					m_clientTCP, m_dbConn);
			backupFileServiceMUSIC.getFilesFromClient(BackupTypes.MUSIC);
			m_lastOperationTime = System.currentTimeMillis();

			break;
		case ClientOperations.MOVIES_BACKUP:

			System.out.println("MOVIES backup is started...");
			BackupFileService backupFileServiceMOVIES = new BackupFileService(
					m_clientTCP, m_dbConn);
			backupFileServiceMOVIES.getFilesFromClient(BackupTypes.MOVIES);
			m_lastOperationTime = System.currentTimeMillis();

			break;

		case ClientOperations.DOWNLOADS_BACKUP:

			System.out.println("DOWNLOADS backup is started...");
			backupFileService = new BackupFileService(
					m_clientTCP, m_dbConn);
			backupFileService.getFilesFromClient(BackupTypes.DOWNLOADS);
			m_lastOperationTime = System.currentTimeMillis();

			break;
		case ClientOperations.DOWNLOADS_RESTORE:
			System.out.println("Downloads sending to client");
			backupFileService.senFiles2Client(BackupTypes.DOWNLOADS); 
			m_lastOperationTime = System.currentTimeMillis();
		default:
			break;
		}
	}

	public void run()
	{
		int opCode;
		try
		{
			while (true)
			{
				opCode = m_clientTCP.ReceiveInt();
				// System.out.println("Op Code aldik : " + opCode);
				this.doOperation(opCode, m_clientTCP);

				if ((m_lastOperationTime + SESSION_TIME) <= System.currentTimeMillis())
				{
					System.out.println("Session timeout");
					break;
				}
			}
		} catch (TCP.TCPException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	private final long SESSION_TIME = 60000;
	
	private BackupFileService backupFileService;
	private BackupNameService backupNameService;
	private long m_lastOperationTime;
	private ContactService contactService;
	private SmsService smsService;
	private UserService userService;
	private DbConnection m_dbConn;
	private TCP m_clientTCP;
	private String threadName;
}
