package agency.highlysuspect.unfaithful.upscale;

import agency.highlysuspect.unfaithful.Init;
import net.minecraft.client.texture.NativeImage;

public class TestUpscaler {
	public static NativeImage upscale(NativeImage input) {
		NativeImage output = new NativeImage(input.getWidth() * Init.config.scaleFactor, input.getHeight() * Init.config.scaleFactor, false);
		
		for(int inX = 0; inX < input.getWidth(); inX++) {
			for(int inY = 0; inY < input.getHeight(); inY++) {
				int outX = inX * 2;
				int outY = inY * 2;
				output.setPixelColor(outX, outY, input.getPixelColor(inX, inY));
				output.setPixelColor(outX + 1, outY, 0xFFFF0000);
				output.setPixelColor(outX, outY + 1, 0xFF00FF00);
				output.setPixelColor(outX + 1, outY + 1, 0xFF0000FF);
			}
		}
		
		return output;
	}
}
