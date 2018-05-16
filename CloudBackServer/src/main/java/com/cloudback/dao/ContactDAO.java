package com.cloudback.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cloudback.common.StatusCodes;
import com.cloudback.db.DbConnection;
import com.cloudback.entities.Contact;

public class ContactDAO
{
	public ContactDAO(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public ContactDAO(DbConnection dbConn, Contact contact)
	{
		this.m_dbConn = dbConn;
		this.m_contact = contact;
	}

	public DbConnection getDbConn()
	{
		return m_dbConn;
	}

	public void setDbConn(DbConnection dbConn)
	{
		this.m_dbConn = dbConn;
	}

	public Contact getContact()
	{
		return m_contact;
	}

	public void setContact(Contact contact)
	{
		this.m_contact = contact;
	}

	public int lastRevisionNum(long userId) throws SQLException
	{
		ResultSet rs = m_dbConn
				.executeQuery("SELECT max(backup_revision) FROM contacts WHERE user_id = "
						+ userId);
		rs.next();
		return rs.getInt(1);
	}

	public int insertContactList(ArrayList<Contact> contactList, long userId,
			int revNum) throws SQLException
	{
		String query = "";
		PreparedStatement ps;
		for (int i = 0; i < contactList.size(); i++)
		{
			query = "INSERT INTO contacts(user_id, display_name, nickname, home_phone, "
					+ "mobile_phone, work_phone, home_email, work_email, company_name, title, "
					+ "backup_revision ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = m_dbConn.getConn().prepareStatement(query);

			ps.setLong(1, userId);
			ps.setString(2, contactList.get(i).getDisplayName());
			ps.setString(3, contactList.get(i).getNickName());
			ps.setString(4, contactList.get(i).getHomePhone());
			ps.setString(5, contactList.get(i).getMobilePhone());
			ps.setString(6, contactList.get(i).getWorkPhone());
			ps.setString(7, contactList.get(i).getHomeEmail());
			ps.setString(8, contactList.get(i).getWorkEmail());
			ps.setString(9, contactList.get(i).getCompanyName());
			ps.setString(10, contactList.get(i).getTitle());
			ps.setInt(11, revNum);

			m_dbConn.executeCRUD(ps);
		}

		return StatusCodes.SUCCESFUL;

	}

	public ArrayList<Contact> selectContactByUserRev(long userId, int revNumber)
			throws SQLException
	{

		ResultSet rs = m_dbConn
				.executeQuery("SELECT id, user_id, display_name, nickname, home_phone, mobile_phone, "
						+ "work_phone, home_email, work_email, company_name, title FROM contacts WHERE user_id = "
						+ userId + "AND backup_revision = " + revNumber);

		System.out.println("SELECT id, user_id, display_name, nickname, home_phone, mobile_phone, "
						+ "work_phone, home_email, work_email, company_name, title FROM contacts WHERE user_id = "
						+ userId + "AND backup_revision = " + revNumber);
		
		ArrayList<Contact> contactList = new ArrayList<Contact>();

		while (rs.next())
		{
			contactList.add(new Contact(rs.getString("display_name"), rs
					.getString("nickname"), rs.getString("home_phone"), rs
					.getString("mobile_phone"), rs.getString("work_phone"), rs
					.getString("home_email"), rs.getString("work_email"), rs
					.getString("company_name"), rs.getString("title")));
		}
		return contactList;
	}

	private DbConnection m_dbConn;
	private Contact m_contact;
}
