package com.andgatech.gtnhitemdocexporter.export;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.andgatech.gtnhitemdocexporter.GTNHItemDocExporter;
import com.andgatech.gtnhitemdocexporter.config.ExporterConfig;

import codechicken.nei.ItemList;

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
        List<FluidDocEntry> fluidEntries = collectFluidEntries();
        List<OreDictionaryDocEntry> oreDictionaryEntries = collectOreDictionaryEntries();

        ItemDocIndex index = new ItemDocIndex(nowIsoLike(), currentLanguage(), entries);
        FluidDocIndex fluidIndex = new FluidDocIndex(nowIsoLike(), currentLanguage(), fluidEntries);
        OreDictionaryDocIndex oreDictionaryIndex = new OreDictionaryDocIndex(
            nowIsoLike(),
            currentLanguage(),
            oreDictionaryEntries);
        File dir = outputDir();
        if (config.writeJson) {
            ItemDocWriters.writeJson(index, dir);
            ItemDocWriters.writeFluidJson(fluidIndex, dir);
            ItemDocWriters.writeOreDictionaryJson(oreDictionaryIndex, dir);
        }
        if (config.writeCsv) {
            ItemDocWriters.writeCsv(entries, dir);
            ItemDocWriters.writeFluidCsv(fluidEntries, dir);
            ItemDocWriters.writeOreDictionaryCsv(oreDictionaryEntries, dir);
        }
        if (config.writeMarkdown) {
            ItemDocWriters.writeMarkdown(entries, dir);
            ItemDocWriters.writeFluidMarkdown(fluidEntries, dir);
            ItemDocWriters.writeOreDictionaryMarkdown(oreDictionaryEntries, dir);
        }
        long elapsed = System.currentTimeMillis() - started;
        ItemDocWriters.writeLastExportLog(
            dir,
            entries.size(),
            fluidEntries.size(),
            oreDictionaryEntries.size(),
            failureCount,
            elapsed);
        return new ExportResult(
            entries.size(),
            fluidEntries.size(),
            oreDictionaryEntries.size(),
            failureCount,
            elapsed,
            dir);
    }

    private List<FluidDocEntry> collectFluidEntries() {
        MinecraftFluidDocCollector collector = new MinecraftFluidDocCollector();
        List<FluidDocEntry> entries = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Map.Entry<String, Fluid> registeredFluid : FluidRegistry.getRegisteredFluids()
            .entrySet()) {
            try {
                Fluid fluid = registeredFluid.getValue();
                if (fluid == null) {
                    continue;
                }
                FluidDocEntry entry = collector.collect(registeredFluid.getKey(), fluid);
                if (seen.add(entry.guid)) {
                    entries.add(entry);
                }
            } catch (Throwable t) {
                failureCount++;
                GTNHItemDocExporter.LOG.warn("Failed to export fluid {}", registeredFluid.getKey(), t);
            }
        }
        entries.sort(Comparator.comparing(entry -> entry.fluidName));
        return entries;
    }

    private List<OreDictionaryDocEntry> collectOreDictionaryEntries() {
        MinecraftOreDictionaryDocCollector collector = new MinecraftOreDictionaryDocCollector();
        List<OreDictionaryDocEntry> entries = new ArrayList<>();
        for (String oreName : OreDictionary.getOreNames()) {
            try {
                OreDictionaryDocEntry entry = collector.collect(oreName);
                if (entry.itemCount > 0) {
                    entries.add(entry);
                }
            } catch (Throwable t) {
                failureCount++;
                GTNHItemDocExporter.LOG.warn("Failed to export ore dictionary entry {}", oreName, t);
            }
        }
        entries.sort(Comparator.comparing(entry -> entry.oreName));
        return entries;
    }

    private static String currentLanguage() {
        try {
            return Minecraft.getMinecraft()
                .getLanguageManager()
                .getCurrentLanguage()
                .getLanguageCode();
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
        public final int fluidEntryCount;
        public final int oreDictionaryEntryCount;
        public final int failureCount;
        public final long elapsedMillis;
        public final File outputDir;

        public ExportResult(int entryCount, int fluidEntryCount, int failureCount, long elapsedMillis, File outputDir) {
            this(entryCount, fluidEntryCount, 0, failureCount, elapsedMillis, outputDir);
        }

        public ExportResult(int entryCount, int fluidEntryCount, int oreDictionaryEntryCount, int failureCount,
            long elapsedMillis, File outputDir) {
            this.entryCount = entryCount;
            this.fluidEntryCount = fluidEntryCount;
            this.oreDictionaryEntryCount = oreDictionaryEntryCount;
            this.failureCount = failureCount;
            this.elapsedMillis = elapsedMillis;
            this.outputDir = outputDir;
        }
    }
}
