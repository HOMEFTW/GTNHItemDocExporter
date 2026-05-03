# GTNHItemDocExporter

`GTNHItemDocExporter` 是一个面向 GT New Horizons 的 Minecraft 1.7.10 Forge 客户端辅助模组，用来在进入游戏后导出脚本编写所需的索引文档。

- GitHub：<https://github.com/HOMEFTW/GTNHItemDocExporter>
- 当前版本：`1.0.0`
- 工作室：`Andgatech`
- 配套 GUI 应用：[`GTNHItemDocScriptBuilder`](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder)

## 项目关系

本模组负责从真实 GTNH 客户端环境里导出数据；[`GTNHItemDocScriptBuilder`](https://github.com/HOMEFTW/GTNHItemDocScriptBuilder) 负责读取这些数据并辅助生成 CraftTweaker / ModTweaker / GregTech `.zs` 脚本。

推荐流程：

1. 把本模组 jar 放入 GTNH 客户端 `mods` 目录。
2. 启动一次游戏，让模组导出索引文件。
3. 在 `GTNHItemDocScriptBuilder` 中选择导出的 `item_index.json`。
4. 在 GUI 里搜索物品、流体、矿物字典和 recipe map，生成或维护 `.zs` 脚本。

## 导出内容

模组会在客户端导出目录生成：

- `item_index.json`：物品和方块索引，包含中文名、英文名、注册 ID、meta、CraftTweaker 表达式等。
- `fluid_index.json`：Forge `FluidRegistry` 中的流体索引，包含流体名、中文名、英文名和 `<liquid:...>` 表达式。
- `ore_dictionary_index.json`：矿物字典索引，包含 `oreName`、`<ore:...>` 表达式和关联物品。

这些文件是 GUI 应用的主要数据来源。

## 使用方法

1. 下载 release 中的 `gtnhitemdocexporter-1.0.0.jar`。
2. 放入 GTNH 客户端的 `mods` 目录。
3. 启动游戏并进入主菜单或世界。
4. 根据配置或命令生成导出目录。
5. 把导出目录中的 `item_index.json` 提供给 `GTNHItemDocScriptBuilder`。

如果 `fluid_index.json` 和 `ore_dictionary_index.json` 与 `item_index.json` 在同一目录，GUI 会自动加载它们。

## 构建

需要 JDK 和 Gradle Wrapper：

```powershell
.\gradlew.bat build
```

构建产物位于：

```text
build/libs/gtnhitemdocexporter-1.0.0.jar
```

## 适用范围

- Minecraft `1.7.10`
- Forge `10.13.4.1614`
- GT New Horizons 客户端环境
- 用于辅助 CraftTweaker / ModTweaker / GregTech 脚本编写

## 许可证

请以仓库中的实际许可证文件为准。
