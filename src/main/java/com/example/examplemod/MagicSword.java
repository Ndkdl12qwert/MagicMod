package com.example.examplemod;

import net.minecraft.world.item.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import javax.annotation.Nullable;
import java.util.List;

public class MagicSword extends SwordItem {

    public MagicSword() {
        super(
                Tiers.NETHERITE,
                0,  // ✨ 神器不需要伤害，直接抹除
                -2.0F,
                new Item.Properties()
                        .durability(1)
                        .rarity(Rarity.EPIC)
                        .fireResistant()
        );
    }

    // ✨ 核心：永不磨损（强制版本）
    @Override
    public void setDamage(ItemStack stack, int damage) {
        // 什么都不做！永远不掉耐久！
    }

    // ✨ 再补一个：强制显示为满耐久！
    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;  // 永远显示为满耐久！
    }

    // ✨ 再补一个：禁止任何修复行为
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;  // 无法修复（也不需要）
    }

    // ===== ☠ 核心3：左键抹除 =====
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            String entityName = target.getName().getString();
            Vec3 pos = target.position();

            // 抹除音效
            attacker.level().playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 2.0F, 0.5F);

            // 粒子风暴
            for (int i = 0; i < 50; i++) {
                ((Level)attacker.level()).addParticle(ParticleTypes.FLASH,
                        pos.x + (attacker.level().random.nextDouble() - 0.5) * 2,
                        pos.y + target.getBbHeight() / 2,
                        pos.z + (attacker.level().random.nextDouble() - 0.5) * 2,
                        0, 0, 0);
            }

            // 直接从世界中抹除
            target.remove(Entity.RemovalReason.KILLED);

            // 神谕
            if (attacker instanceof Player) {
                ((Player)attacker).sendSystemMessage(Component.literal(
                        "§c☠ §6GODSWORD §c☠ §8» §7" + entityName + " §8已被§6抹除§8存在"
                ));
            }
        }
        return true;
    }

    // ===== 🎮 核心4：右键交互 =====
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // === 切换神权（Shift+右键）===
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                cycleMode(stack);
                player.sendSystemMessage(Component.literal(
                        "§a⚡ 神权切换: " +
                                getModeColor(getMode(stack)) + getModeName(getMode(stack))
                ));
            } else {
                // 特效
                for (int i = 0; i < 20; i++) {
                    world.addParticle(ParticleTypes.ENCHANT,
                            player.getX(), player.getY() + 1, player.getZ(),
                            (world.random.nextDouble() - 0.5) * 0.5,
                            0.2,
                            (world.random.nextDouble() - 0.5) * 0.5);
                }
                player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
            }
            return InteractionResultHolder.success(stack);
        }

        // 找到您的飞行代码，修改成这个样子：
        if (world.isClientSide) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();  // ✨ 加这一行！同步服务端！
                player.displayClientMessage(Component.literal("§b🕊️ 神权·翱翔 §7- 开启"), true);
                player.playSound(SoundEvents.PHANTOM_FLAP, 1.0F, 1.0F);

                // ✨ 加这些行！云朵起飞特效！
                for (int i = 0; i < 30; i++) {
                    world.addParticle(ParticleTypes.CLOUD,
                            player.getX(), player.getY(), player.getZ(),
                            (world.random.nextDouble() - 0.5) * 0.5,
                            0.2,
                            (world.random.nextDouble() - 0.5) * 0.5);
                }
            }
        }

        // === 释放技能 ===
        String mode = getMode(stack);
        switch(mode) {
            case "lightning" -> castLightning(world, player);
            case "fire" -> castFire(world, player);
            case "ice" -> castIce(world, player);
            case "wind" -> castWind(world, player);
        }

        return InteractionResultHolder.success(stack);
    }

    // ===== ⚡ 神权·天罚 =====
    private void castLightning(Level world, Player player) {
        if (!world.isClientSide) {
            // 全屏范围
            AABB box = new AABB(
                    player.getX() - 128, player.getY() - 64, player.getZ() - 128,
                    player.getX() + 128, player.getY() + 64, player.getZ() + 128
            );

            List<Entity> entities = world.getEntities(player, box,
                    entity -> !(entity instanceof Player) && entity.isAlive());

            int count = 0;
            for (Entity entity : entities) {
                // 召唤闪电特效
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
                if (lightning != null) {
                    lightning.moveTo(entity.position());
                    world.addFreshEntity(lightning);
                }
                // 抹除
                entity.remove(Entity.RemovalReason.KILLED);
                count++;
            }

            // 全屏神谕
            for (Player p : world.players()) {
                p.sendSystemMessage(Component.literal(
                        "§e⚡ §6GODSWORD §e⚡ §8» §7" + player.getName().getString() +
                                " §8发动了 §e神权·天罚 §8» §7抹除了 §c" + count + " §7个存在"
                ));
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 5.0F, 1.0F);

        } else {
            // 全屏特效
            for (int i = 0; i < 300; i++) {
                world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        player.getX() + (world.random.nextDouble() - 0.5) * 100,
                        player.getY() + world.random.nextDouble() * 20,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 100,
                        0, 0, 0);
            }
            player.displayClientMessage(Component.literal("§e⚡ §l神权·天罚 §e⚡"), true);
            player.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 2.0F, 1.0F);
        }
    }

    // ===== 🔥 神权·焚世 =====
    private void castFire(Level world, Player player) {
        if (!world.isClientSide) {
            AABB box = new AABB(
                    player.getX() - 128, player.getY() - 64, player.getZ() - 128,
                    player.getX() + 128, player.getY() + 64, player.getZ() + 128
            );

            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity != player && !(entity instanceof Player));

            for (LivingEntity target : targets) {
                target.remove(Entity.RemovalReason.KILLED);
            }

            for (Player p : world.players()) {
                p.sendSystemMessage(Component.literal(
                        "§c🔥 §6GODSWORD §c🔥 §8» §7" + player.getName().getString() +
                                " §8发动了 §c神权·焚世 §8» §7抹除了 §6" + targets.size() + " §7个存在"
                ));
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 5.0F, 1.0F);

        } else {
            for (int i = 0; i < 300; i++) {
                world.addParticle(ParticleTypes.FLAME,
                        player.getX() + (world.random.nextDouble() - 0.5) * 100,
                        player.getY() + world.random.nextDouble() * 2,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 100,
                        0, 0.2, 0);
                world.addParticle(ParticleTypes.LAVA,
                        player.getX() + (world.random.nextDouble() - 0.5) * 100,
                        player.getY() + world.random.nextDouble(),
                        player.getZ() + (world.random.nextDouble() - 0.5) * 100,
                        0, 0.1, 0);
            }
            player.displayClientMessage(Component.literal("§c🔥 §l神权·焚世 §c🔥"), true);
            player.playSound(SoundEvents.FIRECHARGE_USE, 2.0F, 1.0F);
        }
    }

    // ===== ❄️ 神权·永冻 =====
    private void castIce(Level world, Player player) {
        if (!world.isClientSide) {
            AABB box = new AABB(
                    player.getX() - 128, player.getY() - 64, player.getZ() - 128,
                    player.getX() + 128, player.getY() + 64, player.getZ() + 128
            );

            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity != player && !(entity instanceof Player));

            for (LivingEntity target : targets) {
                target.remove(Entity.RemovalReason.KILLED);
            }

            for (Player p : world.players()) {
                p.sendSystemMessage(Component.literal(
                        "§b❄️ §6GODSWORD §b❄️ §8» §7" + player.getName().getString() +
                                " §8发动了 §b神权·永冻 §8» §7抹除了 §3" + targets.size() + " §7个存在"
                ));
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 5.0F, 0.5F);

        } else {
            for (int i = 0; i < 300; i++) {
                world.addParticle(ParticleTypes.SNOWFLAKE,
                        player.getX() + (world.random.nextDouble() - 0.5) * 100,
                        player.getY() + world.random.nextDouble() * 3,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 100,
                        0, 0.1, 0);
                world.addParticle(ParticleTypes.ITEM_SNOWBALL,
                        player.getX() + (world.random.nextDouble() - 0.5) * 100,
                        player.getY() + world.random.nextDouble() * 3,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 100,
                        0, 0.1, 0);
            }
            player.displayClientMessage(Component.literal("§b❄️ §l神权·永冻 §b❄️"), true);
            player.playSound(SoundEvents.PLAYER_HURT_FREEZE, 2.0F, 0.5F);
        }
    }

    // ===== 🌪️ 神权·风暴 =====
    private void castWind(Level world, Player player) {
        if (!world.isClientSide) {
            AABB box = new AABB(
                    player.getX() - 128, player.getY() - 64, player.getZ() - 128,
                    player.getX() + 128, player.getY() + 64, player.getZ() + 128
            );

            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity != player && !(entity instanceof Player));

            int count = 0;
            for (LivingEntity target : targets) {
                // 风暴卷起
                target.setDeltaMovement(
                        (target.getX() - player.getX()) * 0.8,
                        3.0,
                        (target.getZ() - player.getZ()) * 0.8
                );
                // 抹除
                target.remove(Entity.RemovalReason.KILLED);
                count++;
            }

            for (Player p : world.players()) {
                p.sendSystemMessage(Component.literal(
                        "§2🌪️ §6GODSWORD §2🌪️ §8» §7" + player.getName().getString() +
                                " §8发动了 §2神权·风暴 §8» §7吞噬了 §2" + count + " §7个存在"
                ));
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 5.0F, 0.7F);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 5.0F, 1.5F);

        } else {
            for (int i = 0; i < 400; i++) {
                double angle = world.random.nextDouble() * 360;
                double radius = world.random.nextDouble() * 80;
                double height = world.random.nextDouble() * 20;

                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                double y = player.getY() - 5 + height;

                world.addParticle(ParticleTypes.COMPOSTER,
                        x, y, z,
                        Math.cos(angle) * 0.5,
                        0.3,
                        Math.sin(angle) * 0.5);

                if (world.random.nextFloat() < 0.2f) {
                    world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                            x, y, z,
                            0, 0.2, 0);
                }
            }
            player.displayClientMessage(Component.literal("§2🌪️ §l神权·风暴 §2🌪️"), true);
            player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2.0F, 0.7F);
            player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 2.0F, 1.5F);
        }
    }

    // ===== 📺 全屏神谕（持剑时）=====
    private static int TICK_COUNTER = 0;

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!world.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (!selected) return;

        // ✨ 每2秒更新一次ActionBar，不刷屏！
        TICK_COUNTER++;
        if (TICK_COUNTER >= 40) {  // 40 tick = 2秒
            TICK_COUNTER = 0;
            String mode = getMode(stack);
            player.displayClientMessage(
                    Component.literal(
                            "§6§lGODSWORD §8| " +
                                    getModeColor(mode) + "§l" + getModeName(mode) + " §8| " +
                                    "§7右键释放 §8| §7Shift切换"
                    ),
                    true  // true = ActionBar位置！
            );
        }
    }

    // ===== 神权管理 =====
    private String getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("SkillMode")) {
            tag.putString("SkillMode", "lightning");
        }
        return tag.getString("SkillMode");
    }

    private void setMode(ItemStack stack, String mode) {
        stack.getOrCreateTag().putString("SkillMode", mode);
    }

    private void cycleMode(ItemStack stack) {
        String current = getMode(stack);
        String next = switch(current) {
            case "lightning" -> "fire";
            case "fire" -> "ice";
            case "ice" -> "wind";
            case "wind" -> "lightning";
            default -> "lightning";
        };
        setMode(stack, next);
    }

    private String getModeName(String mode) {
        return switch(mode) {
            case "lightning" -> "天罚";
            case "fire" -> "焚世";
            case "ice" -> "永冻";
            case "wind" -> "风暴";
            default -> "天罚";
        };
    }

    private String getModeColor(String mode) {
        return switch(mode) {
            case "lightning" -> "§e";
            case "fire" -> "§c";
            case "ice" -> "§b";
            case "wind" -> "§2";
            default -> "§7";
        };
    }

    private String getModeDescription(String mode) {
        return switch(mode) {
            case "lightning" -> "§e天罚·雷霆万钧 §8[全屏抹除]";
            case "fire" -> "§c焚世·业火焚天 §8[全屏抹除]";
            case "ice" -> "§b永冻·绝对零度 §8[全屏抹除]";
            case "wind" -> "§2风暴·吞噬一切 §8[全屏抹除]";
            default -> "§7未知神权";
        };
    }

    // ===== 覆写物品描述（清空！）=====
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.clear();

        // ===== ✨ 顶部神纹（精简）=====
        tooltip.add(Component.literal("§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k"));
        tooltip.add(Component.literal(""));

        // ===== 中央神印（金色！）=====
        tooltip.add(Component.literal("                       §6§lG O D S W O R D"));
        tooltip.add(Component.literal("                           §7§l「神 陨」"));
        tooltip.add(Component.literal(""));

        // ===== 神权宣言（彩色！）=====
        String mode = getMode(stack);
        tooltip.add(Component.literal("  §e✦ §l神权·永恒    §7»  §k rjaoaasfffa§r"));
        tooltip.add(Component.literal("  §b✦ §l神权·不灭    §7»  §a免疫一切伤害"));
        tooltip.add(Component.literal("  §d✦ §l神权·翱翔    §7»  §b双击空格飞行"));
        tooltip.add(Component.literal("  §c✦ §l神权·抹除    §7»  §c左键直接删除"));
        tooltip.add(Component.literal(""));

        // ===== 当前神权（高亮！）=====
        tooltip.add(Component.literal("  §6✦ §l当前神权: " + getModeColor(mode) + "§l" + getModeName(mode)));
        tooltip.add(Component.literal("  §7右键释放: " + getModeDescription(mode)));
        tooltip.add(Component.literal("  §7Shift+右键: 切换神权"));
        tooltip.add(Component.literal(""));

        // ===== 神权轮盘（一行搞定）=====
        tooltip.add(Component.literal("  §6✦ §l神权轮盘: §e⚡天罚 §7| §c🔥焚世 §7| §b❄️永冻 §7| §2🌪️风暴"));
        tooltip.add(Component.literal(""));

        // ===== 神谕碑文（精简）=====
        tooltip.add(Component.literal("  §7§o「持此剑者，即为神明」"));
        tooltip.add(Component.literal(""));

        // ===== 底部神纹 =====
        tooltip.add(Component.literal("§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k§6§m§k"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;  // 永远附魔光效
    }
}