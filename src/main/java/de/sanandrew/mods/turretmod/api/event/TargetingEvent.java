/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.event;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Event.HasResult
public abstract class TargetingEvent
        extends Event
{
    public final ITargetProcessor processor;

    public TargetingEvent(ITargetProcessor processor) {
        this.processor = processor;
    }

    public static class TargetCheck
            extends TargetingEvent
    {
        public final Entity target;

        public TargetCheck(ITargetProcessor processor, Entity target) {
            super(processor);
            this.target = target;
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
        public final @Nonnull
        ItemStack ammoStack;
        public int consumeAmount;

        public ConsumeAmmo(ITargetProcessor processor, @Nonnull ItemStack ammoStack, int consumeAmount) {
            super(processor);
            this.ammoStack = ammoStack;
            this.consumeAmount = consumeAmount;
        }
    }
}
