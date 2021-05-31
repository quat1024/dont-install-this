package agency.highlysuspect.unfaithful.upscale;

import net.minecraft.client.texture.NativeImage;

public abstract class Upscaler {
	protected abstract void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY);
	public abstract int scaleFactor();
	
	public NativeImage upscaleAndFree(NativeImage in) {
		NativeImage out = upscale(in);
		in.close();
		return out;
	}
	
	public NativeImage upscale(NativeImage in) {
		NativeImage out = new NativeImage(in.getWidth() * scaleFactor(), in.getHeight() * scaleFactor(), false);
		
		for(int inX = 0; inX < in.getWidth(); inX++) {
			for(int inY = 0; inY < in.getHeight(); inY++) {
				//todo: this is cute, but slow
				Sampler sampler = Sampler.wrapping(in).offset(inX, inY);
				
				writeUpscalePixels(sampler, out, inX * 2, inY * 2);
			}
		}
		
		return out;
	}
	
	protected static final int Y_WEIGHT = 48;
	protected static final int U_WEIGHT = 7;
	protected static final int V_WEIGHT = 6;
	
	//Returns a value *approximately* between 0 (same color) and 1 (totally disparate colors e.g. white and black)
	protected float weightedYuvDistance(int pixelA, int pixelB) {
		int r = Math.abs(NativeImage.getRed(pixelA) - NativeImage.getRed(pixelB));
		int g = Math.abs(NativeImage.getGreen(pixelA) - NativeImage.getGreen(pixelB));
		int b = Math.abs(NativeImage.getBlue(pixelA) - NativeImage.getBlue(pixelB));
		
		float y = r *  .299000f + g *  .587000f + b *  .114000f;
		float u = r * -.168736f + g * -.331264f + b *  .500000f;
		float v = r *  .500000f + g * -.418688f + b * -.081312f;
		
		return (y * Y_WEIGHT + u * U_WEIGHT + v * V_WEIGHT) / (Y_WEIGHT * U_WEIGHT * V_WEIGHT) / 6.01f;
	}
	
	protected int blend(int pixelA, int pixelB, float aToB) {
		int aAlpha = NativeImage.getAlpha(pixelA);
		int bAlpha = NativeImage.getAlpha(pixelB);
		
		//Always prefer to color in fully transparent pixels  
		if(aAlpha == 0 && bAlpha == 0) return 0x00FFFFFF & pixelA; //hopefully this makes mipmaps look ok
		else if(aAlpha == 0) return pixelB;
		else if(bAlpha == 0) return pixelA;
		
		float reverseAlpha = 1 - aToB;
		float r = (aToB * NativeImage.getRed(pixelB))   + (reverseAlpha * NativeImage.getRed(pixelA));
		float g = (aToB * NativeImage.getGreen(pixelB)) + (reverseAlpha * NativeImage.getGreen(pixelA));
		float b = (aToB * NativeImage.getBlue(pixelB))  + (reverseAlpha * NativeImage.getBlue(pixelA));
		float a = (aToB * aAlpha) + (reverseAlpha * bAlpha);
		return NativeImage.getAbgrColor((int) a, (int) b, (int) g, (int) r);
	}
}
