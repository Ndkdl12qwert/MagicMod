package com.example.examplemod;

import com.mojang.logging.LogUtils;
import com.example.examplemod.MagicAxe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(MagicMod.MODID)
public class MagicMod {
    public static final String MODID = "magicmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> MAGIC_SWORD = ITEMS.register(
            "magic_sword",
            () -> new MagicSword()
    );

    public static final RegistryObject<Item> MAGIC_PICKAXE = ITEMS.register(
            "magic_pickaxe",
            () -> new MagicPickaxe()
    );

    public static final RegistryObject<Item> MAGIC_AXE = ITEMS.register(
            "magic_axe",
            () -> new MagicAxe()
    );

    public MagicMod() {
        LOGGER.info("§6§l╔════════════════════════════════╗");
        LOGGER.info("§6§l║     G O D S W O R D         ║");
        LOGGER.info("§6§l║        神·权·降·临           ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品
        ITEMS.register(modEventBus);

        // ✨ 注册创造标签页（用您已经写好的类！）
        ModCreativeTab.register(modEventBus);

        // 注册无敌事件
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("§6§lGODSWORD §r§7- 神权协议已激活");
        LOGGER.info("§6§lGODSWORD §r§7- 专属神权展柜已注册！");
        LOGGER.info("");
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            if (mainHand.getItem() instanceof MagicSword || offHand.getItem() instanceof MagicSword) {
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth());
                player.removeAllEffects();
                player.invulnerableTime = 40;
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("§6§l╔════════════════════════════════╗");
        LOGGER.info("§6§l║     G O D S W O R D          ║");
        LOGGER.info("§6§l║      神 · 权 · 覆 · 盖       ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("§6§lGODSWORD §r§7- 手持此剑，即为神明");
    }
}