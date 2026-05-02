package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
	private static SoundSystem sndSystem;
	private SoundPool soundPoolSounds = new SoundPool();
	private SoundPool soundPoolStreaming = new SoundPool();
	private SoundPool soundPoolMusic = new SoundPool();
	private int latestSoundID = 0;
	private GameSettings options;
	private Set playingSounds = new HashSet();
	private List field_92072_h = new ArrayList();
	private static boolean loaded = false;
	private Random rand = new Random();
	private int ticksBeforeMusic = this.rand.nextInt(12000);

	public void loadSoundSettings(GameSettings var1) {
		this.soundPoolStreaming.isGetRandomSound = false;
		this.options = var1;
		if(!loaded && (var1 == null || var1.soundVolume != 0.0F || var1.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

	}

	private void tryToSetLibraryAndCodecs() {
		try {
			float var1 = this.options.soundVolume;
			float var2 = this.options.musicVolume;
			this.options.soundVolume = 0.0F;
			this.options.musicVolume = 0.0F;
			this.options.saveOptions();
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("mus", CodecMus.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			sndSystem = new SoundSystem();
			this.options.soundVolume = var1;
			this.options.musicVolume = var2;
			this.options.saveOptions();
		} catch (Throwable var3) {
			var3.printStackTrace();
			System.err.println("error linking with the LibraryJavaSound plug-in");
		}

		loaded = true;
	}

	public void onSoundOptionsChanged() {
		if(!loaded && (this.options.soundVolume != 0.0F || this.options.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

		if(loaded) {
			if(this.options.musicVolume == 0.0F) {
				sndSystem.stop("BgMusic");
				sndSystem.stop("streaming");
			} else {
				sndSystem.setVolume("BgMusic", this.options.musicVolume);
				sndSystem.setVolume("streaming", this.options.musicVolume);
			}
		}

	}

	public void closeMinecraft() {
		if(loaded) {
			sndSystem.cleanup();
		}

	}

	public void addSound(String var1, File var2) {
		this.soundPoolSounds.addSound(var1, var2);
	}

	public void addStreaming(String var1, File var2) {
		this.soundPoolStreaming.addSound(var1, var2);
	}

	public void addMusic(String var1, File var2) {
		this.soundPoolMusic.addSound(var1, var2);
	}

	public void playRandomMusicIfReady() {
		if(loaded && this.options.musicVolume != 0.0F) {
			if(!sndSystem.playing("BgMusic") && !sndSystem.playing("streaming")) {
				if(this.ticksBeforeMusic > 0) {
					--this.ticksBeforeMusic;
					return;
				}

				SoundPoolEntry var1 = this.soundPoolMusic.getRandomSound();
				if(var1 != null) {
					this.ticksBeforeMusic = this.rand.nextInt(12000) + 12000;
					sndSystem.backgroundMusic("BgMusic", var1.soundUrl, var1.soundName, false);
					sndSystem.setVolume("BgMusic", this.options.musicVolume);
					sndSystem.play("BgMusic");
				}
			}

		}
	}

	public void setListener(EntityLiving var1, float var2) {
		if(loaded && this.options.soundVolume != 0.0F) {
			if(var1 != null) {
				float var3 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
				float var4 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2;
				double var5 = var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2;
				double var7 = var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2;
				double var9 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2;
				float var11 = MathHelper.cos(-var4 * ((float)Math.PI / 180.0F) - (float)Math.PI);
				float var12 = MathHelper.sin(-var4 * ((float)Math.PI / 180.0F) - (float)Math.PI);
				float var13 = -var12;
				float var14 = -MathHelper.sin(-var3 * ((float)Math.PI / 180.0F) - (float)Math.PI);
				float var15 = -var11;
				float var16 = 0.0F;
				float var17 = 1.0F;
				float var18 = 0.0F;
				sndSystem.setListenerPosition((float)var5, (float)var7, (float)var9);
				sndSystem.setListenerOrientation(var13, var14, var15, var16, var17, var18);
			}
		}
	}

	public void stopAllSounds() {
		Iterator var1 = this.playingSounds.iterator();

		while(var1.hasNext()) {
			String var2 = (String)var1.next();
			sndSystem.stop(var2);
		}

		this.playingSounds.clear();
	}

	public void playStreaming(String var1, float var2, float var3, float var4) {
		if(loaded && (this.options.soundVolume != 0.0F || var1 == null)) {
			String var5 = "streaming";
			if(sndSystem.playing(var5)) {
				sndSystem.stop(var5);
			}

			if(var1 != null) {
				SoundPoolEntry var6 = this.soundPoolStreaming.getRandomSoundFromSoundPool(var1);
				if(var6 != null) {
					if(sndSystem.playing("BgMusic")) {
						sndSystem.stop("BgMusic");
					}

					float var7 = 16.0F;
					sndSystem.newStreamingSource(true, var5, var6.soundUrl, var6.soundName, false, var2, var3, var4, 2, var7 * 4.0F);
					sndSystem.setVolume(var5, 0.5F * this.options.soundVolume);
					sndSystem.play(var5);
				}

			}
		}
	}

	public void updateSoundLocation(Entity var1) {
		this.updateSoundLocation(var1, var1);
	}

	public void updateSoundLocation(Entity var1, Entity var2) {
		String var3 = "entity_" + var1.entityId;
		if(this.playingSounds.contains(var3)) {
			if(sndSystem.playing(var3)) {
				sndSystem.setPosition(var3, (float)var2.posX, (float)var2.posY, (float)var2.posZ);
				sndSystem.setVelocity(var3, (float)var2.motionX, (float)var2.motionY, (float)var2.motionZ);
			} else {
				this.playingSounds.remove(var3);
			}
		}

	}

	public boolean isEntitySoundPlaying(Entity var1) {
		if(var1 != null && loaded) {
			String var2 = "entity_" + var1.entityId;
			return sndSystem.playing(var2);
		} else {
			return false;
		}
	}

	public void stopEntitySound(Entity var1) {
		if(var1 != null && loaded) {
			String var2 = "entity_" + var1.entityId;
			if(this.playingSounds.contains(var2)) {
				if(sndSystem.playing(var2)) {
					sndSystem.stop(var2);
				}

				this.playingSounds.remove(var2);
			}

		}
	}

	public void setEntitySoundVolume(Entity var1, float var2) {
		if(var1 != null && loaded) {
			if(loaded && this.options.soundVolume != 0.0F) {
				String var3 = "entity_" + var1.entityId;
				if(sndSystem.playing(var3)) {
					sndSystem.setVolume(var3, var2 * this.options.soundVolume);
				}
			}
		}
	}

	public void setEntitySoundPitch(Entity var1, float var2) {
		if(var1 != null && loaded) {
			if(loaded && this.options.soundVolume != 0.0F) {
				String var3 = "entity_" + var1.entityId;
				if(sndSystem.playing(var3)) {
					sndSystem.setPitch(var3, var2);
				}
			}
		}
	}

	public void playEntitySound(String var1, Entity var2, float var3, float var4, boolean var5) {
		if(var2 != null) {
			if(loaded && (this.options.soundVolume != 0.0F || var1 == null)) {
				String var6 = "entity_" + var2.entityId;
				if(this.playingSounds.contains(var6)) {
					this.updateSoundLocation(var2);
				} else {
					if(sndSystem.playing(var6)) {
						sndSystem.stop(var6);
					}

					if(var1 == null) {
						return;
					}

					SoundPoolEntry var7 = this.soundPoolSounds.getRandomSoundFromSoundPool(var1);
					if(var7 != null && var3 > 0.0F) {
						float var8 = 16.0F;
						if(var3 > 1.0F) {
							var8 *= var3;
						}

						sndSystem.newSource(var5, var6, var7.soundUrl, var7.soundName, false, (float)var2.posX, (float)var2.posY, (float)var2.posZ, 2, var8);
						sndSystem.setLooping(var6, true);
						sndSystem.setPitch(var6, var4);
						if(var3 > 1.0F) {
							var3 = 1.0F;
						}

						sndSystem.setVolume(var6, var3 * this.options.soundVolume);
						sndSystem.setVelocity(var6, (float)var2.motionX, (float)var2.motionY, (float)var2.motionZ);
						sndSystem.play(var6);
						this.playingSounds.add(var6);
					}
				}

			}
		}
	}

	public void playSound(String var1, float var2, float var3, float var4, float var5, float var6) {
		if(loaded && this.options.soundVolume != 0.0F) {
			SoundPoolEntry var7 = this.soundPoolSounds.getRandomSoundFromSoundPool(var1);
			if(var7 != null && var5 > 0.0F) {
				this.latestSoundID = (this.latestSoundID + 1) % 256;
				String var8 = "sound_" + this.latestSoundID;
				float var9 = 16.0F;
				if(var5 > 1.0F) {
					var9 *= var5;
				}

				sndSystem.newSource(var5 > 1.0F, var8, var7.soundUrl, var7.soundName, false, var2, var3, var4, 2, var9);
				sndSystem.setPitch(var8, var6);
				if(var5 > 1.0F) {
					var5 = 1.0F;
				}

				sndSystem.setVolume(var8, var5 * this.options.soundVolume);
				sndSystem.play(var8);
			}

		}
	}

	public void playSoundFX(String var1, float var2, float var3) {
		if(loaded && this.options.soundVolume != 0.0F) {
			SoundPoolEntry var4 = this.soundPoolSounds.getRandomSoundFromSoundPool(var1);
			if(var4 != null) {
				this.latestSoundID = (this.latestSoundID + 1) % 256;
				String var5 = "sound_" + this.latestSoundID;
				sndSystem.newSource(false, var5, var4.soundUrl, var4.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
				if(var2 > 1.0F) {
					var2 = 1.0F;
				}

				var2 *= 0.25F;
				sndSystem.setPitch(var5, var3);
				sndSystem.setVolume(var5, var2 * this.options.soundVolume);
				sndSystem.play(var5);
			}

		}
	}

	public void pauseAllSounds() {
		Iterator var1 = this.playingSounds.iterator();

		while(var1.hasNext()) {
			String var2 = (String)var1.next();
			sndSystem.pause(var2);
		}

	}

	public void resumeAllSounds() {
		Iterator var1 = this.playingSounds.iterator();

		while(var1.hasNext()) {
			String var2 = (String)var1.next();
			sndSystem.play(var2);
		}

	}

	public void func_92071_g() {
		if(!this.field_92072_h.isEmpty()) {
			Iterator var1 = this.field_92072_h.iterator();

			while(var1.hasNext()) {
				ScheduledSound var2 = (ScheduledSound)var1.next();
				--var2.field_92064_g;
				if(var2.field_92064_g <= 0) {
					this.playSound(var2.field_92069_a, var2.field_92067_b, var2.field_92068_c, var2.field_92065_d, var2.field_92066_e, var2.field_92063_f);
					var1.remove();
				}
			}
		}

	}

	public void func_92070_a(String var1, float var2, float var3, float var4, float var5, float var6, int var7) {
		this.field_92072_h.add(new ScheduledSound(var1, var2, var3, var4, var5, var6, var7));
	}
}
