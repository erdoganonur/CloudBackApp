package com.cloudback.server;

import com.cloudback.common.TCP;
import com.cloudback.db.DbConnection;

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
			tcp = new TCP(6060);
			dbConn = new DbConnection();
			System.out.println("Server started !!");

			while (true)
			{
				clientTCP = tcp.Accept(true, true);
				System.out.println(clientTCP.GetHostAddress() );
				new ServerOperationThread("ServerOperation", clientTCP, dbConn).start();

			}
		} catch (TCP.TCPException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
