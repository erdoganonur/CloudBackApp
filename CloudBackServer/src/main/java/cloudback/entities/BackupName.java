package cloudback.entities;

public class BackupName
{
	
	
	public BackupName(String backupName, long userId, int revNum, int backupType)
	{
		super();
		this.m_backupName = backupName;
		this.m_userId = userId;
		this.m_revNum = revNum;
		this.m_backupType = backupType;
	}
	public String getBackupName()
	{
		return m_backupName;
	}
	public void setBackupName(String backupName)
	{
		this.m_backupName = backupName;
	}
	public long getUserId()
	{
		return m_userId;
	}
	public void setUserId(long userId)
	{
		this.m_userId = userId;
	}
	public int getRevNum()
	{
		return m_revNum;
	}
	public void setRevNum(int revNum)
	{
		this.m_revNum = revNum;
	}
	public int getBackupType()
	{
		return m_backupType;
	}
	public void setBackupType(int backupType)
	{
		this.m_backupType = backupType;
	}
	
	private String m_backupName;
	private long m_userId;
	private int m_revNum;
	private int m_backupType;
}
