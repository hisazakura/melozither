package io.athanasia.block;

import io.athanasia.Athanasia;
import io.athanasia.block.custom.guzheng.GuzhengBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final Block BLACK_BOX = registerBlock("black_box",
			new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

	public static final Block GUZHENG_BLOCK = registerBlock("guzheng",
			new GuzhengBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Athanasia.MOD_ID, name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(Athanasia.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static void register() {
		Athanasia.LOGGER.info("Registering blocks for " + Athanasia.MOD_ID);
	}
}
