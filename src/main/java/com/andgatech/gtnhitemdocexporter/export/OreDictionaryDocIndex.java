package com.andgatech.gtnhitemdocexporter.export;

import java.util.List;

public final class OreDictionaryDocIndex {

    public final int schemaVersion;
    public final String generatedAt;
    public final String minecraftVersion;
    public final String language;
    public final int entryCount;
    public final List<OreDictionaryDocEntry> entries;

    public OreDictionaryDocIndex(String generatedAt, String language, List<OreDictionaryDocEntry> entries) {
        this.schemaVersion = 1;
        this.generatedAt = generatedAt;
        this.minecraftVersion = "1.7.10";
        this.language = language == null ? "" : language;
        this.entries = entries;
        this.entryCount = entries == null ? 0 : entries.size();
    }
}
