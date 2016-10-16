package org.usfirst.frc.team4141.MDRobotBase.config;


public class BooleanConfigSetting implements ConfigSetting {
	private Boolean min;
	private Boolean max;
	private Boolean value;
	private Type type;
	private String name;
	
	
	public BooleanConfigSetting(Boolean value){
		this.min = false;
		this.max = true;
		this.value = value;
		this.type = Type.binary;	
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
	}

	public void setMax(Object max) {
	}

	public void setValue(Object value) {
		if(value instanceof Boolean){
			this.value = (Boolean)value;
//			System.out.printf("setting %s to %s\n",name,value.toString());
		}
		else if (value instanceof Integer){
			this.value = (((Integer)value).intValue()==0?false:true);
		}
		else if (value instanceof Double){
			this.value = (((Double)value).doubleValue()==0.0?false:true);
		}
		else if (value instanceof String){
			this.value = Boolean.valueOf((String)value);
		}

	}


	public int getInt() {
		return (value.booleanValue()?1:0);
	}

	public double getDouble() {
		return (value.booleanValue()?1.0:0.0);
	}

	public String getString() {
		return value.toString();
	}

	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"");
		sb.append(getName());
		sb.append("\", \"type\":\"");
		sb.append(getType().toString());
		sb.append("\", \"value\":");
		sb.append(getValue().toString());
		sb.append("}");
		return sb.toString();
	}

	public boolean getBoolean() {
		return value;
	}
}

