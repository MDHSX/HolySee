package team4141.MSee.scratch;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.usfirst.frc.team4141.MDRobotBase.config.ConfigManager;

import com.google.gson.Gson;

public class CSeeScratch implements Runnable,ICSee {

//	static{
//		System.loadLibrary("PocoFoundation");
//		System.loadLibrary("PocoXML");
//		System.loadLibrary("PocoJSON");
//		System.loadLibrary("PocoUtil");
//		System.loadLibrary("opencv_world310");
//		System.loadLibrary("CSee");
//	}
	
//	private native void init();
//	private native void start();
//	private native void stop();
//	private native void switchTo(String feedName);
//	private native void set(String elementName, String filterName);
	
	
	private Hashtable<String,Source> sources;
	public Hashtable<String, Source> getSources(){return sources;}
	private Gson gson = new Gson();
	private boolean isInitialized = false;
	private CSeeHandler handler;
	
	public boolean isInitialized(){return isInitialized;}
	
	public CSeeScratch(CSeeHandler handler) {
		this.sources = new Hashtable<String,Source>();
		this.handler = handler;
	}

	private void discoverCameras() {
		//from c++ - JSON string with camera info
		String jsonCameras = "[{\"id\":\"tegraIdentifyer\",\"properties\":[{\"id\":3,\"name\":\"CAP_PROP_FRAME_WIDTH\",\"value\":\"640.000000\"},{\"id\":4,\"name\":\"CAP_PROP_FRAME_HEIGHT\",\"value\":\"480.000000\"},{\"id\":6,\"name\":\"CAP_PROP_FOURCC\",\"value\":\"YUY2\"},{\"id\":10,\"name\":\"CAP_PROP_BRIGHTNESS\",\"value\":\"128.000000\"},{\"id\":11,\"name\":\"CAP_PROP_CONTRAST\",\"value\":\"32.000000\"},{\"id\":12,\"name\":\"CAP_PROP_SATURATION\",\"value\":\"64.000000\"}]}]";
		ArrayList cameras = (ArrayList) gson.fromJson(jsonCameras, Object.class);
		
		for(Object item : cameras){
			// each key represents a camera
			// each camera is a Map in the arraylist of cameras
			Camera camera = new Camera((Map)item);  //create camera object by parsing Map
			String matchName = camera.match();
			if(matchName!=null){
				// the camera was found in properties file, match it up and add the property settings from the config file to the camera
				sources.put(matchName, camera);
				//add property file settings to the camera settings
				camera.add(new Setting("name",matchName));
				Iterator<String> propertyFileKeys = ConfigManager.getConf().getKeys();
				while(propertyFileKeys.hasNext()){
					String key = propertyFileKeys.next();
					if(key.startsWith(handler.getName()+"."+matchName)){
//						System.out.println("***"+key);
						camera.add(new Setting(key.substring(key.lastIndexOf(".")+1),ConfigManager.getConf().getString(key)));
					}
				}
			}
			else{
				// the camera does not match an identifier in the properties file, add it as a rogue camera
				sources.put(camera.getId(), camera);
			}
			System.out.println("added camera "+camera.getName()+":");
			System.out.println(camera);
		}
		return;
	}
	private String discoverLidars() {
		//from c++ - JSON string with lidar info
		String lidars = "{\"0\":{\"id\":0,\"CAP_PROP_FRAME_WIDTH\":{\"id\":3,\"name\":\"CAP_PROP_FRAME_WIDTH\",\"value\":\"640.000000\"},\"CAP_PROP_FRAME_HEIGHT\":{\"id\":4,\"name\":\"CAP_PROP_FRAME_HEIGHT\",\"value\":\"480.000000\"},\"CAP_PROP_FOURCC\":{\"id\":6,\"name\":\"CAP_PROP_FOURCC\",\"value\":\"YUY2\"},\"CAP_PROP_BRIGHTNESS\":{\"id\":10,\"name\":\"CAP_PROP_BRIGHTNESS\",\"value\":\"128.000000\"},\"CAP_PROP_CONTRAST\":{\"id\":11,\"name\":\"CAP_PROP_CONTRAST\",\"value\":\"32.000000\"},\"CAP_PROP_SATURATION\":{\"id\":12,\"name\":\"CAP_PROP_SATURATION\",\"value\":\"64.000000\"}}}";
		return lidars;
	}

	private void discoverSources() {
		//populate Sources Dictionary
		//sources can be cameras or lidars
		discoverCameras();
	}

	@Override
	public void run() {
		discoverSources();
		handler.onCSeeInitialized(this);
	}

	@Override
	public synchronized void start() {
		System.out.println("csee received start.");
		
	}

	@Override
	public void stop() {
		handler.onCSeeStopped();
		
	}

}
