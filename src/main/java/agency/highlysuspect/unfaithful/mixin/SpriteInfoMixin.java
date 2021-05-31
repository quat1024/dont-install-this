package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.UnfaithfulSettings;
import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.*;

@Mixin(Sprite.Info.class)
public class SpriteInfoMixin implements SpriteInfoExt {
	@Shadow @Final @Mutable private int width;
	@Shadow @Final @Mutable private int height;
	@Shadow @Final private AnimationResourceMetadata animationData;
	
	@Unique private UnfaithfulSettings unfaithfulSettings;
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public AnimationResourceMetadata getAnimationResourceMetadata() {
		return animationData;
	}
	
	@Override
	public void setUnfaithfulSettings(UnfaithfulSettings settings) {
		this.unfaithfulSettings = settings;
	}
	
	@Override
	public UnfaithfulSettings getUnfaithfulSettings() {
		return unfaithfulSettings;
	}
}
