import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		InetAddress[] addresses = NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
		InetAddress eth=null;
		for(InetAddress addr:addresses){
			System.out.println(addr.getHostAddress());
			if(addr.getHostAddress().contains("41.41")){
				eth = addr;
			}
		}
		try {
            JmDNS jmdns;
			if(eth!=null){
				// Create a JmDNS instance
	            jmdns = JmDNS.create(eth);
			}
			else {
	            jmdns = JmDNS.create();
			}
            // Add a service listener
            jmdns.addServiceListener("_ws._tcp.local.", new ServiceBrowser());
            // Wait a bit
            Thread.sleep(30000);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
