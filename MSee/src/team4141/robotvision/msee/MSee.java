package team4141.robotvision.msee;

import java.net.Inet4Address;


public class MSee{

	public static void main(String[] args) {
		System.out.println("starting MSee server ... ");
		final Server server = new Server();
//		server
//		.add("frameRate", new IntegerConfigSetting(new Integer(1), new Integer(30), new Integer(20)))
//		.add("serverName", new StringConfigSetting("HolySee"))
//		.configure();(
		server.start();

        new Thread(new RoboRioListener(new ConnectionHandler() {
			
			@Override
			public synchronized void onConnect(String serviceName, Inet4Address address, int port) {
				server.connect(serviceName,address,port);
			}
		})) .start();
    
		
		MSee msee = new MSee();
		
		boolean done = false;
//		while(server.isRunning()){
		while(!done){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
		}
	}
		
		System.out.println("MSee server shutting down ... ");
	}



	private CSee csee;

	public MSee(){
		csee = new CSee();
	
	}
}
