package com.andgatech.gtnhitemdocexporter.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CraftTweakerNameFormatterTest {

    @Test
    public void omitsZeroMeta() {
        assertEquals("<minecraft:stone>", CraftTweakerNameFormatter.format("minecraft:stone", 0));
    }

    @Test
    public void includesPositiveMeta() {
        assertEquals("<gregtech:gt.metaitem.01:1234>",
                CraftTweakerNameFormatter.format("gregtech:gt.metaitem.01", 1234));
    }

    @Test
    public void keepsWildcardMetaVisible() {
        assertEquals("<minecraft:wool:32767>", CraftTweakerNameFormatter.format("minecraft:wool", 32767));
    }
}
