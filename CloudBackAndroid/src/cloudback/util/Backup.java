package cloudback.util;

public class Backup
{
	
	
	
	public Backup(String backupName, int revNum, long userId, int backupType)
	{
		super();
		this.backupName = backupName;
		this.revNum = revNum;
		this.userId = userId;
		this.backupType = backupType;
	}
	
	public String getBackupName()
	{
		return this.backupName;
	}
	public void setBackupName(String backupName)
	{
		this.backupName = backupName;
	}
	public int getRevNum()
	{
		return revNum;
	}
	public void setRevNum(int revNum)
	{
		this.revNum = revNum;
	}
	
	public long getUserId()
	{
		return userId;
	}
	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public int getBackupType()
	{
		return backupType;
	}
	public void setBackupType(int backupType)
	{
		this.backupType = backupType;
	}

	private String backupName;
	private int revNum;
	private long userId;
	private int backupType;
}
