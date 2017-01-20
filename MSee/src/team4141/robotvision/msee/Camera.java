package team4141.MSee;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.usfirst.frc.team4141.MDRobotBase.config.ConfigManager;



public class Camera extends Source{

	//the following constants were taken from OpenCV videoio
	private static final String CAP_PROP_FRAME_WIDTH="CAP_PROP_FRAME_WIDTH";
	private static final String CAP_PROP_FRAME_HEIGHT="CAP_PROP_FRAME_HEIGHT";
	private static final String CAP_PROP_FOURCC="CAP_PROP_FOURCC";
	private static final String CAP_PROP_FPS="CAP_PROP_FPS";
	private String id;
	private Hashtable<String,Setting> settings;


	public Camera(String id,Hashtable<String,Setting> settings) {
		this.id = id;
		this.settings = settings;
	}
	
	public Camera(Map cameraMap) {
		// a cameraMap consists of an ID and a parameters array
		for(Object key : cameraMap.keySet()){
			if("id".equals(key)) {
				this.id = (String)cameraMap.get(key);
			}
			if("properties".equals(key)) {
				//properties are arrays of Map objects, each map object is a property
				//each camera object need to have a dictionary for its camera properties 
				settings = new Hashtable<String,Setting>();
				ArrayList properties = (ArrayList)(cameraMap.get(key));
				for(Object property : properties){
					Integer settingId = null;
					String settingName = null;
					String settingValue = null;
					Map propertyMap = (Map) property;
					for(Object settingKey: propertyMap.keySet())
					{
						if("id".equals(settingKey)) settingId = ((Double)(propertyMap.get(settingKey))).intValue();
						if("name".equals(settingKey)) settingName = (String)propertyMap.get(settingKey);
						if("value".equals(settingKey)) settingValue = String.valueOf(propertyMap.get(settingKey));
					}
					if(settingId==null || settingName==null || settingValue==null) throw new IllegalArgumentException("invalid camera setting");
					settings.put(settingName, new Setting(settingId,settingName,settingValue));
					//each should be a property
					//a property should have an id, name and value
					
				}
			}
		}
		if(id==null || settings==null) throw new IllegalArgumentException("invalid camera configuration");
		
		//TODO:  calculate identification string in order to match with identifier in properties file
		

		
//		System.out.println(this);
	}
	
	
	public String match() {
		String identifier = getId();
		if(identifier==null || identifier.length()<1) return null;
		String deviceName=null;
		Iterator<String> configFilePropertyKeys = ConfigManager.getConf().getKeys();
		while(configFilePropertyKeys.hasNext()){
			String key = configFilePropertyKeys.next();
			if(key.endsWith(".identifyer") && identifier.equals(ConfigManager.getConf().getString(key))){
				StringTokenizer t = new StringTokenizer(key,".");
				int i=0;
				while(t.hasMoreElements()){
					String temp = t.nextToken();
					i++;
					if(i==2) {
						deviceName =temp; 
						break; 
					}
				}
			}
		}
		return deviceName;
	}

	public String getId(){return id;}
	public int getWidth(){return Double.valueOf(settings.get(CAP_PROP_FRAME_WIDTH).getValue()).intValue();}
	public int getHeight(){return Double.valueOf(settings.get(CAP_PROP_FRAME_HEIGHT).getValue()).intValue();}
	public String getEncoding(){return settings.get(CAP_PROP_FOURCC).getValue();}
	public double getFramesPerSecond(){return Double.valueOf(settings.get(CAP_PROP_FPS).getValue());}

	public String get(String settingId){return settings.get(settingId).getValue();}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		if(id!=null){
			if(sb.length()>1){
				sb.append(',');
			}
			sb.append("\"id\":\"");
			sb.append(id);
			sb.append("\"");
		}
		if(settings!=null){
			if(settings.containsKey(CAP_PROP_FRAME_WIDTH)){
				if(sb.length()>1){
					sb.append(',');
				}
				sb.append("\"width\":");
				sb.append(getWidth());
			}
			if(settings.containsKey(CAP_PROP_FRAME_HEIGHT)){
				if(sb.length()>1){
					sb.append(',');
				}
				sb.append("\"height\":");
				sb.append(getHeight());
			}
			if(settings.containsKey(CAP_PROP_FPS)){
				if(sb.length()>1){
					sb.append(',');
				}
				sb.append("\"framesPerSecond\":");
				sb.append(getFramesPerSecond());
			}
			if(settings.containsKey(CAP_PROP_FOURCC)){
				if(sb.length()>1){
					sb.append(',');
				}
				sb.append("\"encoding\":\"");
				sb.append(getEncoding());
				sb.append("\"");
			}
			if(settings.size()>0){
				if(sb.length()>1){
					sb.append(',');
				}
				sb.append("\"settings\":[");
				boolean first = true;
				for(String key : settings.keySet()){
					if(first) first = false;
					else sb.append(',');
					sb.append("{\"");
					sb.append(key);
					sb.append("\":\"");
					sb.append(settings.get(key).getValue());
					sb.append("\"}");
				}
				sb.append("]");
			}
		}
		sb.append('}');
		return sb.toString();
	}

	public void add(Setting setting) {
		if(setting!=null && settings!=null) settings.put(setting.getName(), setting);
		
	}

	public String getName() {
		if(settings==null || !settings.containsKey("name")) return getId();
		return settings.get("name").getValue();
	}


	

}
