package team4141.robotvision.msee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class DiscoverDevices implements Runnable{
	//This class should discover cameras and lidars
	//It should call back with the discovered information
	//It should report the discovered information in an easily consumable manner
	//a simple list of cameras and lidars
	private static String keyIdentifyer = ".identifyer";

	DiscoveryHandler handler;
	private HashMap<String,Source> sources;
	private static String DISCOVER_USB_COMMAND="/usr/local/msee/lshw.sh";
	private static String DISCOVER_CAMERAS_COMMAND="/usr/local/msee/v4l2.sh";
	private static String DISCOVER_CAMERA_CONTROLS_COMMAND="/usr/local/msee/v4l2-controls.sh";
	private static String DISCOVER_CAMERA_INFO_COMMAND="/usr/local/msee/v4l2-all.sh";
	
	public DiscoverDevices(DiscoveryHandler handler) {
		this.sources = new HashMap<String,Source>();
		this.handler = handler;
	}

	@Override
	public void run() {
		discoverConfig(); //devices we need are defined in the config
		discoverUSB();  //for lidar
		discoverCameras(); //for cameras
		retrieveConfigSettings();
		for(String key : sources.keySet()){
			if(sources.get(key)!=null){
				System.out.println(sources.get(key));
			}
		}
		handler.onDiscoveryCompleted(sources);
	}

	private void retrieveConfigSettings() {
		for(String key : sources.keySet()){
			if(sources.get(key)!=null){
//				System.out.println("found "+key);
				Source source = sources.get(key);
				String sourcePrefix = null;
				Iterator<String> configKeys = ConfigManager.getConf().getKeys();
				while(configKeys.hasNext()){
					String confKey = configKeys.next();
					if(confKey.startsWith(this.handler.getName()) && confKey.contains(keyIdentifyer) && ConfigManager.get(confKey).equals(key)){
//						System.out.println(confKey+":"+key);
						sourcePrefix = confKey.substring(0,confKey.lastIndexOf(".")+1);
//						System.out.println(sourcePrefix);
					}
				}
				if(sourcePrefix!=null){
					configKeys=ConfigManager.getConf().getKeys();
					while(configKeys.hasNext()){
						String confKey = configKeys.next();
						if(confKey.startsWith(sourcePrefix) && !confKey.contains(keyIdentifyer)){
//							System.out.println(confKey);
							String parameterName = confKey.substring(sourcePrefix.length());
							String parameterValue = ConfigManager.get(confKey);
//							System.out.println(parameterName+" = "+parameterValue);
							if("AR".equals(parameterName)){
								source.add(new Setting(parameterName,parameterValue));
							}
							if("CV".equals(parameterName)){
								source.add(new Setting(parameterName,parameterValue));
							}
							if("save".equals(parameterName)){
								source.add(new Setting(parameterName,Boolean.valueOf(parameterValue)));
							}
							if("filePath".equals(parameterName)){
								source.add(new Setting(parameterName,parameterValue));
							}
						}
					}
				}
			}
			else{
				System.err.println(key +" was not found in system");
			}
		}
	}

	private void discoverConfig() {
		// list devices in config
		Iterator<String> confIterator = ConfigManager.getConf().getKeys();
		while(confIterator.hasNext()){
			String key = confIterator.next();
			//devices have a key that contains the identifyer token
			if(key.contains(keyIdentifyer)){
				sources.put(ConfigManager.get(key), null);
			}
		}

	}

	private void discoverCameras() {
		System.out.println("discovering cameras");
		String result = discover(DISCOVER_CAMERAS_COMMAND);
		List<Source> cameras = parseCamerasInfo(result);
		for(Source dev : cameras){
			result = discover(DISCOVER_CAMERA_INFO_COMMAND,dev.getId());
			parseCameraInfo(dev,result);
			result = discover(DISCOVER_CAMERA_CONTROLS_COMMAND,dev.getId());
			parseCameraControlsInfo(dev,result);
			sources.put(dev.getName(), dev);
		}
	}

	private void parseCameraInfo(Source source, String cameraDetails) {
		if(!(source instanceof VideoDevice)) throw new IllegalArgumentException("source "+source.getId()+" is not a valid VideoDevice");
		VideoDevice dev = (VideoDevice) source;
//		System.out.println("Parsing camera info");
		//This is a long structure with much information
		//we are going to cherry pick a few lines of data
		//most lines are in the form  <parameter name>:<parameter value>
		// we will ignore the rest for now
		String keyWidthHeight="Width/Height  : ";
		String keyPixelFormat="Pixel Format  : ";
		String keyColorspace="Colorspace    : ";
		String keyFramesPerSecond="Frames per second: ";
		Integer width= null;
		Integer height= null;
		String pixelFormat= null;
		String colorspace= null;
		Integer framesPerSecond = null;
		StringTokenizer st = new StringTokenizer(cameraDetails,"\n"); //get each line 1 at a time
		while(st.hasMoreTokens()){
			String line = st.nextToken();
			if(line.contains(keyWidthHeight)){
				String sizeString = line.substring(line.indexOf(keyWidthHeight)+keyWidthHeight.length()).trim();  // results in a String in the form of ####/###  width/height
//				System.out.printf("sizeString:-->%s<--\n",sizeString);
				StringTokenizer st2 = new StringTokenizer(sizeString,"/");
				if(st2.countTokens()==2){
					width = Integer.valueOf(st2.nextToken());
					height = Integer.valueOf(st2.nextToken());
//					System.out.printf("width,height: %d,%d\n",width.intValue(),height.intValue());
				}
			}
			if(line.contains(keyPixelFormat)){
				pixelFormat = line.substring(line.indexOf(keyPixelFormat)+keyPixelFormat.length()).replace("\'", " ").trim();
//				System.out.printf("pixelFormat:-->%s<--\n",pixelFormat);
			}
			if(line.contains(keyColorspace)){
				colorspace = line.substring(line.indexOf(keyColorspace)+keyColorspace.length()).trim();
//				System.out.printf("colorspace:-->%s<--\n",colorspace);
			}
			if(line.contains(keyFramesPerSecond)){
				// example
				//        Frames per second: 15.000 (15/1)
				String value;
				if(line.indexOf(" ",line.indexOf(keyFramesPerSecond)+keyFramesPerSecond.length())>0){
					value = line.substring(line.indexOf(keyFramesPerSecond)+keyFramesPerSecond.length(),line.indexOf(" ",line.indexOf(keyFramesPerSecond)+keyFramesPerSecond.length())); 
				}
				else{
					value = line.substring(line.indexOf(keyFramesPerSecond)+keyFramesPerSecond.length());
				}
				framesPerSecond = Double.valueOf(value).intValue();
//				System.out.printf("fps: %d\n",framesPerSecond.intValue());
			}
			
			if(width!=null){
				dev.setWidth(width);
			}
			if(height!=null){
				dev.setHeight(height);
			}
			if(framesPerSecond!=null){
				dev.setFramesPerSecond(framesPerSecond);
			}
			if(colorspace!=null){
				dev.setColorspace(colorspace);
			}
			if(pixelFormat!=null){
				dev.setPixelFormat(pixelFormat);
			}
		}
	}

	private List<Source> parseCamerasInfo(String camerasText) {
		List<Source> cameras = new ArrayList<Source>();

		// camera list information comes in a 2 line format
		// the first line has the form of <camera name> (<path>) :
		// the second line has the form of       <device id>
		StringTokenizer st = new StringTokenizer(camerasText,"\n"); //get each line 1 at a time
		while(st.hasMoreTokens()){
			String line1 = st.nextToken().trim();
			String line2 = st.nextToken().trim();
			if(line1 == null || line2 == null){
				System.err.printf("Invalid camera definition.  line1  = %s.  line 2 = %s\n",line1,line2);
			}
			String id= null;
			String name= null;
			String path = null;
			if(line2!=null && line2.length()>0){
				id = line2;
			}
//			if(id!=null) System.out.printf("id -->%s<--\n",id);

			if(line1!=null && line1.length()>0){
				if(line1.lastIndexOf(" (")>=0){
					name = line1.substring(0,line1.lastIndexOf(" (")).trim();
				}
			}
//			if(name!=null) System.out.printf("name -->%s<--\n",name);

			if(line1!=null && line1.length()>0){
				if(line1.lastIndexOf(" (")>=0 && line1.indexOf("):",line1.lastIndexOf(" ("))>=0){
					path = line1.substring(line1.lastIndexOf(" (")+" (".length(),line1.indexOf("):",line1.lastIndexOf(" ("))).trim();
				}
			}
//			if(path!=null) System.out.printf("path -->%s<--\n",path);
			if(id!=null && name!=null && path!=null && sources.containsKey(name)){
				VideoDevice dev = new VideoDevice(id,name,path);
				cameras.add(dev);
			}
		}
		return cameras;
	}

	private void discoverUSB() {
		System.out.println("discovering USB");
		String result = discover(DISCOVER_USB_COMMAND);
		parseUSBInfo(result);
	}
	
	private void parseCameraControlsInfo(Source dev,String cameraDetails) {
		// Camera control info is 1 setting per line in the form of
		//      white_balance_temperature (int)    : min=2800 max=6500 step=1 default=57343 value=4600 flags=inactive
		//           power_line_frequency (menu)   : min=0 max=2 default=2 value=2
		// white_balance_temperature_auto (bool)   : default=1 value=1
		//  <setting name> (<setting type)  : [min=<min value>] [max=<max value>] [step=<step value>] default=<default value> value=<current value> [flags=<flags value>] 
		StringTokenizer st = new StringTokenizer(cameraDetails,"\n");
		while(st.hasMoreTokens()){
			String line = st.nextToken();
//			System.out.println(line);
			StringTokenizer st2 = new StringTokenizer(line,":");
			if(st2.countTokens()==2){
				String part1 = st2.nextToken(); // front part which has name and type
				String part2 = st2.nextToken(); // back part which has a bunch of settings
				String name=null;
				String type=null;
				String min=null;
				String max=null;
				String deFault=null;
				String value=null;
				String step=null;
				String flags=null;
				if(part1.contains("(")){
					name = part1.substring(0,part1.indexOf("(")).trim();
					if(part1.indexOf(")",part1.indexOf("("))>=0){
						type = part1.substring(part1.indexOf("(")+"(".length(),part1.indexOf(")",part1.indexOf("(")));
					}
				}
				else name = part1.trim();
				StringTokenizer st3 = new StringTokenizer(part2," "); //break up part 2 in a bunch of tokens that are simple name=value pairs
				while(st3.hasMoreTokens()){
					StringTokenizer st4 = new StringTokenizer(st3.nextToken(),"=");
					if(st4.countTokens()==2){
						String parameterName = st4.nextToken();
						String parameterValue = st4.nextToken();
						
						if("min".equals(parameterName)){
							min = parameterValue;
						}
						if("max".equals(parameterName)){
							max = parameterValue;
						}
						if("default".equals(parameterName)){
							deFault = parameterValue;
						}
						if("value".equals(parameterName)){
							value = parameterValue;
						}
						if("step".equals(parameterName)){
							step = parameterValue;
						}
						if("flags".equals(parameterName)){
							flags = parameterValue;
						}
					}
				}
				
				try{				
					Setting setting = new Setting(name,type,deFault,value);
					if(min!=null) setting.setMin(min);
					if(max!=null) setting.setMax(max);
					if(step!=null) setting.setStep(step);
					if(flags!=null) setting.setFlags(flags);
					dev.add(setting);
				}
				catch(IllegalArgumentException ex)
				{
					System.err.printf("control %s of type %s is not supported.  Ignored\n",name,type);
				}
				
			}
		}
//		System.out.println(cameraDetails);
	}

	private void parseUSBInfo(String usbText) {
		// data is in the format of:
//		Bus info             Device     Class      Description
//		======================================================
//		/0/100/1f.2/0/1      /dev/sda1  volume     40GiB Linux raid autodetect partition
//		/0/100/1f.2/0/2      /dev/sda2  volume     509MiB Linux raid autodetect partition
//		/0/100/1f.2/0/3      /dev/sda3  volume     500GiB Linux raid autodetect partition
//		/0/100/1f.2/0/4      /dev/sda4  volume     200GiB Linux raid autodetect partition

		StringTokenizer lineTokenizer= new StringTokenizer(usbText,"\n"); //get each line 1 at a time
//		String busKey = "Bus ";
//		String deviceKey = "Device ";
//		String IdKey = "ID ";
//		String fieldSeparator=" ";
//		String idSeparator=":";
//		String addressDelimiter=":";
		String devToken="/dev/";
		String keyHWPathField="Bus info";
		String keyDeviceField="Device";
		String keyClassField="Class";
		String keyDescriptionField="Description";
		String hwPath=null;
		String device=null;
		String deviceClass=null;
		String description=null;
			
		List<String> fields = new ArrayList<String>();  // to track the order in which they came in
		List<Integer> fieldPositions = new ArrayList<Integer>();  // to track the start position of each field in the order in which they came in
		
		String line = lineTokenizer.nextToken();  //get the first line
		
		//if line starts with WARNING: disgregard it
		if(line.startsWith("WARNING:")) line = lineTokenizer.nextToken();
		if(line.contains(keyHWPathField)){
			fieldPositions.add(line.indexOf(keyHWPathField));
			fields.add(keyHWPathField);
		}
		if(line.contains(keyDeviceField)){
			fieldPositions.add(line.indexOf(keyDeviceField));
			fields.add(keyDeviceField);
		}
		if(line.contains(keyClassField)){
			fieldPositions.add(line.indexOf(keyClassField));
			fields.add(keyClassField);
		}
		if(line.contains(keyDescriptionField)){
			fieldPositions.add(line.indexOf(keyDescriptionField));
			fields.add(keyDescriptionField);
		}
		line = lineTokenizer.nextToken(); //skip the separator line
		
		while(lineTokenizer.hasMoreTokens()){
			line = lineTokenizer.nextToken();
			for(int i=0;i<fieldPositions.size();i++){
				int start = fieldPositions.get(i);
				String data;
				if(i==fieldPositions.size()-1){
					//on last field
					data=line.substring(start).trim();
				}
				else
				{
					int end = fieldPositions.get(i+1);
					data=line.substring(start,end).trim();
				}
				if(keyHWPathField.equals(fields.get(i))){
					hwPath = data;
				}
				if(keyDeviceField.equals(fields.get(i))){
					device = data;
				}
				if(keyClassField.equals(fields.get(i))){
					deviceClass = data;
				}
				if(keyDescriptionField.equals(fields.get(i))){
					description = data;
				}
				
			}
//			if(device!=null  && device.length()>devToken.length() && device.contains(devToken) && description!=null){
//				//testing
//				System.out.printf("%s|%s|%s|%s\n", hwPath,device,deviceClass,description 	);
//			}								
			if(device!=null  && device.length()>devToken.length() && device.contains(devToken) && description!=null && sources.containsKey(description)){
				//bare minimum we need device and description
				sources.put(description,new USBDevice(device, description));
			}								
		}

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
