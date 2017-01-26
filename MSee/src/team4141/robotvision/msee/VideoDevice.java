package team4141.robotvision.msee;

public class VideoDevice extends Source {

	private String busInfo;
	private Integer width;
	private Integer height;
	private Integer framesPerSecond;
	private String colorspace;
	private String pixelFormat;
	private String id;
	
	public VideoDevice(String id, String cardType, String busInfo) {
		super(cardType);
		this.busInfo = busInfo;
		this.id=id;
	}
	public String getBusInfo() {
		return busInfo;
	}
	public void setWidth(Integer width) {
		this.width= width;
	}
	public void setHeight(Integer height) {
		this.height= height;
	}
	public void setFramesPerSecond(Integer framesPerSecond) {
		this.framesPerSecond= framesPerSecond;
	}
	public void setColorspace(String colorspace) {
		this.colorspace= colorspace;
	}
	public void setPixelFormat(String pixelFormat) {
		this.pixelFormat= pixelFormat;
	}
	public Integer getWidth() {
		return width;
	}
	public Integer getHeight() {
		return height;
	}
	public Integer getFramesPerSecond() {
		return framesPerSecond;
	}
	public String getColorspace() {
		return colorspace;
	}
	public String getPixelFormat() {
		return pixelFormat;
	}

	@Override
	public String getId() {
		return id;
	}	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if(getId()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"id\":\"");
			sb.append(getId());
			sb.append("\"");
		}
		if(getName()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"name\":\"");
			sb.append(getName());
			sb.append("\"");
		}
		if(getBusInfo()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"busInfo\":\"");
			sb.append(getBusInfo());
			sb.append("\"");
		}
		if(getWidth()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"width\":");
			sb.append(getWidth());
		}
		if(getHeight()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"height\":");
			sb.append(getHeight());
		}
		if(getFramesPerSecond()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"framesPerSecond\":");
			sb.append(getFramesPerSecond());
		}
		if(getColorspace()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"colorspace\":\"");
			sb.append(getColorspace());
			sb.append("\"");
		}
		if(getPixelFormat()!=null){
			if(sb.length()>1) sb.append(",");
			sb.append("\"pixelFormat\":\"");
			sb.append(getPixelFormat());
			sb.append("\"");
		}
		
		if(settings!=null && settings.size()>0){
			if(sb.length()>1) sb.append(",");
			sb.append("\"settings\":[");
			boolean isFirst = true;
			for(String settingName : settings.keySet()){
				if(isFirst) isFirst = false;
				else sb.append(",");
				sb.append(settings.get(settingName).toString());
			}
			sb.append("]");
			
		}
		sb.append("}");
		
		return sb.toString();
	}
}
