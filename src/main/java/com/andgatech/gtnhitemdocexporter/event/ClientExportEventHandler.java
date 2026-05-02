package com.andgatech.gtnhitemdocexporter.event;

import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import com.andgatech.gtnhitemdocexporter.GTNHItemDocExporter;
import com.andgatech.gtnhitemdocexporter.config.ExporterConfig;
import com.andgatech.gtnhitemdocexporter.export.ItemIndexExportService;

public final class ClientExportEventHandler {

    private final ExporterConfig config;
    private final ItemIndexExportService service;
    private boolean exportQueued;
    private boolean exportedThisWorld;
    private int retryTicks;

    public ClientExportEventHandler(ExporterConfig config, ItemIndexExportService service) {
        this.config = config;
        this.service = service;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote && config.autoExportOnJoin) {
            exportQueued = true;
            exportedThisWorld = false;
            retryTicks = 0;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !exportQueued || exportedThisWorld) {
            return;
        }
        if (++retryTicks % 40 != 0) {
            return;
        }
        if (!service.isNeiReady()) {
            return;
        }
        try {
            ItemIndexExportService.ExportResult result = service.exportNow();
            exportedThisWorld = true;
            exportQueued = false;
            GTNHItemDocExporter.LOG.info("Exported {} item doc entries to {}", result.entryCount, result.outputDir);
        } catch (Exception e) {
            exportQueued = false;
            GTNHItemDocExporter.LOG.error("Failed to auto-export item doc index", e);
        }
    }
}
