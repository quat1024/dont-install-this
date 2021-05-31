# unfaithful

heyyyyyyyy, i don't wanna see you un faith ful

rewrite of asie's old [FaithlessMC](https://github.com/asiekierka/FaithlessMC) for fabric 1.16.5.

Automatically upscales every texture in the game using scale2x.

## license

lgpl 3. (FaithlessMC is also lgpl; i didn't look at its source code though, i just happen to also like LGPL for mc mods.)

## internals

### Texture atlas upscaling

The texture atlas loading process is presented below; mixin points in *italic*. Here "model" is used in the sense of "a plan", and not like, an actual 3d model or anything.

* `SpriteAtlasTexture#stitch` is called
	* Profiler stage "extracting_frames"
		* *Uhh, Fabric API has a mixin somewhere around here, to let mods allocate arbitrary textures on the atlas*
		* Reads the PNG header(?) of all textures, to determine their width and height. No pixel data is read.
    	* Combines this information with a `xxx.png.mcmeta` file to produce an `AnimationResourceMetadata`, or a default meta if no file exists.
    	* Creates a `Sprite.Info` from this information.
    	* *`SpriteAtlasTextureMixin`: Upscale the width and height information of the `Sprite.Info` and `AnimationResourceMetadata`.*
		* A `TextureStitcher` consumes `Sprite.Info`s via `TextureStitcher#add`.
	* Profiler stage "register"
		* Adds the pink-and-black missing sprite to the `TextureStitcher`. Doesn't do anything else lol
	* Profiler stage "stitching"
		* TextureStitcher#stitch` runs and determines how textures will be placed on the atlas.
	* Profiler stage "loading"
		* Pixel data is read into `NativeImage`s.
		* *`SpriteAtlasTextureMixin`: Upscale that image, so its dimensions match what was promised, then free the original image.*
		* `Sprite` objects are allocated; these hold the x/y/width/height information from the texture stitcher model, and a reference to the NativeImage.
			* (or, in the case of animated sprites, `NativeImage`s for all keyframes in the animation. something like that?)
	* Return `SpriteAtlasTexture.Data` containing the total width and height of the atlas model, and a list of all `Sprite`s inside it.
* `SpriteAtlasTexture#upload` is called
	* The width and height of `SpriteAtlasTexture.Data` is checked, and a big-enough texture is allocated on the GPU
	* Each `Sprite` writes its pixel contents to the texture via `Sprite#upload`.

In short:

* Promise the texture atlas that sprites are bigger than they are.
* Upscale images as they come in to fulfill that promise.

It does *not* upscale the entire texture atlas as one big image:

* Adjacent textures could bleed in to each other without extra consideration.
* I'd need to allocate one texture for all the `Sprite`s to write into, then allocate a twice-as-big one for the upscaled version.

### Entity model upscaling?

Doesn't do that right now. The caveat is that many different parts of each entity are stored *adjacent* to each other in the entity's texture, so they'd bleed in to each other with a simple upscale.

Possible avenues:

* Idk, lazily upscaling the whole thing might look decent enough?
* Avenues that require some feedback from the model renderer:
	* upscale each face of each cube individually.
	* upscale each cube, taking into account the way textures wrap around the edge of the cube