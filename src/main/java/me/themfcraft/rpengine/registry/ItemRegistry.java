package me.themfcraft.rpengine.registry;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.chat.RadioItem;
import me.themfcraft.rpengine.economy.IDCardItem;
import me.themfcraft.rpengine.registry.BlockRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RPEngine.MODID);

    public static final RegistryObject<Item> RADIO = ITEMS.register("radio", RadioItem::new);
    public static final RegistryObject<Item> CASH = ITEMS.register("cash", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ID_CARD = ITEMS.register("id_card", () -> new IDCardItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ATM = ITEMS.register("atm", () -> new BlockItem(BlockRegistry.ATM.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
