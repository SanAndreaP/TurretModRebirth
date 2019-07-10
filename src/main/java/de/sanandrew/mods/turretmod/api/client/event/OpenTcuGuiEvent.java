/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.event;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * <p>An event fired whilst opening the turret control unit.</p>
 * <p>This can be used to cancel the opening or intercept and replace a page from the TCU GUI.</p>
 */
@Cancelable
public class OpenTcuGuiEvent
        extends Event
{
    /**
     * The player opening the GUI.
     */
    public final EntityPlayer player;
    /**
     * The turret instance the GUI is opened from.
     */
    public final ITurretInst turretInst;

    /**
     * The factory method used to create the {@link IGuiTCU} page.
     */
    @Nonnull
    public Supplier<IGuiTCU> factory;

    public OpenTcuGuiEvent(EntityPlayer player, ITurretInst turretInst, @Nonnull Supplier<IGuiTCU> factory) {
        this.player = player;
        this.turretInst = turretInst;
        this.factory = factory;
    }
}
