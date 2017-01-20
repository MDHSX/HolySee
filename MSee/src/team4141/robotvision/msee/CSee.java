package team4141.robotvision.msee;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import team4141.robotvision.msee.CSeeHandler;
import team4141.robotvision.msee.ICSee;
import team4141.robotvision.msee.Source;


public class CSee  implements Runnable,ICSee {
	
	private Hashtable<String,Source> sources;
	public Hashtable<String, Source> getSources(){return sources;}
	private Gson gson = new Gson();
	private boolean isInitialized = false;
	private CSeeHandler handler;
	
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
//	public native void start();
//	public native void stop();
	public native void switchTo(String streamName);
	public native void setCameraProperty(int cameraId,int propertyId,String value);
	public native void setFilter(String streamName,String filterName);
	public native String getConfig();
	
	public void onInitialized(){
		System.out.println("initialization callback");
		setCameraProperty(0, 6,"YUYV");
		setCameraProperty(0, 5,"20");
		setFilter("0.ar", "targetDetect");
		
		GsonBuilder builder = new GsonBuilder();
		String configData = getConfig();
//		cseeConfig = builder.create().fromJson(configData, Map.class);
		System.out.println(configData);
		
	}
	
	
	public CSee(CSeeHandler handler) {
		this.sources = new Hashtable<String,Source>();
		this.handler = handler;
		
		///
		init();  //needs to move

	}

	private void discoverSources() {
		//populate Sources Dictionary
		//sources can be cameras or lidars
		discoverCameras();
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
		
	@Override
	public boolean isInitialized(){return isInitialized;}
	
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
