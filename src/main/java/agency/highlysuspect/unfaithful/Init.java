package agency.highlysuspect.unfaithful;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Init implements ClientModInitializer {
	public static final String MODID = "unfaithful";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	public static Config config;
	
	@Override
	public void onInitializeClient() {
		try {
			config = new Config(FabricLoader.getInstance().getConfigDir().resolve(MODID + ".cfg"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
