package io.athanasia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.athanasia.block.ModBlockEntities;
import io.athanasia.block.ModBlocks;
import io.athanasia.command.ModCommands;
import io.athanasia.item.ModItems;
import io.athanasia.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

public class MeloZither implements ModInitializer{
	public static final String MOD_ID = "melozither";
	public static final Logger LOGGER = LoggerFactory.getLogger("MeloZither");

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModItems.initialize();
		ModSounds.register();
		ModCommands.register();
		
		LOGGER.info("MeloZither initialized!");
	}
}
