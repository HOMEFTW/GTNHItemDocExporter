package com.andgatech.gtnhitemdocexporter.export;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public final class LanguageNameResolver {

    public String currentName(ItemStack stack) {
        return strip(stack == null ? "" : stack.getDisplayName());
    }

    public String englishName(ItemStack stack) {
        if (stack == null) {
            return "";
        }
        String unlocalizedName = stack.getUnlocalizedName();
        String fallback = StatCollector.translateToFallback(unlocalizedName + ".name");
        if (fallback == null || fallback.equals(unlocalizedName + ".name")) {
            fallback = StatCollector.translateToFallback(unlocalizedName);
        }
        return strip(fallback == null ? "" : fallback);
    }

    private static String strip(String value) {
        String stripped = EnumChatFormatting.getTextWithoutFormattingCodes(value);
        return stripped == null ? "" : stripped.trim();
    }
}
