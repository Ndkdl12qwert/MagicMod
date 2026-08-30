package com.example.examplemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlackGoldMod.MODID);

    public static final RegistryObject<CreativeModeTab> BLACKGOLD_TAB = CREATIVE_MODE_TABS.register(
            "blackgold_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(BlackGoldMod.BLACKGOLD_SWORD.get()))
                    .title(Component.translatable("creativetab.blackgoldmod"))
                    .displayItems((parameters, output) -> {
                        // 武器
                        output.accept(BlackGoldMod.BLACKGOLD_SWORD.get());
                        output.accept(BlackGoldMod.BLACKGOLD_PICKAXE.get());
                        output.accept(BlackGoldMod.BLACKGOLD_AXE.get());
                        // 盔甲
                        output.accept(BlackGoldMod.BLACKGOLD_HELMET.get());
                        output.accept(BlackGoldMod.BLACKGOLD_CHESTPLATE.get());
                        output.accept(BlackGoldMod.BLACKGOLD_LEGGINGS.get());
                        output.accept(BlackGoldMod.BLACKGOLD_BOOTS.get());
                        // 材料
                        output.accept(BlackGoldMod.BLACKGOLD_NUGGET.get());
                        output.accept(BlackGoldMod.BLACKGOLD_INGOT.get());
                        output.accept(BlackGoldMod.GOD_BLACKGOLD_UPGRADE_TEMPLATE.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}