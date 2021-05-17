package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.UUID;

public class ServerProxy
        implements IProxy
{
    @Override
    public void setupClient(FMLClientSetupEvent event) { }

    @Override
    public void fillPlayerListClient(Map<UUID, ITextComponent> map) { }

    @Override
    public boolean checkTurretGlowing(ITurretInst turretInst) {
        return false;
    }
}
