/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.init.config;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.init.TmrConfig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private static boolean loaded = false;

    private static final ITag.INamedTag<EntityType<?>> FLYING_ENTITIES         = EntityTypeTags.bind(TmrConstants.ID + ":flying_target");
    private static final ITag.INamedTag<EntityType<?>> SWIMMING_ENTITIES       = EntityTypeTags.bind(TmrConstants.ID + ":swimming_target");
    private static final ITag.INamedTag<EntityType<?>> GROUNDED_ENTITIES       = EntityTypeTags.bind(TmrConstants.ID + ":grounded_target");
    private static final ITag.INamedTag<EntityType<?>> NON_TARGETABLE_ENTITIES = EntityTypeTags.bind(TmrConstants.ID + ":not_targetable");

    public final ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> flyingEntities;
    public final ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> swimmingEntities;
    public final ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> groundedEntities;

    private static final TargetMap CURRENT_ENTITY_LISTS = new TargetMap();

    private Targets(ForgeConfigSpec.Builder builder) {
        this.flyingEntities = builder.worldRestart().defineListAllowEmpty(Collections.singletonList("flyingEntities"), ArrayList::new, Objects::nonNull);
        this.swimmingEntities = builder.worldRestart().defineListAllowEmpty(Collections.singletonList("swimmingEntities"), ArrayList::new, Objects::nonNull);
        this.groundedEntities = builder.worldRestart().defineListAllowEmpty(Collections.singletonList("groundedEntities"), ArrayList::new, Objects::nonNull);
        builder.pop();
    }

    public void load() {
        if( loaded ) {
            buildTargetLists(null);
        }
    }

    public static Targets build(ForgeConfigSpec.Builder builder) {
        return new Targets(builder.push("targets"));
    }

    public static boolean canBeTargeted(ResourceLocation entityTypeId, ITurret.TargetType targetType) {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entityTypeId);
        return type != null && canBeTargeted(type, targetType);
    }

    public static boolean canBeTargeted(EntityType<?> entityType, ITurret.TargetType targetType) {
        if( NON_TARGETABLE_ENTITIES.contains(entityType) ) {
            TargetEntry ef = getEntry(TmrConfig.TARGETS.flyingEntities, entityType);
            TargetEntry es = getEntry(TmrConfig.TARGETS.swimmingEntities, entityType);
            TargetEntry eg = getEntry(TmrConfig.TARGETS.groundedEntities, entityType);

            if( (ef.isEmpty || ef.remove) && (es.isEmpty || es.remove) && (eg.isEmpty || eg.remove) ) {
                return false;
            }
        }

        if( targetType == ITurret.TargetType.ALL ) {
            return canBeTargeted(entityType, ITurret.TargetType.AIR)
                   || canBeTargeted(entityType, ITurret.TargetType.WATER)
                   || canBeTargeted(entityType, ITurret.TargetType.GROUND);
        }

        if( targetType == ITurret.TargetType.AIR ) {
            TargetEntry e = getEntry(TmrConfig.TARGETS.flyingEntities, entityType);
            return FLYING_ENTITIES.contains(entityType) ? !e.remove : !e.isEmpty;
        } else if( targetType == ITurret.TargetType.WATER ) {
            TargetEntry e = getEntry(TmrConfig.TARGETS.swimmingEntities, entityType);
            return SWIMMING_ENTITIES.contains(entityType) ? !e.remove : !e.isEmpty;
        } else {
            TargetEntry e = getEntry(TmrConfig.TARGETS.groundedEntities, entityType);
            return GROUNDED_ENTITIES.contains(entityType) ? !e.remove : !e.isEmpty;
        }
    }

    @SubscribeEvent
    public static void buildTargetLists(@Nullable FMLServerAboutToStartEvent event) {
        CURRENT_ENTITY_LISTS.clear();

        ForgeRegistries.ENTITIES.getValues().forEach(e -> {
            if( e.getCategory() == EntityClassification.MISC && !canBeTargeted(e, ITurret.TargetType.ALL) ) {
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

        loaded = event != null;
    }

    public static Map<ResourceLocation, Boolean> getTargetList(ITurret.TargetType targetType) {
        return Collections.unmodifiableMap(CURRENT_ENTITY_LISTS.get(targetType));
    }

    public static ITextComponent getTargetName(ResourceLocation creatureId) {
        return MiscUtils.apply(ForgeRegistries.ENTITIES.getValue(creatureId), EntityType::getDescription, StringTextComponent.EMPTY);
    }

    public static EntityClassification getTargetType(ResourceLocation creatureId) {
        return MiscUtils.apply(ForgeRegistries.ENTITIES.getValue(creatureId), EntityType::getCategory, EntityClassification.MISC);
    }

    @Nonnull
    private static TargetEntry getEntry(ForgeConfigSpec.ConfigValue<List<? extends TargetEntry>> entries, EntityType<?> entityType) {
        return entries.get().stream().map(TargetEntry.class::cast).filter(t -> t.id.equals(entityType.toString())).findFirst().orElse(TargetEntry.EMPTY);
    }

    @Nonnull
    public static EntityClassification getCondensedType(EntityClassification cls) {
        if( cls == null ) {
            return EntityClassification.MISC;
        }

        if( !cls.isFriendly() ) {
            return EntityClassification.MONSTER;
        } else if( cls != EntityClassification.MISC ) {
            return EntityClassification.CREATURE;
        } else {
            return EntityClassification.MISC;
        }
    }

    @SuppressWarnings({ "java:S1104", "unused" })
    public static class TargetEntry
    {
        public       String  id;
        public       boolean remove = false;
        public final boolean isEmpty;

        static final TargetEntry EMPTY = new TargetEntry(true);

        public TargetEntry() {
            this(false);
        }

        public TargetEntry(boolean isEmpty) {
            this.isEmpty = isEmpty;
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
