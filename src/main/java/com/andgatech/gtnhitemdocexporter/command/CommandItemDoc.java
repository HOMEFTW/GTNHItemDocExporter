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
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.AQUA + "已导出 "
                        + result.entryCount
                        + " 个物品/方块条目和 "
                        + result.fluidEntryCount
                        + " 个流体条目到 "
                        + result.outputDir.getAbsolutePath()));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "导出失败：" + e.getMessage()));
        }
    }
}
