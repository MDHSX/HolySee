package team4141.robotvision.msee;

public class Setting {
	
	//the following types are defined in the V4L2  videodev2.h
	//V4L2_CTRL_TYPE_INTEGER	     = 1,
	//V4L2_CTRL_TYPE_BOOLEAN	     = 2,
	//V4L2_CTRL_TYPE_MENU	     = 3,
	//V4L2_CTRL_TYPE_BUTTON	     = 4,
	//V4L2_CTRL_TYPE_INTEGER64     = 5,
	//V4L2_CTRL_TYPE_CTRL_CLASS    = 6,
	//V4L2_CTRL_TYPE_STRING        = 7,
	//V4L2_CTRL_TYPE_BITMASK       = 8,
	
	//for now, plan on supporting just a small subset.  Ignore the others
	
	//TODO:  add support for menu
	
	public enum Type{
		bool,
		integer,
		string
	}

	private String name;
	private Object value;       //using Object so any type can be held
	private Object deFault;
	private Object min;
	private Object max;
	private Object step;
	private String flags;
	private Type type;


	public Setting(String name, Type type, Object deFault, Object value) {
	// main cosntructor.  All other constructors shoudl result in this constructor being called
		this.name = name;
		this.type = type;
		this.deFault = deFault;
		this.value= value;
	}

	public Setting(String name, String type, String deFault, String value) {
		this(name,calcType(type),calcValue(calcType(type),deFault),calcValue(calcType(type),value));
	}
	

	public Setting(String name, Integer value) {
		//simplest constructor
		//assume default is same as value
		this(name, value, value);
	}
	
	public Setting(String name, Integer value, Integer deFault) {
		this(name,Type.integer,value,deFault);
	}
	
	public Setting(String name, Boolean value) {
		//simplest constructor
		//assume default is same as value
		this(name, value, value);
	}
	
	public Setting(String name, Boolean value, Boolean deFault) {
		this(name,Type.bool,value,deFault);
	}
	
	public Setting(String name, String value) {
		//simplest constructor
		//assume default is same as value
		this(name, value, value);
	}
	
	public Setting(String name, String value, String deFault) {
		this(name,Type.string,value,deFault);
	}

	private static Object calcValue(Type type, String value) {
		switch(type){
		case bool: return Boolean.valueOf(value);
		case integer: return Integer.valueOf(value);
		default: return value;
		}
	}

	private static Type calcType(String type) {
		if("int".equals(type))type="integer"; //translate because java doesn't allow us to use int as an enum option
		return Type.valueOf(type);
	}


	public Object getValue(){
		return value;
	}

	public Integer getIntValue(){
		if(value instanceof Integer) return (Integer) value;
		else return null;
	}
	
	public Boolean getBooleanValue(){
		if(value instanceof Boolean) return (Boolean) value;
		else return null;
	}
	
	public String getStringValue(){
		if(value instanceof String) return (String) value;
		else return null;
	}

	public String getName(){return name;}
	public Object getMin(){return min;}
	public Object getMax(){return max;}
	public Object getStep(){return step;}
	public Object getDefault(){return deFault;}
	public Type getType(){return type;}
	public String getFlags(){return flags;}

	public void setValue(String value){this.value = value;}
	public void setName(String name){this.name = name;}
	public void setMin(Object min){
		switch(type){
		case integer:
			this.min = Integer.valueOf((String)min);
			break;
		case bool:
			this.min = Boolean.valueOf((String)min);
			break;
		default:
			this.min = min;
		}
	}
	public void setMax(Object max){
		switch(type){
		case integer:
			this.max = Integer.valueOf((String)max);
			break;
		case bool:
			this.max = Boolean.valueOf((String)max);
			break;
		default:
			this.max = max;
		}
	}
	public void setStep(Object step){
		switch(type){
		case integer:
			this.step = Integer.valueOf((String)step);
			break;
		case bool:
			this.step = Boolean.valueOf((String)step);
			break;
		default:
			this.step = step;
		}
	}
	public void setDefault(Object deFault){
		switch(type){
		case integer:
			this.deFault = Integer.valueOf((String)deFault);
			break;
		case bool:
			this.deFault = Boolean.valueOf((String)deFault);
			break;
		default:
			this.deFault = deFault;
		}
	}
	public void setType(Type type){this.type = type;}
	public void setFlags(String flags){this.flags = flags;}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if(getName()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"name\":\"");
			sb.append(name);
			sb.append("\"");
		}
		if(getType()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"type\":\"");
			sb.append(type.toString());
			sb.append("\"");
		}
		
		if(getValue()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"value\":");
			switch(type){
			case string:
				if(getValue() instanceof String){
					sb.append("\"");
					sb.append(getValue());
					sb.append("\"");
				}
				break;
			case integer:
				if(getValue() instanceof Integer) sb.append(getValue());
				break;
			case bool:
				if(getValue() instanceof Boolean) sb.append(getValue());
				break;
			}
		}
		
		if(getDefault()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"default\":");
			switch(type){
			case string:
				if(getDefault() instanceof String){
					sb.append("\"");
					sb.append(getDefault());
					sb.append("\"");
				}
				break;
			case integer:
				if(getDefault() instanceof Integer) sb.append(getDefault());
				break;
			case bool:
				if(getDefault() instanceof Boolean) sb.append(getDefault());
				break;
			}
		}
		
		if(getMin()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"min\":");
			switch(type){
			case string:
				if(getMin() instanceof String){
					sb.append("\"");
					sb.append(getMin());
					sb.append("\"");
				}
				break;
			case integer:
				if(getMin() instanceof Integer) sb.append(getMin());
				break;
			case bool:
				if(getMin() instanceof Boolean) sb.append(getMin());
				break;
			}
		}

		if(getMax()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"max\":");
			switch(type){
			case string:
				if(getMax() instanceof String){
					sb.append("\"");
					sb.append(getMax());
					sb.append("\"");
				}
				break;
			case integer:
				if(getMax() instanceof Integer) sb.append(getMax());
				break;
			case bool:
				if(getMax() instanceof Boolean) sb.append(getMax());
				break;
			}
		}

		if(getStep()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"step\":");
			switch(type){
			case string:
				if(getStep() instanceof String){
					sb.append("\"");
					sb.append(getStep());
					sb.append("\"");
				}
				break;
			case integer:
				if(getStep() instanceof Integer) sb.append(getStep());
				break;
			case bool:
				if(getStep() instanceof Boolean) sb.append(getStep());
				break;
			}
		}
		
		if(getFlags()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"flags\":\"");
			sb.append(flags);
			sb.append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
}
