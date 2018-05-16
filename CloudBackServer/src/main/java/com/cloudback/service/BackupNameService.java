package com.cloudback.service;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cloudback.dao.BackupNameDAO;
import com.cloudback.common.TCP;
import com.cloudback.db.DbConnection;
import com.cloudback.entities.BackupName;

public class BackupNameService
{

	public BackupNameService(DbConnection m_dbConn, TCP m_clientTCP)
	{
		super();
		this.m_dbConn = m_dbConn;
		this.m_clientTCP = m_clientTCP;
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
	
	
	public ArrayList<BackupName> getBackRevList(long userId, int backupType) throws SQLException
	{
		BackupNameDAO backupNameDAO = new BackupNameDAO(m_dbConn);
		return backupNameDAO.getBackupNames(userId, backupType);
	}

	public void sendBackupNames2Server(int backupType) throws SQLException, TCP.TCPException
	{
		String username = m_clientTCP.ReceiveString();
		UserService userService = new UserService(m_clientTCP, m_dbConn);
		long userId = userService.registeredUserId(username);
		System.out.println("username :"+username+" id :"+userId);
		ArrayList<BackupName> backupNameList = this.getBackRevList(userId, backupType);
		
		System.out.println(backupNameList.size());
		m_clientTCP.SendInt(backupNameList.size());
		
		for(int i = 0; i < backupNameList.size(); i++)
		{
			m_clientTCP.SendString(backupNameList.get(i).getBackupName());
			m_clientTCP.SendLong(backupNameList.get(i).getUserId());
			m_clientTCP.SendInt(backupNameList.get(i).getRevNum());
			m_clientTCP.SendInt(backupNameList.get(i).getBackupType());
		}
	}

	private DbConnection m_dbConn;
	private TCP m_clientTCP;
}
