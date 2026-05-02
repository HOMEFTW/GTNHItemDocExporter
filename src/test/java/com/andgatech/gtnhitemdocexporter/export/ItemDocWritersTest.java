package com.andgatech.gtnhitemdocexporter.export;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

public class ItemDocWritersTest {

    @Test
    public void writesAllFormats() throws Exception {
        File dir = Files.createTempDirectory("item-doc-writers")
            .toFile();
        ItemDocEntry entry = new ItemDocEntry(
            "gregtech",
            "gregtech:gt.metaitem.01",
            1234,
            "<gregtech:gt.metaitem.01:1234>",
            "示例物品",
            "Example Item",
            "item.example.name",
            false,
            "gregtech:gt.metaitem.01:1234",
            "");
        ItemDocIndex index = new ItemDocIndex("2026-05-02T19:30:00+08:00", "zh_CN", Arrays.asList(entry));

        ItemDocWriters.writeJson(index, dir);
        ItemDocWriters.writeCsv(index.entries, dir);
        ItemDocWriters.writeMarkdown(index.entries, dir);
        ItemDocWriters.writeLastExportLog(dir, 1, 0, 12L);

        assertTrue(read(new File(dir, "item_index.json")).contains("\"schemaVersion\": 1"));
        assertTrue(read(new File(dir, "item_index.csv")).contains("gregtech:gt.metaitem.01"));
        assertTrue(read(new File(dir, "item_index.md")).contains("## gregtech"));
        assertTrue(read(new File(dir, "last_export.log")).contains("entryCount=1"));
    }

    private static String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
}
