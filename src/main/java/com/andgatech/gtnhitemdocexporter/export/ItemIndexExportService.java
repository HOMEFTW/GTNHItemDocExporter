package com.andgatech.gtnhitemdocexporter.export;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import codechicken.nei.ItemList;

import com.andgatech.gtnhitemdocexporter.GTNHItemDocExporter;
import com.andgatech.gtnhitemdocexporter.config.ExporterConfig;

public final class ItemIndexExportService {

    private final ExporterConfig config;
    private int failureCount;

    public ItemIndexExportService(ExporterConfig config) {
        this.config = config;
    }

    public boolean isNeiReady() {
        return ItemList.loadFinished && !ItemList.items.isEmpty();
    }

    public File outputDir() {
        return new File(Minecraft.getMinecraft().mcDataDir, "gtnh_item_doc_exporter");
    }

    public ExportResult exportNow() throws Exception {
        long started = System.currentTimeMillis();
        failureCount = 0;

        MinecraftItemDocCollector collector = new MinecraftItemDocCollector(
                new LanguageNameResolver(),
                config.includeNbtSummary,
                config.maxNbtSummaryLength);
        List<ItemDocEntry> entries = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (ItemStack stack : ItemList.items) {
            try {
                ItemDocEntry entry = collector.collect(stack);
                String key = entry.registryId + "|" + entry.meta + "|" + entry.guid;
                if (seen.add(key)) {
                    entries.add(entry);
                }
            } catch (Throwable t) {
                failureCount++;
                GTNHItemDocExporter.LOG.warn("Failed to export item stack {}", stack, t);
            }
        }

        entries.sort(
                Comparator.comparing((ItemDocEntry entry) -> entry.modId)
                        .thenComparing(entry -> entry.registryId)
                        .thenComparingInt(entry -> entry.meta)
                        .thenComparing(entry -> entry.chineseName));

        ItemDocIndex index = new ItemDocIndex(nowIsoLike(), currentLanguage(), entries);
        File dir = outputDir();
        if (config.writeJson) {
            ItemDocWriters.writeJson(index, dir);
        }
        if (config.writeCsv) {
            ItemDocWriters.writeCsv(entries, dir);
        }
        if (config.writeMarkdown) {
            ItemDocWriters.writeMarkdown(entries, dir);
        }
        long elapsed = System.currentTimeMillis() - started;
        ItemDocWriters.writeLastExportLog(dir, entries.size(), failureCount, elapsed);
        return new ExportResult(entries.size(), failureCount, elapsed, dir);
    }

    private static String currentLanguage() {
        try {
            return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String nowIsoLike() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date());
    }

    public static final class ExportResult {
        public final int entryCount;
        public final int failureCount;
        public final long elapsedMillis;
        public final File outputDir;

        public ExportResult(int entryCount, int failureCount, long elapsedMillis, File outputDir) {
            this.entryCount = entryCount;
            this.failureCount = failureCount;
            this.elapsedMillis = elapsedMillis;
            this.outputDir = outputDir;
        }
    }
}
