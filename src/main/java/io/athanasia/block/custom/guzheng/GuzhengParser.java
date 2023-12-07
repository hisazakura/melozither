package io.athanasia.block.custom.guzheng;

import java.util.HashMap;

import io.athanasia.sound.ModSounds;

public class GuzhengParser {
	private static final HashMap<String, GuzhengNote> NOTE_LOOKUP = new HashMap<String, GuzhengNote>() {
		{
			put("x", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, -4.0 / 12)));
			put("c", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, -2.0 / 12)));
			put("v", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 0)));
			put("^v", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 1.0 / 12)));
			put("b", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 3.0 / 12)));
			put("n", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 5.0 / 12)));
			put("^n", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 7.0 / 12)));
			put("a", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 8.0 / 12)));
			put("s", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 10.0 / 12)));
			put("d", new GuzhengNote(ModSounds.GUZHENG_PLUCK1, (float) Math.pow(2, 1)));

			put("^d", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -11.0 / 12)));
			put("f", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -9.0 / 12)));
			put("g", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -7.0 / 12)));
			put("^g", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -5.0 / 12)));
			put("h", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -4.0 / 12)));
			put("j", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, -2.0 / 12)));
			put("k", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 0)));
			put("^k", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 1.0 / 12)));
			put("l", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 3.0 / 12)));
			put(";", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 5.0 / 12)));
			put("^;", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 7.0 / 12)));
			put("e", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 8.0 / 12)));
			put("r", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 10.0 / 12)));
			put("t", new GuzhengNote(ModSounds.GUZHENG_PLUCK2, (float) Math.pow(2, 1)));

			put("^t", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float) Math.pow(2, -11.0 / 12)));
			put("y", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float) Math.pow(2, -9.0 / 12)));
			put("u", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float) Math.pow(2, -7.0 / 12)));
			put("^u", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float) Math.pow(2, -5.0 / 12)));
			put("i", new GuzhengNote(ModSounds.GUZHENG_PLUCK3, (float) Math.pow(2, -4.0 / 12)));
		}
	};

	private enum ParserState {
		PLAY, CHANGE_INTERVAL
	}

	public static GuzhengSongData parse(String script) {
		GuzhengSongData songData = new GuzhengSongData();

		int tick = 0;
		int interval = 4;
		int previous_interval = -1;
		boolean bend = false;
		ParserState state = ParserState.PLAY;
		StringBuilder noteString = new StringBuilder();

		for (char character : script.toCharArray()) {
			if (character == '<') {
				state = ParserState.CHANGE_INTERVAL;
				interval = 0;
				continue;
			}
			if (character == '>') {
				state = ParserState.PLAY;
				if (interval == 0)
					throw new IllegalArgumentException("Invalid interval: " + interval);
				continue;
			}
			if (state == ParserState.CHANGE_INTERVAL) {
				if (!Character.isDigit(character))
					throw new IllegalArgumentException("Invalid interval:" + character);
				interval = interval * 10 + (character - '0');
				continue;
			}
			if (character == '[') {
				if (interval == 0)
					throw new IllegalArgumentException("Invalid double bracket");
				previous_interval = interval;
				interval = 0;
				continue;
			}
			if (character == ']') {
				if (previous_interval < 0)
					throw new IllegalArgumentException("Opening bracket not found");
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

			if (bend)
				noteString.append('^');
			noteString.append(character);
			GuzhengNote note = NOTE_LOOKUP.get(noteString.toString());

			if (note == null)
				throw new IllegalArgumentException("Invalid character: " + character);

			songData.addNote(tick, note);

			noteString.setLength(0);

			bend = false;
			tick += interval;
		}

		return songData;
	}
}
