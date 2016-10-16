package team4141.robotvision.msee;

import java.util.Hashtable;

import org.usfirst.frc.team4141.MDRobotBase.config.ConfigSetting;
import org.usfirst.frc.team4141.MDRobotBase.eventmanager.Notification;

public class ConfigurationNotification extends Notification {	
	
	private Server server;

	public ConfigurationNotification(Server server, boolean showJavaConsole, boolean broadcast,
			boolean record, boolean display) {
		super("ConfigurationNotification", showJavaConsole, broadcast, record, display);
		this.server = server;
	}

	public ConfigurationNotification(Server server) {
		this(server,true,true,false,true);
	}

	public Server getServer(){return server;}
	
	@Override
	protected void addJSONPayload() {
		if(getServer().getSettings()!=null && getServer().getSettings().size()>0){
			if(sb.length()>0){
				sb.append(", ");
			}
			appendSettings(getServer().getSettings());
		}
	}
	
	private void appendSettings(Hashtable<String, ConfigSetting> settings){
		boolean first = true;
		sb.append("\"settings\":[");
		for(String settingName : settings.keySet()){
			if(first) first = false;
			else sb.append(',');
			sb.append(settings.get(settingName).toJSON());
		}
		sb.append("]");
	}

	@Override
	public Notification parse(String notification) {
		// TODO Auto-generated method stub
		return null;
	}

}
