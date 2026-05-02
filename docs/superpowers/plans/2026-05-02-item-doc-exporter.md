# Item Doc Exporter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a GTNH 2.8.x client-side addon mod that exports NEI-indexed item/block documentation to JSON, CSV, and Markdown.

**Architecture:** Scaffold a normal GTNH addon project, then keep most exporter logic in small pure Java classes that can be unit tested without Minecraft. Minecraft/NEI integration stays in thin adapter classes that wait for `codechicken.nei.ItemList` and call the tested exporter pipeline.

**Tech Stack:** Java 17 syntax via Jabel targeting JVM 8, Minecraft Forge 1.7.10, GTNH Gradle template, NotEnoughItems dev dependency, Gson, JUnit 4.

---

## File Map

- `build.gradle`, `settings.gradle`, `gradle.properties`, `dependencies.gradle`, `addon.gradle`, `repositories.gradle`: generated GTNH addon build files, then adjusted for this exporter.
- `src/main/java/com/andgatech/gtnhitemdocexporter/GTNHItemDocExporter.java`: Forge mod entry point.
- `src/main/java/com/andgatech/gtnhitemdocexporter/CommonProxy.java`: common lifecycle hooks and command registration.
- `src/main/java/com/andgatech/gtnhitemdocexporter/ClientProxy.java`: client-only event registration.
- `src/main/java/com/andgatech/gtnhitemdocexporter/config/ExporterConfig.java`: Forge configuration wrapper.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocEntry.java`: immutable export row.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocIndex.java`: top-level JSON document model.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/CraftTweakerNameFormatter.java`: pure CT expression formatter.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/NbtSummaryFormatter.java`: pure NBT summary truncation helper.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocWriters.java`: JSON, CSV, Markdown writers.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemIndexExportService.java`: orchestrates collection, sorting, and file writing.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/MinecraftItemDocCollector.java`: converts NEI `ItemStack` values into `ItemDocEntry`.
- `src/main/java/com/andgatech/gtnhitemdocexporter/export/LanguageNameResolver.java`: current-language and `en_US` name resolution.
- `src/main/java/com/andgatech/gtnhitemdocexporter/command/CommandItemDoc.java`: `/itemdoc export`.
- `src/main/java/com/andgatech/gtnhitemdocexporter/event/ClientExportEventHandler.java`: auto-export after world join and NEI readiness.
- `src/test/java/com/andgatech/gtnhitemdocexporter/export/CraftTweakerNameFormatterTest.java`: CT expression tests.
- `src/test/java/com/andgatech/gtnhitemdocexporter/export/NbtSummaryFormatterTest.java`: NBT summary tests.
- `src/test/java/com/andgatech/gtnhitemdocexporter/export/ItemDocWritersTest.java`: JSON/CSV/Markdown writer tests.
- `log.md`, `ToDOLIST.md`, `context.md`: Chinese project continuity docs.

## Additional Reference Notes

- `D:/Code/GTNH LIB/ModTweaker-master` is a CraftTweaker addon reference.
- ModTweaker registers as `required-after:MineTweaker3` and exposes helper/log commands through MineTweaker APIs.
- ModTweaker's `LogHelper.getStackDescription(ItemStack)` uses `new MCItemStack(stack).toString()` to produce script-usable item strings. The exporter should prefer this pattern when CraftTweaker classes are available, while keeping `CraftTweakerNameFormatter` as a pure fallback for unit-tested formatting.

## Task 1: Scaffold GTNH Addon Project

**Files:**
- Create/Modify: project root Gradle files from `C:/Users/homeftw/.codex/skills/gtnh-addon-generator/templates`
- Create/Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/GTNHItemDocExporter.java`
- Create/Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/CommonProxy.java`
- Create/Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/ClientProxy.java`
- Create/Modify: `src/main/resources/META-INF/accesstransformer.cfg`
- Create/Modify: `src/main/resources/assets/gtnhitemdocexporter/lang/en_US.lang`
- Modify: `dependencies.gradle`
- Modify: `gradle.properties`

- [ ] **Step 1: Render GTNH addon templates**

Use these template variables:

