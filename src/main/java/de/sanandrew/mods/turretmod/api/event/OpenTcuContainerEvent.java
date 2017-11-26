/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.event;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.BiFunction;

@Cancelable
public class OpenTcuContainerEvent
        extends Event
{
    public final EntityPlayer player;
    public final ITurretInst turretInst;
    public BiFunction<EntityPlayer, ITurretInst, Container> factory;

    public OpenTcuContainerEvent(EntityPlayer player, ITurretInst turretInst, BiFunction<EntityPlayer, ITurretInst, Container> factory) {
        this.player = player;
        this.turretInst = turretInst;
        this.factory = factory;
    }
}
