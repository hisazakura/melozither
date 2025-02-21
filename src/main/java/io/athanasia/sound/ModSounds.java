package io.athanasia.sound;

import io.athanasia.MeloZither;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
	public static final SoundEvent GUZHENG_PLUCK1 = registerSoundEvent("guzheng_pluck1");
	public static final SoundEvent GUZHENG_PLUCK2 = registerSoundEvent("guzheng_pluck2");
	public static final SoundEvent GUZHENG_PLUCK3 = registerSoundEvent("guzheng_pluck3");

	private static SoundEvent registerSoundEvent(String name) {
		Identifier id = Identifier.of(MeloZither.MOD_ID, name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	public static void register() {
		return;
	}
}