```text
MOD_NAME=GTNHItemDocExporter
MOD_ID=gtnhitemdocexporter
MOD_ID_LOWER=gtnhitemdocexporter
ROOT_PACKAGE=com.andgatech.gtnhitemdocexporter
MOD_CLASS_NAME=GTNHItemDocExporter
PACKAGE_PATH=com/andgatech/gtnhitemdocexporter
TARGET_DIR=D:/Code/GTNHItemDocExporter
GTNH_VERSION=2.8.4
GRADLE_VERSION=8.14.3
GRADLE_MIRROR_BASE=https://mirrors.cloud.tencent.com/gradle
MAVEN_MIRROR_URL=https://mirrors.cloud.tencent.com/nexus/repository/maven-public/
```

Create the standard source and resource tree:

```text
src/main/java/com/andgatech/gtnhitemdocexporter/
src/main/java/com/andgatech/gtnhitemdocexporter/config/
src/main/java/com/andgatech/gtnhitemdocexporter/export/
src/main/java/com/andgatech/gtnhitemdocexporter/command/
src/main/java/com/andgatech/gtnhitemdocexporter/event/
src/main/resources/META-INF/
src/main/resources/assets/gtnhitemdocexporter/lang/
src/test/java/com/andgatech/gtnhitemdocexporter/export/
```

- [ ] **Step 2: Replace generic mod dependencies**

In `src/main/java/com/andgatech/gtnhitemdocexporter/GTNHItemDocExporter.java`, set the `@Mod` dependencies to NEI only:

```java
@Mod(
    modid = Tags.MODID,
    version = Tags.VERSION,
    name = Tags.MODNAME,
    dependencies = "required-after:NotEnoughItems;",
    acceptedMinecraftVersions = "[1.7.10]")
public class GTNHItemDocExporter {
    public static final String MODID = Tags.MODID;
    public static final String MOD_ID = Tags.MODID;
    public static final String MOD_NAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;
    public static final String RESOURCE_ROOT_ID = "gtnhitemdocexporter";

    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    @Mod.Instance
    public static GTNHItemDocExporter instance;

    @SidedProxy(
        clientSide = "com.andgatech.gtnhitemdocexporter.ClientProxy",
        serverSide = "com.andgatech.gtnhitemdocexporter.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
```

- [ ] **Step 3: Add NEI and JUnit dependencies**

In `dependencies.gradle`, keep `elytraModpackVersion { setGtnhVersion("2.8.4") }`, remove unused GT/GregTech-only implementation dependencies, and add:

```groovy
dependencies {
    elytraModpackVersion {
        setGtnhVersion("2.8.4")
    }

    implementation(gtnh("NotEnoughItems"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
}

String gtnh(String name, String classifier = "dev") {
    return project.elytraModpackVersion.gtnhdev(name)
}
```

- [ ] **Step 4: Run compile to validate scaffold**

Run:

```powershell
.\gradlew.bat compileJava
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit scaffold**

Run:

```powershell
git add -A
git commit -m "chore: scaffold item doc exporter mod"
```

## Task 2: Add Export Data Model and Pure Formatters

**Files:**
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocEntry.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocIndex.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/CraftTweakerNameFormatter.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/NbtSummaryFormatter.java`
- Test: `src/test/java/com/andgatech/gtnhitemdocexporter/export/CraftTweakerNameFormatterTest.java`
- Test: `src/test/java/com/andgatech/gtnhitemdocexporter/export/NbtSummaryFormatterTest.java`

- [ ] **Step 1: Write failing CT formatter tests**

Create `CraftTweakerNameFormatterTest.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CraftTweakerNameFormatterTest {

    @Test
    public void omitsZeroMeta() {
        assertEquals("<minecraft:stone>", CraftTweakerNameFormatter.format("minecraft:stone", 0));
    }

    @Test
    public void includesPositiveMeta() {
        assertEquals("<gregtech:gt.metaitem.01:1234>",
                CraftTweakerNameFormatter.format("gregtech:gt.metaitem.01", 1234));
    }

    @Test
    public void keepsWildcardMetaVisible() {
        assertEquals("<minecraft:wool:32767>", CraftTweakerNameFormatter.format("minecraft:wool", 32767));
    }
}
```

- [ ] **Step 2: Write failing NBT summary tests**

Create `NbtSummaryFormatterTest.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NbtSummaryFormatterTest {

    @Test
    public void emptyWhenNbtIsMissing() {
        assertEquals("", NbtSummaryFormatter.summarize(null, true, 20));
        assertEquals("", NbtSummaryFormatter.summarize("", true, 20));
    }

    @Test
    public void emptyWhenDisabled() {
        assertEquals("", NbtSummaryFormatter.summarize("{foo:1}", false, 20));
    }

    @Test
    public void truncatesLongText() {
        assertEquals("1234567890...", NbtSummaryFormatter.summarize("123456789012345", true, 13));
    }
}
```

