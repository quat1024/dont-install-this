package agency.highlysuspect.unfaithful;

import agency.highlysuspect.unfaithful.upscale.Epx;
import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UpscaleUtil {
	public static void upscaleSpriteInfoQueue(ConcurrentLinkedQueue<Sprite.Info> queue) {
		queue.forEach(UpscaleUtil::upscaleSpriteInfo);
	}
	
	private static void upscaleSpriteInfo(Sprite.Info info) {
		Config config = Init.config;
		
		SpriteInfoExt ext = SpriteInfoExt.cast(info);
		ext.setWidth(info.getWidth() * config.scaleFactor);
		ext.setHeight(info.getHeight() * config.scaleFactor);
	}
	
	public static NativeImage upscaleNativeImage(NativeImage original) {
		NativeImage upscaled = Epx.upscale(original);
		original.close(); //Free the original image
		return upscaled;
	}
}
