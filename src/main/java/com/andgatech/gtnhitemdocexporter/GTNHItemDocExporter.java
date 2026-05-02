package com.andgatech.gtnhitemdocexporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = GTNHItemDocExporter.MODID,
    version = Tags.VERSION,
    name = GTNHItemDocExporter.MOD_NAME,
    dependencies = "required-after:NotEnoughItems;",
    acceptedMinecraftVersions = "[1.7.10]")
public class GTNHItemDocExporter {

    public static final String MODID = "gtnhitemdocexporter";
    public static final String MOD_ID = MODID;
    public static final String MOD_NAME = "GTNHItemDocExporter";
    public static final String VERSION = Tags.VERSION;
    public static final String RESOURCE_ROOT_ID = "gtnhitemdocexporter";

    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance
    public static GTNHItemDocExporter instance;

    @SidedProxy(
        clientSide = "com.andgatech.gtnhitemdocexporter.ClientProxy",
        serverSide = "com.andgatech.gtnhitemdocexporter.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
