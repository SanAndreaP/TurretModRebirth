/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret.techi;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretCrossbow
        extends EntityTurretBase
{
    private static final AxisAlignedBB RANGE_AABB = AxisAlignedBB.getBoundingBox(-16.0F, -4.0F, -16.0F, 16.0F, 4.0F, 16.0F);
    private final TargetSelector targetSelector = new TargetSelector();

    public EntityTurretCrossbow(World par1World) {
        super(par1World);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
    }

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_AABB.getOffsetBoundingBox(this.posX, this.posY, this.posZ);
    }

    @Override
    public IEntitySelector getTargetSelector() {
        return this.targetSelector;
    }

//    @Override
//    protected EntityTurretProjectile getProjectile() {
//        return new EntityProjectileArrow(this.worldObj);
//    }

    @Override
    protected String getShootSound() {
        return "random.bow";
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Textures.TURRET_T1_CROSSBOW.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Textures.TURRET_T1_CROSSBOW_GLOW.getResource();
    }

    public class TargetSelector implements IEntitySelector
    {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return EntityTurretCrossbow.this.canEntityBeSeen(entity) && EntityTurretCrossbow.this.getRangeBB().intersectsWith(entity.boundingBox);
        }
    }
}
