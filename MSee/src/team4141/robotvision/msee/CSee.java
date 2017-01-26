package team4141.robotvision.msee;



import com.google.gson.Gson;

public class CSee implements Runnable,ICSee {

	static{
		System.loadLibrary("PocoFoundation");
		System.loadLibrary("PocoXML");
		System.loadLibrary("PocoJSON");
		System.loadLibrary("PocoUtil");
//		System.loadLibrary("opencv_world310");
		System.loadLibrary("CSee");
	}
	
	private native void init();
	public native void start();
	public native void stop();
	private native void switchTo(String feedName);
	
	

	private Gson gson = new Gson();
	private boolean isInitialized = false;
	private CSeeHandler handler;
	
	public boolean isInitialized(){return isInitialized;}
	
	public CSee(CSeeHandler handler) {
		this.handler = handler;
	}

	public void onInitialized(){
		handler.onCSeeInitialized(this);
	}


	@Override
	public void run() {
//		discoverSources();
		init();
	}

//	private void init() {
//		isInitialized = true;
//	}
//
//	@Override
//	public synchronized void start() {
//		System.out.println("csee received start.");
//		
//	}
//
//	@Override
//	public void stop() {
//		handler.onCSeeStopped();
//		
//	}

}