- [ ] **Step 3: Run tests and verify they fail**

Run:

```powershell
.\gradlew.bat test --tests "*CraftTweakerNameFormatterTest" --tests "*NbtSummaryFormatterTest"
```

Expected: FAIL because formatter classes do not exist.

- [ ] **Step 4: Implement data model and formatters**

Create `ItemDocEntry.java`:

```java
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
```

Create `ItemDocIndex.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

import java.util.List;

public final class ItemDocIndex {

    public final int schemaVersion;
    public final String generatedAt;
    public final String minecraftVersion;
    public final String language;
    public final int entryCount;
    public final List<ItemDocEntry> entries;

    public ItemDocIndex(String generatedAt, String language, List<ItemDocEntry> entries) {
        this.schemaVersion = 1;
        this.generatedAt = generatedAt;
        this.minecraftVersion = "1.7.10";
        this.language = language == null ? "" : language;
        this.entries = entries;
        this.entryCount = entries == null ? 0 : entries.size();
    }
}
```

Create `CraftTweakerNameFormatter.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

public final class CraftTweakerNameFormatter {

    private CraftTweakerNameFormatter() {}

    public static String format(String registryId, int meta) {
        if (registryId == null || registryId.isEmpty()) {
            return "<unknown>";
        }
        if (meta == 0) {
            return "<" + registryId + ">";
        }
        return "<" + registryId + ":" + meta + ">";
    }
}
```

Create `NbtSummaryFormatter.java`:

```java
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
```

- [ ] **Step 5: Run tests and verify they pass**

Run:

```powershell
.\gradlew.bat test --tests "*CraftTweakerNameFormatterTest" --tests "*NbtSummaryFormatterTest"
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit formatters**

Run:

```powershell
git add src/main/java/com/andgatech/gtnhitemdocexporter/export src/test/java/com/andgatech/gtnhitemdocexporter/export
git commit -m "feat: add item doc export model"
```

## Task 3: Add JSON, CSV, and Markdown Writers

**Files:**
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemDocWriters.java`
- Test: `src/test/java/com/andgatech/gtnhitemdocexporter/export/ItemDocWritersTest.java`

- [ ] **Step 1: Write failing writer tests**

Create `ItemDocWritersTest.java`:

```java
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
        File dir = Files.createTempDirectory("item-doc-writers").toFile();
        ItemDocEntry entry = new ItemDocEntry("gregtech", "gregtech:gt.metaitem.01", 1234,
                "<gregtech:gt.metaitem.01:1234>", "示例物品", "Example Item", "item.example.name", false,
                "gregtech:gt.metaitem.01:1234", "");
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
```

- [ ] **Step 2: Run test and verify it fails**

Run:

```powershell
.\gradlew.bat test --tests "*ItemDocWritersTest"
```

Expected: FAIL because `ItemDocWriters` does not exist.

- [ ] **Step 3: Implement writers**

Create `ItemDocWriters.java`:

```java
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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private ItemDocWriters() {}

    public static void writeJson(ItemDocIndex index, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputDir, "item_index.json").toPath(),
                StandardCharsets.UTF_8)) {
            GSON.toJson(index, writer);
        }
    }

    public static void writeCsv(List<ItemDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputDir, "item_index.csv").toPath(),
                StandardCharsets.UTF_8)) {
            writer.write("modId,registryId,meta,ctExpression,chineseName,englishName,unlocalizedName,isBlock,guid,nbtSummary");
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

    public static void writeMarkdown(List<ItemDocEntry> entries, File outputDir) throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputDir, "item_index.md").toPath(),
                StandardCharsets.UTF_8)) {
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
                writer.write("| " + escapeMarkdown(entry.chineseName)
                        + " | " + escapeMarkdown(entry.englishName)
                        + " | `" + entry.ctExpression + "`"
                        + " | `" + entry.registryId + "`"
                        + " | " + entry.meta
                        + " | " + (entry.isBlock ? "是" : "否") + " |");
                writer.newLine();
            }
        }
    }

    public static void writeLastExportLog(File outputDir, int entryCount, int failureCount, long elapsedMillis)
            throws IOException {
        ensureDir(outputDir);
        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputDir, "last_export.log").toPath(),
                StandardCharsets.UTF_8)) {
            writer.write("entryCount=" + entryCount);
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
        return value == null ? "" : value.replace("|", "\\|").replace("\r", " ").replace("\n", " ");
    }
}
```

