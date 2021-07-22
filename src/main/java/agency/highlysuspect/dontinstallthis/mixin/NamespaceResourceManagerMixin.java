package agency.highlysuspect.dontinstallthis.mixin;

import agency.highlysuspect.dontinstallthis.resource.ResourceImplExt;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
	@Inject(method = "getResource", at = @At(value = "RETURN"))
	private void heckResource(Identifier id, CallbackInfoReturnable<Resource> cir) {
		if(id.getPath().startsWith("textures/block")) {
			((ResourceImplExt) cir.getReturnValue()).ohNo();
		}
	}
}
