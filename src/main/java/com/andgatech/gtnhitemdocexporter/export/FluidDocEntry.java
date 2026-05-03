package com.andgatech.gtnhitemdocexporter.export;

public final class FluidDocEntry {

    public final String fluidName;
    public final String ctExpression;
    public final String chineseName;
    public final String englishName;
    public final String unlocalizedName;
    public final int temperature;
    public final int density;
    public final int viscosity;
    public final boolean gaseous;
    public final String guid;

    public FluidDocEntry(String fluidName, String ctExpression, String chineseName, String englishName,
        String unlocalizedName, int temperature, int density, int viscosity, boolean gaseous, String guid) {
        this.fluidName = safe(fluidName);
        this.ctExpression = safe(ctExpression);
        this.chineseName = safe(chineseName);
        this.englishName = safe(englishName);
        this.unlocalizedName = safe(unlocalizedName);
        this.temperature = temperature;
        this.density = density;
        this.viscosity = viscosity;
        this.gaseous = gaseous;
        this.guid = safe(guid);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