- [ ] **Step 4: Run writer tests**

Run:

```powershell
.\gradlew.bat test --tests "*ItemDocWritersTest"
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit writers**

Run:

```powershell
git add src/main/java/com/andgatech/gtnhitemdocexporter/export src/test/java/com/andgatech/gtnhitemdocexporter/export
git commit -m "feat: write item doc export files"
```

## Task 4: Add Minecraft and NEI Export Integration

**Files:**
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/config/ExporterConfig.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/LanguageNameResolver.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/MinecraftItemDocCollector.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/export/ItemIndexExportService.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/event/ClientExportEventHandler.java`
- Create: `src/main/java/com/andgatech/gtnhitemdocexporter/command/CommandItemDoc.java`
- Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/GTNHItemDocExporter.java`
- Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/CommonProxy.java`
- Modify: `src/main/java/com/andgatech/gtnhitemdocexporter/ClientProxy.java`

- [ ] **Step 1: Add config wrapper**

Create `ExporterConfig.java`:

```java
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
            autoExportOnJoin = config.getBoolean("autoExportOnJoin", Configuration.CATEGORY_GENERAL, true,
                    "Export the NEI item index once after joining a world.");
            writeJson = config.getBoolean("writeJson", Configuration.CATEGORY_GENERAL, true,
                    "Write item_index.json.");
            writeCsv = config.getBoolean("writeCsv", Configuration.CATEGORY_GENERAL, true,
                    "Write item_index.csv.");
            writeMarkdown = config.getBoolean("writeMarkdown", Configuration.CATEGORY_GENERAL, true,
                    "Write item_index.md.");
            includeNbtSummary = config.getBoolean("includeNbtSummary", Configuration.CATEGORY_GENERAL, true,
                    "Include truncated NBT text in the exported index.");
            maxNbtSummaryLength = config.getInt("maxNbtSummaryLength", Configuration.CATEGORY_GENERAL, 240, 0, 4096,
                    "Maximum length for NBT summary text.");
            forceEnglishLocale = config.getString("forceEnglishLocale", Configuration.CATEGORY_GENERAL, "en_US",
                    "Locale used for English display names.");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
```

- [ ] **Step 2: Add language resolver**

Create `LanguageNameResolver.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public final class LanguageNameResolver {

    public String currentName(ItemStack stack) {
        return strip(stack == null ? "" : stack.getDisplayName());
    }

    public String englishName(ItemStack stack) {
        if (stack == null) {
            return "";
        }
        String unlocalizedName = stack.getUnlocalizedName();
        String fallback = net.minecraft.util.StatCollector.translateToFallback(unlocalizedName + ".name");
        if (fallback == null || fallback.equals(unlocalizedName + ".name")) {
            fallback = net.minecraft.util.StatCollector.translateToFallback(unlocalizedName);
        }
        return strip(fallback == null ? "" : fallback);
    }

    private static String strip(String value) {
        String stripped = EnumChatFormatting.getTextWithoutFormattingCodes(value);
        return stripped == null ? "" : stripped.trim();
    }
}
```

If `StatCollector.translateToFallback` is unavailable in the mapped 1.7.10 environment, replace only `englishName` with a reflection-backed fallback and keep the same method signature.

- [ ] **Step 3: Add ItemStack collector**

Create `MinecraftItemDocCollector.java`:

```java
package com.andgatech.gtnhitemdocexporter.export;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class MinecraftItemDocCollector {

    private final LanguageNameResolver names;
    private final boolean includeNbtSummary;
    private final int maxNbtSummaryLength;

    public MinecraftItemDocCollector(LanguageNameResolver names, boolean includeNbtSummary, int maxNbtSummaryLength) {
        this.names = names;
        this.includeNbtSummary = includeNbtSummary;
        this.maxNbtSummaryLength = maxNbtSummaryLength;
    }

    public ItemDocEntry collect(ItemStack stack) {
        String registryId = String.valueOf(Item.itemRegistry.getNameForObject(stack.getItem()));
        int meta = stack.getItemDamage();
        String modId = registryId.contains(":") ? registryId.substring(0, registryId.indexOf(':')) : "";
        String nbt = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";
        Block block = Block.getBlockFromItem(stack.getItem());

        return new ItemDocEntry(
                modId,
                registryId,
                meta,
                CraftTweakerNameFormatter.format(registryId, meta),
                names.currentName(stack),
                names.englishName(stack),
                stack.getUnlocalizedName(),
                block != null && block != Blocks.air,
                registryId + ":" + meta + (nbt.isEmpty() ? "" : "@" + Integer.toHexString(nbt.hashCode())),
                NbtSummaryFormatter.summarize(nbt, includeNbtSummary, maxNbtSummaryLength));
    }
}
```

