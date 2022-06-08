package de.sanandrew.mods.turretmod.item.upgrades;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Creative
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "creative_upgrade");

    private final AttributeModifier reload = new AttributeModifier(UUID.fromString("71101ff0-3e31-4673-be35-6d5d66a7b500"), TmrConstants.ID + ":reload_creative", -1.0D, AttributeModifier.Operation.MULTIPLY_BASE);
    private final AttributeModifier initShootTicks = new AttributeModifier(UUID.fromString("f94891fe-1989-47d8-aa1b-cae9ceacf240"), TmrConstants.ID + ":init_shoot_ticks", -1.0D, AttributeModifier.Operation.MULTIPLY_BASE);

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretEntity turretInst, ItemStack stack) {
        LivingEntity turretLiving = turretInst.get();
        if( !turretLiving.level.isClientSide ) {
            EntityUtils.tryRemoveModifier(turretLiving, TurretAttributes.MAX_RELOAD_TICKS, this.reload);
            EntityUtils.tryApplyModifier(turretLiving, TurretAttributes.MAX_RELOAD_TICKS, this.reload, true);

            EntityUtils.tryRemoveModifier(turretLiving, TurretAttributes.MAX_INIT_SHOOT_TICKS, this.initShootTicks);
            EntityUtils.tryApplyModifier(turretLiving, TurretAttributes.MAX_INIT_SHOOT_TICKS, this.initShootTicks, true);
        }
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        LivingEntity turretLiving = turretInst.get();
        if( !turretLiving.level.isClientSide ) {
            EntityUtils.tryRemoveModifier(turretLiving, TurretAttributes.MAX_RELOAD_TICKS, this.reload);
            EntityUtils.tryRemoveModifier(turretLiving, TurretAttributes.MAX_INIT_SHOOT_TICKS, this.initShootTicks);
        }
    }
}
