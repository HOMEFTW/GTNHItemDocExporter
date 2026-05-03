package com.andgatech.gtnhitemdocexporter.export;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class MinecraftFluidDocCollector {

    public FluidDocEntry collect(String fluidName, Fluid fluid) {
        FluidStack stack = new FluidStack(fluid, 1000);
        String name = fluidName == null || fluidName.isEmpty() ? fluid.getName() : fluidName;
        String unlocalizedName = safeGetUnlocalizedName(fluid, stack);
        return new FluidDocEntry(
            name,
            "<liquid:" + name + ">",
            currentName(fluid, stack),
            englishName(unlocalizedName),
            unlocalizedName,
            fluid.getTemperature(stack),
            fluid.getDensity(stack),
            fluid.getViscosity(stack),
            fluid.isGaseous(stack),
            name);
    }

    private static String currentName(Fluid fluid, FluidStack stack) {
        try {
            return strip(fluid.getLocalizedName(stack));
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String englishName(String unlocalizedName) {
        if (unlocalizedName.isEmpty()) {
            return "";
        }
        String fallback = StatCollector.translateToFallback(unlocalizedName);
        if (fallback == null || fallback.equals(unlocalizedName)) {
            fallback = StatCollector.translateToFallback(unlocalizedName + ".name");
        }
        return strip(fallback == null ? "" : fallback);
    }

    private static String safeGetUnlocalizedName(Fluid fluid, FluidStack stack) {
        try {
            String value = fluid.getUnlocalizedName(stack);
            return value == null ? "" : value;
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String strip(String value) {
        String stripped = EnumChatFormatting.getTextWithoutFormattingCodes(value);
        return stripped == null ? "" : stripped.trim();
    }
}
