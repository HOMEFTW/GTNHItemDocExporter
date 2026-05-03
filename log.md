# 开发日志

## 2026-05-03: 发布前版本和 README

### 已完成
- 将 `gradle.properties` 的 `modVersion` 更新为 `1.0.0`。
- 新增中文 `README.md`，说明模组用途、导出文件、使用流程和构建方式。
- README 链接到 GitHub 仓库 `GTNHItemDocExporter` 与配套 GUI `GTNHItemDocScriptBuilder`，并说明二者关系。
- 更新 `context.md` 和 `ToDOLIST.md` 中的版本与发布记录。

### 决策
- release 主产物使用 `build/libs/gtnhitemdocexporter-1.0.0.jar`；sources jar 只作为源码辅助产物，不作为客户端安装主 jar。

---

## 2026-05-03: 检查包含矿物字典索引的游戏内导出目录

### 已完成
- 检查用户从游戏中复制出的 `D:\Code\gtnh_item_doc_exporter`。
- 确认生成了 `ore_dictionary_index.json`、`ore_dictionary_index.csv`、`ore_dictionary_index.md`。
- 确认 `last_export.log` 记录 `entryCount=57228`、`fluidEntryCount=1576`、`oreDictionaryEntryCount=22205`、`failureCount=0`。
- 确认 OreDict JSON 条目数与 CSV 行数一致，且 `oreName` 与 `ctExpression` 均无重复。
- 交叉检查 `ZZZ-NxerCustoms.zs` 中 21 个 `<ore:...>`，均可在 `ore_dictionary_index.json` 中找到。

### 遇到的问题
- **通配 meta 示例存在**：OreDict 包含物品列表中有 203 个 `:32767>` 表达式，来自 Forge `OreDictionary.WILDCARD_VALUE`；不影响直接使用 `<ore:...>`。
- **少量注册名质量问题**：包含 2 个 `GalacticraftMars:item.null` 形式的物品表达式，看起来来自原模组注册名或显示名质量问题；不影响矿物字典条目本身。

### 决策
- 当前 `ore_dictionary_index.*` 可用于 GUI 的 `<ore:...>` 搜索和填入功能。
- GUI 应优先插入 `ctExpression` 的 `<ore:...>`，包含物品列表只作为辅助识别信息。

### 验证
- 使用 Python 解析 `ore_dictionary_index.json` 成功。
- 使用 CSV 解析器统计 `ore_dictionary_index.csv` 行数成功。

---

## 2026-05-03: 增加 Forge OreDictionary 索引导出

### 已完成
- 新增 `OreDictionaryDocEntry`、`OreDictionaryDocIndex` 和 `MinecraftOreDictionaryDocCollector`。
- 从 Forge `OreDictionary.getOreNames()` / `OreDictionary.getOres(oreName)` 收集矿物字典条目。
- 新增 `ore_dictionary_index.json`、`ore_dictionary_index.csv`、`ore_dictionary_index.md` 写出逻辑。
- 矿物字典条目包含 `oreName`、`ctExpression`、`itemCount`、包含物品 CT 表达式列表和 `guid`。
- 自动导出和 `/itemdoc export` 现在会同时输出物品/方块索引、流体索引与矿物字典索引。
- `last_export.log` 新增 `oreDictionaryEntryCount`。

### 遇到的问题
- **`build --offline` 首次失败**：Spotless 检查发现新增 Java 文件和部分换行格式不符合项目规则 → 运行 `.\gradlew.bat spotlessApply --offline` 后重新构建通过。

### 决策
- 不从 NEI 私有结构导出 OreDict；NEI 也是读取 Forge `OreDictionary`，直接使用 Forge 数据源更稳定。
- GUI 后续读取 `ore_dictionary_index.json`，点击后直接填入 `<ore:...>` 表达式。

### 验证
- 先新增 `ItemDocWritersTest#writesOreDictionaryFormats` 并确认因缺少 OreDict 模型和 writer 编译失败。
- `.\gradlew.bat test --offline`：通过。
- `.\gradlew.bat build --offline`：通过。

---

## 2026-05-03: 检查包含流体索引的游戏内导出目录

