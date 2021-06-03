package agency.highlysuspect.unfaithful.upscale.sampler;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public abstract class Sampler {
	public Sampler(NativeImage img) {
		this.img = img;
	}
	
	protected final NativeImage img;
	protected int originX;
	protected int originY;
	
	public void setOrigin(int x, int y) {
		this.originX = x;
		this.originY = y;
	}
	
	public int sample(int dx, int dy) {
		return sampleWithinRegion(dx, dy, 0, img.getWidth(), 0, img.getHeight());
	}
	
	protected abstract int sampleWithinRegion(int dx, int dy, int windowXMin, int windowWidth, int windowYMin, int windowHeight);
	
	public static class Wrapping extends Sampler {
		public Wrapping(NativeImage img) {
			super(img);
		}
		
		@Override
		public int sampleWithinRegion(int dx, int dy, int windowXMin, int windowWidth, int windowYMin, int windowHeight) {
			return img.getPixelColor(
				Math.floorMod(originX + dx, windowWidth) + windowXMin,
				Math.floorMod(originY + dy, windowHeight) + windowYMin
			);
		}
	}
	
	public static class ClampTransparent extends Sampler {
		public ClampTransparent(NativeImage img) {
			super(img);
		}
		
		@Override
		public int sampleWithinRegion(int dx, int dy, int windowXMin, int windowWidth, int windowYMin, int windowHeight) {
			int x = originX + dx;
			int y = originY + dy;
			
			if(x >= windowXMin && x < windowXMin + windowWidth && y >= windowYMin && y < windowYMin + windowHeight) {
				return img.getPixelColor(x, y);
			} else {
				return 0x00000000;
			}
		}
	}
	
	public static class ClampEdge extends Sampler {
		public ClampEdge(NativeImage img) {
			super(img);
		}
		
		@Override
		public int sampleWithinRegion(int dx, int dy, int windowXMin, int windowWidth, int windowYMin, int windowHeight) {
			return img.getPixelColor(
				MathHelper.clamp(originX + dx, windowXMin, windowXMin + windowWidth - 1),
				MathHelper.clamp(originY + dy, windowYMin, windowYMin + windowHeight - 1)
			);
		}
	}
	
	public static class Filmstrip extends Sampler {
		public Filmstrip(Sampler sampler, AnimationResourceMetadata arm) {
			super(sampler.img);
			this.sampler = sampler;
			
			//TODO: Properly support non-square animations.
			frameHeight = img.getWidth();
		}
		
		public static boolean needsFilmstrip(NativeImage image, AnimationResourceMetadata arm) {
			return image.getWidth() != image.getHeight() &&
				arm != null &&
				arm != AnimationResourceMetadata.EMPTY &&
				arm.getFrameCount() > 1;
		}
		
		protected final Sampler sampler;
		protected final int frameHeight;
		protected int frameIndex;
		
		@Override
		public void setOrigin(int x, int y) {
			sampler.setOrigin(x, y);
			frameIndex = y / frameHeight;
		}
		
		@Override
		public int sample(int dx, int dy) {
			return sampleWithinRegion(dx, dy, 0, img.getWidth(), frameHeight * frameIndex, frameHeight);
		}
		
		@Override
		public int sampleWithinRegion(int dx, int dy, int windowXMin, int windowWidth, int windowYMin, int windowHeight) {
			return sampler.sampleWithinRegion(dx, dy, windowXMin, windowWidth, windowYMin, windowHeight);
		}
	}
}