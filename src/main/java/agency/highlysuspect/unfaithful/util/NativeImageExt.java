package agency.highlysuspect.unfaithful.util;

import net.minecraft.client.texture.NativeImage;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface NativeImageExt {
	boolean writeButItsPublic(WritableByteChannel writableByteChannel) throws IOException;
	
	@SuppressWarnings("ConstantConditions")
	static NativeImageExt cast(NativeImage image) {
		return (NativeImageExt) (Object) image;
	}
}
