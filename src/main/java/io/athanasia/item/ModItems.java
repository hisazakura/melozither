package io.athanasia.item;

import io.athanasia.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModItems {
    public static final RegistryKey<Item> GUZHENG_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, ModBlocks.GUZHENG_BLOCK_KEY.getValue());
    public static final BlockItem GUZHENG_ITEM = new BlockItem(ModBlocks.GUZHENG_BLOCK, new Item.Settings().registryKey(GUZHENG_ITEM_KEY));

    public static void initialize() {
        Registry.register(Registries.ITEM, GUZHENG_ITEM_KEY, GUZHENG_ITEM);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.add(GUZHENG_ITEM);
        });
    }
}
