package cloudback.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PreDestroy;

public class DbConnection
{

	private final String URL = "jdbc:postgresql://localhost:5432/CloudBack";
	private final String USER_NAME = "cloudback";
	private final String PASSWORD = "As123456";

	private Connection conn;
	private Statement stmt;
	private PreparedStatement ps;
	private ResultSet rset;

	public DbConnection()
	{

		try
		{
			Class.forName("org.postgresql.Driver");
			setConn(DriverManager.getConnection(URL, USER_NAME, PASSWORD));
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public ResultSet executeQuery(String query)
	{
			try
			{
				stmt = getConn().createStatement();
				rset = stmt.executeQuery(query);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			
		
		return rset;
	}

	
	public boolean executeCRUD(String sql)
	{
			try
			{
				ps = getConn().prepareStatement(sql);
				ps.execute();

				return true;
			} catch (SQLException e)
			{
				e.printStackTrace();
				
				return false;
			}
	}
	

	public boolean executeCRUD(PreparedStatement ps )
	{
			try
			{
				ps.execute();

				return true;
			} catch (SQLException e)
			{
				e.printStackTrace();
				
				return false;
			}
	}
	
	@PreDestroy
	public void closeConnection() throws SQLException
	{
		getConn().close();
	}

	public Connection getConn()
	{
		return conn;
	}

	public void setConn(Connection conn)
	{
		this.conn = conn;
	}
}
