import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Streamer {
	  
		public static  String SETTING_FRAMERATE="frameRate";
		public static  String SETTING_MAX_LENGTH="maxLength";

		protected Hashtable<String, String> settings;
		protected boolean isStreaming;

		public Streamer(Hashtable<String,String> settings) {
			this.setSettings(settings);
			this.isStreaming = false;
			initialize();
		}

		public Hashtable<String, String> getSettings() {
			return settings;
		}

		private void setSettings(Hashtable<String, String> settings) {
			this.settings = settings;
		}
		
		public boolean isStreaming(){return isStreaming;}
		
		protected synchronized void stop() {
			System.out.println("stop streaming");
			isStreaming = false;
			if(grabPool!=null){
				grabPool.shutdown();
			}
			if(grabPool!=null && !grabPool.isShutdown() && !grabPool.isTerminated()){
				while(!grabPool.isShutdown() || !grabPool.isTerminated()){
					System.out.println("waiting for grapPool to shutdown");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						
					}
				}
			}
		}
		
		private long frameCount = 0;
		private synchronized long getFrameCount(){return frameCount;}

		protected ScheduledExecutorService grabPool;
		protected VideoCapture cap;

		private String[] cameraPropertyNames = {
				"Videoio.CAP_PROP_GUID",
				"Videoio.CAP_PROP_SETTINGS",
				"Videoio.CV_CAP_PROP_SETTINGS",
				"Videoio.CAP_PROP_FOURCC",
				"Videoio.CV_CAP_PROP_FRAME_WIDTH",
				"Videoio.CV_CAP_PROP_FRAME_HEIGHT",
				"Videoio.CAP_PROP_FRAME_WIDTH",
				"Videoio.CAP_PROP_FRAME_HEIGHT",
				"Videoio.CAP_PROP_GAIN",
				"Videoio.CAP_PROP_GAMMA",
				"Videoio.CAP_PROP_SATURATION",
				"Videoio.CAP_PROP_SHARPNESS",
				"Videoio.CAP_PROP_FPS",
				"Videoio.CAP_PROP_POS_FRAMES",
				"Videoio.CAP_PROP_POS_MSEC",
				"Videoio.CV_CAP_PROP_APERTURE",
				"Videoio.CV_CAP_PROP_AUTOFOCUS",
				"Videoio.CV_CAP_PROP_AUTOGRAB",
				"Videoio.CV_CAP_PROP_BACKLIGHT",
				"Videoio.CV_CAP_PROP_BUFFERSIZE",
				"Videoio.CV_CAP_PROP_FOCUS",
				"Videoio.CV_CAP_PROP_IRIS",
				"Videoio.CV_CAP_PROP_ISO_SPEED"					
		};
		
		int[] cameraProperties = {
				Videoio.CAP_PROP_GUID,
				Videoio.CAP_PROP_SETTINGS,
				Videoio.CV_CAP_PROP_SETTINGS,
				Videoio.CAP_PROP_FOURCC,
				Videoio.CV_CAP_PROP_FRAME_WIDTH,
				Videoio.CV_CAP_PROP_FRAME_HEIGHT,
				Videoio.CAP_PROP_FRAME_WIDTH,
				Videoio.CAP_PROP_FRAME_HEIGHT,
				Videoio.CAP_PROP_GAIN,
				Videoio.CAP_PROP_GAMMA,
				Videoio.CAP_PROP_SATURATION,
				Videoio.CAP_PROP_SHARPNESS,
				Videoio.CAP_PROP_FPS,
				Videoio.CAP_PROP_POS_FRAMES,
				Videoio.CAP_PROP_POS_MSEC,
				Videoio.CV_CAP_PROP_APERTURE,
				Videoio.CV_CAP_PROP_AUTOFOCUS,
				Videoio.CV_CAP_PROP_AUTOGRAB,
				Videoio.CV_CAP_PROP_BACKLIGHT,
				Videoio.CV_CAP_PROP_BUFFERSIZE,
				Videoio.CV_CAP_PROP_FOCUS,
				Videoio.CV_CAP_PROP_IRIS,
				Videoio.CV_CAP_PROP_ISO_SPEED					
		};	
		

		protected void initialize(){
			System.out.println("streaming with the following settings");
			for(String key : settings.keySet()){
				System.out.println("\t"+key+": "+settings.get(key));
			}
			if( !settings.containsKey(SETTING_FRAMERATE) 
					|| !settings.containsKey(SETTING_MAX_LENGTH)){
				if(!settings.containsKey(SETTING_FRAMERATE)) System.out.println("missing "+SETTING_FRAMERATE);
				if(!settings.containsKey(SETTING_MAX_LENGTH)) System.out.println("missing "+SETTING_MAX_LENGTH);
				throw new IllegalArgumentException("Missing attributes");
			}
			
			cap = new VideoCapture(0);
			if(!cap.isOpened()){
				throw new UnsupportedOperationException("Unable to open VideoCapture");
			}

			for(int i=0;i<cameraProperties.length;i++){
				double val = cap.get(cameraProperties[i]);
				if(val>=0){
					if(cameraProperties[i] == Videoio.CAP_PROP_FOURCC){
						System.out.println(cameraPropertyNames[i]+" = "+Recorder.decodeFourCC((int)val));
					}
					else System.out.println(cameraPropertyNames[i]+" = "+val);
				}
			}
			
	   		grabPool = Executors.newScheduledThreadPool(4);
		}
		
		public void stream() {
			if(!cap.isOpened()) throw new UnsupportedOperationException("VideoCapture unexpectedly closed");
			int width = (int) cap.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
			int height = (int) cap.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
			System.out.println("camera resolution = "+width+" x "+height);

			double fps = Double.valueOf(settings.get(SETTING_FRAMERATE)); 
			System.out.println("fps = "+fps);
			long period = (long)(1000.0/Double.valueOf(settings.get(SETTING_FRAMERATE))); 
			System.out.println("period = "+period);

			
			isStreaming = true;

			long end = Long.valueOf(settings.get(SETTING_MAX_LENGTH)) * 1000; 
			System.out.println("scheduling termination to be in "+end+" ms");

			//create a pool of executors to do frame grabbing
			grabPool.scheduleAtFixedRate(new Runnable(){
				
				@Override
				public void run() {
					//this code is executed in its own thread and will happen concurrent to any
					//currently executing other instances of this executor
					long grabStart = new Date().getTime();
//					System.out.println("start = "+grabStart);
			        if(!cap.isOpened()){throw new UnsupportedOperationException("Unable to open VideoWriter"); }
			        Mat mat = new Mat();
			        cap.read(mat);
			        
//			        describe(mat);
			        
					long grabEnd =  new Date().getTime();
					System.out.println("grab completed in "+(grabEnd-grabStart)+" ms");
				}}, 10, period, TimeUnit.MILLISECONDS);
			
			//schedule a timer to stop once we've gotten to the max length
			Timer timer = new Timer(true);
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					stop();
				}
				}, end);
		}
		static String decodeFourCC(int ex){
			char EXT[] = {(char)(ex & 0XFF) , (char)((ex & 0XFF00) >> 8),(char)((ex & 0XFF0000) >> 16),(char)((ex & 0XFF000000) >> 24), 0};
			return new String(EXT);
		}

		public void close() {
			if(cap!=null && cap.isOpened()){
				cap.release();
				System.out.println("video capture released");
			}
		}	
}
