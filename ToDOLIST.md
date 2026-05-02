# TODO

## 当前计划
- [ ] 改进英文名解析，降低 `englishName` 回退为 `item.*` / `tile.*` 的比例。
- [ ] 确认 `AWWayofTime:fluidSigil:0` 中文名为空是否来自原模组显示名。

## 未来想法
- [ ] 创建沿用 `gtnh-mod-installer` 架构的桌面 GUI，用鼠标点击生成 CraftTweaker `.zs` 脚本。
- [ ] 在 GUI 中加入物品搜索、配方格编辑、脚本模板、ZS 预览和文件输出。
- [ ] 后续考虑导出配方索引，辅助更复杂的脚本生成。

## 已完成
- [x] 确认第一阶段选择路线 A：先做索引导出器。
- [x] 审阅并确认 `docs/superpowers/specs/2026-05-02-item-doc-exporter-design.md`。
- [x] 编写实现计划 `docs/superpowers/plans/2026-05-02-item-doc-exporter.md`。
- [x] 使用 GTNH addon 模板脚手架创建项目。
- [x] 实现 NEI 物品/方块索引导出 MVP。
- [x] 将 `build/libs/gtnhitemdocexporter-0.1.0-dev.jar` 放入实际 GTNH 客户端并生成导出目录。
- [x] 检查 `D:\Code\gtnh_item_doc_exporter` 的导出文件结构与条目数量。

## 拒绝 / 暂缓
- 暂不修改 NotEnoughItems 本体：维护成本高。
- 暂不修改 CraftTweaker 本体：NEI 运行时索引更适合第一阶段。
- 暂不在第一阶段实现 GUI：先固定可靠数据源。
