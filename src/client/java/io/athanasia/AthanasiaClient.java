package io.athanasia;

import io.athanasia.block.ModBlockRenderLayer;
import net.fabricmc.api.ClientModInitializer;

public class AthanasiaClient implements ClientModInitializer{

	@Override
	public void onInitializeClient() {
		ModBlockRenderLayer.setBlockRenderLayer();
	}
	
}
