package com.andgatech.gtnhitemdocexporter.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class ExporterConfig {

    public boolean autoExportOnJoin = true;
    public boolean writeJson = true;
    public boolean writeCsv = true;
    public boolean writeMarkdown = true;
    public boolean includeNbtSummary = true;
    public int maxNbtSummaryLength = 240;
    public String forceEnglishLocale = "en_US";

    public void load(File configFile) {
        Configuration config = new Configuration(configFile);
        try {
            config.load();
            autoExportOnJoin = config.getBoolean(
                "autoExportOnJoin",
                Configuration.CATEGORY_GENERAL,
                true,
                "Export the NEI item index once after joining a world.");
            writeJson = config.getBoolean("writeJson", Configuration.CATEGORY_GENERAL, true, "Write item_index.json.");
            writeCsv = config.getBoolean("writeCsv", Configuration.CATEGORY_GENERAL, true, "Write item_index.csv.");
            writeMarkdown = config
                .getBoolean("writeMarkdown", Configuration.CATEGORY_GENERAL, true, "Write item_index.md.");
            includeNbtSummary = config.getBoolean(
                "includeNbtSummary",
                Configuration.CATEGORY_GENERAL,
                true,
                "Include truncated NBT text in the exported index.");
            maxNbtSummaryLength = config.getInt(
                "maxNbtSummaryLength",
                Configuration.CATEGORY_GENERAL,
                240,
                0,
                4096,
                "Maximum length for NBT summary text.");
            forceEnglishLocale = config.getString(
                "forceEnglishLocale",
                Configuration.CATEGORY_GENERAL,
                "en_US",
                "Locale used for English display names.");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
