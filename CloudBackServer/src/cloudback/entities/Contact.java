package cloudback.entities;

public class Contact
{
	

	@Override
	public String toString()
	{
		return "Contact [m_displayName=" + m_displayName + ", m_nickName="
				+ m_nickName + ", m_homePhone=" + m_homePhone
				+ ", m_mobilePhone=" + m_mobilePhone + ", m_workPhone="
				+ m_workPhone + ", m_homeEmail=" + m_homeEmail
				+ ", m_workEmail=" + m_workEmail + ", m_companyName="
				+ m_companyName + ", m_title=" + m_title + "]";
	}
	
	public Contact(String m_displayName, String m_nickName, String m_homePhone,
			String m_mobilePhone, String m_workPhone, String m_homeEmail,
			String m_workEmail, String m_companyName, String m_title)
	{
		super();
		this.m_displayName = m_displayName;
		this.m_nickName = m_nickName;
		this.m_homePhone = m_homePhone;
		this.m_mobilePhone = m_mobilePhone;
		this.m_workPhone = m_workPhone;
		this.m_homeEmail = m_homeEmail;
		this.m_workEmail = m_workEmail;
		this.m_companyName = m_companyName;
		this.m_title = m_title;
	}

	public String getDisplayName()
	{
		return m_displayName;
	}
	public void setDisplayName(String displayName)
	{
		this.m_displayName = displayName;
	}
	public String getNickName()
	{
		return m_nickName;
	}
	public void setNickName(String nickName)
	{
		this.m_nickName = nickName;
	}
	public String getHomePhone()
	{
		return m_homePhone;
	}
	public void setHomePhone(String homePhone)
	{
		this.m_homePhone = homePhone;
	}
	public String getMobilePhone()
	{
		return m_mobilePhone;
	}
	public void setMobilePhone(String mobilePhone)
	{
		this.m_mobilePhone = mobilePhone;
	}
	public String getWorkPhone()
	{
		return m_workPhone;
	}
	public void setWorkPhone(String workPhone)
	{
		this.m_workPhone = workPhone;
	}
	public String getHomeEmail()
	{
		return m_homeEmail;
	}
	public void setHomeEmail(String homeEmail)
	{
		this.m_homeEmail = homeEmail;
	}
	public String getWorkEmail()
	{
		return m_workEmail;
	}
	public void setWorkEmail(String workEmail)
	{
		this.m_workEmail = workEmail;
	}
	public String getCompanyName()
	{
		return m_companyName;
	}
	public void setCompanyName(String companyName)
	{
		this.m_companyName = companyName;
	}
	public String getTitle()
	{
		return m_title;
	}
	public void setTitle(String title)
	{
		this.m_title = title;
	}
	
	private String m_displayName;
    private String m_nickName;
    private String m_homePhone;
    private String m_mobilePhone;
    private String m_workPhone;
    private String m_homeEmail;
    private String m_workEmail;
    private String m_companyName;
    private String m_title;
}
