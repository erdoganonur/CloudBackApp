package cloudback.service;

import java.sql.SQLException;

import cloudback.DAO.UserDAO;
import cloudback.common.StatusCodes;
import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;
import cloudback.db.DbConnection;
import cloudback.entities.User;

public class UserService
{

	public UserService(TCP clientTCP, DbConnection dbConn)
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

	public User getUser()
	{
		return m_user;
	}

	public void setUser(User user)
	{
		this.m_user = user;
	}

	public long registeredUserId(String username) throws SQLException
	{
		userDAO = new UserDAO(m_dbConn);

		if (userDAO.selectUser(username) != null)
			return userDAO.selectUser(username).getUserId();
		else
			return StatusCodes.FAILD;
	}
	public int registerUser() throws TCPException, SQLException
	{

		String username = m_clientTCP.ReceiveString();
		String password = m_clientTCP.ReceiveString();
		String email = m_clientTCP.ReceiveString();
		User user = new User(username, password, email);

		userDAO = new UserDAO(user, m_dbConn);

		int statusCode = userDAO.insertUser();

		return statusCode;
	}

	public int userLogin() throws TCPException, SQLException
	{
		String username = m_clientTCP.ReceiveString();
		String password = m_clientTCP.ReceiveString();
		
		System.out.println("Username :'"+username+"' attempting to login...");
		User user = new User(username, password);

		userDAO = new UserDAO(user, m_dbConn);

		if (userDAO.selectUser() != null)
			return StatusCodes.SUCCESFUL;
		else
			return StatusCodes.FAILD;

	}

	private UserDAO userDAO;
	private DbConnection m_dbConn;
	private TCP m_clientTCP;
	private User m_user;

}
