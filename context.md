# Project Context

## Basic Info
- Mod Name: GTNHItemDocExporter
- Mod ID: gtnhitemdocexporter
- Package: com.andgatech.gtnhitemdocexporter
- Target: MC 1.7.10 + GTNH 2.8.x

## Implemented Content

### Machines
| Name | Meta ID | Type | Status |
|------|---------|------|--------|

### Items
| Name | Registration | Description |
|------|--------------|-------------|

### Blocks
| Name | Registration | Description |
|------|--------------|-------------|

### Materials
- 无。

### Recipes
| Recipe Pool | Type | Count |
|-------------|------|-------|

### Config Options
| Key | Default | Description |
|-----|---------|-------------|
| autoExportOnJoin | true | 进入世界后自动导出一次 |
| writeJson | true | 写出 `item_index.json` |
| writeCsv | true | 写出 `item_index.csv` |
| writeMarkdown | true | 写出 `item_index.md` |
| includeNbtSummary | true | 导出 NBT 摘要 |
| maxNbtSummaryLength | 240 | NBT 摘要最大长度 |
| forceEnglishLocale | en_US | 英文名解析语言 |

### Mixins
- 暂无。

## Dependencies
- NotEnoughItems
- Minecraft Forge 1.7.10
- GTNH 2.8.x 环境

## Architecture Notes
- 第一阶段只实现游戏内索引导出 MVP。
- 主数据源为 NEI `codechicken.nei.ItemList.items`。
- 输出目录为 `<minecraft>/gtnh_item_doc_exporter/`。
- GUI 脚本生成器是第二阶段，读取 `item_index.json`。
- 参考 `GTNH LIB\ModTweaker-master`：其作为 CraftTweaker 附属模组使用 `MCItemStack.toString()` 输出可用于脚本的物品表达式。
