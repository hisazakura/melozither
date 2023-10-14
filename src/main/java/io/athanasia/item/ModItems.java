package io.athanasia.item;

import io.athanasia.Athanasia;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Athanasia.MOD_ID, name), item);
	}

	public static void register() {
		Athanasia.LOGGER.info("Registering mod items for " + Athanasia.MOD_ID);
	}
}
