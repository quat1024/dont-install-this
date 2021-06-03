package agency.highlysuspect.unfaithful.upscale;

import agency.highlysuspect.unfaithful.Init;
import agency.highlysuspect.unfaithful.Settings;
import agency.highlysuspect.unfaithful.upscale.sampler.Sampler;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class Upscaler {
	protected abstract void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY);
	public abstract int scaleFactor();
	
	public NativeImage upscale(NativeImage in, Identifier id, @Nullable AnimationResourceMetadata arm) {
		NativeImage out = new NativeImage(in.getWidth() * scaleFactor(), in.getHeight() * scaleFactor(), false);
		
		Settings settings = Init.config.rules.getSettingsFor(id);
		Sampler sampler = settings.clamp ? new Sampler.ClampTransparent(in) : new Sampler.Wrapping(in);
		
		if(Sampler.Filmstrip.needsFilmstrip(in, arm)) {
			sampler = new Sampler.Filmstrip(sampler, arm);
		}
		
		for(int inX = 0; inX < in.getWidth(); inX++) {
			for(int inY = 0; inY < in.getHeight(); inY++) {
				sampler.setOrigin(inX, inY);
				writeUpscalePixels(sampler, out, inX * 2, inY * 2);
			}
		}
		
		return out;
	}
	
	protected static final int Y_WEIGHT = 48;
	protected static final int U_WEIGHT = 7;
	protected static final int V_WEIGHT = 6;
	
	//Returns a value *approximately* between 0 (same color) and 1 (totally disparate colors e.g. white and black)
	//Performs alpha multiplication - Hopefully this works OK on things like stained glass
	protected float weightedYuvDistance(int pixelA, int pixelB) {
		float aAlpha = NativeImage.getAlpha(pixelA) / 255f;
		float bAlpha = NativeImage.getAlpha(pixelB) / 255f;
		
		float r = Math.abs(aAlpha * NativeImage.getRed  (pixelA) - bAlpha * NativeImage.getRed  (pixelB));
		float g = Math.abs(aAlpha * NativeImage.getGreen(pixelA) - bAlpha * NativeImage.getGreen(pixelB));
		float b = Math.abs(aAlpha * NativeImage.getBlue (pixelA) - bAlpha * NativeImage.getBlue (pixelB));
		
		float y = r *  .299000f + g *  .587000f + b *  .114000f;
		float u = r * -.168736f + g * -.331264f + b *  .500000f;
		float v = r *  .500000f + g * -.418688f + b * -.081312f;
		
		//Magic number here is an attempt to get everything within 0 and 1
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
		float a = Math.max(aAlpha, bAlpha);
		float b = (aToB * NativeImage.getBlue(pixelB))  + (reverseAlpha * NativeImage.getBlue(pixelA));
		float g = (aToB * NativeImage.getGreen(pixelB)) + (reverseAlpha * NativeImage.getGreen(pixelA));
		float r = (aToB * NativeImage.getRed(pixelB))   + (reverseAlpha * NativeImage.getRed(pixelA));
		return NativeImage.getAbgrColor((int) a, (int) b, (int) g, (int) r);
	}
}
