package agency.highlysuspect.unfaithful.upscale.algo;

import agency.highlysuspect.unfaithful.upscale.Sampler;
import agency.highlysuspect.unfaithful.upscale.Upscaler;
import net.minecraft.client.texture.NativeImage;

//Toy upscaler that produces an identical-looking image
public class NearestNeighbor extends Upscaler {
	@Override
	protected void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY) {
		int lol = in.sample(0, 0);
		
		out.setPixelColor(outX,     outY    , lol);
		out.setPixelColor(outX + 1, outY    , lol);
		out.setPixelColor(outX    , outY + 1, lol);
		out.setPixelColor(outX + 1, outY + 1, lol);
	}
	
	@Override
	public int scaleFactor() {
		return 2;
	}
}
