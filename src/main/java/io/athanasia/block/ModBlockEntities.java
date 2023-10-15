package io.athanasia.block;

import io.athanasia.MeloZither;
import io.athanasia.block.custom.guzheng.GuzhengBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
	public static final BlockEntityType<GuzhengBlockEntity> GUZHENG_BLOCK_ENTITY = Registry
			.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MeloZither.MOD_ID, "guzheng"),
			FabricBlockEntityTypeBuilder.create(GuzhengBlockEntity::new, ModBlocks.GUZHENG_BLOCK).build(null));

	public static void register() {
		return;
	}
}
