package agency.highlysuspect.unfaithful.util;

import agency.highlysuspect.unfaithful.UnfaithfulSettings;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;

public interface SpriteInfoExt {
	void setWidth(int width);
	void setHeight(int height);
	
	AnimationResourceMetadata getAnimationResourceMetadata();
	
	void setUnfaithfulSettings(UnfaithfulSettings settings);
	UnfaithfulSettings getUnfaithfulSettings();
	
	@SuppressWarnings("ConstantConditions")
	static SpriteInfoExt cast(Sprite.Info info) {
		return (SpriteInfoExt) (Object) info;
	}
}
