package io.athanasia.block.custom.guzheng;

import java.util.HashMap;

import io.athanasia.sound.ModSounds;

public class GuzhengParser {
	private static final HashMap<String, GuzhengNote> NOTE_LOOKUP = new HashMap<String, GuzhengNote>() {{
		put("x", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, -4.0/12)));
		put("c", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, -2.0/12)));
		put("v", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 0)));
		put("^v", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 1.0/12)));
		put("b", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 3.0/12)));
		put("n", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 5.0/12)));
		put("^n", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 7.0/12)));
		put("a", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 8.0/12)));
		put("s", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 10.0/12)));
		put("d", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float)Math.pow(2, 1)));

		put("^d", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -11.0/12)));
		put("f", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -9.0/12)));
		put("g", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -7.0/12)));
		put("^g", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -5.0/12)));
		put("h", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -4.0/12)));
		put("j", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, -2.0/12)));
		put("k", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 0)));
		put("^k", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 1.0/12)));
		put("l", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 3.0/12)));
		put(";", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 5.0/12)));
		put("^;", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 7.0/12)));
		put("e", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 8.0/12)));
		put("r", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 10.0/12)));
		put("t", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float)Math.pow(2, 1)));

		put("^t", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float)Math.pow(2, -11.0/12)));
		put("y", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float)Math.pow(2, -9.0/12)));
		put("u", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float)Math.pow(2, -7.0/12)));
		put("^u", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float)Math.pow(2, -5.0/12)));
		put("i", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float)Math.pow(2, -4.0/12)));
	}};

	public static GuzhengSongData parse(String script) {
		GuzhengSongData songData = new GuzhengSongData();

		int tick = 0;
		int interval = 4;
		int previous_interval = -1;
		boolean bend = false;
		String state = "play";

		for (char character : script.toCharArray()) {
			if (character == '<') {
				state = "change_interval";
				interval = 0;
				continue;
			}
			if (character == '>') {
				state = "play";
				if (interval == 0) throw new IllegalArgumentException("Invalid interval");
				continue;
			}
			if (state == "change_interval") {
				if (!Character.isDigit(character)) throw new IllegalArgumentException("Invalid interval");
				interval = interval * 10 + (character - '0');
				continue;
			}
			if (character == '[') {
				if (interval == 0) throw new IllegalArgumentException("Double bracket");
				previous_interval = interval;
				interval = 0;
				continue;
			}
			if (character == ']') {
				if (previous_interval < 0) throw new IllegalArgumentException("No opening bracket");
				interval = previous_interval;
				previous_interval = -1;
				tick += interval;
				continue;
			}
			if (character == ' ') {
				tick += interval;
				continue;
			}
			if (character == '^') {
				bend = true;
				continue;
			}
			String noteString = bend ? "^" + String.valueOf(character) : String.valueOf(character);
			if (!NOTE_LOOKUP.containsKey(noteString)) throw new IllegalArgumentException("Invalid character: " + character);
			songData.addNote(tick, NOTE_LOOKUP.get(noteString));
			bend = false;
			tick += interval;
		}

		return songData;
	}
}
