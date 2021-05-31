package agency.highlysuspect.unfaithful.upscale;

import agency.highlysuspect.unfaithful.UnfaithfulSettings;
import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

import java.util.function.BiFunction;

public abstract class Upscaler {
	protected abstract void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY);
	public abstract int scaleFactor();
	
	public NativeImage upscaleAndFree(NativeImage in, UnfaithfulSettings settings, Sprite.Info info) {
		NativeImage out = upscale(in, settings, info);
		in.close();
		return out;
	}
	
	public NativeImage upscale(NativeImage in, UnfaithfulSettings settings, Sprite.Info info) {
		NativeImage out = new NativeImage(in.getWidth() * scaleFactor(), in.getHeight() * scaleFactor(), false);
		
		BiFunction<NativeImage, Boolean, Sampler> sampMaker = settings.clamp ? Sampler.Clamping::new : Sampler.Wrapping::new;
		
		AnimationResourceMetadata arm = SpriteInfoExt.cast(info).getAnimationResourceMetadata();
		boolean animated = arm != null && arm != AnimationResourceMetadata.EMPTY && arm.getFrameCount() > 1;
		
		Sampler sampler = sampMaker.apply(in, animated);
		
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
	//Uhh, mostly in the Y'UV colorspace, but i threw on a (non-physically based) alpha parameter
	protected float weightedYuvDistance(int pixelA, int pixelB) {
		int r = Math.abs(NativeImage.getRed(pixelA) - NativeImage.getRed(pixelB));
		int g = Math.abs(NativeImage.getGreen(pixelA) - NativeImage.getGreen(pixelB));
		int b = Math.abs(NativeImage.getBlue(pixelA) - NativeImage.getBlue(pixelB));
		int a = Math.abs(NativeImage.getAlpha(pixelA) - NativeImage.getAlpha(pixelB));
		
		float y = r *  .299000f + g *  .587000f + b *  .114000f;
		float u = r * -.168736f + g * -.331264f + b *  .500000f;
		float v = r *  .500000f + g * -.418688f + b * -.081312f;
		
		return (y * Y_WEIGHT + u * U_WEIGHT + v * V_WEIGHT + a * 10) / (Y_WEIGHT * U_WEIGHT * V_WEIGHT) / 6.01f;
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
		float a = Math.max(aAlpha, bAlpha);
		return NativeImage.getAbgrColor((int) a, (int) b, (int) g, (int) r);
	}
}
