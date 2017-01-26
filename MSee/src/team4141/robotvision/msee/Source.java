package team4141.robotvision.msee;

import java.util.Hashtable;

public abstract class Source {
	public abstract String getId();
	
	protected Hashtable<String,Setting> settings;
	protected String name;
	
	public Source(String name){
		this.name = name;
		this.settings = new Hashtable<String,Setting>();
	}
	@Override
	public String toString() {
		return getId();
	}
	public void add(Setting setting) {
		settings.put(setting.getName(), setting);
	}
	public Setting get(String settingName) {
		if(!settings.containsKey(settingName)) return null;
		return settings.get(settingName);
	}
	public String getName(){
		return name;
	}
}
