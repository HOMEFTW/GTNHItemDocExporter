package com.andgatech.gtnhitemdocexporter.export;

import java.lang.reflect.Constructor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class MinecraftItemDocCollector {

    private static Constructor<?> craftTweakerStackConstructor;
    private static boolean craftTweakerStackConstructorLoaded;

    private final LanguageNameResolver names;
    private final boolean includeNbtSummary;
    private final int maxNbtSummaryLength;

    public MinecraftItemDocCollector(LanguageNameResolver names, boolean includeNbtSummary, int maxNbtSummaryLength) {
        this.names = names;
        this.includeNbtSummary = includeNbtSummary;
        this.maxNbtSummaryLength = maxNbtSummaryLength;
    }

    public ItemDocEntry collect(ItemStack stack) {
        String registryId = String.valueOf(Item.itemRegistry.getNameForObject(stack.getItem()));
        int meta = stack.getItemDamage();
        String modId = registryId.contains(":") ? registryId.substring(0, registryId.indexOf(':')) : "";
        String nbt = stack.hasTagCompound() ? stack.getTagCompound()
            .toString() : "";
        Block block = Block.getBlockFromItem(stack.getItem());

        return new ItemDocEntry(
            modId,
            registryId,
            meta,
            ctExpression(stack, registryId, meta),
            names.currentName(stack),
            names.englishName(stack),
            stack.getUnlocalizedName(),
            block != null && block != Blocks.air,
            registryId + ":" + meta + (nbt.isEmpty() ? "" : "@" + Integer.toHexString(nbt.hashCode())),
            NbtSummaryFormatter.summarize(nbt, includeNbtSummary, maxNbtSummaryLength));
    }

    private static String ctExpression(ItemStack stack, String registryId, int meta) {
        try {
            Constructor<?> constructor = craftTweakerStackConstructor();
            if (constructor != null) {
                return constructor.newInstance(stack)
                    .toString();
            }
        } catch (Throwable ignored) {
            // Fall back below if CraftTweaker internals differ across GTNH versions.
        }
        return CraftTweakerNameFormatter.format(registryId, meta);
    }

    private static Constructor<?> craftTweakerStackConstructor() {
        if (craftTweakerStackConstructorLoaded) {
            return craftTweakerStackConstructor;
        }
        craftTweakerStackConstructorLoaded = true;
        try {
            Class<?> type = Class.forName("minetweaker.mc1710.item.MCItemStack");
            craftTweakerStackConstructor = type.getConstructor(ItemStack.class);
            return craftTweakerStackConstructor;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
