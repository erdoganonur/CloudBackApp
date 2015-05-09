package cloudback.entities;

public class Sms
{
	
	
	@Override
	public String toString()
	{
		return "Sms [m_address=" + m_address + ", m_date=" + m_date
				+ ", m_protocol=" + m_protocol + ", m_read=" + m_read
				+ ", m_status=" + m_status + ", m_type=" + m_type + ", m_body="
				+ m_body + ", m_service_center=" + m_service_center
				+ ", m_locked=" + m_locked + "]";
	}
	public Sms(String m_address, long m_date, int m_protocol, int m_read,
			int m_status, int m_type, String m_body, String m_service_center,
			int m_locked)
	{
		super();
		this.m_address = m_address;
		this.m_date = m_date;
		this.m_protocol = m_protocol;
		this.m_read = m_read;
		this.m_status = m_status;
		this.m_type = m_type;
		this.m_body = m_body;
		this.m_service_center = m_service_center;
		this.m_locked = m_locked;
	}
	public String getAddress()
	{
		return m_address;
	}
	public void setAddress(String address)
	{
		this.m_address = address;
	}
	public long getDate()
	{
		return m_date;
	}
	public void setDate(long date)
	{
		this.m_date = date;
	}
	public int getProtocol()
	{
		return m_protocol;
	}
	public void setProtocol(int protocol)
	{
		this.m_protocol = protocol;
	}
	public int getRead()
	{
		return m_read;
	}
	public void setRead(int read)
	{
		this.m_read = read;
	}
	public int getStatus()
	{
		return m_status;
	}
	public void setStatus(int status)
	{
		this.m_status = status;
	}
	public int getType()
	{
		return m_type;
	}
	public void setType(int type)
	{
		this.m_type = type;
	}
	public String getBody()
	{
		return m_body;
	}
	public void setBody(String body)
	{
		this.m_body = body;
	}
	public String getService_center()
	{
		return m_service_center;
	}
	public void setService_center(String service_center)
	{
		this.m_service_center = service_center;
	}
	public int getLocked()
	{
		return m_locked;
	}
	public void setLocked(int locked)
	{
		this.m_locked = locked;
	}
	
	//Private variables
	private String m_address;
	private long m_date;
	private int m_protocol;
	private int m_read;
	private int m_status;
	private int m_type;
	private String m_body;
	private String m_service_center;
	private int m_locked;
}
