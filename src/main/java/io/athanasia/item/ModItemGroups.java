package io.athanasia.item;

import java.util.Arrays;
import java.util.List;

import io.athanasia.Athanasia;
import io.athanasia.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
	public static final ItemGroup ATHANASIA_GROUP = registerItemGroup("athanasia",
			Text.translatable("itemgroup.athanasia"),
			new ItemStack(ModBlocks.BLACK_BOX),
			Arrays.asList(
				ModBlocks.BLACK_BOX,
				ModBlocks.GUZHENG_BLOCK
				// add more items
			));

	private static ItemGroup registerItemGroup(String itemGroupId, Text displayName, ItemStack displayItem,
			List<ItemConvertible> items) {
		return Registry.register(Registries.ITEM_GROUP,
				new Identifier(Athanasia.MOD_ID, itemGroupId),
				FabricItemGroup.builder().displayName(displayName)
						.icon(() -> displayItem).entries((displayContext, entries) -> {
							for (ItemConvertible item : items) {
								entries.add(item);
							}
						}).build());
	}

	public static void register() {
		Athanasia.LOGGER.info("Registering item groups for " + Athanasia.MOD_ID);
	}
}
