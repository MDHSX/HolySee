package team4141.robotvision.msee;

import java.util.Map;

import com.google.gson.GsonBuilder;

public class CSee {
//This class interfaces with the CSee C++ video processing through JNI
	static{
		//load the libraries needed by the JNI
		System.loadLibrary("PocoXML");
		System.loadLibrary("PocoJSON");
		System.loadLibrary("PocoFoundation");
		System.loadLibrary("CSee");
		// add gstreamer libraries
	}
	
	private native void init();
	public native void start();
	public native void stop();
	public native void switchTo(String streamName);
	public native void setCameraProperty(int cameraId,int propertyId,String value);
	public native void setFilter(String streamName,String filterName);
	public native String getConfig();
	
	Map cseeConfig; 
	
	public void onInitialized(){
		System.out.println("initialization callback");
		setCameraProperty(0, 6,"YUYV");
		setCameraProperty(0, 5,"20");
		setFilter("0.ar", "targetDetect");
		
		GsonBuilder builder = new GsonBuilder();
		String configData = getConfig();
		cseeConfig = builder.create().fromJson(configData, Map.class);
		System.out.println(configData);
		start();
	}
	
	public CSee() {
		init();
	}
	
	public void close() {
		stop();
		if(cseeConfig!=null) cseeConfig.clear();
	} 
}
