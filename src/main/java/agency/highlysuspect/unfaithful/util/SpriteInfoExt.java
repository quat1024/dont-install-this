package agency.highlysuspect.unfaithful.util;

import agency.highlysuspect.unfaithful.UnfaithfulSettings;
import net.minecraft.client.texture.Sprite;

public interface SpriteInfoExt {
	void setWidth(int width);
	void setHeight(int height);
	
	void setUnfaithfulSettings(UnfaithfulSettings settings);
	UnfaithfulSettings getUnfaithfulSettings();
	
	@SuppressWarnings("ConstantConditions")
	static SpriteInfoExt cast(Sprite.Info info) {
		return (SpriteInfoExt) (Object) info;
	}
}
