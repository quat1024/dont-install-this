package agency.highlysuspect.unfaithful.upscale.algo;

import agency.highlysuspect.unfaithful.upscale.Sampler;
import agency.highlysuspect.unfaithful.upscale.Upscaler;
import net.minecraft.client.texture.NativeImage;

//Based on 2xbr Javascript implementation from https://github.com/carlosascari/2xBR-Filter/blob/master/xbr.js
public class Xbr extends Upscaler {
	public static final Xbr INSTANCE = new Xbr();
	
	@Override
	public int scaleFactor() {
		return 2;
	}
	
	@Override
	protected void writeUpscalePixels(Sampler in, NativeImage out, int outX, int outY) {
		int[] neighborhood = new int[21];
		
		neighborhood[ 0] = in.sample(-1, -2);
		neighborhood[ 1] = in.sample( 0, -2);
		neighborhood[ 2] = in.sample(+1, -2);
		neighborhood[ 3] = in.sample(-2, -1);
		neighborhood[ 4] = in.sample(-1, -1);
		neighborhood[ 5] = in.sample( 0, -1);
		neighborhood[ 6] = in.sample(+1, -1);
		neighborhood[ 7] = in.sample(+2, -1);
		neighborhood[ 8] = in.sample(-2,  0);
		neighborhood[ 9] = in.sample(-1,  0);
		neighborhood[10] = in.sample( 0,  0);
		neighborhood[11] = in.sample(+1,  0);
		neighborhood[12] = in.sample(+2,  0);
		neighborhood[13] = in.sample(-2, +1);
		neighborhood[14] = in.sample(-1, +1);
		neighborhood[15] = in.sample( 0, +1);
		neighborhood[16] = in.sample(+1, +1);
		neighborhood[17] = in.sample(+2, +1);
		neighborhood[18] = in.sample(-1, +2);
		neighborhood[19] = in.sample( 0, +2);
		neighborhood[20] = in.sample(+1, +2);
		
		float d_10_9  = weightedYuvDistance(neighborhood[10], neighborhood[ 9]);
		float d_10_5  = weightedYuvDistance(neighborhood[10], neighborhood[ 5]);
		float d_10_11 = weightedYuvDistance(neighborhood[10], neighborhood[11]);
		float d_10_15 = weightedYuvDistance(neighborhood[10], neighborhood[15]);
		float d_10_14 = weightedYuvDistance(neighborhood[10], neighborhood[14]);
		float d_10_6  = weightedYuvDistance(neighborhood[10], neighborhood[ 6]);
		float d_4_8   = weightedYuvDistance(neighborhood[ 4], neighborhood[ 8]);
		float d_4_1   = weightedYuvDistance(neighborhood[ 4], neighborhood[ 1]);
		float d_9_5   = weightedYuvDistance(neighborhood[ 9], neighborhood[ 5]);
		float d_9_15  = weightedYuvDistance(neighborhood[ 9], neighborhood[15]);
		float d_9_3   = weightedYuvDistance(neighborhood[ 9], neighborhood[ 3]);
		float d_5_11  = weightedYuvDistance(neighborhood[ 5], neighborhood[11]);
		float d_5_0   = weightedYuvDistance(neighborhood[ 5], neighborhood[ 0]);
		float d_10_4  = weightedYuvDistance(neighborhood[10], neighborhood[ 4]);
		float d_10_16 = weightedYuvDistance(neighborhood[10], neighborhood[16]);
		float d_6_12  = weightedYuvDistance(neighborhood[ 6], neighborhood[12]);
		float d_6_1   = weightedYuvDistance(neighborhood[ 6], neighborhood[ 1]);
		float d_11_15 = weightedYuvDistance(neighborhood[11], neighborhood[15]);
		float d_11_7  = weightedYuvDistance(neighborhood[11], neighborhood[ 7]);
		float d_5_2   = weightedYuvDistance(neighborhood[ 5], neighborhood[ 2]);
		float d_14_8  = weightedYuvDistance(neighborhood[14], neighborhood[ 8]);
		float d_14_19 = weightedYuvDistance(neighborhood[14], neighborhood[19]);
		float d_15_18 = weightedYuvDistance(neighborhood[15], neighborhood[18]);
		float d_9_13  = weightedYuvDistance(neighborhood[ 9], neighborhood[13]);
		float d_16_12 = weightedYuvDistance(neighborhood[16], neighborhood[12]);
		float d_16_19 = weightedYuvDistance(neighborhood[16], neighborhood[19]);
		float d_15_20 = weightedYuvDistance(neighborhood[15], neighborhood[20]);
		float d_15_17 = weightedYuvDistance(neighborhood[15], neighborhood[17]);
		
		// Top Left Edge Detection Rule
		float a1 = (d_10_14 + d_10_6 + d_4_8 + d_4_1 + (4 * d_9_5));
		float b1 = (d_9_15 + d_9_3 + d_5_11 + d_5_0 + (4 * d_10_4));
		if(a1 < b1) {
			int new_pixel = (d_10_9 <= d_10_5) ? neighborhood[9] : neighborhood[5];
			int blended_pixel = blend(new_pixel, neighborhood[10], .5f);
			out.setPixelColor(outX, outY, blended_pixel);
		} else {
			out.setPixelColor(outX, outY, neighborhood[10]);
		}
		
		// Top Right Edge Detection Rule
		float a2 = (d_10_16 + d_10_4 + d_6_12 + d_6_1 + (4 * d_5_11));
		float b2 = (d_11_15 + d_11_7 + d_9_5 + d_5_2 + (4 * d_10_6));
		if(a2 < b2) {
			int new_pixel = (d_10_5 <= d_10_11) ? neighborhood[5] : neighborhood[11];
			int blended_pixel = blend(new_pixel, neighborhood[10], .5f);
			out.setPixelColor(outX + 1, outY, blended_pixel);
		} else {
			out.setPixelColor(outX + 1, outY, neighborhood[10]);
		}
		
		// Bottom Left Edge Detection Rule
		float a3 = (d_10_4 + d_10_16 + d_14_8 + d_14_19 + (4 * d_9_15));
		float b3 = (d_9_5 + d_9_13 + d_11_15 + d_15_18 + (4 * d_10_14));
		if(a3 < b3) {
			int new_pixel = (d_10_9 <= d_10_15) ? neighborhood[9] : neighborhood[15];
			int blended_pixel = blend(new_pixel, neighborhood[10], .5f);
			out.setPixelColor(outX, outY + 1, blended_pixel);
		} else {
			out.setPixelColor(outX, outY + 1, neighborhood[10]);
		}
		
		// Bottom Right Edge Detection Rule
		float a4 = (d_10_6 + d_10_14 + d_16_12 + d_16_19 + (4 * d_11_15));
		float b4 = (d_9_15 + d_15_20 + d_15_17 + d_5_11 + (4 * d_10_16));
		if(a4 < b4) {
			int new_pixel = (d_10_11 <= d_10_15) ? neighborhood[11] : neighborhood[15];
			int blended_pixel = blend(new_pixel, neighborhood[10], .5f);
			out.setPixelColor(outX + 1, outY + 1, blended_pixel);
		} else {
			out.setPixelColor(outX + 1, outY + 1, neighborhood[10]);
		}
	}
}
