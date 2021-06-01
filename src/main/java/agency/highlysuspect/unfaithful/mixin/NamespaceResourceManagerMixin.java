package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.resource.ResourceImplExt;
import agency.highlysuspect.unfaithful.resource.UpscalingResourcePack;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
	@Unique ThreadLocal<Boolean> isPng = ThreadLocal.withInitial(() -> false);
	@Unique ThreadLocal<Boolean> doUpscale = ThreadLocal.withInitial(() -> false);
	
	@Inject(method = "getResource", at = @At("HEAD"))
	private void getResourceHead(Identifier id, CallbackInfoReturnable<Resource> cir) {
		isPng.set(id.getPath().endsWith(".png"));
		doUpscale.set(false);
	}
	
	//This is a grody injection and i'm sorry for it
	@Inject(method = "getResource", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePack;contains(Lnet/minecraft/resource/ResourceType;Lnet/minecraft/util/Identifier;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void examineResourcePack(Identifier id, CallbackInfoReturnable<Resource> cir, ResourcePack mcmetaPack, Identifier mcmetaId, int index, ResourcePack pack) {
		if(pack instanceof UpscalingResourcePack && !doUpscale.get() && isPng.get() && ((UpscalingResourcePack) pack).interestedInUpscaling(id)) {
			doUpscale.set(true);
		}
	}
	
	//I feel like a ModifyVariable would work here but idk how to snag that
	@Inject(method = "getResource", at = @At(value = "RETURN"))
	private void heckResource(Identifier id, CallbackInfoReturnable<Resource> cir) {
		if(doUpscale.get()) ((ResourceImplExt) cir.getReturnValue()).upscaleMe();
	}
}
