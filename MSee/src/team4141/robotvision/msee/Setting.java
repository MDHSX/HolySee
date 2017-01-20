package team4141.MSee;

public class Setting {

	private Integer id;
	private String name;
	private String value;

	public Setting(Integer settingId, String settingName, String settingValue) {
		this.id = settingId;
		this.name = settingName;
		this.value = settingValue; 
	}
	public Setting(String settingName, String settingValue) {
		this.name = settingName;
		this.value = settingValue; 
	}
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
