package com.limplungs.flatxp;

import java.util.Arrays;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class FlatXPDummyContainer extends DummyModContainer
{
	public FlatXPDummyContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "flatxp";
        meta.name = "FlatXP";
        meta.description = "Makes Enchantment levels all equal amounts of XP, and makes the Enchantment Table cost the full levels (1-30).";
        meta.version = "1.12.2-1.0.1";
        meta.authorList = Arrays.asList("LimpLungs");
        meta.credits = "My brother Gigapickles' idea and request. Also, VikeStep and his github/youtube tutorials on ASM Bytecode Manipulation in Minecraft Modding https://github.com/VikeStep/Coremod-Tutorial";
    }

    @Override
    public boolean registerBus(com.google.common.eventbus.EventBus bus, LoadController controller)
    {
    	super.registerBus(bus, controller);
        return true;
    }
}
