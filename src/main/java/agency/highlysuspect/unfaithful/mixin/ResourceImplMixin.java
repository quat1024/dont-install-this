package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.Init;
import agency.highlysuspect.unfaithful.resource.ResourceImplExt;
import agency.highlysuspect.unfaithful.util.NativeImageExt;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

@Mixin(ResourceImpl.class)
public abstract class ResourceImplMixin implements ResourceImplExt {
	@Shadow @Final @Mutable private InputStream inputStream;
	@Shadow @Final private Identifier id;
	
	@Shadow @Nullable public abstract <T> T getMetadata(ResourceMetadataReader<T> metaReader);
	
	@Override
	public void upscaleMe() {
		try {
			//Read the PNG from the original input stream. Discard the original input stream.
			ByteBuffer buf = TextureUtil.readAllToByteBuffer(inputStream);
			inputStream.close();
			
			//Decode the PNG from the byte buffer.
			buf.rewind();
			NativeImage unscaledImage = NativeImage.read(buf);
			
			//Grab animation metadata, if there is any
			@Nullable AnimationResourceMetadata arm = getMetadata(AnimationResourceMetadata.READER);
			
			//Perform the upscaling algorithm on the image.
			NativeImage scaledImage = Init.config.upscaler.upscale(unscaledImage, id, arm);
			unscaledImage.close();
			
			//Write the upscaled image to a byte array? I guess this works?
			byte[] upscaledBytes;
			
			try(ByteArrayOutputStream out = new ByteArrayOutputStream();
				WritableByteChannel byteWrite = Channels.newChannel(out)) {
				NativeImageExt.cast(scaledImage).writeButItsPublic(byteWrite);
				upscaledBytes = out.toByteArray();
			}
			
			//Turn around and replace the original input stream with this one containing the upscaled PNG.
			inputStream = new ByteArrayInputStream(upscaledBytes);
		} catch (IOException e) {
			throw new RuntimeException("Problem upscaling", e);
		}
	}
}
