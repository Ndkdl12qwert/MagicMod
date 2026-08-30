package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class BlackGoldBlastingRecipe extends AbstractCookingRecipe {

    public BlackGoldBlastingRecipe(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeType.BLASTING, id, group, category, ingredient, result, experience, cookingTime);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return BlackGoldMod.BLACKGOLD_BLASTING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeType.BLASTING;
    }

}