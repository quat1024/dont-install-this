package agency.highlysuspect.unfaithful.util;

import net.minecraft.client.texture.Sprite;

public interface SpriteInfoExt {
	void setWidth(int width);
	void setHeight(int height);
	
	@SuppressWarnings("ConstantConditions")
	static SpriteInfoExt cast(Sprite.Info info) {
		return (SpriteInfoExt) (Object) info;
	}
}
