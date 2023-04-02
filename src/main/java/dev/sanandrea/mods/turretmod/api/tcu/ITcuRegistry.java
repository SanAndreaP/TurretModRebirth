/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.tcu;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITcuRegistry
{
    void registerTcuPage(@Nonnull ResourceLocation id);

    void registerTcuPage(@Nonnull ResourceLocation id, @Nullable TcuContainer.TcuContainerProvider containerProvider);
}
