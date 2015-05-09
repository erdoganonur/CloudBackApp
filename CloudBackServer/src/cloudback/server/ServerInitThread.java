package cloudback.server;

import cloudback.common.TCP;
import cloudback.common.TCP.TCPException;
import cloudback.db.DbConnection;

public class ServerInitThread extends Thread
{

	public ServerInitThread(String name)
	{
		super(name);
	}

	public void run()
	{
		TCP tcp = null;
		TCP clientTCP = null;
		DbConnection dbConn = null;

		try
		{
			tcp = new TCP(7070);
			dbConn = new DbConnection();
			System.out.println("Server started !!");

			while (true)
			{
				clientTCP = tcp.Accept(true, true);
				System.out.println(clientTCP.GetHostAddress() );
				new ServerOperationThread("ServerOperation", clientTCP, dbConn).start();

			}
		} catch (TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
