package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.Init;
import agency.highlysuspect.unfaithful.resource.ResourceImplExt;
import agency.highlysuspect.unfaithful.resource.UpscalingResourcePack;
import agency.highlysuspect.unfaithful.util.ExaminingResourceState;
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
	@Unique ThreadLocal<ExaminingResourceState> state = ThreadLocal.withInitial(() -> ExaminingResourceState.NOT_PNG);
	
	@Inject(method = "getResource", at = @At("HEAD"))
	private void getResourceHead(Identifier id, CallbackInfoReturnable<Resource> cir) {
		state.set(id.getPath().endsWith(".png") ? ExaminingResourceState.PNG : ExaminingResourceState.NOT_PNG);
	}
	
	@Inject(method = "getResource", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePack;contains(Lnet/minecraft/resource/ResourceType;Lnet/minecraft/util/Identifier;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void examineResourcePack(Identifier id, CallbackInfoReturnable<Resource> cir, ResourcePack mcmetaPack, Identifier mcmetaId, int index, ResourcePack pack) {
		if(pack instanceof UpscalingResourcePack && state.get() == ExaminingResourceState.PNG) {
			if(((UpscalingResourcePack) pack).interestedInUpscaling(id)) {
				state.set(ExaminingResourceState.UPSCALE);
			} else {
				state.set(ExaminingResourceState.DONT_UPSCALE);
			}
		}
	}
	
	@Inject(method = "getResource", at = @At(value = "RETURN"))
	private void heckResource(Identifier id, CallbackInfoReturnable<Resource> cir) {
		if(state.get() == ExaminingResourceState.UPSCALE) {
			((ResourceImplExt) cir.getReturnValue()).upscaleMe();
		}
		
		state.remove();
	}
}
