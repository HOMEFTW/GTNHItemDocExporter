package com.andgatech.gtnhitemdocexporter;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        GTNHItemDocExporter.CONFIG.load(event.getSuggestedConfigurationFile());
        GTNHItemDocExporter.LOG.info(GTNHItemDocExporter.MOD_NAME + " at version " + Tags.VERSION + " preInit.");
    }

    public void init(FMLInitializationEvent event) {
        GTNHItemDocExporter.LOG.info(GTNHItemDocExporter.MOD_NAME + " init.");
    }

    public void serverStarting(FMLServerStartingEvent event) {
        GTNHItemDocExporter.LOG.info(GTNHItemDocExporter.MOD_NAME + " server starting.");
    }
}
