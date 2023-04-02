/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.entity;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.TurretAttributes;
import dev.sanandrea.mods.turretmod.entity.projectile.TurretProjectileEntity;
import dev.sanandrea.mods.turretmod.entity.turret.TurretEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry
{
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TmrConstants.ID);
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TmrConstants.ID);

    public static final EntityType<TurretEntity>           TURRET     = EntityType.Builder.<TurretEntity>of(TurretEntity::new, EntityClassification.MISC)
                                                                                          .clientTrackingRange(10)
                                                                                          .build("turret");
    public static final EntityType<TurretProjectileEntity> PROJECTILE = EntityType.Builder.<TurretProjectileEntity>of(TurretProjectileEntity::new, EntityClassification.MISC)
                                                                                          .clientTrackingRange(10)
                                                                                          .build("projectile");

    private EntityRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register("turret", () -> TURRET);
        ENTITY_TYPES.register("projectile", () -> PROJECTILE);

        ATTRIBUTES.register("max_ammo_capacity", () -> TurretAttributes.MAX_AMMO_CAPACITY);
        ATTRIBUTES.register("max_reload_ticks", () -> TurretAttributes.MAX_RELOAD_TICKS);
        ATTRIBUTES.register("max_init_shoot_ticks", () -> TurretAttributes.MAX_INIT_SHOOT_TICKS);

        ENTITY_TYPES.register(bus);
        ATTRIBUTES.register(bus);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(TURRET, LivingEntity.createLivingAttributes()
                                      .add(Attributes.FOLLOW_RANGE, 0.0D)
                                      .add(Attributes.ATTACK_DAMAGE, 1.0D)
                                      .add(TurretAttributes.MAX_AMMO_CAPACITY)
                                      .add(TurretAttributes.MAX_RELOAD_TICKS)
                                      .add(TurretAttributes.MAX_INIT_SHOOT_TICKS)
                                      .build());
    }
}
