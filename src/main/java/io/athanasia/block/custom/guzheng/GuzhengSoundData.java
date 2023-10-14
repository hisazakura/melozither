package io.athanasia.block.custom.guzheng;

import net.minecraft.sound.SoundEvent;

public class GuzhengSoundData {
	public SoundEvent SOUND;
	public float PITCH;

	public GuzhengSoundData(SoundEvent sound, float pitch) {
		this.SOUND = sound;
		this.PITCH = pitch;
	}
}
