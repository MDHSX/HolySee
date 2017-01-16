package team4141.robotvision.msee;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class RoboRioListener implements ServiceListener,Runnable {

	private ConnectionHandler connectionHandler;

	public RoboRioListener(ConnectionHandler connectionHandler) {
		super();
		this.connectionHandler = connectionHandler;
	}

	public static final String SERVICE_TYPE="_ws._tcp.local.";
	public static final String SERVICE_NAME="Team4141Robot";
	private JmDNS jmdns;
	
	@Override
	public void serviceAdded(ServiceEvent event) {

	}

	@Override
	public void serviceRemoved(ServiceEvent event) {

	}

	@Override
	public void serviceResolved(ServiceEvent event) {
		if(SERVICE_NAME.equals(event.getName()) && SERVICE_TYPE.equals(event.getType()) && event.getInfo()!=null)
		{
			Inet4Address[] addresses = event.getInfo().getInet4Addresses();
			if(addresses!=null && addresses.length>0){
				connectionHandler.onConnect(event.getInfo().getName(),addresses[0],event.getInfo().getPort());
			}
		}
	}

	@Override
	public void run() {
		System.out.println("service browser started.");
		InetAddress[] addresses = NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
		for(InetAddress addr: addresses){
			if(addr.getHostName().contains("41.41")){
				try {
					jmdns = JmDNS.create(addr);
					jmdns.addServiceListener(SERVICE_TYPE, this);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

}