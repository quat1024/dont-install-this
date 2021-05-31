package agency.highlysuspect.unfaithful;

import com.google.gson.Gson;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnfaithfulSettings {
	public int maxSize = 32;
	public boolean clamp;
	
	private static final Gson GSON = new Gson();
	
	private static final String SUFFIX = ".unfaithfulmeta";
	private static final String STANDALONE = "default" + SUFFIX;
	
	public static UnfaithfulSettings getForTexture(ResourceManager mgr, Identifier id) {
		Resource rc;
		try {
			rc = getResourceForTexture(mgr, id);
		} catch (IOException e) {
			Init.LOG.error("cannot read unfaithfulmeta file for {}", id, e);
			throw new RuntimeException(e);
		}
		
		try(InputStreamReader reader = new InputStreamReader(rc.getInputStream())) {
			return GSON.fromJson(reader, UnfaithfulSettings.class);
		} catch (IOException e) {
			Init.LOG.error("cannot read unfaithfulmeta input stream for {}", id, e);
			throw new RuntimeException(e);
		}
	}
	
	private static Resource getResourceForTexture(ResourceManager mgr, Identifier originalId) throws IOException {
		// minecraft:particles/something -> unfaithful:settings/minecraft/particles/something.png.unfaithfulmeta
		Identifier id = new Identifier(Init.MODID, "settings/" + originalId.getNamespace() + "/" + originalId.getPath() + ".png.unfaithfulmeta");
		if(mgr.containsResource(id)) {
			return mgr.getResource(id);
		}
		
		//Start waking up the file tree
		// minecraft:textures/block/whatever/stone.png.unfaithfulmeta (already tried that, didn't work)
		// minecraft:textures/block/whatever/meta.unfaithfulmeta (already tried that, didn't work)
		// minecraft:textures/block/meta.unfaithfulmeta
		// minecraft:textures/meta.unfaithfulmeta
		int lastSlash = Integer.MAX_VALUE;
		for(;;) {
			lastSlash = id.getPath().lastIndexOf('/', lastSlash - 1);
			if(lastSlash == -1) break;
			
			Identifier cutId = new Identifier(id.getNamespace(), id.getPath().substring(0, lastSlash) + "/" + STANDALONE);
			if(mgr.containsResource(cutId)) return mgr.getResource(cutId);
		}
		
		throw new FileNotFoundException("Could not find unfaithful:settings/default.unfaithfulmeta");
	}
}
