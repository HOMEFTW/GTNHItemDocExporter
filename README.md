# GTNHItemDocExporter

从正在运行的 **GT New Horizons** 客户端导出物品、流体和矿物字典索引，为查找资源和编写 CraftTweaker 脚本提供真实游戏数据。

**模组版本：1.0.0 · 目标整合包：GTNH 2.9.0-beta-3 · 工作室：Andgatech**

[下载 JAR](https://github.com/HOMEFTW/GTNHItemDocExporter/releases) · [配套脚本编辑器](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder) · [反馈问题](https://github.com/HOMEFTW/GTNHItemDocExporter/issues)

## 快速开始

1. 在 [Releases](https://github.com/HOMEFTW/GTNHItemDocExporter/releases) 下载面向 GTNH **2.9.0-beta-3** 的 `gtnhitemdocexporter-1.0.0.jar`。
2. 将 JAR 放入对应客户端实例的 `mods` 文件夹。`-dev.jar` 和 `-sources.jar` 不用于正常游戏安装。
3. 启动客户端并进入世界。默认在 NEI 物品列表加载完成后自动导出一次。
4. 在该实例的 `gtnh_item_doc_exporter` 文件夹查看结果。
5. 启动 [GTNHItemDocScriptBuilder](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder)，选择导出的 `item_index.json`，即可搜索资源并编写脚本。

需要重新导出时，在游戏内执行：

```text
/itemdoc export
```

如果提示 NEI 尚未就绪，等待其物品列表加载完成后重试。导出使用当前客户端实际加载的数据；升级整合包或变更模组后，应重新生成索引。

## 导出结果

默认输出到 `<客户端实例>/gtnh_item_doc_exporter/`：

| 文件 | 内容 |
| --- | --- |
| `item_index.json` | 物品与方块：注册 ID、meta、名称、CraftTweaker 表达式、NBT 摘要等 |
| `fluid_index.json` | 流体：注册名、名称、物理属性和 `<liquid:...>` 表达式 |
| `ore_dictionary_index.json` | 矿物字典：矿词名称、`<ore:...>` 表达式及成员物品 |
| `*_index.csv` | 三类索引的表格版本 |
| `*_index.md` | 三类索引的 Markdown 文档 |
| `last_export.log` | 最近一次导出的条目数量、失败统计和耗时 |

配套编辑器以 JSON 为数据源。请把三个 JSON 文件保留在同一目录，选择物品索引时会自动尝试加载流体和矿物字典索引。

名称取决于客户端语言和模组提供的翻译。某些英文名或中文名可能回退为语言键；NBT 摘要用于查看，不等同于完整 NBT 数据。

## 配置

首次运行后，在客户端 `config` 目录生成的本模组配置文件中调整：

| 选项 | 默认值 | 用途 |
| --- | --- | --- |
| `autoExportOnJoin` | `true` | 进入世界后自动导出 |
| `writeJson` | `true` | 输出 JSON，供配套编辑器使用 |
| `writeCsv` | `true` | 输出 CSV |
| `writeMarkdown` | `true` | 输出 Markdown |
| `includeNbtSummary` | `true` | 包含 NBT 摘要 |
| `maxNbtSummaryLength` | `240` | NBT 摘要的最大长度 |

`forceEnglishLocale` 当前为预留项；英文名实际通过 Minecraft 的回退翻译机制解析。

## 兼容性与验证范围

| 组件 | 本次基线 |
| --- | --- |
| Minecraft / Forge | `1.7.10` / `10.13.4.1614` |
| GTNH | `2.9.0-beta-3` |
| NotEnoughItems | `2.8.130-GTNH` |
| CraftTweaker | `3.4.8` |

本模组在客户端导出数据，目标依赖版本由 GTNH manifest 解析。已完成构建、9 项单元测试，以及目标 NEI 的就绪字段和 CraftTweaker 物品表达式接口核验。

**本次尚未在 beta3 客户端完成游戏内导出验证。** 实际使用时应确认三类索引有内容，并检查 `last_export.log` 中的失败统计。旧版本的导出样本不代表本次基线的运行结果。

## 从源码构建

构建需要 **JDK 25**，仓库包含 **Gradle 9.2.1 Wrapper**；Minecraft 模组代码仍面向 Java 8。

```powershell
git clone https://github.com/HOMEFTW/GTNHItemDocExporter.git
cd GTNHItemDocExporter
# 将 JAVA_HOME 指向本机安装的 JDK 25
.\gradlew.bat build
```

首次构建需要联网下载依赖。构建产物在 `build/libs/`，玩家使用不带 `-dev` 或 `-sources` 后缀的 JAR。仅运行测试可使用 `./gradlew.bat test`。

## 两个项目如何配合

```text
GTNH 客户端 → Exporter 导出索引 → ScriptBuilder 编辑配方 → .zs 脚本
```

Exporter 负责读取真实游戏注册数据；[ScriptBuilder](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder) 负责搜索、配方设计、脚本编辑与保存。导出器本身不修改配方，也不执行编辑器生成的脚本。
