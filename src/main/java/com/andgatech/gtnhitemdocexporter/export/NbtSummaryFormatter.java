package com.andgatech.gtnhitemdocexporter.export;

public final class NbtSummaryFormatter {

    private NbtSummaryFormatter() {}

    public static String summarize(String nbtText, boolean enabled, int maxLength) {
        if (!enabled || nbtText == null || nbtText.isEmpty()) {
            return "";
        }
        if (maxLength < 4 || nbtText.length() <= maxLength) {
            return nbtText;
        }
        return nbtText.substring(0, maxLength - 3) + "...";
    }
}
