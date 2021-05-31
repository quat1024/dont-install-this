package agency.highlysuspect.unfaithful;

import agency.highlysuspect.unfaithful.upscale.algo.Epx;
import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UpscaleUtil {
	public static void upscaleSpriteInfoQueue(ConcurrentLinkedQueue<Sprite.Info> queue) {
		Config config = Init.config;
		queue.forEach(info -> {
			SpriteInfoExt ext = SpriteInfoExt.cast(info);
			ext.setWidth(info.getWidth() * config.upscaler.scaleFactor());
			ext.setHeight(info.getHeight() * config.upscaler.scaleFactor());
		});
	}
	
	public static NativeImage upscaleNativeImage(NativeImage original) {
		return Epx.DEFAULT.upscaleAndFree(original);
	}
}
