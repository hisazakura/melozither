package io.athanasia.command.examples.songs;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class ExampleSongsSuggestionProvider {
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        List<ExampleSong> examples = ExampleSongs.getAllSongs();

		for (ExampleSong example : examples) {
			builder.suggest(example.id());
		}
 
        return builder.buildFuture();
    }
}
