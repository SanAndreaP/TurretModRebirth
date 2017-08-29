package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface ITmrUtils
{
    void updateTurretState(ITurretInst turret);

    void openGui(EntityPlayer player, EnumGui id, int x, int y, int z);

    boolean canPlayerEditAll();

    boolean canOpEditAll();

    <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass);

    void addForcefield(Entity e, IForcefieldProvider provider);
}
