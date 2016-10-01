import java.util.Date;
import java.util.Hashtable;

public class Main {
	public static void main (String args[]) throws InterruptedException{
		Hashtable<String, String> snapshotSettings = new Hashtable<String,String>();
		snapshotSettings.put(Recorder.SETTING_FILENAME, "eye.jpg");

		Hashtable<String, String> cosineSettings = new Hashtable<String,String>();
		cosineSettings.put(Recorder.SETTING_FILENAME, "cosine.avi");
		cosineSettings.put(Recorder.SETTING_WIDTH, "640");
		cosineSettings.put(Recorder.SETTING_HEIGHT, "480");
		cosineSettings.put(Recorder.SETTING_FRAMERATE, "25");
		cosineSettings.put(Recorder.SETTING_MAX_LENGTH, "6");

		Hashtable<String, String> recordSettings = new Hashtable<String,String>();
		recordSettings.put(Recorder.SETTING_FILENAME, "video.avi");
		recordSettings.put(Recorder.SETTING_FRAMERATE, "25");
		recordSettings.put(Recorder.SETTING_MAX_LENGTH, "6");

		Recorder recorder = new Recorder(recordSettings);
//		Recorder recorder = new Cosine(cosineSettings);
//		Recorder recorder = new Snapshot(snapshotSettings);

		recorder.record();
		
		long start = new Date().getTime();
		
		while(recorder.isRecording()){
			Thread.sleep(100);
		}
		
		long end = new Date().getTime();
		recorder.close();
		System.out.println("shutting down ... execution time = "+(end-start));
		System.exit(0);
	}

}
