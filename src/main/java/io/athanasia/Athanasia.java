package io.athanasia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.athanasia.block.ModBlockEntities;
import io.athanasia.block.ModBlocks;
import io.athanasia.item.ModItemGroups;
import io.athanasia.item.ModItems;
import io.athanasia.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

public class Athanasia implements ModInitializer{
	public static final String MOD_ID = "athanasia";
	public static final Logger LOGGER = LoggerFactory.getLogger("Athanasia");

	@Override
	public void onInitialize() {
		ModItemGroups.register();

		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModSounds.register();
		
		LOGGER.info("Athanasia initialized!");
	}
}
