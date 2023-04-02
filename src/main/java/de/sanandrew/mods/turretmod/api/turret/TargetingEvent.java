/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

@Event.HasResult
public abstract class TargetingEvent
        extends Event
{
    public final ITargetProcessor processor;

    public TargetingEvent(ITargetProcessor processor) {
        this.processor = processor;
    }

    @Cancelable
    public static class ProcessorTick
            extends TargetingEvent
    {
        public final long processTicks;
        public ProcessorTick(ITargetProcessor processor, long processTicks) {
            super(processor);
            this.processTicks = processTicks;
        }
    }

    public static class TargetCheck
            extends TargetingEvent
    {
        public final Entity target;
        public final boolean isLast;

        public TargetCheck(ITargetProcessor processor, Entity target, boolean isLast) {
            super(processor);
            this.target = target;
            this.isLast = isLast;
        }
    }

    @Cancelable
    public static class Shooting
            extends TargetingEvent
    {
        public Shooting(ITargetProcessor processor) {
            super(processor);
        }
    }

    public static class ConsumeAmmo
            extends TargetingEvent
    {
        public final @Nonnull ItemStack ammoStack;
        public int consumeAmount;

        public ConsumeAmmo(ITargetProcessor processor, @Nonnull ItemStack ammoStack, int consumeAmount) {
            super(processor);
            this.ammoStack = ammoStack;
            this.consumeAmount = consumeAmount;
        }
    }
}
