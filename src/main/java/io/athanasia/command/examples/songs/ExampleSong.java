package io.athanasia.command.examples.songs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;

public record ExampleSong(String id, String title, String author, String[] script) {
	public ExampleSong(String id, String title, String author, String script) {
		this(id, title, author, splitEqualLength(script, 250));
	}

	public ItemStack getSongBook() {
		RawFilteredPair<String> title = RawFilteredPair.of(this.title);
		List<RawFilteredPair<Text>> pages = new ArrayList<>();
		for (String page : script) {
			pages.add(RawFilteredPair.of(Text.literal(page)));
		}

		WrittenBookContentComponent content = new WrittenBookContentComponent(title, author, 0, pages, false);
		ItemStack songBook = new ItemStack(Items.WRITTEN_BOOK, 1);
		songBook.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, content);

		return songBook;
	}

	private static String[] splitEqualLength(String str, int length) {
		String regex = String.format("(?<=\\G.{%s})", length);
		return str.split(regex);
	}

}
