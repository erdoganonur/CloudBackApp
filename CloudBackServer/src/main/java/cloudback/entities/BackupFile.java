package cloudback.entities;

import java.io.File;

public class BackupFile
{
	
	public BackupFile(String m_directory, File m_file)
	{
		super();
		this.m_directory = m_directory;
		this.m_file = m_file;
	}
	public String getDirectory()
	{
		return m_directory;
	}
	public void setDirectory(String directory)
	{
		this.m_directory = directory;
	}
	public File getFile()
	{
		return m_file;
	}
	public void setFile(File file)
	{
		this.m_file = file;
	}
	
	private String m_directory;
	private File m_file;

}
