package com.andgatech.gtnhitemdocexporter.export;

public final class CraftTweakerNameFormatter {

    private CraftTweakerNameFormatter() {}

    public static String format(String registryId, int meta) {
        if (registryId == null || registryId.isEmpty()) {
            return "<unknown>";
        }
        if (meta == 0) {
            return "<" + registryId + ">";
        }
        return "<" + registryId + ":" + meta + ">";
    }
}
