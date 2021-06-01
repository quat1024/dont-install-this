package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.resource.UpscalingResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ClientBuiltinResourcePackProviderMixin {
	@Inject(method = "register", at = @At("RETURN"))
	private void addBuiltinResourcePacks(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory, CallbackInfo ci) {
		//yoinked this mixin from fabric-api
		//Add magic upscaling pack
		UpscalingResourcePackCreator.INSTANCE.register(consumer, factory);
	}
}
