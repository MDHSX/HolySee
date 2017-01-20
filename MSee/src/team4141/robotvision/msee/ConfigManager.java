package team4141.MSee;

import java.net.URL;


import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


public class ConfigManager {
	public static String FILENAME="/conf.xml";
	//Helper class to manage the persistence and retrieval of config settings to disk 
	// using the Preferences feature of the RoboRio
	private static FileBasedConfiguration conf;
	
	public static FileBasedConfiguration getConf(){
		if(conf == null){
			try {
				Parameters params = new Parameters();

				URL url = ConfigManager.class.getClassLoader().getResource("conf.properties");
				FileBasedBuilderParameters parameters = params.fileBased().setURL(url);
				FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
				builder.configure(parameters);
				conf = builder.getConfiguration();
			} catch (ConfigurationException e) {
				System.err.println("ERROR - configuration not found");
				System.exit(1);
			}
		}
		return conf;
	}
	
	public static String get(String key){
		return getConf().getString(key);
	}

	public static boolean contains(String key) {
		return getConf().containsKey(key);
	}
}
