/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;

/**
 * This is the base class for all Turret Upgrades. If you want to make your own, extend this to your own class, instanciate that and register
 * your new Instance via {@link de.sanandrew.mods.turretmod.util.TurretUpgradeRegistry#registerUpgrade(TurretUpgrade)
 * TurretUpgradeRegistry#registerUpgrade()}.<br>
 * Those instances are acting like an instance of {@link net.minecraft.item.Item}, as they exist only once in the environment across every turret ever, so you
 * can compare 2 upgrade instances via ==, no need for .equals() here.<br>
 * It also provides some control methods, which affect the upgrade directly (apply an upgrade to a turret, load the upgrade etc.). For more
 * in-depth control, use the Event Bus system (mostly the {@link de.sanandrew.mods.turretmod.util.TurretUpgradeRegistry#EVENT_BUS
 * TurretUpgradeRegistry#EVENT_BUS}, but you
 * can also use others if they get fired by the turret, like the {@link net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent LivingUpdateEvent})
 */
public class TurretUpgrade
{
    public final String modId;
    public final String name;
    public final String textureName;
    public final TurretUpgrade dependantOn;

    public TurretUpgrade(String modID, String upgName, String texture) {
        this(modID, upgName, texture, null);
    }

    public TurretUpgrade(String modID, String upgName, String texture, TurretUpgrade dependUpgrade) {
        this.modId = modID;
        this.name = upgName;
        this.textureName = texture;
        this.dependantOn = dependUpgrade;
    }

    public final String getUnlocName() {
        return String.format("turretUpg.%s.%s.name", this.modId, this.name);
    }

    public final String getRegistrationName() {
        return String.format("%s:%s", this.modId, this.name);
    }

    public final String getItemTextureLoc() {
        return String.format("%s:%s", this.modId, this.textureName);
    }

    /**
     * Called when this upgrade gets applied to the turret (through interaction from the player or other means).
     * This is called client and server side!
     * Note for client side: This gets also called when the upgrades are loaded from NBT!
     * @param turret The turret this upgrade gets applied
     */
    public void onApply(AEntityTurretBase turret) { }

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turret The turret which loads this upgrade
     */
    public void onLoad(AEntityTurretBase turret) { } // TODO: possibly add NBTTagCompound

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turret The turret which loads this upgrade
     */
    public void onSave(AEntityTurretBase turret) { } // TODO: possibly add NBTTagCompound

    /**
     * Called when this upgrade gets removed from the turret.
     * This is called client and server side!
     * @param turret The turret which loads this upgrade
     */
    public void onRemove(AEntityTurretBase turret) { }
}
