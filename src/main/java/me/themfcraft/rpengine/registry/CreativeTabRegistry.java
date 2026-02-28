package me.themfcraft.rpengine.registry;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPEngine.MODID);

    public static final RegistryObject<CreativeModeTab> RP_TAB = CREATIVE_MODE_TABS.register("rp_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ItemRegistry.CASH.get()))
            .title(Component.translatable("itemGroup.rpengine"))
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.CASH.get());
                output.accept(ItemRegistry.ID_CARD.get());
                output.accept(ItemRegistry.RADIO.get());
                output.accept(ItemRegistry.BANDAGE.get());
                output.accept(ItemRegistry.HANDCUFFS.get());
                output.accept(ItemRegistry.LOCKPICK.get());
                output.accept(ItemRegistry.ATM.get());
            }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
