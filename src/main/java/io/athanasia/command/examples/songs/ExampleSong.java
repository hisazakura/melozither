package io.athanasia.command.examples.songs;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public record ExampleSong(String id, String title, String author, String[] script) {
	public ExampleSong(String id, String title, String author, String script) {
		this(id, title, author, splitEqualLength(script, 250));
	}

	public ItemStack getSongBook() {
		NbtString title = NbtString.of(this.title);
		NbtString author = NbtString.of(this.author);

		NbtList pages = new NbtList();
		for (String page : script) {
			pages.add(NbtString.of(String.format("{\"text\":\"%s\"}", page)));
		}

		ItemStack songBook = new ItemStack(Items.WRITTEN_BOOK, 1);
		songBook.setSubNbt("title", title);
		songBook.setSubNbt("author", author);
		songBook.setSubNbt("pages", pages);

		return songBook;
	}

	private static String[] splitEqualLength(String str, int length) {
		String regex = String.format("(?<=\\G.{%s})", length);
		return str.split(regex);
	}

}
