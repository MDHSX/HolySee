import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;


public class Cosine extends Recorder {

	public Cosine(Hashtable<String, String> settings) {
		super(settings);
	}

	@Override
	protected void initialize() {
	      System.out.println("COSINE is recording with the following settings");
		  for(String key : settings.keySet()){
				System.out.println("\t"+key+": "+settings.get(key));
		  }
		  if(!settings.containsKey(SETTING_MAX_LENGTH) 
			   || !settings.containsKey(SETTING_WIDTH) 
			   || !settings.containsKey(SETTING_HEIGHT)
			   || !settings.containsKey(SETTING_FRAMERATE)){
			  
			  if(!settings.containsKey(SETTING_MAX_LENGTH)) System.out.println("missing "+SETTING_MAX_LENGTH); 
			  if(!settings.containsKey(SETTING_WIDTH))  System.out.println("missing "+SETTING_WIDTH);
			  if(!settings.containsKey(SETTING_HEIGHT)) System.out.println("missing "+SETTING_HEIGHT);
			  if(!settings.containsKey(SETTING_FRAMERATE)) System.out.println("missing "+SETTING_FRAMERATE);

			  throw new IllegalArgumentException("Missing attributes");
		  }
  		  writer = new VideoWriter();

		  grabPool = Executors.newScheduledThreadPool(4);

	}

	private Mat mat;

	@Override
	public void record() {
		isRecording = true;
		int width = Integer.valueOf( settings.get(SETTING_WIDTH) );
		int height = Integer.valueOf( settings.get(SETTING_HEIGHT) );

		System.out.println("camera resolution = "+width+" x "+height);

		double fps = Double.valueOf(settings.get(SETTING_FRAMERATE));
		writer.open(settings.get(SETTING_FILENAME),  VideoWriter.fourcc('D', 'I', 'V', 'X'), fps, new Size(width,height), true);

		long period = (long)(1000.0 / fps); 
		
		
		if(!writer.isOpened()){
			throw new UnsupportedOperationException("Unable to open VideoWriter");
		}
		isRecording = true;
		long end = Long.valueOf(settings.get(SETTING_MAX_LENGTH)) * 1000; 
		System.out.println("scheduling termination to be in "+end+" ms");
	    mat = generateCosine(width,height);
//	      System.out.println( mat.dump() );

		//create a pool of executors to do frame grabbing
		grabPool.scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run() {
				long grabStart = new Date().getTime();
//				System.out.println("start = "+grabStart);
		        if(!writer.isOpened()){throw new UnsupportedOperationException("Unable to open VideoWriter");}
		      	writer.write(shift());
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
	

	private static Mat generateCosine(int width, int height) {
		Mat cosine= new Mat(height,width, CvType.CV_8UC3, new Scalar(255,255,255));
		
		for(int c=0; c<cosine.cols();c++){
			  double rad = 2*Math.PI*c/cosine.cols();
			  double v = Math.cos(rad);
			  int min = 0;
			  double minVal = 10.0;
			  for(int r=0;r<cosine.rows();r++){
			  	double h = (((cosine.rows()-1)/2.0)-r) / ((cosine.rows()-1)/2.0);
			  	double diff = Math.pow((v-h),2);
			  	if(diff < minVal){
			  		minVal = diff;
			  		min = r;
			  	}
			  }
//			  System.out.println("(c, cos(c))=("+c+","+v+")"+"@"+min)
				cosine.put(min,c,0,0,0);
		}		
		System.out.println();
		return cosine;
	}
	private synchronized Mat shift() {
//		System.out.println(mat.col(0).dump());
		Mat part1 = mat.col(0);
		Mat part2 = mat.colRange(1,mat.cols());
		Mat newMat = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC3);
		part2.copyTo(newMat.colRange(0, newMat.cols()-1));
		part1.copyTo(newMat.col(newMat.cols()-1));
		mat = newMat;
//		System.out.println(mat.dump());
		return mat;
	}

}
