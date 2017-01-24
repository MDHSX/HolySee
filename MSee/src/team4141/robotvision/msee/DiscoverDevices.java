package team4141.MSee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DiscoverDevices implements Runnable{
	//This class should discover cameras and lidars
	//It should call back with the discovered information
	//It should report the discovered information in an easily consumable manner
	//a simple list of cameras and lidars
	
	DiscoveryHandler handler;
	private ArrayList<Source> sources;
	private static String DISCOVER_USB_COMMAND="w:/list.bat";
	private static String DISCOVER_CAMERAS_COMMAND="w:/list.bat";
	private static String DISCOVER_CAMERA_DETAILS_COMMAND="w:/getDetails.bat";
	
	public DiscoverDevices(DiscoveryHandler handler) {
		//register the msee instance
		this.handler = handler;
		this.sources = new ArrayList<Source>();
	}

	@Override
	public void run() {
		discoverUSB();
		discoverCameras();
		handler.onDiscoveryCompleted(sources);
	}

	private void discoverCameras() {
		System.out.println("discovering cameras");
		String result = discover(DISCOVER_CAMERAS_COMMAND);
		List<String> cameraPaths = parseCamerasInfo(result);
		for(String cameraPath : cameraPaths){
			result = discover(DISCOVER_CAMERA_DETAILS_COMMAND,cameraPath);
			parseCameraInfo(cameraPath,result);
		}
	}

	private List<String> parseCamerasInfo(String camerasText) {
		System.out.println(camerasText);
		// parse the first result to get a list of camera devices
		// each item on the list will then be used to get details for each device
		List<String> names = new ArrayList<String>();
		names.add("cam1");
		return names;
	}

	private void discoverUSB() {
		System.out.println("discovering USB");
		String result = discover(DISCOVER_USB_COMMAND);
		parseUSBInfo(result);
	}
	
	private void parseCameraInfo(String cameraPath,String cameraDetails) {
		System.out.println("camera details for "+cameraPath);
		System.out.println(cameraDetails);
		
	}

	private void parseUSBInfo(String usbText) {
		System.out.println(usbText);
	}

	private String discover(String... command) {
		Process process;
		StringBuilder sb = new StringBuilder();
		try {
			process = new ProcessBuilder(command).start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
//			System.out.printf("Output of running %s is:",command);

			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
//			  System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("error occured:");
			e.printStackTrace();
			System.exit(1);
		}
		return sb.toString();
	}
}
