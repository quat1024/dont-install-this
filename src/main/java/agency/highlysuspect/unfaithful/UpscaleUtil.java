package agency.highlysuspect.unfaithful;

import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UpscaleUtil {
	public static void upscaleSpriteInfoQueue(ConcurrentLinkedQueue<Sprite.Info> queue) {
		Config config = Init.config;
		queue.forEach(info -> {
			SpriteInfoExt ext = SpriteInfoExt.cast(info);
			
			UnfaithfulSettings settings = UnfaithfulSettings.getForTexture(MinecraftClient.getInstance().getResourceManager(), info.getId());
			
			if(info.getWidth() <= settings.maxSize) {
				//todo: 'null' means 'don't upscale', but there's probably a better way to pass that along
				ext.setUnfaithfulSettings(settings);
				ext.setWidth(info.getWidth() * config.upscaler.scaleFactor());
				ext.setHeight(info.getHeight() * config.upscaler.scaleFactor());
			}
		});
	}
	
	public static NativeImage upscaleNativeImage(NativeImage original, Sprite.Info info) {
		SpriteInfoExt ext = SpriteInfoExt.cast(info);
		UnfaithfulSettings settings = ext.getUnfaithfulSettings();
		if(settings == null) return original;
		
		return Init.config.upscaler.upscaleAndFree(original, settings, info);
	}
}
