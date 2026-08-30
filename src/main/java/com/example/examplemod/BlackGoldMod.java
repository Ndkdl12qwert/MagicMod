package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fml.ModLoadingContext;

@Mod(BlackGoldMod.MODID)
public class BlackGoldMod {
    public static final String MODID = "blackgoldmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> BLACKGOLD_SWORD = ITEMS.register(
            "blackgold_sword",
            BlackGoldSword::new
    );

    public static final RegistryObject<Item> BLACKGOLD_PICKAXE = ITEMS.register(
            "blackgold_pickaxe",
            BlackGoldPickaxe::new
    );

    public static final RegistryObject<Item> BLACKGOLD_AXE = ITEMS.register(
            "blackgold_axe",
            BlackGoldAxe::new
    );

    public static final RegistryObject<Item> BLACKGOLD_HELMET = ITEMS.register(
            "blackgold_helmet",
            () -> new BlackGoldArmor(ArmorItem.Type.HELMET)
    );

    public static final RegistryObject<Item> BLACKGOLD_CHESTPLATE = ITEMS.register(
            "blackgold_chestplate",
            () -> new BlackGoldArmor(ArmorItem.Type.CHESTPLATE)
    );

    public static final RegistryObject<Item> BLACKGOLD_LEGGINGS = ITEMS.register(
            "blackgold_leggings",
            () -> new BlackGoldArmor(ArmorItem.Type.LEGGINGS)
    );

    public static final RegistryObject<Item> BLACKGOLD_BOOTS = ITEMS.register(
            "blackgold_boots",
            () -> new BlackGoldArmor(ArmorItem.Type.BOOTS)
    );

    public static final RegistryObject<Item> BLACKGOLD_NUGGET = ITEMS.register(
            "blackgold_nugget",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> BLACKGOLD_INGOT = ITEMS.register(
            "blackgold_ingot",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> GOD_BLACKGOLD_UPGRADE_TEMPLATE = ITEMS.register(
            "god_blackgold_upgrade_template",
            () -> new Item(new Item.Properties())
    );

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

    public static final RegistryObject<RecipeType<BlackGoldBlastingRecipe>> BLACKGOLD_BLASTING =
            RECIPE_TYPES.register("blackgold_blasting", () -> new RecipeType<BlackGoldBlastingRecipe>() {});

    // 配方序列化器注册
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<BlackGoldBlastingRecipe>> BLACKGOLD_BLASTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("blackgold_blasting",
                    BlackGoldBlastingRecipeSerializer::new);

    public BlackGoldMod() {
        LOGGER.info("§6§l╔════════════════════════════════╗");
        LOGGER.info("§6§l║     G O D S W O R D         ║");
        LOGGER.info("§6§l║      神 · 权 · 降 · 临       ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        ModCreativeTab.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean hasFullBlackGoldArmor(Player player) {
        int pieces = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof BlackGoldArmor) {
                pieces++;
            }
        }
        return pieces >= 4;
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 条件1：手持神剑 → 免疫伤害（已有）
            // 条件2：穿全套黑金盔甲 → 免疫所有伤害
            if (hasGodItem(player) || hasFullBlackGoldArmor(player)) {
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth());
                player.invulnerableTime = 40;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && hasGodItem(player)) {
            event.setCanceled(true);
            player.fallDistance = 0.0f;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        Player player = event.player;
        boolean god = hasGodItem(player) || hasFullBlackGoldArmor(player);

        player.setInvulnerable(god);
        if (god) {
            player.setHealth(player.getMaxHealth());
            player.removeAllEffects();
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20f);
            player.hurtTime = 0;
            player.hurtDuration = 0;
        }
    }

    public static boolean hasGodItem(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.getItem() instanceof BlackGoldSword ||
                off.getItem() instanceof BlackGoldSword ||
                main.getItem() instanceof BlackGoldPickaxe ||
                off.getItem() instanceof BlackGoldPickaxe ||
                main.getItem() instanceof BlackGoldAxe ||
                off.getItem() instanceof BlackGoldAxe;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("§6§l╔════════════════════════════════╗");
        LOGGER.info("§6§l║     G O D S W O R D     ║");
        LOGGER.info("§6§l║      神 · 权 · 覆 · 盖     ║");
        LOGGER.info("§6§l╚════════════════════════════════╝");
        LOGGER.info("§6§lGODSWORD §r§7- 手持此剑，即为神明");
    }
}