- [ ] **Step 4: Add export service**

Create `ItemIndexExportService.java`:

```java
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

        MinecraftItemDocCollector collector = new MinecraftItemDocCollector(new LanguageNameResolver(),
                config.includeNbtSummary, config.maxNbtSummaryLength);
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

        entries.sort(Comparator
                .comparing((ItemDocEntry entry) -> entry.modId)
                .thenComparing(entry -> entry.registryId)
                .thenComparingInt(entry -> entry.meta)
                .thenComparing(entry -> entry.chineseName));

        ItemDocIndex index = new ItemDocIndex(nowIsoLike(), Minecraft.getMinecraft().getLanguageManager()
                .getCurrentLanguage().getLanguageCode(), entries);
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
```

- [ ] **Step 5: Add command and client auto-export event**

Create `CommandItemDoc.java`:

```java
package com.andgatech.gtnhitemdocexporter.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.andgatech.gtnhitemdocexporter.export.ItemIndexExportService;

public final class CommandItemDoc extends CommandBase {

    private final ItemIndexExportService service;

    public CommandItemDoc(ItemIndexExportService service) {
        this.service = service;
    }

    @Override
    public String getCommandName() {
        return "itemdoc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/itemdoc export";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || !"export".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + getCommandUsage(sender)));
            return;
        }
        if (!service.isNeiReady()) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "NEI 物品索引尚未加载完成，请稍后再试。"));
            return;
        }
        try {
            ItemIndexExportService.ExportResult result = service.exportNow();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "已导出 "
                    + result.entryCount + " 个条目到 " + result.outputDir.getAbsolutePath()));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "导出失败：" + e.getMessage()));
        }
    }
}
```

Create `ClientExportEventHandler.java`:

```java
package com.andgatech.gtnhitemdocexporter.event;

import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import com.andgatech.gtnhitemdocexporter.GTNHItemDocExporter;
import com.andgatech.gtnhitemdocexporter.config.ExporterConfig;
import com.andgatech.gtnhitemdocexporter.export.ItemIndexExportService;

public final class ClientExportEventHandler {

    private final ExporterConfig config;
    private final ItemIndexExportService service;
    private boolean exportQueued;
    private boolean exportedThisWorld;
    private int retryTicks;

    public ClientExportEventHandler(ExporterConfig config, ItemIndexExportService service) {
        this.config = config;
        this.service = service;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote && config.autoExportOnJoin) {
            exportQueued = true;
            exportedThisWorld = false;
            retryTicks = 0;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !exportQueued || exportedThisWorld) {
            return;
        }
        if (++retryTicks % 40 != 0) {
            return;
        }
        if (!service.isNeiReady()) {
            return;
        }
        try {
            ItemIndexExportService.ExportResult result = service.exportNow();
            exportedThisWorld = true;
            exportQueued = false;
            GTNHItemDocExporter.LOG.info("Exported {} item doc entries to {}", result.entryCount, result.outputDir);
        } catch (Exception e) {
            exportQueued = false;
            GTNHItemDocExporter.LOG.error("Failed to auto-export item doc index", e);
        }
    }
}
```

- [ ] **Step 6: Wire lifecycle**

In `GTNHItemDocExporter.java`, add static config and service fields:

```java
public static final ExporterConfig CONFIG = new ExporterConfig();
public static ItemIndexExportService EXPORT_SERVICE;
```

In `CommonProxy.java`, load config and create service:

```java
public void preInit(FMLPreInitializationEvent event) {
    GTNHItemDocExporter.CONFIG.load(event.getSuggestedConfigurationFile());
    GTNHItemDocExporter.EXPORT_SERVICE = new ItemIndexExportService(GTNHItemDocExporter.CONFIG);
}

public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandItemDoc(GTNHItemDocExporter.EXPORT_SERVICE));
}
```

In `ClientProxy.java`, register the client event handler:

```java
@Override
public void init(FMLInitializationEvent event) {
    super.init(event);
    MinecraftForge.EVENT_BUS.register(new ClientExportEventHandler(
            GTNHItemDocExporter.CONFIG, GTNHItemDocExporter.EXPORT_SERVICE));
    FMLCommonHandler.instance().bus().register(new ClientExportEventHandler(
            GTNHItemDocExporter.CONFIG, GTNHItemDocExporter.EXPORT_SERVICE));
}
```

