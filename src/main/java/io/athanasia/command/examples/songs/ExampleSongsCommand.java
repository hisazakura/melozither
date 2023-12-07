package io.athanasia.command.examples.songs;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.athanasia.MeloZither;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ExampleSongsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
			RegistrationEnvironment environment) {
		dispatcher.register(
				CommandManager.literal("melozither")
						.then(CommandManager.literal("examples")
								.then(CommandManager.argument("id", StringArgumentType.string())
										.suggests(ExampleSongsSuggestionProvider::getSuggestions)
										.executes(ExampleSongsCommand::getExampleSongs))));
	}

	public static int getExampleSongs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null)
			return 1;

		String songId = StringArgumentType.getString(context, "id");

		List<ExampleSong> examples = ExampleSongs.getAllSongs();
		for (ExampleSong example : examples) {
			MeloZither.LOGGER.info(String.valueOf(songId.equals(example.id())));
			if (songId.equals(example.id())) {
				player.giveItemStack(example.getSongBook());
				return 1;
			}
		}

		player.sendMessage(Text.literal("Song doesn't exist"));
		return 1;
	}
}
