package com.cloudback.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudback.common.StatusCodes;
import com.cloudback.db.DbConnection;
import com.cloudback.entities.User;

public class UserDAO
{
	public UserDAO(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public UserDAO(User user, DbConnection dbConn)
	{
		this.m_user = user;
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

	public User getUser()
	{
		return m_user;
	}

	public void setUser(User user)
	{
		this.m_user = user;
	}

	public int insertUser() throws SQLException
	{
		User user = this.selectUser();
		if (user != null)
		{
			System.out.println("User '" + m_user.getUserName()
					+ "' is already exist...");
			return StatusCodes.USER_ALREADY_EXIST;
		} else
		{
			

			m_dbConn.executeCRUD("INSERT INTO credentials (username, password, email) VALUES ('"
					+ m_user.getUserName()
					+ "', '"
					+ m_user.getPassword()
					+ "', '" + m_user.getEmail() + "')");

			return StatusCodes.SUCCESFUL;
		}

	}

	public int userPasswordUpdate() throws SQLException
	{
		User user = this.selectUser();
		
		if (user != null)
		{
			m_dbConn.executeCRUD("UPDATE credentials SET password = '"
					+ m_user.getPassword() + "' WHERE username = '"
					+ m_user.getUserName() + "'");

			return 0;
		} else
		{
			System.out.println("There is no user as username = '"
					+ m_user.getUserName() + "'");
			return 1;
		}
	}

	public User selectUser() throws SQLException
	{
		ResultSet rs = m_dbConn
				.executeQuery("SELECT * FROM credentials WHERE username = '"
						+ m_user.getUserName() + "'");

		if (rs.next())
			return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
		else
			return null;
	}

	public User selectUser(String userName) throws SQLException
	{
		ResultSet rs = m_dbConn
				.executeQuery("SELECT * FROM credentials WHERE username = '"
						+userName+ "'");

		if (rs.next())
			return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
		else
			return null;
	}
	
	private DbConnection m_dbConn;
	private User m_user;
}
