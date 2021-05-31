package agency.highlysuspect.unfaithful.mixin;

import agency.highlysuspect.unfaithful.util.SpriteInfoExt;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Sprite.Info.class)
public class SpriteInfoMixin implements SpriteInfoExt {
	@Shadow @Final @Mutable private int width;
	@Shadow @Final @Mutable private int height;
	@Shadow @Final private AnimationResourceMetadata animationData;
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
}
