package agency.highlysuspect.unfaithful.resource;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.function.Consumer;

public class UpscalingResourcePackCreator implements ResourcePackProvider {
	public static final UpscalingResourcePackCreator INSTANCE = new UpscalingResourcePackCreator();
	public static final ResourcePackSource src = text -> new TranslatableText("pack.nameAndSource", text, new LiteralText("Unfaithful"));
	
	@Override
	public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		consumer.accept(ResourcePackProfile.of(
			"Unfaithful",
			false, 
			UpscalingResourcePack::new,
			factory,
			ResourcePackProfile.InsertionPosition.TOP,
			src
		));
	}
}
