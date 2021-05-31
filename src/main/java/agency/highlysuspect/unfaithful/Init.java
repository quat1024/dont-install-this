package agency.highlysuspect.unfaithful;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ClientModInitializer {
	public static final String MODID = "unfaithful";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	public static Config config;
	
	@Override
	public void onInitializeClient() {
		config = new Config();
	}
}
