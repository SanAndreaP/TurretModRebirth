package de.sanandrew.mods.turretmod.entity;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry
{
    //TODO: use setCustomClientFactory for sending delegate (& owner)?
    public static final EntityType<EntityTurret> TURRET = EntityType.Builder.<EntityTurret>of(EntityTurret::new, EntityClassification.MISC)
                                                                            .clientTrackingRange(10)
                                                                            .build("turret");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(TURRET.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret")));
    }

    @SubscribeEvent
    public static void registerAttributes(RegistryEvent.Register<Attribute> event) {
        event.getRegistry().registerAll(TurretAttributes.MAX_AMMO_CAPACITY.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_ammo_capacity")),
                                        TurretAttributes.MAX_RELOAD_TICKS.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_reload_ticks")),
                                        TurretAttributes.MAX_INIT_SHOOT_TICKS.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_init_shoot_ticks")));
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
