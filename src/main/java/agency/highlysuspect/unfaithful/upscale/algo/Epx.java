package agency.highlysuspect.unfaithful.upscale.algo;

import agency.highlysuspect.unfaithful.upscale.Sampler;
import agency.highlysuspect.unfaithful.upscale.Upscaler;
import net.minecraft.client.texture.NativeImage;

public class Epx extends Upscaler {
	public static final Epx DEFAULT = new Epx(0.01f);
	
	public Epx(float yuvSimilarity) {
		this.yuvSimilarity = yuvSimilarity;
	}
	
	private final float yuvSimilarity;
	
	@Override
	protected void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY) {
		//based on the epx algorithm description on Wikipedia
		//neighborhood:
		// a
		//cpb
		// d
		int p = in.sample( 0, 0);
		int a = in.sample( 0,-1);
		int b = in.sample( 1, 0);
		int c = in.sample(-1, 0);
		int d = in.sample( 0, 1);
		
		out.setPixelColor(outX    , outY    , (similar(c, a) && !similar(c, d) && !similar(a, b)) ? blend(p, blend(a, c, 0.5f), 1f) : p);
		out.setPixelColor(outX + 1, outY    , (similar(a, b) && !similar(a, c) && !similar(b, d)) ? blend(p, blend(a, b, 0.5f), 1f) : p);
		out.setPixelColor(outX    , outY + 1, (similar(d, c) && !similar(d, b) && !similar(c, a)) ? blend(p, blend(d, c, 0.5f), 1f) : p);
		out.setPixelColor(outX + 1, outY + 1, (similar(b, d) && !similar(b, a) && !similar(d, c)) ? blend(p, blend(b, d, 0.5f), 1f) : p);
	}
	
	private boolean similar(int pixelA, int pixelB) {
		return weightedYuvDistance(pixelA, pixelB) < yuvSimilarity;
	}
	
	@Override
	public int scaleFactor() {
		return 2;
	}
}
