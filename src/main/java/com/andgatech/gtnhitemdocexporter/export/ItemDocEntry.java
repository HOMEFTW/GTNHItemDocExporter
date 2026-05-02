package com.andgatech.gtnhitemdocexporter.export;

public final class ItemDocEntry {

    public final String modId;
    public final String registryId;
    public final int meta;
    public final String ctExpression;
    public final String chineseName;
    public final String englishName;
    public final String unlocalizedName;
    public final boolean isBlock;
    public final String guid;
    public final String nbtSummary;

    public ItemDocEntry(String modId, String registryId, int meta, String ctExpression, String chineseName,
        String englishName, String unlocalizedName, boolean isBlock, String guid, String nbtSummary) {
        this.modId = safe(modId);
        this.registryId = safe(registryId);
        this.meta = meta;
        this.ctExpression = safe(ctExpression);
        this.chineseName = safe(chineseName);
        this.englishName = safe(englishName);
        this.unlocalizedName = safe(unlocalizedName);
        this.isBlock = isBlock;
        this.guid = safe(guid);
        this.nbtSummary = safe(nbtSummary);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
