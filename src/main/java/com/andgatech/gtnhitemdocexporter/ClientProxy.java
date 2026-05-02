package com.andgatech.gtnhitemdocexporter;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.FMLCommonHandler;

import com.andgatech.gtnhitemdocexporter.command.CommandItemDoc;
import com.andgatech.gtnhitemdocexporter.event.ClientExportEventHandler;
import com.andgatech.gtnhitemdocexporter.export.ItemIndexExportService;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        GTNHItemDocExporter.exportService = new ItemIndexExportService(GTNHItemDocExporter.CONFIG);
        ClientExportEventHandler handler = new ClientExportEventHandler(
                GTNHItemDocExporter.CONFIG,
                GTNHItemDocExporter.exportService);
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
        ClientCommandHandler.instance.registerCommand(new CommandItemDoc(GTNHItemDocExporter.exportService));
        GTNHItemDocExporter.LOG.info("registered client item doc export handlers.");
    }
}
