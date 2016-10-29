package team4141.robotvision.msee;

import java.util.Map;

import com.google.gson.GsonBuilder;

public class CSee {
//This class interfaces with the CSee C++ video processing through JNI
	static{
		//load the libraries needed by the JNI
		System.loadLibrary("PocoFoundation");
		System.loadLibrary("PocoXML");
		System.loadLibrary("PocoJSON");
		System.loadLibrary("CSee");
		// add gstreamer libraries
	}
	
	private native void init();
	private native void start();
	private native void stop();
	private native void switchTo();
	private native void set();
	
	public void onInitialized(String jsonConfig){
		System.out.println("initialization callback, received: "+jsonConfig);
		GsonBuilder builder = new GsonBuilder();
		Map CSeeConfig = builder.create().fromJson(jsonConfig, Map.class);
		for (Object key: CSeeConfig.keySet()){
			System.out.println("key: "+key);
			System.out.println("value: "+CSeeConfig.get(key).getClass().getName());
		}
	}
	
	public CSee() {
		init();
	}
}
