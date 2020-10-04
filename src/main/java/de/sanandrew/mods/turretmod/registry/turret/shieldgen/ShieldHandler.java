/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
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

                    if( knockBackEntity(turretInst, target, dX, dY, dZ) ) {
                        if( target instanceof EntityCreature ) {
                            TmrUtils.INSTANCE.setEntityTarget((EntityCreature) target, processor.getTurretInst());
                        }

                        hasPushed = true;

                        shield.damage(TurretForcefield.shieldDamagePerEntity);
                        turretInst.updateState();

                        if( shield.getValue() <= 0.0F ) {
                            break;
                        }
                    }

                    recognizedEntities.add(target);
                }
            }

            if( shield != null && shield.getValue() > 0.0F && turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PROJECTILE) ) {
                for( Entity projectile : turretL.world.getEntitiesWithinAABB(Entity.class, processor.getAdjustedRange(true)) ) {
                    Optional<Entity> opOwner = PROJ_GET_OWNER.stream().map(func -> func.apply(projectile))
                                                             .filter(owner -> owner != null && processor.isEntityTargeted(owner)).findFirst();
                    if( opOwner.isPresent() ) {
                        if( knockBackProjectile(turretInst, projectile) ) {
                            Entity owner = opOwner.get();
                            if( owner instanceof EntityCreature ) {
                                TmrUtils.INSTANCE.setEntityTarget((EntityCreature) owner, processor.getTurretInst());
                            }

                            hasPushed = true;

                            shield.damage(TurretForcefield.shieldDamagePerProjectile);
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
                processor.getTurretInst().updateState();
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
                        shield.damage(TurretForcefield.shieldDamagePerExplodedBlock);
                        return true;
                    }
                }
                return false;
            });
            entitiesAffected.removeIf(entity -> {
                if( turretBB.intersects(entity.getEntityBoundingBox()) && !turretInst.getTargetProcessor().isEntityTargeted(entity) ) {
                    ShieldTurret shield = turretInst.getRAM(null);
                    if( shield != null && shield.isShieldActive() ) {
                        shield.damage(TurretForcefield.shieldDamagePerExplodedEntity);
                        return true;
                    }
                }
                return false;
            });
            turretInst.updateState();
        }
    }

    private static boolean knockBackProjectile(ITurretInst turretInst, Entity projectile) {
        int ticksPushed = ALREADY_PUSHED.containsKey(turretInst) ? ALREADY_PUSHED.get(turretInst).getOrDefault(projectile, -1) : -1;

        boolean hasPushed = false;
        if( ticksPushed < 0 ) {
            projectile.prevRotationPitch = 0.0F;
            projectile.prevRotationYaw = 0.0F;

            projectile.setVelocity(-projectile.motionX, projectile.motionY, -projectile.motionZ);

            projectile.velocityChanged = true;

            ALREADY_PUSHED.computeIfAbsent(turretInst, inst -> new WeakHashMap<>()).put(projectile, turretInst.get().ticksExisted + 20);

            hasPushed = true;
        } else if( ticksPushed < turretInst.get().ticksExisted ) {
            projectile.setDead();

            EnumEffect.PROJECTILE_DEATH.addEffect(projectile);

            hasPushed = true;
        }

        return hasPushed;
    }

    private static boolean knockBackEntity(ITurretInst turretInst, Entity entity, double xRatio, double yRatio, double zRatio) {
        final float strength = 1.0F;
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
