# 项目上下文

## 基本信息
- Mod Name: GTNHItemDocExporter
- Version: 1.0.0
- Mod ID: gtnhitemdocexporter
- Package: com.andgatech.gtnhitemdocexporter
- Target: MC 1.7.10 + GTNH 2.9.0-beta-3
- Build JDK: `JAVA_HOME` 指向 Zulu25；验证时使用 `D:\Code\.tools\gradle-home-java25` 复用已缓存的 Azul JDK8 toolchain
- Manifest: `elytra.manifest.version = 2.9.0-beta-3`；`dependencies.gradle` 使用 `elytraModpackVersion.gtnh(...)` 解析 GTNH 模组版本

## 已实现内容

### 机器
| 名称 | Meta ID | 类型 | 状态 |
|------|---------|------|--------|

### 物品
| 名称 | 注册 | 说明 |
|------|--------------|-------------|

### 方块
| 名称 | 注册 | 说明 |
|------|--------------|-------------|

### 材料
- 无。

### 配方
| Recipe Pool | 类型 | 数量 |
|-------------|------|-------|

### 配置项
| Key | 默认值 | 说明 |
|-----|---------|-------------|
| autoExportOnJoin | true | 进入世界后自动导出一次 |
| writeJson | true | 写出 `item_index.json` |
| writeCsv | true | 写出 `item_index.csv` |
| writeMarkdown | true | 写出 `item_index.md` |
| includeNbtSummary | true | 导出 NBT 摘要 |
| maxNbtSummaryLength | 240 | NBT 摘要最大长度 |
| forceEnglishLocale | en_US | 预留的英文名解析语言配置；当前实现使用 `StatCollector.translateToFallback` |

### 命令
| 命令 | 侧 | 说明 |
|------|----|------|
| `/itemdoc export` | 客户端 | 立即从 NEI 索引导出物品/方块文档 |

### 导出文件
| 文件 | 说明 |
|------|------|
| `item_index.json` | GUI 第二阶段使用的结构化索引 |
| `item_index.csv` | 表格工具可读索引 |
| `item_index.md` | 人类可读 Markdown 文档 |
| `fluid_index.json` | GUI 读取的结构化流体索引，包含 `<liquid:...>` 表达式 |
| `fluid_index.csv` | 表格工具可读流体索引 |
| `fluid_index.md` | 人类可读流体 Markdown 文档 |
| `ore_dictionary_index.json` | GUI 读取的结构化矿物字典索引，包含 `<ore:...>` 表达式与包含物品列表 |
| `ore_dictionary_index.csv` | 表格工具可读矿物字典索引 |
| `ore_dictionary_index.md` | 人类可读矿物字典 Markdown 文档 |
| `last_export.log` | 最近一次导出数量、失败数和耗时 |

### 构建产物
| 文件 | 说明 |
|------|------|
| `build/libs/gtnhitemdocexporter-1.0.0.jar` | 放入 GTNH 客户端 `mods` 目录测试和发布的主 jar |
| `build/libs/gtnhitemdocexporter-1.0.0-sources.jar` | 源码 jar，不用于客户端测试 |

### 已观察到的导出样本
| 来源目录 | 条目数 | 失败数 | 语言 | 说明 |
|----------|--------|--------|------|------|
| `D:\Code\gtnh_item_doc_exporter` | 57228 | 0 | zh_CN | 物品/方块 JSON/CSV/Markdown 均已生成 |

### 已观察到的流体导出样本
| 来源目录 | 条目数 | 失败数 | 语言 | 说明 |
|----------|--------|--------|------|------|
| `D:\Code\gtnh_item_doc_exporter` | 1576 | 0 | zh_CN | 流体 JSON/CSV/Markdown 均已生成；`ZZZ-NxerCustoms.zs` 中 18 个 `<liquid:...>` 均可匹配 |

### 已观察到的矿物字典导出样本
| 来源目录 | 条目数 | 失败数 | 语言 | 说明 |
|----------|--------|--------|------|------|
| `D:\Code\gtnh_item_doc_exporter` | 22205 | 0 | zh_CN | 矿物字典 JSON/CSV/Markdown 均已生成；`ZZZ-NxerCustoms.zs` 中 21 个 `<ore:...>` 均可匹配 |

### Mixin
- 暂无。

## 依赖
- NotEnoughItems `2.8.130-GTNH`
- CraftTweaker `3.4.8` / MineTweaker3（运行时依赖，编译期通过反射调用 `MCItemStack`，避免离线缓存版本差异）
- Minecraft Forge 1.7.10
- GTNH 2.9.0-beta-3 环境

## 验证状态（2026-09-24）
- 已按本地 `2.9.0-beta-3.json` 对齐两个版本声明。
- JDK 25 + Gradle 9.2.1，使用 `--gradle-user-home D:\Code\.tools\gradle-home-java25 compileJava build`：通过；9 项测试通过。
- 已通过 `javap` 核验 beta3 NEI 的 `ItemList.items/loadFinished` 和 CraftTweaker 的 `MCItemStack(ItemStack)/toString()`。
- Forge/MC 版本未变，流体与矿词使用原有注册表 API，导出 JSON 格式不变。
- 未启动 beta3 客户端；进入世界后自动导出、手动命令、三类实际数量/失败数仍需游戏内验证。上面的历史样本不是 beta3 的验证结果。

## 架构记录
- 第一阶段已实现游戏内索引导出 MVP。
- 主数据源为 NEI `codechicken.nei.ItemList.items`，等待 `ItemList.loadFinished` 后导出。
- 输出目录为 `<minecraft>/gtnh_item_doc_exporter/`。
- `ClientExportEventHandler` 在客户端世界加载后排队导出，每 40 tick 检查一次 NEI 是否就绪。
- `CommandItemDoc` 通过 `ClientCommandHandler` 注册为客户端命令。
- `MinecraftItemDocCollector` 收集 registry ID、meta、当前语言名称、英文名称、未本地化名称、方块标记、CraftTweaker 表达式和 NBT 摘要。
- `MinecraftFluidDocCollector` 从 `FluidRegistry.getRegisteredFluids()` 收集流体注册名、当前语言名称、英文名称、未本地化名称、温度、密度、粘度、气体标记和 `<liquid:...>` 表达式。
- `MinecraftOreDictionaryDocCollector` 从 Forge `OreDictionary` 收集 `oreName`、`<ore:...>` 表达式和该条目包含的物品 CT 表达式列表。
- `ItemDocWriters` 同时负责写出 `item_index.*`、`fluid_index.*` 与 `ore_dictionary_index.*`。
- GUI 脚本生成器 [`GTNHItemDocScriptBuilder`](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder) 是第二阶段，读取 `item_index.json`、`fluid_index.json` 和 `ore_dictionary_index.json`。
- GUI 后续应同时读取 `fluid_index.json`，用于 GT RA2 `.fluidInputs(...)`、`.fluidOutputs(...)` 和 `RecipeRemover.remove(...)` 的流体参数。
- GUI 后续应同时读取 `ore_dictionary_index.json`，用于配方输入中的 `<ore:...>`。
- 参考 `GTNH LIB\ModTweaker-master`：其作为 CraftTweaker 附属模组使用 `MCItemStack.toString()` 输出可用于脚本的物品表达式。
