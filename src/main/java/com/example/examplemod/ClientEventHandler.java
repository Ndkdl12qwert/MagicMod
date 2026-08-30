package com.example.examplemod;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlackGoldMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在客户端且是玩家 tick 结束时处理
        if (event.phase != TickEvent.Phase.END) return;
        if (!event.player.level().isClientSide) return;

        Player player = event.player;
        if (BlackGoldMod.hasGodItem(player) || BlackGoldMod.hasFullBlackGoldArmor(player)) {
            // 立即清除受击动画和音效状态
            player.hurtTime = 0;
            player.hurtDuration = 0;
            // 如果还想让玩家完全不抖动，还可以重置 invulnerableTime（但 setInvulnerable 已处理）
        }
    }
}