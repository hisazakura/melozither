package io.athanasia.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ModBlockRenderLayer extends ModBlocks {
	public static void setBlockRenderLayer() {
		BlockRenderLayerMap.INSTANCE.putBlock(GUZHENG_BLOCK, RenderLayer.getCutout());
	}
}
