package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.UpscaleUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
	//Look in SpriteAtlasTexture#stitch.
	//
	//loadSprites with two arguments:
	// * reads all texture png files for their width and height *only*
	// * something about AnimationResourceMetadata? (investigate)
	// * anyway, it creates a big list of Sprite.Info objects
	// * submits those to TextureStitcher via TextureStitcher#add
	//
	// I need to upscale the width and height *values* here, but the image hasn't been loaded yet.
	//
	// Later, I need to upscale the actual image pixel data when it gets loaded, to match the expected width and height.
	//
	
	@Inject(
		method = "loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;",
		at = @At("RETURN"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void upscaleTextureStitcherModel(ResourceManager resourceManager, Set<Identifier> ids, CallbackInfoReturnable<Collection<Sprite.Info>> cir, List<?> whatever, ConcurrentLinkedQueue<?> notThisOne, ConcurrentLinkedQueue<Sprite.Info> spriteQueue) {
		UpscaleUtil.upscaleSpriteInfoQueue(spriteQueue);
	}
	
	@ModifyVariable(
		method = "loadSprite",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/client/texture/NativeImage;read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;"
		)
	)
	public NativeImage upscaleImage(NativeImage image, ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) {
		return UpscaleUtil.upscaleNativeImage(image, info);
	}
}
