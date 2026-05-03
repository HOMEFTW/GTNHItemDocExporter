package com.andgatech.gtnhitemdocexporter.export;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class MinecraftOreDictionaryDocCollector {

    public OreDictionaryDocEntry collect(String oreName) {
        Set<String> itemExpressions = new LinkedHashSet<>();
        for (ItemStack stack : OreDictionary.getOres(oreName)) {
            String expression = ctExpression(stack);
            if (!expression.isEmpty()) {
                itemExpressions.add(expression);
            }
        }
        List<String> items = new ArrayList<>(itemExpressions);
        items.sort(Comparator.naturalOrder());
        return new OreDictionaryDocEntry(oreName, "<ore:" + oreName + ">", items, oreName);
    }

    private static String ctExpression(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return "";
        }
        String registryId = String.valueOf(Item.itemRegistry.getNameForObject(stack.getItem()));
        if (registryId == null || registryId.isEmpty() || "null".equals(registryId)) {
            return "";
        }
        return CraftTweakerNameFormatter.format(registryId, stack.getItemDamage());
    }
}
