package com.andgatech.gtnhitemdocexporter.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ItemDocWriters {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private ItemDocWriters() {}

    public static void writeJson(ItemDocIndex index, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "item_index.json").toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(index, writer);
        }
    }

    public static void writeFluidJson(FluidDocIndex index, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "fluid_index.json").toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(index, writer);
        }
    }

    public static void writeOreDictionaryJson(OreDictionaryDocIndex index, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "ore_dictionary_index.json").toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(index, writer);
        }
    }

    public static void writeCsv(List<ItemDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "item_index.csv").toPath(), StandardCharsets.UTF_8)) {
            writer.write(
                "modId,registryId,meta,ctExpression,chineseName,englishName,unlocalizedName,isBlock,guid,nbtSummary");
            writer.newLine();
            for (ItemDocEntry entry : entries) {
                writer.write(csv(entry.modId));
                writer.write(',');
                writer.write(csv(entry.registryId));
                writer.write(',');
                writer.write(Integer.toString(entry.meta));
                writer.write(',');
                writer.write(csv(entry.ctExpression));
                writer.write(',');
                writer.write(csv(entry.chineseName));
                writer.write(',');
                writer.write(csv(entry.englishName));
                writer.write(',');
                writer.write(csv(entry.unlocalizedName));
                writer.write(',');
                writer.write(Boolean.toString(entry.isBlock));
                writer.write(',');
                writer.write(csv(entry.guid));
                writer.write(',');
                writer.write(csv(entry.nbtSummary));
                writer.newLine();
            }
        }
    }

    public static void writeFluidCsv(List<FluidDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "fluid_index.csv").toPath(), StandardCharsets.UTF_8)) {
            writer.write(
                "fluidName,ctExpression,chineseName,englishName,unlocalizedName,temperature,density,viscosity,gaseous,guid");
            writer.newLine();
            for (FluidDocEntry entry : entries) {
                writer.write(csv(entry.fluidName));
                writer.write(',');
                writer.write(csv(entry.ctExpression));
                writer.write(',');
                writer.write(csv(entry.chineseName));
                writer.write(',');
                writer.write(csv(entry.englishName));
                writer.write(',');
                writer.write(csv(entry.unlocalizedName));
                writer.write(',');
                writer.write(Integer.toString(entry.temperature));
                writer.write(',');
                writer.write(Integer.toString(entry.density));
                writer.write(',');
                writer.write(Integer.toString(entry.viscosity));
                writer.write(',');
                writer.write(Boolean.toString(entry.gaseous));
                writer.write(',');
                writer.write(csv(entry.guid));
                writer.newLine();
            }
        }
    }

    public static void writeOreDictionaryCsv(List<OreDictionaryDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "ore_dictionary_index.csv").toPath(), StandardCharsets.UTF_8)) {
            writer.write("oreName,ctExpression,itemCount,items,guid");
            writer.newLine();
            for (OreDictionaryDocEntry entry : entries) {
                writer.write(csv(entry.oreName));
                writer.write(',');
                writer.write(csv(entry.ctExpression));
                writer.write(',');
                writer.write(Integer.toString(entry.itemCount));
                writer.write(',');
                writer.write(csv(String.join(" ", entry.items)));
                writer.write(',');
                writer.write(csv(entry.guid));
                writer.newLine();
            }
        }
    }

    public static void writeMarkdown(List<ItemDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "item_index.md").toPath(), StandardCharsets.UTF_8)) {
            String currentMod = null;
            for (ItemDocEntry entry : entries) {
                if (!entry.modId.equals(currentMod)) {
                    currentMod = entry.modId;
                    writer.newLine();
                    writer.write("## " + escapeMarkdown(currentMod));
                    writer.newLine();
                    writer.newLine();
                    writer.write("| 中文名 | 英文名 | CT 表达式 | registryId | meta | 是否方块 |");
                    writer.newLine();
                    writer.write("|---|---|---|---|---:|---|");
                    writer.newLine();
                }
                writer.write(
                    "| " + escapeMarkdown(entry.chineseName)
                        + " | "
                        + escapeMarkdown(entry.englishName)
                        + " | `"
                        + entry.ctExpression
                        + "`"
                        + " | `"
                        + entry.registryId
                        + "`"
                        + " | "
                        + entry.meta
                        + " | "
                        + (entry.isBlock ? "是" : "否")
                        + " |");
                writer.newLine();
            }
        }
    }

    public static void writeFluidMarkdown(List<FluidDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "fluid_index.md").toPath(), StandardCharsets.UTF_8)) {
            writer.write("# Fluid Index");
            writer.newLine();
            writer.newLine();
            writer.write("| 中文名 | 英文名 | CT 表达式 | fluidName | 温度 | 密度 | 粘度 | 气体 |");
            writer.newLine();
            writer.write("|---|---|---|---|---:|---:|---:|---|");
            writer.newLine();
            for (FluidDocEntry entry : entries) {
                writer.write(
                    "| " + escapeMarkdown(entry.chineseName)
                        + " | "
                        + escapeMarkdown(entry.englishName)
                        + " | `"
                        + entry.ctExpression
                        + "` | `"
                        + entry.fluidName
                        + "` | "
                        + entry.temperature
                        + " | "
                        + entry.density
                        + " | "
                        + entry.viscosity
                        + " | "
                        + (entry.gaseous ? "是" : "否")
                        + " |");
                writer.newLine();
            }
        }
    }

    public static void writeOreDictionaryMarkdown(List<OreDictionaryDocEntry> entries, File outputDir)
        throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "ore_dictionary_index.md").toPath(), StandardCharsets.UTF_8)) {
            writer.write("# Ore Dictionary Index");
            writer.newLine();
            writer.newLine();
            writer.write("| OreDict 名称 | CT 表达式 | 物品数 | 示例物品 |");
            writer.newLine();
            writer.write("|---|---|---:|---|");
            writer.newLine();
            for (OreDictionaryDocEntry entry : entries) {
                writer.write(
                    "| " + escapeMarkdown(entry.oreName)
                        + " | `"
                        + entry.ctExpression
                        + "` | "
                        + entry.itemCount
                        + " | "
                        + escapeMarkdown(String.join(" ", entry.items))
                        + " |");
                writer.newLine();
            }
        }
    }

    public static void writeLastExportLog(File outputDir, int entryCount, int failureCount, long elapsedMillis)
        throws IOException {
        writeLastExportLog(outputDir, entryCount, 0, failureCount, elapsedMillis);
    }

    public static void writeLastExportLog(File outputDir, int entryCount, int fluidEntryCount, int failureCount,
        long elapsedMillis) throws IOException {
        writeLastExportLog(outputDir, entryCount, fluidEntryCount, 0, failureCount, elapsedMillis);
    }

    public static void writeLastExportLog(File outputDir, int entryCount, int fluidEntryCount,
        int oreDictionaryEntryCount, int failureCount, long elapsedMillis) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files
            .newBufferedWriter(new File(outputDir, "last_export.log").toPath(), StandardCharsets.UTF_8)) {
            writer.write("entryCount=" + entryCount);
            writer.newLine();
            writer.write("fluidEntryCount=" + fluidEntryCount);
            writer.newLine();
            writer.write("oreDictionaryEntryCount=" + oreDictionaryEntryCount);
            writer.newLine();
            writer.write("failureCount=" + failureCount);
            writer.newLine();
            writer.write("elapsedMillis=" + elapsedMillis);
            writer.newLine();
        }
    }

    private static void ensureDir(File outputDir) throws IOException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + outputDir);
        }
    }

    private static String csv(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private static String escapeMarkdown(String value) {
        return value == null ? ""
            : value.replace("|", "\\|")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}
