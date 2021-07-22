package agency.highlysuspect.dontinstallthis.mixin;

import agency.highlysuspect.dontinstallthis.util.NativeImageExt;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin implements NativeImageExt {
	@Shadow protected abstract boolean write(WritableByteChannel writableByteChannel) throws IOException;
	
	@Override
	public boolean writeButItsPublic(WritableByteChannel writableByteChannel) throws IOException {
		return write(writableByteChannel);
	}
}
