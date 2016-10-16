package org.usfirst.frc.team4141.MDRobotBase.eventmanager;


public class Dispatcher implements Runnable {
	
	
	private EventManager eventManager;

	public Dispatcher(EventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void run() {
//		System.out.println("dispatching");
		eventManager.post();
		eventManager.process();
	}

}
