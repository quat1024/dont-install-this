package agency.highlysuspect.dontinstallthis.mixin;

import agency.highlysuspect.dontinstallthis.Init;
import agency.highlysuspect.dontinstallthis.resource.ResourceImplExt;
import agency.highlysuspect.dontinstallthis.util.NativeImageExt;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	
	@Shadow @Final private InputStream metaInputStream;
	@Shadow private JsonObject metadata;
	@Shadow private boolean readMetadata;
	@Shadow @Nullable public abstract <T> T getMetadata(ResourceMetadataReader<T> metaReader);
	
	@Unique JsonObject epicAnimationBlock = null;
	
	@Override
	public void ohNo() {
		try {
			//Read the PNG from the original input stream. Discard the original input stream.
			ByteBuffer buf = TextureUtil.readAllToByteBuffer(inputStream);
			inputStream.close();
			
			//Decode the PNG from the byte buffer.
			buf.rewind();
			NativeImage original = NativeImage.read(buf);
			
			NativeImage scrolling = original;
			@Nullable JsonObject armJson = null;
			
			//If the image is small enough & a reasonable pixel format
			if(original.getFormat() == NativeImage.Format.ABGR && original.getWidth() * original.getHeight() < 64 * 64) {
				//Grab animation metadata, if there is any
				@Nullable AnimationResourceMetadata originalArm = getMetadata(AnimationResourceMetadata.READER);
				if(originalArm == null) {
					Pair<NativeImage, JsonObject> pair = produceScrollingImage(original, originalArm);
					scrolling = pair.getFirst();
					armJson = pair.getSecond();
					
					original.close();
				} //TODO animated textures
			}
			
			//Turn around and replace the original input stream with this one containing the epic png
			byte[] upscaledBytes;
			try(ByteArrayOutputStream out = new ByteArrayOutputStream(); WritableByteChannel byteWrite = Channels.newChannel(out)) {
				NativeImageExt.cast(scrolling).writeButItsPublic(byteWrite);
				upscaledBytes = out.toByteArray();
			}
			inputStream = new ByteArrayInputStream(upscaledBytes);
			scrolling.close();
			
			//Dont mind me
			epicAnimationBlock = armJson;
		} catch (IOException e) {
			throw new RuntimeException("Problem doing the thing", e);
		}
	}
	
	@SuppressWarnings("UnnecessaryLocalVariable") //but what if i wanna rename a variable, huh
	@Unique
	private Pair<NativeImage, JsonObject> produceScrollingImage(NativeImage original, @Nullable AnimationResourceMetadata originalArm) {
		int frameCount = original.getWidth();
		NativeImage scrolling = new NativeImage(original.getWidth(), original.getHeight() * frameCount, false);
		
		for(int frame = 0; frame < frameCount; frame++) {
			for(int x = 0; x < original.getWidth(); x++) {
				for(int y = 0; y < original.getHeight(); y++) {
					int srcX = (x + frame) % original.getWidth();
					int srcY = y;
					
					int dstX = x;
					int dstY = y + (frame * original.getHeight());
					
					scrolling.setPixelColor(dstX, dstY, original.getPixelColor(srcX, srcY));
				}
			}
		}
		
		JsonObject animation = new JsonObject();
		animation.addProperty("frametime", 1);
		
//		AnimationResourceMetadata arm = new AnimationResourceMetadata(
//			IntStream.range(0, frameCount).mapToObj(AnimationFrameResourceMetadata::new).collect(Collectors.toList()), //frames - all default frametimes here
//			original.getWidth(), //width of each frame in the filmstrip
//			original.getHeight(), //height of each frame in the filmstrip
//			3, //frametime
//			false //interpolate?
//		);
		//mfw i cant serialize that^ to json easily
		
		return Pair.of(scrolling, animation);
	}
	
	@Inject(
		method = "hasMetadata",
		at = @At("HEAD"),
		cancellable = true
	)
	private void hasMetadataEpic(CallbackInfoReturnable<Boolean> cir) {
		if(epicAnimationBlock != null) cir.setReturnValue(true);
	}
	
	@Inject(
		method = "getMetadata",
		at = @At("HEAD")
	)
	private <T> void getMetadataEpic(ResourceMetadataReader<T> metaReader, CallbackInfoReturnable<T> cir) {
		if(epicAnimationBlock != null) {
			//add synthetic animation metadata
			if(metadata == null) metadata = new JsonObject();
			metadata.add("animation", epicAnimationBlock);
			
			//hack because it doesn't come from a file. just say "yeah man, totally read the file" so it doesn't try
			if(metaInputStream == null) readMetadata = true;
		}
	}
}
