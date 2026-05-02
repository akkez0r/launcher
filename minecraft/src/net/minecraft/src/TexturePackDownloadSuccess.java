package net.minecraft.src;

import java.io.File;

class TexturePackDownloadSuccess implements IDownloadSuccess {
	final TexturePackList texturePacks;

	TexturePackDownloadSuccess(TexturePackList var1) {
		this.texturePacks = var1;
	}

	public void onSuccess(File var1) {
		if(TexturePackList.isDownloading(this.texturePacks)) {
			TexturePackList.setSelectedTexturePack(this.texturePacks, new TexturePackCustom(TexturePackList.generateTexturePackID(this.texturePacks, var1), var1, TexturePackList.func_98143_h()));
			TexturePackList.getMinecraft(this.texturePacks).scheduleTexturePackRefresh();
		}
	}
}
