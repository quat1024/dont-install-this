package agency.highlysuspect.unfaithful.upscale;

import net.minecraft.client.texture.NativeImage;

public class Epx {
	public static NativeImage upscale(NativeImage input) {
		NativeImage output = new NativeImage(input.getWidth() * 2, input.getHeight() * 2, false);
		
		for(int inX = 0; inX < input.getWidth(); inX++) {
			for(int inY = 0; inY < input.getHeight(); inY++) {
				//Based on the Epx algorithm description from Wikipedia.
				int p = input.getPixelColor(inX, inY);
				int a = getWrapping(input, inX, inY - 1);
				int b = getWrapping(input, inX + 1, inY);
				int c = getWrapping(input, inX - 1, inY);
				int d = getWrapping(input, inX, inY + 1);
				
				int outX = inX * 2;
				int outY = inY * 2;
				
				output.setPixelColor(outX, outY, (eq(c, a) && !eq(c, d) && !eq(a, b)) ? a : p);
				output.setPixelColor(outX + 1, outY, (eq(a, b) && !eq(a, c) && !eq(b, d)) ? b : p);
				output.setPixelColor(outX, outY + 1, (eq(d, c) && !eq(d, b) && !eq(c, a)) ? c : p);
				output.setPixelColor(outX + 1, outY + 1, (eq(b, d) && !eq(b, a) && !eq(d, c)) ? d : p);
			}
		}
		
		return output;
	}
	
	private static boolean eq(int a, int b) {
		int ar = NativeImage.getRed(a);
		int ag = NativeImage.getGreen(a);
		int ab = NativeImage.getBlue(a);
		
		int br = NativeImage.getRed(b);
		int bg = NativeImage.getGreen(b);
		int bb = NativeImage.getBlue(b);
		
		//Lazy lazy...
		return Math.abs((ar + ag + ab) - (br + bg + bb)) < 20;
	}
	
	private static int getWrapping(NativeImage img, int x, int y) {
		return img.getPixelColor(Math.floorMod(x, img.getWidth()), Math.floorMod(y, img.getHeight()));
	}
}
