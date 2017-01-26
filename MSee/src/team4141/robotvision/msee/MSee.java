package team4141.robotvision.msee;


import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;



public class MSee implements CSeeHandler,SocketClientHandler, DiscoveryHandler, ServiceDiscoveryHandler {
	private String name;
	private final ExecutorService discoverExecutor = Executors.newSingleThreadExecutor();
	private final ExecutorService cseeExecutor = Executors.newSingleThreadExecutor();
	private final ExecutorService socketClientExecutor = Executors.newSingleThreadExecutor();
	private final ExecutorService dnssdBrowserExecutor = Executors.newSingleThreadExecutor();
	private boolean discoveryCompleted = false;
	private boolean cseeInitialized = false;;
	private boolean socketClientInitialized = false;
	private ICSee csee;
	private WebSocketClient client;
	private HashMap<String, Source> sources;
	
	public MSee(String name){
		this.name = name;
		// start the background threads
		dnssdBrowserExecutor.execute(new RobotServiceListener(this));
		socketClientExecutor.execute(new Client(this));
		discoverExecutor.execute(new DiscoverDevices(this));
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
        	if(dnssdBrowserExecutor!=null){
        		dnssdBrowserExecutor.shutdown();
        	}
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		String name = args[0];  //since there can be more than one instance of MSee interacting with the robot and the console, it is important to name them
		
		System.out.println("MSee starting ...");
		
		MSee msee = new MSee(name); 
		//creating an msee instance initializes the system
		//1 - the c++ core CSee is instantiated and attached cameras and lidars are discovered.  
        //          1) configuration file is checked for additional configuration settings
		//          2) based on discovered sources and configuration settings, the gstreamer pipeline is configured
		//2 - a client thread is instantiated, this manages communications with the robot
		//			upon connecting to the robot:
		//          1) the computer vision configuration of this instance is sent to the robot

		long start = (new Date()).getTime();
		while(!msee.isInitialized()){
//			while(true){			
			long now = (new Date()).getTime();
			if(now-start > 60000) {
				System.err.println("Initialization failed, aborting!");
				System.exit(1);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		
		msee.start(); //starts the robot communications client and starts streaming
		//3 - streaming is started with default channel (first channel or set in configuration file)

		//program will stay running :
		
		msee.stop(); //starts the robot communications client and starts streaming

		System.exit(0);
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
    	if(dnssdBrowserExecutor!=null){
    		dnssdBrowserExecutor.shutdown();
    	}
    	if(csee!=null){
    		csee.stop();    		
    	}
	}

	private boolean isInitialized() {
		return discoveryCompleted  && cseeInitialized && socketClientInitialized; 
	}


	private void stop() {
		System.out.println("MSee shutting down ...");
		//TODO free resources and stop processes
		
	}

	private void start() {
		System.out.println("MSee starting ...");
		csee.start();
		csee.stop();
	}

	@Override
	public synchronized void onSocketClientInitialized() {
		this.socketClientInitialized = true;
	}
	
	@Override
	public synchronized void onDiscoveryCompleted(HashMap<String,Source> sources) {
		this.discoveryCompleted = true;
		this.sources = sources;
		cseeExecutor.execute(new CSee(this));
	}
	
	@Override
	public synchronized void onCSeeInitialized(ICSee csee) {
		this.csee = csee;
		this.cseeInitialized = true;		
	}

	@Override
	public synchronized String getName(){return name;}

	@Override
	public void onCSeeStopped() {
		System.out.println("CSee has stopped");
		
	}

}
