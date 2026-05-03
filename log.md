# 开发日志

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
