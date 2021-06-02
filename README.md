# unfaithful

heyyyyyyyy, i don't wanna see you un faith ful

rewrite of asie's old [FaithlessMC](https://github.com/asiekierka/FaithlessMC) for fabric 1.16.5.

Automatically upscales every texture in the game using ~~scale2x~~ 2xbr

Not done yet!

## license

lgpl 3. (FaithlessMC is also lgpl; i didn't look at its source code though, i just happen to also like LGPL for mc mods.)

## issues

* overall issues with "things scale up weirdly"
	* entity models sometimes look weird
	* blocks with detailed models might get messed up
	* animated textures don't upscale each piece individually (there's code for this but it's a bit broke, and disabled rn)
	* Beds look weird
	* Armor models have stray pixels
	* Chainmail armor looks horrendous
* Cobblestone, in particular, looks like crap for some reason under 2xbr? (i think: just unlucky)
* config file situation isn't great
* Need to specify resource domains up-front so the resourcepack gets put in all the namespaceresourcemanagers... not great

### idea pile

* Make the rule system less ass
	* Wildcarding? (think about perf)
	* definitely needs a "first-match" system
* Noise filter, that tries to detect noisy textures (grass blocks) and introduce a similar-feeling high-res noise pattern instead of upscaling what's there (artifacty)
* Clamp-to-transparent. Might make books look better than clamp-to-edge
* Checkerboard filter that tries to find checkerboards and scale them up
	* Was thinking "4 surrounding pixels" but maybe allowing for 3 could be interesting
	* Dear god, chainmail looks awful with the current system
* Gotta think about clamp-to-arbitrary-box as well... so many uses (entity textures, fonts, armor models). Wont be too hard to add in theory (wrap-around-arbitrary-box is harder) but in practice assigning textures to use a clamp-to-boxes system will be hard and there's soooo many..
	* IS THERE A WAY.. to detect how each block entity renderer picks cubes out from each texture, and export that data somehow... Will be super cool if i can pull that off

Dump generated textures to an on-disk resourcepack? (ui for that?)

Profile memory usage. Do the upscaled images go away after being loaded? Any leaks?