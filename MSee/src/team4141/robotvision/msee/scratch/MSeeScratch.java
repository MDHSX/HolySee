package team4141.MSee.scratch;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MSeeScratch implements CSeeHandler,SocketClientHandler{
	//TODO spawn threads for csee and client, make these Runnables?
	private String name;
	private final ExecutorService cseeExecutor = Executors.newSingleThreadExecutor();
	private final ExecutorService socketClientExecutor = Executors.newSingleThreadExecutor();
	private boolean cseeInitialized = false;;
	private boolean socketClientInitialized = false;
	private ICSee csee;

	public MSeeScratch(String name){
		this.name = name;
		// start a thread for CSee
		cseeExecutor.execute(new CSeeScratch(this));
		cseeExecutor.execute(new Client(this));
	}
	
	public static void main(String[] args) {
		String name = args[0];  //since there can be more than one instance of MSee interacting with the robot and the console, it is important to name them
		
		System.out.println("MSee starting ...");
		
		MSeeScratch msee = new MSeeScratch(name); 
		//creating an msee instance initializes the system
		//1 - the c++ core CSee is instantiated and attached cameras and lidars are discovered.  
        //          1) configuration file is checked for additional configuration settings
		//          2) based on discovered sources and configuration settings, the gstreamer pipeline is configured
		//2 - a client thread is instantiated, this manages communications with the robot
		//			upon connecting to the robot:
		//          1) the computer vision configuration of this instance is sent to the robot

		long start = (new Date()).getTime();
		while(!msee.isInitialized()){
			long now = (new Date()).getTime();
			if(now-start > 60000) {
				System.err.println("Initialization failed, aborting!");
				System.exit(1);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Thread error, aborting!");
				System.exit(1);
			}
		}

		
		msee.start(); //starts the robot communications client and starts streaming
		//3 - streaming is started with default channel (first channel or set in configuration file)

		//program will stay running :
		
		msee.stop(); //starts the robot communications client and starts streaming

		System.exit(0);
	}

	private boolean isInitialized() {
		return cseeInitialized && socketClientInitialized; 
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
