package io.athanasia.block.custom.guzheng;

import io.athanasia.sound.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class GuzhengNote {
	private String sound;
	private float pitch;

	public GuzhengNote(String sound, float pitch) {
		this.sound = sound;
		this.pitch = pitch;
	}

	public GuzhengNote(SoundEvent sound, float pitch) {
		if (sound == ModSounds.GUZHENG_PLUCK1) this.sound = "guzheng_pluck1";
		if (sound == ModSounds.GUZHENG_PLUCK2) this.sound = "guzheng_pluck2";
		if (sound == ModSounds.GUZHENG_PLUCK3) this.sound = "guzheng_pluck3";
		this.pitch = pitch;
	}

	public String getSound() {
		return sound;
	}

	public float getPitch() {
		return pitch;
	}

	public SoundEvent getSoundEvent() {
		switch (sound) {
			case "guzheng_pluck1":
				return ModSounds.GUZHENG_PLUCK1;
			case "guzheng_pluck2":
				return ModSounds.GUZHENG_PLUCK2;
			case "guzheng_pluck3":
				return ModSounds.GUZHENG_PLUCK3;
		}
		return SoundEvents.INTENTIONALLY_EMPTY;
	}
}