Create one handler instance before registering if duplicate state becomes a problem:

```java
ClientExportEventHandler handler = new ClientExportEventHandler(
        GTNHItemDocExporter.CONFIG, GTNHItemDocExporter.EXPORT_SERVICE);
MinecraftForge.EVENT_BUS.register(handler);
FMLCommonHandler.instance().bus().register(handler);
```

- [ ] **Step 7: Compile integration**

Run:

```powershell
.\gradlew.bat compileJava
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 8: Commit integration**

Run:

```powershell
git add src/main/java/com/andgatech/gtnhitemdocexporter
git commit -m "feat: export NEI item document index"
```

## Task 5: Verify Build, Update Docs, and Prepare Game Test

**Files:**
- Modify: `log.md`
- Modify: `ToDOLIST.md`
- Modify: `context.md`

- [ ] **Step 1: Run all automated verification**

Run:

```powershell
.\gradlew.bat test
.\gradlew.bat compileJava
```

Expected: both commands end with `BUILD SUCCESSFUL`.

- [ ] **Step 2: Build jar**

Run:

```powershell
.\gradlew.bat build
```

Expected: `BUILD SUCCESSFUL` and a jar under `build/libs/`.

- [ ] **Step 3: Record manual game-test procedure**

Append to `log.md`:

```markdown
## 2026-05-02: 索引导出器实现

### Completed
- 完成 `GTNHItemDocExporter` GTNH addon 脚手架。
- 实现 NEI `ItemList.items` 导出到 JSON、CSV、Markdown。
- 实现 `/itemdoc export` 手动导出命令。
- 实现进入世界后的自动导出。

### Verification
- `.\gradlew.bat test`：通过。
- `.\gradlew.bat compileJava`：通过。
- `.\gradlew.bat build`：通过。

### Manual Game Test
- 将 `build/libs/*.jar` 放入 GTNH 2.8.x 客户端 `mods/`。
- 启动客户端并进入世界。
- 检查 `<minecraft>/gtnh_item_doc_exporter/item_index.json`、`.csv`、`.md` 是否生成。
- 在聊天栏执行 `/itemdoc export`，确认文件重新生成。
```

- [ ] **Step 4: Update project plan and context docs**

Move implementation work in `ToDOLIST.md` from Current Plans to Completed:

```markdown
## Current Plans
- [ ] 在 GTNH 客户端中执行一次真实游戏测试。

## Completed
- [x] 确认第一阶段选择路线 A：先做索引导出器。
- [x] 使用 GTNH addon 模板脚手架创建项目。
- [x] 实现 NEI 物品/方块索引导出 MVP。
```

Update `context.md` architecture notes:

```markdown
## Architecture Notes
- 第一阶段实现游戏内索引导出 MVP。
- 主数据源为 NEI `codechicken.nei.ItemList.items`。
- 输出目录为 `<minecraft>/gtnh_item_doc_exporter/`。
- 自动导出通过客户端世界加载事件和 NEI 就绪检查触发。
- 手动导出命令为 `/itemdoc export`。
- GUI 脚本生成器是第二阶段，读取 `item_index.json`。
```

- [ ] **Step 5: Commit verification docs**

Run:

```powershell
git add log.md ToDOLIST.md context.md
git commit -m "docs: record item doc exporter implementation"
```

## Self-Review

Spec coverage:

- Independent addon mod: Task 1.
- NEI `ItemList.items` data source: Task 4.
- Auto export and `/itemdoc export`: Task 4.
- Output directory and files: Tasks 3 and 4.
- JSON/CSV/Markdown schema: Tasks 2 and 3.
- Config options: Task 4.
- Error handling for bad stacks and failed writes: Task 4.
- Build and game-test preparation: Task 5.
- Chinese continuity docs: Task 5.

Placeholder scan:

- No unspecified implementation placeholders remain.
- The only conditional note is the `StatCollector.translateToFallback` compatibility fallback, with an explicit replacement boundary and unchanged method signature.

Type consistency:

- `ItemDocEntry`, `ItemDocIndex`, `ItemDocWriters`, `ItemIndexExportService`, and `ExporterConfig` names match across tasks.
- `CommandItemDoc` and `ClientExportEventHandler` both consume `ItemIndexExportService`.
