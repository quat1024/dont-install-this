package agency.highlysuspect.unfaithful.upscale;

import agency.highlysuspect.unfaithful.Init;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public abstract class Sampler {
	public Sampler(NativeImage img, boolean animated) {
		this.img = img;
		this.windowWidth = img.getWidth();
		
		this.animated = animated;
		if(animated) {
			//todo support non-square animated textures!!!!!
			this.windowHeight = img.getWidth();
		} else {
			this.windowHeight = img.getHeight();
		}
	}
	
	protected final NativeImage img;
	
	protected final boolean animated;
	
	protected int originX;
	protected int originY;
	
	protected final int windowXMin = 0;
	protected int windowYMin = 0;
	protected final int windowWidth;
	protected final int windowHeight;
	
	public void setOrigin(int x, int y) {
		this.originX = x;
		this.originY = y;
		
		if(animated) {
			//which frame of the animation is this pixel in?
			int frame = Math.floorDiv(y, windowHeight);
			windowYMin = frame * windowHeight;
		}
	}
	
	public abstract int sample(int x, int y);
	
	public static class Wrapping extends Sampler {
		public Wrapping(NativeImage img, boolean animated) {
			super(img, animated);
		}
		
		@Override
		public int sample(int x, int y) {
			int sampleX = Math.floorMod(originX + x, windowWidth) + windowXMin;
			int sampleY = Math.floorMod(originY + y, windowHeight) + windowYMin;
			if(animated && windowYMin != 0) {
				//I don't know what I'm doing
				sampleY--;
			}
			return img.getPixelColor(sampleX, sampleY);
		}
	}
	
	public static class Clamping extends Sampler {
		public Clamping(NativeImage img, boolean animated) {
			super(img, animated);
		}
		
		@Override
		public int sample(int x, int y) {
			int sampleX = MathHelper.clamp(originX + x, windowXMin, windowXMin + windowWidth - 1);
			int sampleY = MathHelper.clamp(originY + y, windowYMin, windowYMin + windowHeight - 1);
			return img.getPixelColor(sampleX, sampleY);
		}
	}
}