package com.cloudback.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cloudback.common.StatusCodes;
import com.cloudback.db.DbConnection;
import com.cloudback.entities.Sms;

public class SmsDAO
{

	public SmsDAO(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public SmsDAO(Sms sms, DbConnection dbConn)
	{
		this.m_sms = sms;
		this.m_dbConn = dbConn;
	}

	public Sms getSms()
	{
		return m_sms;
	}

	public void setSms(Sms sms)
	{
		this.m_sms = sms;
	}

	public DbConnection getDbConn()
	{
		return m_dbConn;
	}

	public void setDbConn(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public int lastRevisionNum(long userId) throws SQLException
	{
		ResultSet rs = m_dbConn
				.executeQuery("SELECT max(backup_revision) FROM sms WHERE user_id = "
						+ userId);
		rs.next();
		return rs.getInt(1);
	}

	public int insertSmsList(ArrayList<Sms> smsList, long userId, int revNum)
			throws SQLException
	{
		String query = "";
		PreparedStatement ps;
		for (int i = 0; i < smsList.size(); i++)
		{
			query = "INSERT INTO sms( user_id, adress, date, protocol, read, status, type, body, service_center, locked, backup_revision) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

			ps = m_dbConn.getConn().prepareStatement(query);

			ps.setLong(1, userId);
			ps.setString(2, smsList.get(i).getAddress());
			ps.setLong(3, smsList.get(i).getDate());
			ps.setInt(4, smsList.get(i).getProtocol());
			ps.setInt(5, smsList.get(i).getRead());
			ps.setInt(6, smsList.get(i).getStatus());
			ps.setInt(7, smsList.get(i).getType());
			ps.setString(8, smsList.get(i).getBody());
			ps.setString(9, smsList.get(i).getService_center());
			ps.setInt(10, smsList.get(i).getLocked());
			ps.setInt(11, revNum);

			m_dbConn.executeCRUD(ps);
		}

		return StatusCodes.SUCCESFUL;

	}

	public ArrayList<Sms> selectSmsByUserRev(long userId, int revNumber)
			throws SQLException
	{

		ResultSet rs = m_dbConn
				.executeQuery("SELECT adress, date, protocol, read, status, type, "
						+ "body, service_center, locked, backup_revision FROM sms WHERE user_id = "
						+ userId + "AND backup_revision = " + revNumber);

		ArrayList<Sms> smsList = new ArrayList<Sms>();

		while (rs.next())
		{
			smsList.add(new Sms(rs.getString("adress"), rs.getLong("date"), rs
					.getInt("protocol"), rs.getInt("read"),
					rs.getInt("status"), rs.getInt("type"), rs
							.getString("body"), rs.getString("service_center"),
					rs.getInt("locked")));
		}
		return smsList;
	}

	private DbConnection m_dbConn;
	private Sms m_sms;
}
