package de.sanandrew.mods.turretmod.init;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.UUID;

public interface IProxy
{
    void setupClient(FMLClientSetupEvent event);

    void fillPlayerListClient(Map<UUID, ITextComponent> map);
}