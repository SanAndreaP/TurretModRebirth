package de.sanandrew.mods.turretmod.api.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

public class TcuContainer
        extends Container
{
    public ITurretEntity turret;

    public TcuContainer(int windowId, PlayerInventory playerInventory, ITurretEntity turret) {
        super(ContainerRegistry.TCU, windowId);

        this.turret = turret;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.turret != null && this.turret.get().isAlive();
    }
}
