package agency.highlysuspect.unfaithful.resource;

import agency.highlysuspect.unfaithful.Init;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class UpscalingResourcePack implements ResourcePack {
	public boolean interestedInUpscaling(Identifier id) {
		//Init.LOG.info("interestedInUpscaling {}", id);
		return !Init.config.rules.getSettingsFor(id).ignore;
	}
	
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		//If anyone has any better ideas for where to put these, hmu, lol
		if(fileName.equals("pack.png")) {
			return Init.class.getClassLoader().getResourceAsStream("unfaithful-builtin/pack.png");
		} else if(fileName.equals("pack.mcmeta")) {
			return Init.class.getClassLoader().getResourceAsStream("unfaithful-builtin/pack.mcmeta");
		}
		
		throw new FileNotFoundException("UpscalingResourcePack - tried to openRoot " + fileName);
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		throw new FileNotFoundException("UpscalingResourcePack doesn't actually contain anything - tried to open " + id.toString());
	}
	
	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		return Collections.emptySet();
	}
	
	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return false;
	}
	
	@Override
	public Set<String> getNamespaces(ResourceType type) {
		//TODO!
		return ImmutableSet.of("minecraft", "realms");
	}
	
	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		try(InputStream mcmeta = openRoot("pack.mcmeta")) {
			return AbstractFileResourcePack.parseMetadata(metaReader, mcmeta);
		}
	}
	
	@Override
	public String getName() {
		return "Upscaling Pack";
	}
	
	@Override
	public void close() {
		//Nothing to do.
	}
}