### 已完成
- 检查用户从游戏中复制出的 `D:\Code\gtnh_item_doc_exporter`。
- 确认生成了 `item_index.json`、`item_index.csv`、`item_index.md`、`fluid_index.json`、`fluid_index.csv`、`fluid_index.md` 和 `last_export.log`。
- 确认 `last_export.log` 记录 `entryCount=57228`、`fluidEntryCount=1576`、`failureCount=0`。
- 确认 JSON 条目数与 CSV 行数一致，且物品/流体 `guid` 与 `ctExpression` 均无重复。
- 交叉检查 `ZZZ-NxerCustoms.zs` 中 18 个 `<liquid:...>`，均可在 `fluid_index.json` 中找到。

### 遇到的问题
- **英文名仍大量回退为 lang key**：物品 `englishName` 有 30735 条为 `item.*` / `tile.*` 形式；流体 `englishName` 1576 条均为 `fluid.*.name` 或类似 key。
- **少量中文名缺失或未翻译**：`AWWayofTime:fluidSigil:0` 的 `chineseName` 为空；流体 `aquaregiaoth`、`lcl` 的 `chineseName` 看起来仍是未翻译 key。

### 决策
- 本次导出数据结构可用于 GUI 继续开发，特别是流体搜索和 RA2 流体参数选择。
- 英文名质量改进仍作为后续任务，不阻塞中文 GUI 与脚本生成。

### 验证
- 使用 Python 解析 `item_index.json` 与 `fluid_index.json` 成功。
- 使用 CSV 解析器统计 `item_index.csv` 与 `fluid_index.csv` 行数成功。

---

## 2026-05-03: 增加 Forge 流体索引导出

### 已完成
- 新增 `FluidDocEntry`、`FluidDocIndex` 和 `MinecraftFluidDocCollector`，从 `FluidRegistry.getRegisteredFluids()` 收集流体。
- 新增 `fluid_index.json`、`fluid_index.csv`、`fluid_index.md` 写出逻辑。
- 流体条目包含 `fluidName`、`ctExpression`、中文名、英文名、未本地化名、温度、密度、粘度、是否气体和 `guid`。
- 自动导出和 `/itemdoc export` 现在会同时输出物品/方块索引与流体索引。
- `last_export.log` 新增 `fluidEntryCount`。

### 遇到的问题
- **`build --offline` 首次失败**：Spotless 检查发现新文件换行格式不符合项目规则 → 运行 `.\gradlew.bat spotlessApply --offline` 后重新构建通过。

### 决策
- 流体 CraftTweaker 表达式按 Forge 注册名生成 `<liquid:fluidName>`，用于后续 GUI 直接插入 RA2 `.fluidInputs(...)` / `.fluidOutputs(...)`。
- 流体导出跟随现有 `writeJson`、`writeCsv`、`writeMarkdown` 配置开关，不额外增加独立开关。

### 验证
- 先新增 `ItemDocWritersTest#writesFluidFormats` 并确认因缺少流体模型编译失败。
- `.\gradlew.bat test --offline`：通过。
- `.\gradlew.bat build --offline`：通过。

---

## 2026-05-02: 检查游戏内生成的导出目录

### 已完成
- 检查用户从游戏中复制出的 `D:\Code\gtnh_item_doc_exporter`。
- 确认生成了 `item_index.json`、`item_index.csv`、`item_index.md` 和 `last_export.log`。
- 确认 JSON 与 CSV 均为 57232 条，`last_export.log` 记录 `failureCount=0`。

### 遇到的问题
- **英文名回退率偏高**：30739 / 57232 条 `englishName` 仍为 `item.*` 或 `tile.*` 未本地化 key，占 53.71%。
- **中文名空值极少**：仅 `AWWayofTime:fluidSigil:0` 的 `chineseName` 为空。

### 决策
- 当前导出结果可用于第一阶段 ID/CT 表达式文档测试。
- 后续若 GUI 需要更可靠英文显示，应单独改进英文名解析逻辑。

### 验证
- JSON 可解析，`entryCount` 与实际 `entries` 数量一致。
- CSV 可解析，行数与 JSON 一致。
- `guid` 无重复，`registryId` 与 `ctExpression` 无空值。

---

## 2026-05-02: 打包测试用 jar

