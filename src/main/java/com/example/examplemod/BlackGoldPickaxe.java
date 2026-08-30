package com.example.examplemod;

import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.List;

public class BlackGoldPickaxe extends PickaxeItem {

    public BlackGoldPickaxe() {
        super(
                Tiers.NETHERITE,
                0,  // ✨ 神器不需要伤害
                -2.0F,
                new Item.Properties()
                        .durability(1)
                        .rarity(Rarity.EPIC)
                        .fireResistant()
        );
    }

    // ===== ✨ 永不磨损三重保障 =====
    @Override
    public void setDamage(ItemStack stack, int damage) {
        // 什么都不做！永远不掉耐久！
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;  // 永远显示满耐久！
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;  // 无法修复
    }

    // ===== ✨ 挖方块不掉耐久 =====
    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        return true;  // 挖掘成功，不掉耐久！
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 99999.0F;  // 秒天秒地秒空气！
    }

    // ===== ⛏️ 右键技能：3x3范围挖掘 =====
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            // 服务器端：执行挖掘
            BlockPos center = player.blockPosition();
            int count = 0;

            // 3x3x3 范围挖掘
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos target = center.offset(x, y, z);
                        BlockState state = world.getBlockState(target);

                        // 不是空气、不是基岩、可以挖掘
                        if (!state.isAir() && state.getDestroySpeed(world, target) >= 0) {
                            world.destroyBlock(target, true);  // true = 掉落物品
                            count++;
                        }
                    }
                }
            }

            // 发送消息
            player.sendSystemMessage(Component.literal(
                    "§2⛏️ §6GODSWORD §2⛏️ §8» §7神权·开山 §8» §7挖掉了 §a" + count + " §7个方块"
            ));

        } else {
            // 客户端：粒子特效
            player.displayClientMessage(Component.literal("§2⛏️ 神权·开山"), true);
            player.playSound(SoundEvents.STONE_BREAK, 1.0F, 1.0F);

            for (int i = 0; i < 30; i++) {
                world.addParticle(ParticleTypes.CLOUD,
                        player.getX() + (world.random.nextDouble() - 0.5) * 5,
                        player.getY() + world.random.nextDouble() * 2,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 5,
                        0, 0.1, 0);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    // ===== 📺 全屏神谕介绍框 =====
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.clear();

        tooltip.add(Component.literal("§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("       §2§lG O D P I C K A X E"));
        tooltip.add(Component.literal("         §7§l「神 权 · 开 山」"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  §2✦ §l神权·永恒    §7»  §c∞ 永不磨损"));
        tooltip.add(Component.literal("  §2✦ §l神权·开山    §7»  §b右键3x3范围挖掘"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  §7右键: §b3x3范围挖掘"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  §7§o「一镐开山，万石臣服」"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k§2§m§k"));
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true;  // 永远附魔光效
    }
}
