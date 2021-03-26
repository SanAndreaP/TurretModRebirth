package de.sanandrew.mods.turretmod.entity;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry
{
    //TODO: use setCustomClientFactory for sending delegate (& owner)?
    public static final EntityType<EntityTurret> TURRET = EntityType.Builder.create(EntityTurret::new, EntityClassification.MISC).trackingRange(10).build("turret");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(TURRET.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret")));
    }

    @SubscribeEvent
    public static void registerAttributes(RegistryEvent.Register<Attribute> event) {
        event.getRegistry().registerAll(TurretAttributes.MAX_AMMO_CAPACITY.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_ammo_capacity")),
                                        TurretAttributes.MAX_RELOAD_TICKS.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_reload_ticks")),
                                        TurretAttributes.MAX_INIT_SHOOT_TICKS.setRegistryName(new ResourceLocation(TmrConstants.ID, "max_init_shoot_ticks")));

        GlobalEntityTypeAttributes.put(TURRET, LivingEntity.registerAttributes()
                                                           .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                                                           .createMutableAttribute(TurretAttributes.MAX_AMMO_CAPACITY)
                                                           .createMutableAttribute(TurretAttributes.MAX_RELOAD_TICKS)
                                                           .createMutableAttribute(TurretAttributes.MAX_INIT_SHOOT_TICKS)
                                                           .create());
    }
}
