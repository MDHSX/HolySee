package team4141.MSee;

import java.util.HashMap;

public interface DiscoveryHandler {

	String getName();
	void onDiscoveryCompleted(HashMap<String,Source> sources);

}
