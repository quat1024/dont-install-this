package agency.highlysuspect.unfaithful;

import agency.highlysuspect.unfaithful.upscale.Upscaler;
import agency.highlysuspect.unfaithful.upscale.algo.Epx;
import agency.highlysuspect.unfaithful.upscale.algo.Xbr;

import java.io.IOException;
import java.nio.file.Path;

public class Config {
	public Config(Path path) throws IOException {
		rules = Rules.readRules(path);
	}
	
	public Upscaler upscaler = Epx.DEFAULT;
	public Rules rules;
}
