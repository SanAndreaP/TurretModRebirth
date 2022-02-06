package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IUpgradeInstance<T extends IUpgradeInstance<T>>
{
    default void fromBytes(ObjectInputStream stream) throws IOException { }

    default void toBytes(ObjectOutputStream stream) throws IOException { }

    default void onTick(ITurretEntity turretInst) { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Tickable
    {

    }
}
