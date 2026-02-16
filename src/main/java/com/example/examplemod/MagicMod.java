package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
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

    // ===== 物品注册 =====
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
        LOGGER.info("§6§l║      神 · 权 · 降 · 临       ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品
        ITEMS.register(modEventBus);

        // 注册创造标签页
        ModCreativeTab.register(modEventBus);

        // 注册事件监听
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("§6§lGODSWORD §r§7- 神权协议已激活");
        LOGGER.info("§6§lGODSWORD §r§7- 专属神权展柜已注册！");
        LOGGER.info("");
    }

    // ===== 🛡️ 拦截一切伤害 =====
    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && hasGodItem(player)) {
            event.setCanceled(true);
            player.setHealth(player.getMaxHealth());
            player.invulnerableTime = 40;
        }
    }

    // ===== 🪂 拦截摔落伤害 =====
    @SubscribeEvent
    public void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && hasGodItem(player)) {
            event.setCanceled(true);
            player.fallDistance = 0.0f;
        }
    }

    // ===== ⏱️ 每 tick 维持无敌 + 免疫一切负面 =====
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        Player player = event.player;
        boolean god = hasGodItem(player);

        // ✨ 真无敌（免疫 /kill、虚空、所有伤害源）
        player.setInvulnerable(god);

        if (god) {
            // 强制满血
            player.setHealth(player.getMaxHealth());

            // 清除一切负面效果（凋零、中毒、缓慢等）
            player.removeAllEffects();

            // 免疫击退
            player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);

            // 免疫饥饿
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20f);
        }
    }

    // ===== 🔍 判断是否持有神器 =====
    private static boolean hasGodItem(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.getItem() instanceof MagicSword ||
                off.getItem() instanceof MagicSword ||
                main.getItem() instanceof MagicPickaxe ||
                off.getItem() instanceof MagicPickaxe ||
                main.getItem() instanceof MagicAxe ||
                off.getItem() instanceof MagicAxe;
    }

    // ===== 🌍 服务器启动 =====
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("§6§l╔════════════════════════════════╗");
        LOGGER.info("§6§l║     G O D S W O R D          ║");
        LOGGER.info("§6§l║      神 · 权 · 覆 · 盖       ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("§6§lGODSWORD §r§7- 手持此剑，即为神明");
    }
}