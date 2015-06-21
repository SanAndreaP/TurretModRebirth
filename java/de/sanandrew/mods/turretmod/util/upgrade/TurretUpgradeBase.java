/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.api.UpgrateQueueData;
import de.sanandrew.mods.turretmod.api.registry.TurretUpgradeRegistry;
import de.sanandrew.mods.turretmod.util.TurretMod;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all Turret Upgrades. If you want to make your own, extend this to your own class, instanciate that and register
 * your new Instance via {@link TurretUpgradeRegistry#registerUpgrade(TurretUpgrade)
 * TurretUpgradeRegistry#registerUpgrade()}.<br>
 * Those instances are acting like an instance of {@link net.minecraft.item.Item}, as they exist only once in the environment across every turret ever, so you
 * can compare 2 upgrade instances via ==, no need for .equals() here.<br>
 * It also provides some control methods, which affect the upgrade directly (apply an upgrade to a turret, load the upgrade etc.). For more
 * in-depth control, use the Event Bus system (mostly the {@link TurretUpgradeRegistry#EVENT_BUS
 * TurretUpgradeRegistry#EVENT_BUS}, but you
 * can also use others if they get fired by the turret, like the {@link net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent LivingUpdateEvent})
 */
public class TurretUpgradeBase
        implements TurretUpgrade
{
    public final String name;
    public final String textureName;
    public final TurretUpgrade dependantOn;
    private final List<Class<? extends Turret>> applicableTurrets = new ArrayList<>();

    public TurretUpgradeBase(String upgName, String texture) {
        this(upgName, texture, null);
    }

    public TurretUpgradeBase(String upgName, String texture, TurretUpgrade dependUpgrade) {
        this.name = upgName;
        this.textureName = texture;
        this.dependantOn = dependUpgrade;
    }

    @Override
    public final String getModId() {
        return TurretMod.MOD_ID;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final String getIconTexture() {
        return this.textureName;
    }

    @Override
    public final TurretUpgrade getDependantOn() {
        return this.dependantOn;
    }

    @Override
    public final List<Class<? extends Turret>> getApplicableTurrets() {
        return new ArrayList<>(this.applicableTurrets);
    }

    public final void addTurretApplicable(Class<? extends Turret> turretCls) {
        this.applicableTurrets.add(turretCls);
    }

    @Override
    public void onApply(Turret turret) { }

    @Override
    public void onLoad(Turret turret) { }

    @Override
    public void onSave(Turret turret) { }

    @Override
    public void onRemove(Turret turret) { }

    @Override
    public void onUpdateQueue(Turret turret, UpgrateQueueData queueData) { }
}
