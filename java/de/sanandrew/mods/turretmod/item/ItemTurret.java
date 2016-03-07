/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;

public class ItemTurret
        extends Item
{
    public ItemTurret() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_placer");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float offX, float offY, float offZ) {
        if( !world.isRemote ) {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double shiftY = 0.0D;
            if( side == 1 && block.getRenderType() == 11 ) {
                shiftY = 0.5D;
            }

            EntityTurret turret = spawnTurret(world, ""/*getTurretName(stack)*/, x + 0.5D, y + shiftY, z + 0.5D);
            if( turret != null ) {
                if( stack.hasDisplayName() ) {
                    turret.setCustomNameTag(stack.getDisplayName());
                }

                if( !player.capabilities.isCreativeMode ) {
                    stack.stackSize--;
                }

//                turret.setOwner(player);
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if( !world.isRemote ) {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
            if( movingobjectposition == null ) {
                return stack;
            } else {
                if( movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK ) {
                    int x = movingobjectposition.blockX;
                    int y = movingobjectposition.blockY;
                    int z = movingobjectposition.blockZ;
                    if( !world.canMineBlock(player, x, y, z) ) {
                        return stack;
                    }
                    if( !player.canPlayerEdit(x, y, z, movingobjectposition.sideHit, stack) ) {
                        return stack;
                    }
                    if( world.getBlock(x, y, z) instanceof BlockLiquid ) {
                        EntityTurret turret = spawnTurret(world, ""/*getTurretName(stack)*/, x, y, z);
                        if( turret != null ) {
                            if( stack.hasDisplayName() ) {
                                turret.setCustomNameTag(stack.getDisplayName());
                            }

                            if( !player.capabilities.isCreativeMode ) {
                                stack.stackSize--;
                            }

//                            turret.setOwner(player);
                        }
                    }
                }
            }
        }
        return stack;
    }


    public static EntityTurret spawnTurret(World world, String name, double x, double y, double z) {
        EntityTurret turret = createEntity(name, world);
        if (turret != null) {
            turret.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            turret.rotationYawHead = turret.rotationYaw;
            turret.renderYawOffset = turret.rotationYaw;
            turret.onSpawnWithEgg(null);
            world.spawnEntityInWorld(turret);
            turret.playLivingSound();
        }

        return turret;
    }

    private static EntityTurret createEntity(String name, World world) {
        EntityTurret entity = null;
        try {
            Class<? extends EntityTurret> entityClass = EntityTurretCrossbow.class;//TurretRegistry.getTurretInfo(name).getTurretClass();
            if( entityClass != null ) {
                entity = entityClass.getConstructor(World.class).newInstance(world);
            }
        } catch( InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ex ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot instanciate turret!", ex);
        }

        if( entity == null ) {
            TurretModRebirth.LOG.printf(Level.WARN, "Skipping turret with name %s", name);
        }

        return entity;
    }
}
