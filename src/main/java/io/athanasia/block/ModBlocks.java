package io.athanasia.block;

import io.athanasia.MeloZither;
import io.athanasia.block.custom.guzheng.GuzhengBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final RegistryKey<Block> GUZHENG_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK,
			Identifier.of(MeloZither.MOD_ID, "guzheng"));
	public static final Block GUZHENG_BLOCK = Blocks.register(GUZHENG_BLOCK_KEY, GuzhengBlock::new,
			AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD)
					.strength(0.2F)
					.nonOpaque()
					.burnable()
					.pistonBehavior(PistonBehavior.DESTROY));

	public static void initialize() {
	}
}
