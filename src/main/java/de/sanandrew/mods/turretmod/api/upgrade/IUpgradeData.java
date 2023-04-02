/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IUpgradeData<T extends IUpgradeData<T>>
{
    default void load(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) { }

    default void save(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) { }

//    @Deprecated
//    default void fromBytes(ObjectInputStream stream) throws IOException { }
//
//    @Deprecated
//    default void toBytes(ObjectOutputStream stream) throws IOException { }

    default void onTick(ITurretEntity turretInst) { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Syncable
    { }
}
