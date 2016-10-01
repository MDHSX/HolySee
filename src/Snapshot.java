import java.util.Date;
import java.util.Hashtable;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Snapshot extends Recorder {

	public Snapshot(Hashtable<String, String> settings) {
		super(settings);		
	}

	@Override
	protected void initialize() {
		  if(!settings.containsKey(SETTING_FILENAME)){
				  if(!settings.containsKey(SETTING_FILENAME)) System.out.println("missing "+SETTING_FILENAME);
				  throw new IllegalArgumentException("Missing attributes");
		  }
		  cap = new VideoCapture(0);
			if(!cap.isOpened()){
				throw new UnsupportedOperationException("Unable to open VideoCapture");
			}
	}

	@Override
	public void record() {
		if(!cap.isOpened()) throw new UnsupportedOperationException("VideoCapture unexpectedly closed");
		isRecording = true;

		long grabStart = new Date().getTime();
		Mat mat = new Mat();
		boolean snapTaken = cap.read(mat);
		if(snapTaken){
			System.out.println("pitcure taken");
			MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 80);
			Imgcodecs.imwrite(settings.get(SETTING_FILENAME), mat,params);
		}
		long grabEnd =  new Date().getTime();
		System.out.println("grab completed in "+(grabEnd-grabStart)+" ms");

		stop();
	}
}
