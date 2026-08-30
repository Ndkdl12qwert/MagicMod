package com.example.examplemod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class BlackGoldArmor extends ArmorItem {

    // ===== ✨ 神权盔甲材质 =====
    public static final ArmorMaterial BLACKGOLD_MATERIAL = new ArmorMaterial() {
        @Override
        public String getName() {
            return "blackgoldmod:blackgold";
        }

        @Override
        public int getDurabilityForType(Type type) {
            return 9999;  // 永不磨损！
        }

        @Override
        public int getDefenseForType(Type type) {
            return switch (type) {
                case HELMET -> 5;
                case CHESTPLATE -> 8;
                case LEGGINGS -> 6;
                case BOOTS -> 5;
            };
        }

        @Override
        public int getEnchantmentValue() {
            return 30;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_NETHERITE;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;  // 无法修复，但永不磨损！
        }

        @Override
        public float getToughness() {
            return 5.0F;  // 韧性拉满
        }

        @Override
        public float getKnockbackResistance() {
            return 1.0F;  // 100% 免疫击退
        }
    };

    public BlackGoldArmor(Type type) {
        super(BLACKGOLD_MATERIAL, type, new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    // ===== ✨ 永不磨损 =====
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

    // ===== 🛡️ 全套效果：每 tick 触发 =====
    @Override
    public void inventoryTick(ItemStack stack, Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!world.isClientSide && entity instanceof Player player) {
            int armorPieces = countArmorPieces(player);

            if (armorPieces >= 2) {
                // 2件套：速度提升
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false, true));
            }

            if (armorPieces >= 3) {
                // 3件套：伤害吸收
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 40, 1, false, false, true));
            }

            if (armorPieces >= 4) {
                // 4件套：全套神权！
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 2, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0, false, false, true));
            }
        }
    }

    // ===== 📺 全屏神谕 =====
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.clear();

        String color = switch (this.getType()) {
            case HELMET -> "§b";
            case CHESTPLATE -> "§c";
            case LEGGINGS -> "§a";
            case BOOTS -> "§e";
        };

        tooltip.add(Component.literal(color + "§m§k" + color + "§m§k" + color + "§m§k"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("       " + color + "§lG O D S W O R D"));
        tooltip.add(Component.literal(" §k afh§r §7§l「神 权 · " + getPieceName() + "」§k akjfh§r"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  " + color + "✦ §l神权·永恒    §7»  §k永不磨损"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  §7套装效果:"));
        tooltip.add(Component.literal("    §7• §b2件: 速度提升"));
        tooltip.add(Component.literal("    §7• §d3件: 伤害吸收"));
        tooltip.add(Component.literal("    §7• §6全套: 全神权加持"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("  §7§o「四神加身，即为神明」"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal(color + "§m§k" + color + "§m§k" + color + "§m§k"));
    }

    // ===== 🎯 辅助方法 =====
    private String getPieceName() {
        return switch (this.getType()) {
            case HELMET -> "神 识";
            case CHESTPLATE -> "神 躯";
            case LEGGINGS -> "神 行";
            case BOOTS -> "神 足";
        };
    }

    private int countArmorPieces(Player player) {
        int count = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof BlackGoldArmor) {
                count++;
            }
        }
        return count;
    }
}