package com.example.examplemod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagicMod.MODID)
public class ModEffects {

    // 每秒检查一次，不是每 tick！
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在服务端执行，只在玩家 tick 结束时
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        Player player = event.player;

        // 每秒执行一次（20 tick = 1秒）
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        // 检查主手和副手
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        // ===== 神剑效果 =====
        if (mainHand.getItem() instanceof MagicSword || offHand.getItem() instanceof MagicSword) {
            // 力量 V (5级)
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST, 999999999, 255, false, true, true
            ));
            // 急迫
            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.WATER_BREATHING , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.ABSORPTION , 999999999, 255, false, true, true
            ));
        }

        // ===== 神镐效果 =====
        if (mainHand.getItem() instanceof MagicSword || offHand.getItem() instanceof MagicSword) {
            // 力量 V (5级)
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST, 999999999, 255, false, true, true
            ));
            // 急迫
            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.WATER_BREATHING , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.ABSORPTION , 999999999, 255, false, true, true
            ));
        }

        // ===== 神斧效果 =====
        if (mainHand.getItem() instanceof MagicSword || offHand.getItem() instanceof MagicSword) {
            // 力量 V (5级)
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_BOOST, 999999999, 255, false, true, true
            ));
            // 急迫
            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE, 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.WATER_BREATHING , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION , 999999999, 255, false, true, true
            ));

            player.addEffect(new MobEffectInstance(
                    MobEffects.ABSORPTION , 999999999, 255, false, true, true
            ));
        }
    }
}
