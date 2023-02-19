package de.sanandrew.mods.turretmod.item.repairkits.delegate;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RegenRepairKit
        extends SimpleRepairKit
{
    private final int regenTime;
    private final int regenAmplifier;

    public RegenRepairKit(@Nonnull ResourceLocation id, int regenTime, int regenAmplifier) {
        super(id, 0.5F);
        this.regenTime = regenTime;
        this.regenAmplifier = regenAmplifier;
    }

    @Override
    public boolean isApplicable(@Nonnull ITurretEntity turret) {
        return super.isApplicable(turret) && !turret.get().hasEffect(Effects.REGENERATION);
    }

    @Override
    public void onApply(@Nonnull ITurretEntity turret) {
        turret.get().addEffect(new EffectInstance(Effects.REGENERATION, this.regenTime, this.regenAmplifier, true, false));
    }
}
