package agency.highlysuspect.dontinstallthis;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Init implements ClientModInitializer {
	public static final String MODID = "dont-install-this";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	@Override
	public void onInitializeClient() {
	}
}
