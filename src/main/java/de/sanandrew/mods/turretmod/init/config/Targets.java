package de.sanandrew.mods.turretmod.init.config;

import com.google.common.collect.Collections2;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public final class Targets
{
    private static final ITag.INamedTag<EntityType<?>> FLYING_ENTITIES         = EntityTypeTags.bind(TmrConstants.ID + ":flying_target");
    private static final ITag.INamedTag<EntityType<?>> SWIMMING_ENTITIES       = EntityTypeTags.bind(TmrConstants.ID + ":swimming_target");
    private static final ITag.INamedTag<EntityType<?>> GROUNDED_ENTITIES       = EntityTypeTags.bind(TmrConstants.ID + ":grounded_target");
    private static final ITag.INamedTag<EntityType<?>> NON_TARGETABLE_ENTITIES = EntityTypeTags.bind(TmrConstants.ID + ":not_targetable");

    public static ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> flyingEntities;
    public static ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> swimmingEntities;
    public static ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> groundedEntities;

    private static final TargetMap CURRENT_ENTITY_LISTS = new TargetMap();

    public Targets(ForgeConfigSpec.Builder builder) {
        builder.push("targets");

        flyingEntities = builder.defineListAllowEmpty(Collections.singletonList("flyingEntities"), ArrayList::new, Objects::nonNull);
        swimmingEntities = builder.defineListAllowEmpty(Collections.singletonList("swimmingEntities"), ArrayList::new, Objects::nonNull);
        groundedEntities = builder.defineListAllowEmpty(Collections.singletonList("groundedEntities"), ArrayList::new, Objects::nonNull);
    }

    public static boolean canBeTargeted(ResourceLocation entityTypeId, ITurret.TargetType targetType) {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entityTypeId);
        return type != null && canBeTargeted(type, targetType);
    }

    public static boolean canBeTargeted(EntityType<?> entityType, ITurret.TargetType targetType) {
        if( NON_TARGETABLE_ENTITIES.contains(entityType) ) {
            TargetEntry ef = getEntry(flyingEntities, entityType);
            TargetEntry es = getEntry(swimmingEntities, entityType);
            TargetEntry eg = getEntry(groundedEntities, entityType);

            if( (ef.empty || ef.remove) && (es.empty || es.remove) && (eg.empty || eg.remove) ) {
                return false;
            }
        }

        if( targetType == ITurret.TargetType.ALL ) {
            return canBeTargeted(entityType, ITurret.TargetType.AIR)
                   || canBeTargeted(entityType, ITurret.TargetType.WATER)
                   || canBeTargeted(entityType, ITurret.TargetType.GROUND);
        }

        if( targetType == ITurret.TargetType.AIR ) {
            TargetEntry e = getEntry(flyingEntities, entityType);
            return FLYING_ENTITIES.contains(entityType) ? !e.remove : !e.empty;
        } else if( targetType == ITurret.TargetType.WATER ) {
            TargetEntry e = getEntry(swimmingEntities, entityType);
            return SWIMMING_ENTITIES.contains(entityType) ? !e.remove : !e.empty;
        } else {
            TargetEntry e = getEntry(groundedEntities, entityType);
            return e.empty
                   ? GROUNDED_ENTITIES.contains(entityType) || !(canBeTargeted(entityType, ITurret.TargetType.AIR) || canBeTargeted(entityType, ITurret.TargetType.WATER))
                   : !e.remove;
        }
    }

    @SubscribeEvent
    public static void buildTargetLists(FMLServerAboutToStartEvent event) {
        CURRENT_ENTITY_LISTS.clear();

        ForgeRegistries.ENTITIES.getValues().forEach(e -> {
            if( e.getCategory() == EntityClassification.MISC ) {
                return;
            }

            if( canBeTargeted(e, ITurret.TargetType.AIR) ) {
                CURRENT_ENTITY_LISTS.add(ITurret.TargetType.AIR, e);
            } else if( canBeTargeted(e, ITurret.TargetType.WATER) ) {
                CURRENT_ENTITY_LISTS.add(ITurret.TargetType.WATER, e);
            } else if( canBeTargeted(e, ITurret.TargetType.GROUND) ) {
                CURRENT_ENTITY_LISTS.add(ITurret.TargetType.GROUND, e);
            }
        });
    }

    public static Map<ResourceLocation, Boolean> getTargetList(ITurret.TargetType targetType) {
        return Collections.unmodifiableMap(CURRENT_ENTITY_LISTS.get(targetType));
    }

    @Nonnull
    private static TargetEntry getEntry(ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> entries, EntityType<?> entityType) {
        return entries.get().stream().map(e -> (TargetEntry) e).filter(t -> t.id.equals(entityType.toString())).findFirst().orElse(TargetEntry.EMPTY);
    }

    public static class TargetEntry
    {
        public       String  id;
        public       boolean remove = false;
        public final boolean empty;

        static final TargetEntry EMPTY = new TargetEntry(true);

        public TargetEntry() {
            this(false);
        }

        public TargetEntry(boolean empty) {
            this.empty = empty;
        }
    }

    private static class TargetMap
            extends EnumMap<ITurret.TargetType, Map<ResourceLocation, Boolean>>
    {
        public TargetMap() {
            super(ITurret.TargetType.class);

            this.put(ITurret.TargetType.ALL, new HashMap<>());
            this.put(ITurret.TargetType.AIR, new HashMap<>());
            this.put(ITurret.TargetType.WATER, new HashMap<>());
            this.put(ITurret.TargetType.GROUND, new HashMap<>());
        }

        public boolean add(ITurret.TargetType key, EntityType<?> value) {
            boolean hostile = value.getCategory() == EntityClassification.MONSTER;
            ResourceLocation valueId = value.getRegistryName();

            this.get(ITurret.TargetType.ALL).put(valueId, hostile);
            this.get(key).put(valueId, hostile);

            return true;
        }

        @Override
        public void clear() {
            this.forEach((key, value) -> value.clear());
        }
    }
}
