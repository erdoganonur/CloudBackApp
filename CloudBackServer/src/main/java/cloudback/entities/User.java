package cloudback.entities;

public class User
{
	public User(String userName, String password)
	{
		super();
		this.m_userName = userName;
		this.m_password = password;
		
	}
	
	public User(String userName, String password, String email)
	{
		super();
		this.m_userName = userName;
		this.m_password = password;
		this.m_email = email;
	}
	
	public User(long userId, String userName, String password, String email)
	{
		super();
		this.m_userId = userId;
		this.m_userName = userName;
		this.m_password = password;
		this.m_email = email;
	}
	public long getUserId()
	{
		return m_userId;
	}
	public void setUserId(long userId)
	{
		this.m_userId = userId;
	}
	public String getUserName()
	{
		return m_userName;
	}
	public void setUserName(String userName)
	{
		this.m_userName = userName;
	}
	public String getPassword()
	{
		return m_password;
	}
	public void setPassword(String password)
	{
		this.m_password = password;
	}
	
	public String getEmail()
	{
		return m_email;
	}

	public void setEmail(String m_email)
	{
		this.m_email = m_email;
	}

	public boolean isActive()
	{
		return m_isActive;
	}

	public void setActive(boolean m_isActive)
	{
		this.m_isActive = m_isActive;
	}

	private long m_userId;
	private String m_userName;
	private String m_password;
	private String m_email;
	private boolean m_isActive;
	

}
