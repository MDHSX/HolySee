package org.usfirst.frc.team4141.MDRobotBase.config;

public class StringConfigSetting implements ConfigSetting {
	private String value;
	private String min;
	private String max;
	private Type type;
	private String name;
	
	public StringConfigSetting(String value){
		this(value,null,null);
	}	
	
	public StringConfigSetting(String value, String min, String max){
		this.value = value;
		this.min= min;
		this.max = max;
		this.type = Type.string;	
	}

	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public Object getMin() {
		return min;
	}

	public Object getMax() {
		return max;
	}

	public void setMin(Object min) {
		this.min = min.toString();
	}

	public void setMax(Object max) {
		this.max = max.toString();
	}

	public void setValue(Object value) {
		this.value = (String)value;
	}


	public int getInt() {
		throw new UnsupportedOperationException("function getInt() on a StringConfigItem is not implemented.");
	}

	public double getDouble() {
		throw new UnsupportedOperationException("function getDouble() on a StringConfigItem is not implemented.");
	}

	public String getString() {
		return value;
	}

	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"name\":\"");
		sb.append(getName());
		sb.append("\", \"type\":\"");
		sb.append(getType().toString());
		sb.append("\", \"value\":\"");
		sb.append(getValue().toString());
		if(this.min!=null){
			sb.append("\", \"min\":\"");
			sb.append(getMin().toString());
		}
		if(this.max!=null){
			sb.append("\", \"max\":\"");
			sb.append(getMax().toString());
		}
		sb.append("\"}");
		return sb.toString();
	}


	public boolean getBoolean() {
		return Boolean.parseBoolean(value);
	}
}
