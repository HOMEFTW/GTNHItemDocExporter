# Development Log

## 2026-05-02: 实现计划确认

### Completed
- 用户确认设计文档可以进入实现计划阶段。
- 写入实现计划 `docs/superpowers/plans/2026-05-02-item-doc-exporter.md`。

### Issues Encountered
- 无。

### Decisions Made
- 计划分为脚手架、数据模型与格式化、文件写出、NEI 集成、构建与文档收尾五个任务。

---

## 2026-05-02: 项目设计确认

### Completed
- 确认第一阶段采用路线 A：先实现游戏内物品/方块索引导出 MVP。
- 确认新项目为独立 GTNH addon mod，不修改 NotEnoughItems 或 CraftTweaker 本体。
- 确认项目目录为 `D:\Code\GTNHItemDocExporter`。
- 确认 Mod ID 为 `gtnhitemdocexporter`，Java 包名为 `com.andgatech.gtnhitemdocexporter`。
- 确认导出文件为 `item_index.json`、`item_index.csv`、`item_index.md`，输出到 `<minecraft>/gtnh_item_doc_exporter/`。
- 写入设计文档 `docs/superpowers/specs/2026-05-02-item-doc-exporter-design.md`。

### Issues Encountered
- 无。

### Decisions Made
- 使用 NEI `ItemList.items` 作为主数据源：这样能覆盖 NEI 实际显示的变体和 GregTech meta item。
- GUI 延后到第二阶段：先保证导出的运行时数据准确，再围绕 JSON 构建脚本生成器。

---