### 已完成
- 运行 `.\gradlew.bat build --offline` 重新打包测试构建。
- 生成测试用主 jar：`build/libs/gtnhitemdocexporter-0.1.0-dev.jar`。

### 遇到的问题
- 无。

### 决策
- 测试时使用 `gtnhitemdocexporter-0.1.0-dev.jar`，不要使用 `sources` jar；`dev-dev` jar 保留给开发环境用途。

### 验证
- `.\gradlew.bat build --offline`：通过。

---

## 2026-05-02: 完成 NEI 物品索引导出 MVP

### 已完成
- 实现 `ItemDocEntry`、`ItemDocIndex`、`CraftTweakerNameFormatter`、`NbtSummaryFormatter` 数据模型与格式化逻辑。
- 实现 `ItemDocWriters`，可写出 `item_index.json`、`item_index.csv`、`item_index.md` 和 `last_export.log`。
- 接入 NEI `ItemList.items`，实现进入世界后自动导出和客户端命令 `/itemdoc export`。
- 参考 `GTNH LIB\ModTweaker-master`，优先通过反射调用 `minetweaker.mc1710.item.MCItemStack.toString()` 生成 CraftTweaker 表达式，失败时回退到 `<modid:item:meta>`。
- 运行 `spotlessApply --offline` 修复 GTNH Spotless 格式要求。

### 遇到的问题
- **CraftTweaker 离线依赖不可解析**：GTNH 2.8.4 清单解析到 `CraftTweaker 3.4.2`，本机 Gradle 离线缓存只有 `3.4.6` → 改为运行时反射调用 CraftTweaker 内部类，移除编译期硬依赖，同时保留 `required-after:MineTweaker3`。
- **`build --offline` 首次失败**：`spotlessJavaCheck` 报导入顺序和换行格式不符合规则 → 运行 `spotlessApply --offline` 后重新构建通过。

### 决策
- 将 `/itemdoc export` 注册为客户端命令，因为 NEI 索引和语言表都来自客户端环境。
- 保持第一阶段只导出索引，不在本阶段实现桌面 GUI。

### 验证
- `.\gradlew.bat test --offline`：通过。
- `.\gradlew.bat compileJava --offline`：通过。
- `.\gradlew.bat build --offline`：通过。

---

## 2026-05-02: 实现计划确认

### 已完成
- 用户确认设计文档可以进入实现计划阶段。
- 写入实现计划 `docs/superpowers/plans/2026-05-02-item-doc-exporter.md`。

### 遇到的问题
- 无。

### 决策
- 计划分为脚手架、数据模型与格式化、文件写出、NEI 集成、构建与文档收尾五个任务。

---

## 2026-05-02: 补充 ModTweaker 参考

### 已完成
- 阅读 `GTNH LIB\ModTweaker-master` 的入口、命令注册和物品表达式输出辅助逻辑。

### 遇到的问题
- 无。

### 决策
- 后续 Minecraft/NEI 集成阶段优先参考 ModTweaker 的 `new MCItemStack(stack).toString()` 思路生成脚本可用物品表达式，同时保留纯 Java `CraftTweakerNameFormatter` 作为 fallback。

---

## 2026-05-02: 项目设计确认

### 已完成
- 确认第一阶段采用路线 A：先实现游戏内物品/方块索引导出 MVP。
- 确认新项目为独立 GTNH addon mod，不修改 NotEnoughItems 或 CraftTweaker 本体。
- 确认项目目录为 `D:\Code\GTNHItemDocExporter`。
- 确认 Mod ID 为 `gtnhitemdocexporter`，Java 包名为 `com.andgatech.gtnhitemdocexporter`。
- 确认导出文件为 `item_index.json`、`item_index.csv`、`item_index.md`，输出到 `<minecraft>/gtnh_item_doc_exporter/`。
- 写入设计文档 `docs/superpowers/specs/2026-05-02-item-doc-exporter-design.md`。

### 遇到的问题
- 无。

### 决策
- 使用 NEI `ItemList.items` 作为主数据源：这样能覆盖 NEI 实际显示的变体和 GregTech meta item。
- GUI 延后到第二阶段：先保证导出的运行时数据准确，再围绕 JSON 构建脚本生成器。

---
