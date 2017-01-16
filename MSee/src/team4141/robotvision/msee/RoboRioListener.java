package team4141.robotvision.msee;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class RoboRioListener implements ServiceListener,Runnable {

	public static final String SERVICE_TYPE="_ssh._tcp.local.";
	public static final String SERVICE_NAME="roboRIO-4141-FRC";
	private JmDNS jmdns;
	
	@Override
	public void serviceAdded(ServiceEvent event) {
		System.out.println("service added: "+event.getInfo());
	}

	@Override
	public void serviceRemoved(ServiceEvent event) {
		System.out.println("service removed: "+event.getInfo());
		System.out.println(event.getName());
		System.out.println(event.getType());	
	}

	@Override
	public void serviceResolved(ServiceEvent event) {
		System.out.println("service resolved: "+event.getInfo());
		System.out.println(event.getName());
		System.out.println(event.getType());
		for(Inet4Address address : event.getInfo().getInet4Addresses())
			System.out.println("-->"+address.getHostAddress());
	}

	@Override
	public void run() {
		System.out.println("service browser started.");
		 try {
			jmdns = JmDNS.create(InetAddress.getLocalHost());
			jmdns.addServiceListener(SERVICE_TYPE, this);
			jmdns.addServiceListener("_http._tcp.local.", this);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
	}

}