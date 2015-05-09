package cloudback.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cloudback.db.DbConnection;
import cloudback.entities.BackupName;

public class BackupNameDAO
{

	public void insertBackupName(long userId, int backupTypeId,
			int backupRevNum, String backupName) throws SQLException
	{
		String query = "INSERT INTO backup_rev_names"
				+ "( user_id, backup_type_id, backup_rev_num, backup_name) "
				+ "VALUES ( ?, ?, ?, ?);";

		PreparedStatement ps = m_dbConn.getConn().prepareStatement(query);
		ps.setLong(1, userId);
		ps.setInt(2, backupTypeId);
		ps.setInt(3, backupRevNum);
		ps.setString(4, backupName);

		m_dbConn.executeCRUD(ps);

		System.out.println(backupName + " || " + userId + " || " + backupTypeId
				+ " || " + backupRevNum);

	}

	public ArrayList<BackupName> getBackupNames(long userId, int backupType) throws SQLException
	{
		ResultSet rs = m_dbConn
				.executeQuery("select * from backup_rev_names where user_id = "
						+ userId + " and backup_type_id = " + backupType);

		ArrayList<BackupName> backupNameList = new ArrayList<BackupName>();

		while (rs.next())
		{
			backupNameList.add(new BackupName(rs.getString("backup_name"), rs
					.getLong("user_id"), rs.getInt("backup_rev_num"), rs
					.getInt("backup_type_id")));

		}
		return backupNameList;
	}

	public BackupNameDAO(DbConnection dbConn)
	{
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

	private DbConnection m_dbConn;

}
