package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

@SuppressWarnings("unused")
public interface ITmrUtils
{
    void openGui(PlayerEntity player, EnumGui id, int x, int y, int z);

    boolean canPlayerEditAll();

    boolean canOpEditAll();

    <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass);

    void addForcefield(Entity e, IForcefieldProvider provider);

    boolean hasForcefield(Entity e, Class<? extends IForcefieldProvider> providerCls);

    void setEntityTarget(MobEntity target, ITurretInst attackingTurret);
}
