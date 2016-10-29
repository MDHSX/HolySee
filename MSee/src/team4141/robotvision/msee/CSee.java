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
	
	public void onInitialized(String jsonConfig){
		System.out.println("initialization callback, received: "+jsonConfig);
		GsonBuilder builder = new GsonBuilder();
		Map CSeeConfig = builder.create().fromJson(jsonConfig, Map.class);
		setCameraProperty(0, 6,"YUYV");
		setCameraProperty(0, 5,"10");
		start();
		setFilter("0.ar", "targetDetect");
		stop();
	}
	
	public CSee() {
		init();
	}
}
