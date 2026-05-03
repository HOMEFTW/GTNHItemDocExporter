package com.andgatech.gtnhitemdocexporter.export;

import java.util.Collections;
import java.util.List;

public final class OreDictionaryDocEntry {

    public final String oreName;
    public final String ctExpression;
    public final int itemCount;
    public final List<String> items;
    public final String guid;

    public OreDictionaryDocEntry(String oreName, String ctExpression, List<String> items, String guid) {
        this.oreName = safe(oreName);
        this.ctExpression = safe(ctExpression);
        this.items = items == null ? Collections.emptyList() : items;
        this.itemCount = this.items.size();
        this.guid = safe(guid);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
