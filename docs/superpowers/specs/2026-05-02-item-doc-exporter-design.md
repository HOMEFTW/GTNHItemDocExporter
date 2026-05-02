# GTNHItemDocExporter 设计文档

## 目标

创建一个独立的 GTNH addon mod。玩家进入 GTNH 2.8.x 客户端后，模组根据 NotEnoughItems 的运行时索引导出所有可见物品和方块的文档，供后续桌面 GUI 应用读取并辅助生成 CraftTweaker `.zs` 脚本。

第一阶段只实现“游戏内索引导出 MVP”，不实现 GUI，不导出配方，不修改 NotEnoughItems 或 CraftTweaker 本体。

## 基本信息

- 项目目录：`D:\Code\GTNHItemDocExporter`
- Mod 名称：`GTNHItemDocExporter`
- Mod ID：`gtnhitemdocexporter`
- Java 包名：`com.andgatech.gtnhitemdocexporter`
- 目标环境：Minecraft 1.7.10 + GTNH 2.8.x
- 主要依赖：NotEnoughItems、Forge、Minecraft 1.7.10

## 推荐架构

模组作为独立客户端侧 addon 运行。它不改 NEI 和 CraftTweaker 源码，而是读取 NEI 暴露的运行时数据。

核心模块：

- `GTNHItemDocExporter`：Forge `@Mod` 入口，负责生命周期、配置加载、客户端事件和命令注册。
- `ItemIndexExportService`：导出主流程。等待 NEI 数据就绪，遍历 `codechicken.nei.ItemList.items`，生成结构化条目并写入文件。
- `ItemDocEntry`：单个物品或方块条目的数据模型。
- `LanguageNameResolver`：解析当前语言中文名和 `en_US` 英文名。
- `CraftTweakerNameFormatter`：生成 CraftTweaker 尖括号表达式。
- `ItemDocWriters`：分别写 `item_index.json`、`item_index.csv`、`item_index.md`。
- `CommandItemDoc`：提供 `/itemdoc export` 手动重新导出。

## 导出时机

- 玩家进入世界后自动导出一次。
- 提供 `/itemdoc export` 命令手动重新导出。
- 自动导出必须等待 NEI `ItemList` 完成加载，避免生成空文档。
- 自动导出建议通过 NEI `ItemList.loadCallbacks` 或 `ItemList.loadFinished` 加延迟重试实现。

## 输出目录

所有文件输出到当前客户端实例目录：

```text
<minecraft>/gtnh_item_doc_exporter/
```

输出文件：

- `item_index.json`：后续 GUI 主数据源。
- `item_index.csv`：方便使用表格软件查看。
- `item_index.md`：可读文档，按 mod 分组，方便搜索和分享。
- `last_export.log`：记录最近一次导出的数量、耗时和失败条目数。

## JSON 数据格式

`item_index.json` 使用稳定 schema，便于后续 GUI 读取。

```json
{
  "schemaVersion": 1,
  "generatedAt": "2026-05-02T19:30:00+08:00",
  "minecraftVersion": "1.7.10",
  "language": "zh_CN",
  "entryCount": 12345,
  "entries": [
    {
      "registryId": "gregtech:gt.metaitem.01",
      "meta": 1234,
      "ctExpression": "<gregtech:gt.metaitem.01:1234>",
      "chineseName": "示例物品",
      "englishName": "Example Item",
      "unlocalizedName": "item.example.name",
      "isBlock": false,
      "guid": "gregtech:gt.metaitem.01:1234",
      "nbtSummary": "",
      "modId": "gregtech"
    }
  ]
}
```

字段说明：

- `registryId`：`Item.itemRegistry.getNameForObject(stack.getItem())`。
- `meta`：`stack.getItemDamage()`。
- `ctExpression`：CraftTweaker 表达式，格式为 `<modid:item>` 或 `<modid:item:meta>`。
- `chineseName`：当前语言下的 `stack.getDisplayName()`，通常是 `zh_CN`。
- `englishName`：通过 `en_US` 语言数据解析得到。
- `unlocalizedName`：`stack.getUnlocalizedName()`。
- `isBlock`：`Block.getBlockFromItem(stack.getItem()) != Blocks.air`。
- `guid`：用于区分 NEI 中的唯一物品展示项。
- `nbtSummary`：可选 NBT 摘要，长度受配置限制。
- `modId`：`registryId` 冒号前的命名空间。

排序规则：

1. `modId`
2. `registryId`
3. `meta`
4. `chineseName`

去重规则：

- `registryId + meta + guid/NBT` 相同视为同一项。
- 同 `registryId + meta` 但 NBT 或 GUID 不同的条目保留多条。

## CSV 和 Markdown

`item_index.csv` 使用与 JSON entry 相同的字段，字段顺序固定：

```text
modId,registryId,meta,ctExpression,chineseName,englishName,unlocalizedName,isBlock,guid,nbtSummary
```

`item_index.md` 按 `modId` 分组，每组输出 Markdown 表格，包含常用字段：

```text
| 中文名 | 英文名 | CT 表达式 | registryId | meta | 是否方块 |
```

## 配置

配置文件：

```text
config/gtnhitemdocexporter.cfg
```

配置项：

- `autoExportOnJoin = true`
- `writeJson = true`
- `writeCsv = true`
- `writeMarkdown = true`
- `includeNbtSummary = true`
- `maxNbtSummaryLength = 240`
- `forceEnglishLocale = en_US`

## 错误处理

- NEI 未加载完成时，自动导出延迟重试。
- 手动 `/itemdoc export` 在 NEI 未就绪时提示玩家稍后再试。
- 单个物品读取名称、NBT、方块信息失败时，只记录该条目的错误，不中断整次导出。
- 写文件失败时，在聊天栏和日志中提示具体路径。
- 每次导出覆盖旧文件，并写入 `last_export.log`。

## 依赖 CraftTweaker 的边界

第一阶段不直接调用 CraftTweaker API，只生成 CraftTweaker 能识别的尖括号表达式。

表达式规则参考 CraftTweaker 1.7.10 中的 `ItemBracketHandler` 和 `MCItemStack.toString()`：

- meta 为 `0` 时：`<modid:item>`
- meta 大于 `0` 时：`<modid:item:meta>`
- wildcard 或 NBT 条目后续需要在 GUI 阶段单独处理，MVP 先以真实 NEI 条目为准导出基础表达式和 NBT 摘要。

## 验收标准

- 项目能构建 jar，并能放入 GTNH 2.8.x 客户端启动。
- 进入单人世界或服务器后自动生成三份索引文档。
- `/itemdoc export` 可以手动重新生成文档。
- JSON 中包含 NEI 可见物品的大部分条目，而不是只扫描 vanilla 注册表。
- GregTech meta item 能生成正确 CT 表达式，例如 `<gregtech:gt.metaitem.01:1234>`。
- 中文名和英文名尽量填充；英文缺失时允许为空并记录。
- 单个异常物品不会导致整个导出失败。
- 项目根目录维护中文 `log.md`、`ToDOLIST.md`、`context.md`。

## 后续阶段

第二阶段创建桌面 GUI 应用，架构沿用 `gtnh-mod-installer`：

- `main.py`
- `core/`
- `gui/`
- `utils/`
- `tests/`

GUI 读取 `item_index.json`，提供搜索、配方格编辑、模板选择、ZS 预览和 `.zs` 文件输出。
