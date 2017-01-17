package team4141.robotvision.msee;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class RobotServiceListener implements ServiceListener,Runnable {
	public static String ServiceType="_ws._tcp.local.";
	public static String ServiceName="Team4141Robot";
	
	private ServiceDiscoveryHandler handler;
	
	public RobotServiceListener(ServiceDiscoveryHandler handler) {
		this.handler = handler;
	}
	
    @Override
    public void serviceAdded(ServiceEvent event) {
        System.out.println("Service added: " + event.getInfo());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        System.out.println("Service removed: " + event.getInfo());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        System.out.println("Service resolved: " + event.getInfo());
        if(event.getInfo()!=null && ServiceType.equals(event.getInfo().getType()) && ServiceName.equals(event.getInfo().getName())
        		&& event.getInfo().getInet4Addresses()!=null && event.getInfo().getInet4Addresses().length>0){
        	String name = event.getInfo().getName();
        	String address = event.getInfo().getInet4Addresses()[0].getHostAddress();
        	int port = event.getInfo().getPort();
        	handler.onServiceFound(name,address,port);
        }
    }

	@Override
	public void run() {
    	InetAddress[] addresses = NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
    	if(addresses!=null && addresses.length>0){
    		for(InetAddress addr : addresses){
    			if(addr.getHostAddress().contains("41.41")){
    				System.out.println(addr);
    				JmDNS jmdns;
					try {
						jmdns = JmDNS.create(addr);
	    		        jmdns.addServiceListener("_ws._tcp.local.", this);
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    		}
    	}		
	}
}
