/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;

public class ShieldHandler
{
    public static final List<Function<Entity, Entity>> PROJ_GET_OWNER = new ArrayList<>();

    private static final WeakHashMap<ITurretInst, WeakHashMap<Entity, Integer>> ALREADY_PUSHED = new WeakHashMap<>();

    static {
        PROJ_GET_OWNER.add(proj -> {
            if( proj instanceof EntityArrow ) {
                return ((EntityArrow) proj).shootingEntity;
            } else if( proj instanceof EntityThrowable ) {
                return ((EntityThrowable) proj).getThrower();
            } else if( proj instanceof EntityLlamaSpit ) {
                return ((EntityLlamaSpit) proj).owner;
            } else if( proj instanceof EntityFireball ) {
                return ((EntityFireball) proj).shootingEntity;
            }

            return null;
        });
    }

    public static void onTargeting(ITurretInst turretInst, ITargetProcessor processor) {
        ShieldTurret shield = turretInst.getRAM(null);
        EntityLiving turretL = turretInst.get();
        boolean hasPushed = false;
        List<Entity> recognizedEntities = new ArrayList<>();

        if( processor.canShoot() ) {
            if( shield != null && shield.getValue() > 0.0F ) {
                for( Entity target : processor.getValidTargetList() ) {
                    double dX = turretInst.get().posX - target.posX;
                    double dY = turretInst.get().posY - target.posY;
                    double dZ = turretInst.get().posZ - target.posZ;

                    if( knockBackEntity(turretInst, target, 1.0F, dX, dY, dZ) ) {
                        if( target instanceof EntityCreature ) {
                            TmrUtils.INSTANCE.setEntityTarget((EntityCreature) target, processor.getTurret());
                        }

                        hasPushed = true;

                        shield.damage(1.0F);
                        turretInst.updateState();

                        if( shield.getValue() <= 0.0F ) {
                            break;
                        }
                    }

                    recognizedEntities.add(target);
                }
            }

            if( shield != null && shield.getValue() > 0.0F ) {
                for( Entity projectile : turretL.world.getEntitiesWithinAABB(Entity.class, processor.getAdjustedRange(true)) ) {
                    Optional<Entity> opOwner = PROJ_GET_OWNER.stream().map(func -> func.apply(projectile))
                                                             .filter(owner -> owner != null && processor.isEntityTargeted(owner)).findFirst();
                    if( opOwner.isPresent() ) {
                        double dX = turretInst.get().posX - projectile.posX;
                        double dY = turretInst.get().posY - projectile.posY;
                        double dZ = turretInst.get().posZ - projectile.posZ;

                        if( knockBackEntity(turretInst, projectile, 0.5F, dX, dY, dZ) ) {
                            if( opOwner.get() instanceof EntityCreature ) {
                                TmrUtils.INSTANCE.setEntityTarget((EntityCreature) opOwner.get(), processor.getTurret());
                            }

                            hasPushed = true;

                            shield.damage(1.0F);
                            turretInst.updateState();

                            if( shield.getValue() <= 0.0F ) {
                                break;
                            }
                        }

                        recognizedEntities.add(projectile);
                    }
                }
            }
        }

        if( recognizedEntities.size() > 0 ) {
            if( hasPushed ) {
                processor.setShot(true);
                processor.getTurret().updateState();
            } else if( !processor.isShooting() ) {
                processor.decrInitShootTicks();
            }
        } else {
            processor.resetInitShootTicks();
        }

        if( ALREADY_PUSHED.containsKey(turretInst) ) {
            ALREADY_PUSHED.get(turretInst).entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() < turretInst.get().ticksExisted
                                                                                || !recognizedEntities.contains(entry.getKey()));
        }
        ALREADY_PUSHED.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue().isEmpty());
    }

    public static void onExplosion(ITurretInst turretInst, AxisAlignedBB explosionBB, List<BlockPos> blocksAffected, List<Entity> entitiesAffected) {
        AxisAlignedBB turretBB = turretInst.getTargetProcessor().getAdjustedRange(true);
        if( turretBB.intersects(explosionBB) ) {
            blocksAffected.removeIf(blockPos -> {
                if( turretInst.get().world.getBlockState(blockPos).getMaterial() != Material.AIR && turretBB.intersects(new AxisAlignedBB(blockPos)) ) {
                    ShieldTurret shield = turretInst.getRAM(null);
                    if( shield != null && shield.isShieldActive() ) {
                        shield.damage(2.0F);
                        return true;
                    }
                }
                return false;
            });
            entitiesAffected.removeIf(entity -> {
                if( turretBB.intersects(entity.getEntityBoundingBox()) && !turretInst.getTargetProcessor().isEntityTargeted(entity) ) {
                    ShieldTurret shield = turretInst.getRAM(null);
                    if( shield != null && shield.isShieldActive() ) {
                        shield.damage(2.0F);
                        return true;
                    }
                }
                return false;
            });
            turretInst.updateState();
        }
    }

    private static boolean knockBackEntity(ITurretInst turretInst, Entity entity, float strength, double xRatio, double yRatio, double zRatio) {
        boolean hasBeenPushed = ALREADY_PUSHED.containsKey(turretInst) && ALREADY_PUSHED.get(turretInst).getOrDefault(entity, -1) > turretInst.get().ticksExisted;
        if( hasBeenPushed ) {
            return false;
        } else {
            entity.isAirBorne = true;
            double avgRatio = Math.sqrt(xRatio * xRatio + yRatio * yRatio + zRatio * zRatio) * 1.0F;
            entity.motionY /= 2.0D;
            entity.motionX = -xRatio / avgRatio * strength;
            entity.motionY = Math.abs(yRatio) < 0.1F ? 0.4D : -yRatio * 2.0D / avgRatio * strength;
            entity.motionZ = -zRatio / avgRatio * strength;

            if( entity.onGround ) {
                entity.motionY /= 2.0D;
                entity.motionY += strength;

                if( entity.motionY > 0.4D ) {
                    entity.motionY = 0.4D;
                }
            }

            if( entity.world instanceof WorldServer ) {
                ((WorldServer) entity.world).getEntityTracker().sendToTracking(turretInst.get(), new SPacketEntityVelocity(entity));
            }

            ALREADY_PUSHED.computeIfAbsent(turretInst, inst -> new WeakHashMap<>()).put(entity, turretInst.get().ticksExisted + 20);

            return true;
        }
    }
}
