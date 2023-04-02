/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public interface ITcuClientRegistry
{
    void registerTcuScreen(@Nonnull ResourceLocation id, Supplier<ItemStack> iconSupplier,
                           Function<ContainerScreen<TcuContainer>, ITcuScreen> screenProvider);

    ILabelRegistry getLabelRegistry();

    void registerTcuInfoProvider(int priority, ITcuInfoProvider provider);
}
