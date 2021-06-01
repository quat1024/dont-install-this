package agency.highlysuspect.unfaithful;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Rules {
	public Rules(List<Rule> rules) {
		this.rules = rules;
	}
	
	public interface Rule {
		void applyTo(Identifier id, Settings settings);
	}
	
	private final List<Rule> rules;
	
	public Settings getSettingsFor(Identifier id) {
		Settings s = new Settings();
		for(Rule rule : rules) rule.applyTo(id, s);
		return s;
	}
	
	public static Rules readRules(Path path) throws IOException {
		List<Rule> rules = new ArrayList<>();
		
		Files.lines(path).forEach(lineU -> {
			String line = lineU.trim();
			if(line.startsWith("#") || line.isEmpty()) return;
			
			String[] split = line.split(" ");
			
			final String namespace = split[0].split(":")[0];
			final String pathPrefix = split[0].split(":")[1];
			
			TriState ignore = TriState.DEFAULT;
			TriState clamp = TriState.DEFAULT;
			
			for(int i = 1; i < split.length; i++) {
				String s = split[i];
				
				if(s.equalsIgnoreCase("ignore")) ignore = TriState.TRUE;
				if(s.equalsIgnoreCase("!ignore")) ignore = TriState.FALSE;
				else if(s.equalsIgnoreCase("clamp")) clamp = TriState.TRUE;
				else if(s.equalsIgnoreCase("!clamp")) clamp = TriState.FALSE;
			}
			
			final TriState finalIgnore = ignore;
			final TriState finalClamp = clamp;
			rules.add((id, settings) -> {
				if(id.getNamespace().equals(namespace) && id.getPath().startsWith(pathPrefix)) {
					if(finalIgnore != TriState.DEFAULT) settings.ignore = finalIgnore.get();
					if(finalClamp != TriState.DEFAULT) settings.clamp = finalClamp.get();
				}
			});
		});
		
		return new Rules(rules);
	}
}
