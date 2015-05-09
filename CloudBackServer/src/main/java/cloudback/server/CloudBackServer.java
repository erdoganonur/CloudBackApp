package cloudback.server;

public class CloudBackServer {

	public static void Run() {

		new ServerInitThread("RegisterThread").start();
		
		for(;;);

	}
}
