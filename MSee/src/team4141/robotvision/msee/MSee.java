package team4141.robotvision.msee;


import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


public class MSee implements ServiceDiscoveryHandler {
	private CSee csee;
	private WebSocketClient client;
	private ExecutorService dnsServiceBrowser;
	
	public MSee(){
		csee = new CSee();
		dnsServiceBrowser = Executors.newSingleThreadExecutor();
		dnsServiceBrowser.execute(new RobotServiceListener(this));
	}

	@Override
	public synchronized void onServiceFound(String name, String address, int port) {
    	System.out.printf("MSee sees service: %s@%s:%d\n", name,address,port);
    	String dest = "ws://"+address+":"+port;
    	System.out.printf("uri: %s\n", dest);

        client = new WebSocketClient();
    	RobotSocketClient robot;
		robot = new RobotSocketClient();
        try
        {
            client.start();
            URI destUri = new URI(dest);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(robot,destUri,request);
            System.out.printf("Connecting to : %s%n",destUri);
        	if(dnsServiceBrowser!=null){
        		dnsServiceBrowser.shutdown();
        		dnsServiceBrowser = null;
        	}
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		System.out.println("starting MSee server ... ");
 
		MSee msee = new MSee();

    	boolean done = false;

		while(!done){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
    	msee.close();		    	
	}

	public void close() {
		System.out.println("MSee server shutting down ... ");
    	if(client!=null){
			try
	        {
	           client.stop();
	        }
	        catch (Exception e)
	        {
	           e.printStackTrace();
	        }
    	}
    	if(dnsServiceBrowser!=null){
    		dnsServiceBrowser.shutdown();
    		dnsServiceBrowser = null;
    	}
    	if(csee!=null){
    		csee.close();    		
    	}
	}

}
