package team4141.robotvision.msee;

import java.io.IOException;
import java.net.Inet4Address;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class MSee{

	public static void main(String[] args) {
		System.out.println("starting MSee server ... ");
///		Server server = new Server();
//		server
//		.add("frameRate", new IntegerConfigSetting(new Integer(1), new Integer(30), new Integer(20)))
//		.add("serverName", new StringConfigSetting("HolySee"))
//		.configure();
//		server.start();

        new Thread(new RoboRioListener()) .start();
    
		
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
