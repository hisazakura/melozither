package io.athanasia.block;

import io.athanasia.MeloZither;
import io.athanasia.block.custom.guzheng.GuzhengBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final Block GUZHENG_BLOCK = registerBlock("guzheng",
			new GuzhengBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(MeloZither.MOD_ID, name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(MeloZither.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static void register() {
		// add to functional group
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
			content.add(ModBlocks.GUZHENG_BLOCK);
		});
	}
}
