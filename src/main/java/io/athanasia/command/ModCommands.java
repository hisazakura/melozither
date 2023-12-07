package io.athanasia.command;

import io.athanasia.command.examples.songs.ExampleSongsCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
	public static void register() {
		CommandRegistrationCallback.EVENT.register(ExampleSongsCommand::register);
	}
}
