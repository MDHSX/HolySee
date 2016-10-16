package org.usfirst.frc.team4141.MDRobotBase.config;

public class IntegerConfigSetting implements ConfigSetting {
	private Integer min;
	private Integer max;
	private Integer value;
	private Type type;
	private String name;
	
	public IntegerConfigSetting(Integer min, Integer max, Integer value){
		this.min = min;
		this.max = max;
		this.value = value;
		this.type = Type.integer;		
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
		if(min instanceof Integer){
			this.min = (Integer)min;
		}
		else if (min instanceof Double){
			this.min = new Integer((int)((Double)min).doubleValue());
		}
		else if (min instanceof String){
			this.min = Integer.valueOf((String)min);
		}
		else if (min instanceof Boolean){
			this.min = (((Boolean)min).booleanValue()?1:0);
		}
	}

	public void setMax(Object max) {
		if(max instanceof Integer){
			this.max = (Integer)max;
		}
		else if (max instanceof Double){
			this.max = new Integer((int)((Double)max).doubleValue());
		}
		else if (max instanceof String){
			this.max = Integer.valueOf((String)max);
		}
		else if (max instanceof Boolean){
			this.max = (((Boolean)max).booleanValue()?1:0);
		}
	}

	public void setValue(Object value) {
		if(value instanceof Integer){
			this.value = (Integer)value;
//			System.out.printf("setting %s to %d\n",name,value);
		}
		else if (value instanceof Double){
			this.value = new Integer((int)((Double)value).doubleValue());
		}
		else if (value instanceof String){
			this.value = Integer.valueOf((String)value);
		}
		else if (value instanceof Boolean){
			this.value = (((Boolean)value).booleanValue()?1:0);
		}
	}


	public int getInt() {
		return value.intValue();
	}

	public double getDouble() {
		return value.intValue();
	}

	public String getString() {
		return value.toString();
	}


	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"name\":\"");
		sb.append(getName());
		sb.append("\", \"type\":\"");
		sb.append(getType().toString());
		sb.append("\", \"value\":");
		sb.append(getValue().toString());
		sb.append(", \"min\":");
		sb.append(getMin().toString());
		sb.append(", \"max\":");
		sb.append(getMax().toString());
		sb.append("}");
		return sb.toString();
	}

	public boolean getBoolean() {
		if (value == 0) return false;
		return true;
	}
}
