/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Category("targets")
@SuppressWarnings("WeakerAccess")
public class TargetList
{
    public static boolean groundRegenerate = true;

    public static String[] groundEntities = {};

    public static String[] flyingEntities = {
            "minecraft:ghast",
            "minecraft:wither",
            "minecraft:ender_dragon",
            "minecraft:vex"
    };

    public static String[] waterEntities = {
            "minecraft:squid",
            "minecraft:guardian",
            "minecraft:elder_guardian"
    };

    private static final List<ResourceLocation> TARGETABLE_GROUND = new ArrayList<>();
    private static final List<ResourceLocation> TARGETABLE_AIR = new ArrayList<>();
    private static final List<ResourceLocation> TARGETABLE_WATER = new ArrayList<>();

    public static void initializePostInit() {
        if( groundRegenerate ) {
            Stream<String> feStr = Arrays.stream(flyingEntities);
            Stream<String> weStr = Arrays.stream(waterEntities);
            groundEntities = EntityList.getEntityNameList().stream()
                                       .filter(nm -> {
                                           String nmStr = nm.toString();
                                           Class c = EntityList.getClass(nm);
                                           return c != null && EntityLiving.class.isAssignableFrom(c) && feStr.noneMatch(nmStr::equals) && weStr.noneMatch(nmStr::equals)
                                                  && !ITurretInst.class.isAssignableFrom(c) && !EntityLiving.class.equals(c);
                                       }).map(ResourceLocation::toString).toArray(String[]::new);
        }

        finalizeWhitelists();
    }

    private static void finalizeWhitelists() {
        TARGETABLE_GROUND.clear();
        TARGETABLE_AIR.clear();
        TARGETABLE_WATER.clear();

        TARGETABLE_GROUND.addAll(Arrays.stream(groundEntities).map(ResourceLocation::new).collect(Collectors.toList()));
        TARGETABLE_AIR.addAll(Arrays.stream(flyingEntities).map(ResourceLocation::new).collect(Collectors.toList()));
        TARGETABLE_WATER.addAll(Arrays.stream(waterEntities).map(ResourceLocation::new).collect(Collectors.toList()));
    }

    public static boolean isEntityTargetable(ResourceLocation res, ITurret.AttackType type) {
        switch( type ) {
            case ALL:
                return Stream.of(TARGETABLE_GROUND.stream(), TARGETABLE_AIR.stream(), TARGETABLE_WATER.stream()).flatMap(s -> s)
                             .anyMatch(r -> r.equals(res));
            case GROUND:
                return TARGETABLE_GROUND.stream().anyMatch(r -> r.equals(res));
            case AIR:
                return TARGETABLE_AIR.stream().anyMatch(r -> r.equals(res));
            case WATER:
                return TARGETABLE_WATER.stream().anyMatch(r -> r.equals(res));
        }

        return false;
    }

    public static Map<ResourceLocation, Boolean> getStandardTargetList(ITurret.AttackType type) {
        List<ResourceLocation> entities;
        switch( type ) {
            case ALL:
                entities = Stream.of(TARGETABLE_GROUND.stream(), TARGETABLE_AIR.stream(), TARGETABLE_WATER.stream()).flatMap(s -> s).collect(Collectors.toList());
                break;
            case GROUND:
                entities = new ArrayList<>(TARGETABLE_GROUND);
                break;
            case AIR:
                entities = new ArrayList<>(TARGETABLE_AIR);
                break;
            case WATER:
                entities = new ArrayList<>(TARGETABLE_WATER);
                break;
                default:
            entities = Collections.emptyList();
        }

        return entities.stream().collect(Collectors.toMap(Function.identity(), e -> IMob.class.isAssignableFrom(Objects.requireNonNull(EntityList.getClass(e)))));
    }
}
