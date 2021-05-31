package agency.highlysuspect.unfaithful.upscale;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public interface Sampler {
	int sample(int x, int y);
	
	default Sampler offset(int dx, int dy) {
		return (x, y) -> this.sample(x + dx, y + dy);
	}
	
	static Sampler clamping(NativeImage img) {
		return (x, y) -> img.getPixelColor(MathHelper.clamp(x, 0, img.getWidth() - 1), MathHelper.clamp(y, 0, img.getHeight() - 1));
	}
	
	static Sampler wrapping(NativeImage img) {
		return (x, y) -> img.getPixelColor(Math.floorMod(x, img.getWidth()), Math.floorMod(y, img.getHeight()));
	}
	
	static Sampler constant(int color) {
		return (x, y) -> color;
	}
	
	// todo: "entity cube" sampler, that correctly handles cube edges (sounds very hard)
}
