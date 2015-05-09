package cloudback.service;

import java.io.File;
import java.sql.SQLException;

import cloudback.DAO.BackupNameDAO;
import cloudback.common.BackupTypes;
import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;
import cloudback.db.DbConnection;

public class BackupFileService
{

	public BackupFileService(TCP m_clientTCP, DbConnection dbConn)
	{
		super();
		this.m_clientTCP = m_clientTCP;
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

	public DbConnection getDbConn()
	{
		return m_dbConn;
	}

	public void setDbConn(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public void senFiles2Client(int backupType) throws TCPException, SQLException
	{
		String pathName = "";
		
		switch (backupType)
		{
		case BackupTypes.DCIM:
			pathName = "DCIM";
			break;
		case BackupTypes.PICTURES:
			pathName = "PICTURES";
			break;
		case BackupTypes.MUSIC:
			pathName = "MUSIC";
			break;
		case BackupTypes.MOVIES:
			pathName = "MOVIES";
			break;
		case BackupTypes.DOWNLOADS:
			pathName = "DOWNLOADS";
			break;
		default:
			break;
		}
		
		
		File dir;
		//Getting backup name
//		String backupName = m_clientTCP.ReceiveString();
		//Getting user name 
		String username = m_clientTCP.ReceiveString();

		UserService userService = new UserService(m_clientTCP, m_dbConn);

		long userId = userService.registeredUserId(username);

		String filePath;
		String fileName;
		filePath = "/Users/onurerdogan/CB_BacupFiles/" + userId + "/"+ pathName;
		dir = new File(filePath);
		
		int revNum = 1;
		if (!dir.exists())
		{
			filePath += "/1";

			dir = new File(filePath);
			
		} else
		{
			File[] fileList = dir.listFiles();

			int max = 0;
			int temp = 0;
			for (int i = 0; i < fileList.length; i++)
			{
				try
				{
					temp = Integer.parseInt(fileList[i].getName());
					if (temp > max)
						max = temp;
					
				} catch (NumberFormatException e)
				{
					continue;
				}
			}
			revNum = max ;

			filePath += "/" + revNum;
			dir = new File(filePath);
			
			copyFile(dir);
			
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
					m_clientTCP.SendBoolean(true);
					System.out
							.println("The file path is : "
									+ fileList[i].getParent()
									+ "\n File is copying...");
					System.out.println(workDir.getPath());

					// Sending file's parent path
					m_clientTCP.SendString(
							fileList[i].getParent().replaceFirst(
									directory, ""));
					// Sending file name
					m_clientTCP.SendString(
							fileList[i].getName());

					// Sending file
					m_clientTCP.SendFile(fileList[i].getPath(),
							2048);
				}
			}
		}

	}
	
	public void getFilesFromClient(int backupType) throws TCPException,
			SQLException
	{
		String pathName = "";
		
		switch (backupType)
		{
		case BackupTypes.DCIM:
			pathName = "DCIM";
			break;
		case BackupTypes.PICTURES:
			pathName = "PICTURES";
			break;
		case BackupTypes.MUSIC:
			pathName = "MUSIC";
			break;
		case BackupTypes.MOVIES:
			pathName = "MOVIES";
			break;
		case BackupTypes.DOWNLOADS:
			pathName = "DOWNLOADS";
			break;
		default:
			break;
		}
		boolean isContinue = true;
		File dir;
		//Getting backup name
		String backupName = m_clientTCP.ReceiveString();
		//Getting user name 
		String username = m_clientTCP.ReceiveString();

		UserService userService = new UserService(m_clientTCP, m_dbConn);

		long userId = userService.registeredUserId(username);

		String filePath;
		String fileName;
		filePath = "/Users/onurerdogan/CB_BacupFiles/" + userId + "/"
				+ pathName;
		dir = new File(filePath);
		int revNum = 0;

		if (!dir.exists())
		{
			filePath += "/1";

			dir = new File(filePath);
			dir.mkdirs();
		} else
		{
			File[] fileList = dir.listFiles();

			int max = 0;
			int temp = 0;
			for (int i = 0; i < fileList.length; i++)
			{
				try
				{
					temp = Integer.parseInt(fileList[i].getName());
					if (temp > max)
						max = temp;
					
				} catch (NumberFormatException e)
				{
					continue;
				}
			}

			revNum = max + 1;

			directory = filePath += "/" + revNum;
			dir = new File(filePath);
			dir.mkdirs();
		}

		while (isContinue)
		{
			isContinue = m_clientTCP.ReceiveBoolean();

			String workingDir = "";

			if (!isContinue)
			{
				System.out.println("###### Dosyalar bitti... #####");
				break;
			}

			workingDir = filePath + m_clientTCP.ReceiveString();
//			System.out.println(workingDir);
			dir = new File(workingDir);

			fileName = m_clientTCP.ReceiveString();
			
			System.out.println(dir.getAbsolutePath());
			// if the directory does not exist, create it
			if (!dir.exists())
			{
				System.out.println("Creating directory: " + workingDir);
				boolean result = dir.mkdirs();
				if (result)
				{
					System.out.println("Directory was created");
				}

			}
			System.out.println(workingDir + "/" + fileName
					+ "\t file copying...");
			m_clientTCP.ReceiveFile(workingDir + "/" + fileName, 2048);

		}

		BackupNameDAO backupNameDAO = new BackupNameDAO(m_dbConn);	
		backupNameDAO.insertBackupName(userId, backupType, revNum, backupName);
	}

	private String directory;
	private TCP m_clientTCP;
	private DbConnection m_dbConn;
}
