package agency.highlysuspect.unfaithful.upscale.algo;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public class Xbr {
	//Based on 2xbr Javascript implementation from https://github.com/carlosascari/2xBR-Filter/blob/master/xbr.js
	public static NativeImage upscale(NativeImage input) {
		NativeImage output = new NativeImage(input.getWidth() * 2, input.getHeight() * 2, false);
		
		int[] matrix = new int[21];
		
		for(int inX = 0; inX < input.getWidth(); inX++) {
			for(int inY = 0; inY < input.getHeight(); inY++) {
				matrix[ 0] = getWrapping(input, inX-1, inY-2);
				matrix[ 1] = getWrapping(input, inX  , inY-2);
				matrix[ 2] = getWrapping(input, inX+1, inY-2);
				matrix[ 3] = getWrapping(input, inX-2, inY-1);
				matrix[ 4] = getWrapping(input, inX-1, inY-1);
				matrix[ 5] = getWrapping(input, inX  , inY-1);
				matrix[ 6] = getWrapping(input, inX+1, inY-1);
				matrix[ 7] = getWrapping(input, inX+2, inY-1);
				matrix[ 8] = getWrapping(input, inX-2, inY  );
				matrix[ 9] = getWrapping(input, inX-1, inY  );
				matrix[10] = getWrapping(input, inX  , inY  );
				matrix[11] = getWrapping(input, inX+1, inY  );
				matrix[12] = getWrapping(input, inX+2, inY  );
				matrix[13] = getWrapping(input, inX-2, inY+1);
				matrix[14] = getWrapping(input, inX-1, inY+1);
				matrix[15] = getWrapping(input, inX  , inY+1);
				matrix[16] = getWrapping(input, inX+1, inY+1);
				matrix[17] = getWrapping(input, inX+2, inY+1);
				matrix[18] = getWrapping(input, inX-1, inY+2);
				matrix[19] = getWrapping(input, inX  , inY+2);
				matrix[20] = getWrapping(input, inX+1, inY+2);
				
				float d_10_9  = d(matrix[10], matrix[ 9]);
				float d_10_5  = d(matrix[10], matrix[ 5]);
				float d_10_11 = d(matrix[10], matrix[11]);
				float d_10_15 = d(matrix[10], matrix[15]);
				float d_10_14 = d(matrix[10], matrix[14]);
				float d_10_6  = d(matrix[10], matrix[ 6]);
				float d_4_8   = d(matrix[ 4], matrix[ 8]);
				float d_4_1   = d(matrix[ 4], matrix[ 1]);
				float d_9_5   = d(matrix[ 9], matrix[ 5]);
				float d_9_15  = d(matrix[ 9], matrix[15]);
				float d_9_3   = d(matrix[ 9], matrix[ 3]);
				float d_5_11  = d(matrix[ 5], matrix[11]);
				float d_5_0   = d(matrix[ 5], matrix[ 0]);
				float d_10_4  = d(matrix[10], matrix[ 4]);
				float d_10_16 = d(matrix[10], matrix[16]);
				float d_6_12  = d(matrix[ 6], matrix[12]);
				float d_6_1   = d(matrix[ 6], matrix[ 1]);
				float d_11_15 = d(matrix[11], matrix[15]);
				float d_11_7  = d(matrix[11], matrix[ 7]);
				float d_5_2   = d(matrix[ 5], matrix[ 2]);
				float d_14_8  = d(matrix[14], matrix[ 8]);
				float d_14_19 = d(matrix[14], matrix[19]);
				float d_15_18 = d(matrix[15], matrix[18]);
				float d_9_13  = d(matrix[ 9], matrix[13]);
				float d_16_12 = d(matrix[16], matrix[12]);
				float d_16_19 = d(matrix[16], matrix[19]);
				float d_15_20 = d(matrix[15], matrix[20]);
				float d_15_17 = d(matrix[15], matrix[17]);
				
				// Top Left Edge Detection Rule
				float a1 = (d_10_14 + d_10_6 + d_4_8 + d_4_1 + (4 * d_9_5));
				float b1 = (d_9_15 + d_9_3 + d_5_11 + d_5_0 + (4 * d_10_4));
				if(a1 < b1) {
					int new_pixel = (d_10_9 <= d_10_5) ? matrix[9] : matrix[5];
					int blended_pixel = blend(new_pixel, matrix[10], .5f);
					output.setPixelColor(inX * 2, inY * 2, blended_pixel);
				} else {
					output.setPixelColor(inX * 2, inY * 2, matrix[10]);
				}
				
				// Top Right Edge Detection Rule
				float a2 = (d_10_16 + d_10_4 + d_6_12 + d_6_1 + (4 * d_5_11));
				float b2 = (d_11_15 + d_11_7 + d_9_5 + d_5_2 + (4 * d_10_6));
				if(a2 < b2) {
					int new_pixel = (d_10_5 <= d_10_11) ? matrix[5] : matrix[11];
					int blended_pixel = blend(new_pixel, matrix[10], .5f);
					output.setPixelColor(inX * 2 + 1, inY * 2, blended_pixel);
				} else {
					output.setPixelColor(inX * 2 + 1, inY * 2, matrix[10]);
				}
				
				// Bottom Left Edge Detection Rule
				float a3 = (d_10_4 + d_10_16 + d_14_8 + d_14_19 + (4 * d_9_15));
				float b3 = (d_9_5 + d_9_13 + d_11_15 + d_15_18 + (4 * d_10_14));
				if(a3 < b3) {
					int new_pixel = (d_10_9 <= d_10_15) ? matrix[9] : matrix[15];
					int blended_pixel = blend(new_pixel, matrix[10], .5f);
					output.setPixelColor(inX * 2, inY * 2 + 1, blended_pixel);
				} else {
					output.setPixelColor(inX * 2, inY * 2 + 1, matrix[10]);
				}
				
				// Bottom Right Edge Detection Rule
				float a4 = (d_10_6 + d_10_14 + d_16_12 + d_16_19 + (4 * d_11_15));
				float b4 = (d_9_15 + d_15_20 + d_15_17 + d_5_11 + (4 * d_10_16));
				if(a4 < b4) {
					int new_pixel = (d_10_11 <= d_10_15) ? matrix[11] : matrix[15];
					int blended_pixel = blend(new_pixel, matrix[10], .5f);
					output.setPixelColor(inX * 2 + 1, inY * 2 + 1, blended_pixel);
				} else {
					output.setPixelColor(inX * 2 + 1, inY * 2 + 1, matrix[10]);
				}
			}
		}
		
		return output;
	}
	
	private static final int Y_WEIGHT = 48;
	private static final int U_WEIGHT = 7;
	private static final int V_WEIGHT = 6;
	
	//weighted difference between two pixels in Y'UV color space
	private static float d(int px, int py) {
		int r = Math.abs(NativeImage.getRed(px) - NativeImage.getRed(py));
		int g = Math.abs(NativeImage.getGreen(px) - NativeImage.getGreen(py));
		int b = Math.abs(NativeImage.getBlue(px) - NativeImage.getBlue(py));
		
		float y = r *  .299000f + g *  .587000f + b *  .114000f;
		float u = r * -.168736f + g * -.331264f + b *  .500000f;
		float v = r *  .500000f + g * -.418688f + b * -.081312f;
		
		return y * Y_WEIGHT + u * U_WEIGHT + v * V_WEIGHT;
	}
	
	//Modified from the Javascript implementation to handle alpha differently.
	private static int blend(int pixelA, int pixelB, float alpha) {
		int aAlpha = NativeImage.getAlpha(pixelA);
		int bAlpha = NativeImage.getAlpha(pixelB);
		
		//Always prefer to color in fully transparent pixels  
		if(aAlpha == 0 && bAlpha == 0) return 0x00FFFFFF & pixelA;
		else if(aAlpha == 0) return pixelB;
		else if(bAlpha == 0) return pixelA;
		
		float reverseAlpha = 1 - alpha;
		float r = (alpha * NativeImage.getRed(pixelB))   + (reverseAlpha * NativeImage.getRed(pixelA));
		float g = (alpha * NativeImage.getGreen(pixelB)) + (reverseAlpha * NativeImage.getGreen(pixelA));
		float b = (alpha * NativeImage.getBlue(pixelB))  + (reverseAlpha * NativeImage.getBlue(pixelA));
		float a = (alpha * aAlpha) + (reverseAlpha * bAlpha);
		return NativeImage.getAbgrColor((int) a, (int) b, (int) g, (int) r);
	}
	
	private static int getWrapping(NativeImage img, int x, int y) {
		return img.getPixelColor(Math.floorMod(x, img.getWidth()), Math.floorMod(y, img.getHeight()));
		//return img.getPixelColor(MathHelper.clamp(x, 0, img.getWidth() - 1), MathHelper.clamp(y, 0, img.getHeight() - 1));
	}
}
