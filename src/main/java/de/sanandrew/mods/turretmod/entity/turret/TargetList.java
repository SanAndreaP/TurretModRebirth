/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Pattern;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.TmrConfig;
import net.minecraft.entity.Entity;
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

@Category(TargetList.NAME)
@SuppressWarnings("WeakerAccess")
public final class TargetList
{
    public static final String NAME = "targets";

    @Value(comment = "Wether or not to regenerate the groundEntities config. This will be set to false once it is done.\n" +
                             "The generator will scoop through the entity registry and add any entity extending EntityLiving and filter out anything that is already\n" +
                             "whitelisted as a flying or water-based entity.")
    public static boolean groundRegenerate = true;

    @Value(comment = "A whitelist to determine targetable entity types by \"anti-ground\" turrets (anything that cannot attack flying or water-based entities).\n" +
                             "This needs to be the registry name, like \"minecraft:cow\" for a cow, etc.",
           range = @Range(validationPattern = @Pattern(".*?:.*")))
    public static String[] groundEntities = {};

    @Value(comment = "A whitelist to determine targetable entity types by \"anti-air\" turrets (anything that cannot attack ground-based or water-based entities).\n" +
                             "This needs to be the registry name, like \"minecraft:ghast\" for a ghast, etc.",
           range = @Range(validationPattern = @Pattern(".*?:.*")))
    public static String[] flyingEntities = {
            "aether_legacy:aerwhale",
            "aether_legacy:flying_cow",
            "aether_legacy:zephyr",
            "babymobs:babyblaze",
            "babymobs:babydragon",
            "babymobs:babyghast",
            "babymobs:babywither",
            "botania:pixie",
            "claysoldiers:mountpegasus",
            "enderiozoo:owl",
            "exoticbirds:bluejay",
            "exoticbirds:booby",
            "exoticbirds:cardinal",
            "exoticbirds:cassowary",
            "exoticbirds:crane",
            "exoticbirds:gouldianfinch",
            "exoticbirds:heron",
            "exoticbirds:hummingbird",
            "exoticbirds:kingfisher",
            "exoticbirds:kookaburra",
            "exoticbirds:lyrebird",
            "exoticbirds:magpie",
            "exoticbirds:owl",
            "exoticbirds:parrot",
            "exoticbirds:peafowl",
            "exoticbirds:pelican",
            "exoticbirds:phoenix",
            "exoticbirds:pigeon",
            "exoticbirds:robin",
            "exoticbirds:seagull",
            "exoticbirds:swan",
            "exoticbirds:toucan",
            "exoticbirds:vulture",
            "exoticbirds:woodpecker",
            "familiarfauna:familiarfauna.butterfly",
            "familiarfauna:familiarfauna.dragonfly",
            "familiarfauna:familiarfauna.pixie",
            "forestry:butterflyge",
            "minecraft:bat",
            "minecraft:blaze",
            "minecraft:ender_dragon",
            "minecraft:ghast",
            "minecraft:parrot",
            "minecraft:vex",
            "minecraft:wither",
            "nex:ghast_queen",
            "nex:ghastling",
            "primitivemobs:blazing_juggernaut",
            "quark:wraith",
            "thaumcraft:firebat",
            "thaumcraft:wisp",
            "thebetweenlands:dragonfly",
            "thebetweenlands:firefly",
            "thebetweenlands:chiromaw",
            "totemic:bald_eagle",
            "twilightforest:firefly",
            "twilightforest:mini_ghast",
            "twilightforest:mosquito_swarm",
            "twilightforest:snow_queen",
            "twilightforest:snow_guardian",
            "twilightforest:tower_ghast",
            "twilightforest:ur_ghast",
            "twilightforest:wraith",
            "twilightforest:raven"
    };

    @Value(comment = "A whitelist to determine targetable entity types by \"anti-water\" turrets (anything that cannot attack flying or ground-based entities).\n" +
                             "This needs to be the registry name, like \"minecraft:squid\" for a squid, etc.",
           range = @Range(validationPattern = @Pattern(".*?:.*")))
    public static String[] waterEntities = {
            "babymobs:babyguardian",
            "babymobs:babysquid",
            "enderiozoo:epicsquid",
            "minecraft:elder_guardian",
            "claysoldiers:mountturtle",
            "minecraft:guardian",
            "minecraft:squid",
            "primitivemobs:lily_lurker",
            "thebetweenlands:angler",
            "thebetweenlands:blind_cave_fish"
    };

    private static final List<ResourceLocation> TARGETABLE_GROUND = new ArrayList<>();
    private static final List<ResourceLocation> TARGETABLE_AIR = new ArrayList<>();
    private static final List<ResourceLocation> TARGETABLE_WATER = new ArrayList<>();

    public static void initializePostInit() {
        if( groundRegenerate ) {
            groundEntities = EntityList.getEntityNameList().stream()
                                       .filter(nm -> {
                                           Stream<String> feStr = Arrays.stream(flyingEntities);
                                           Stream<String> weStr = Arrays.stream(waterEntities);
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

        groundRegenerate = false;

        TmrConfig.Targets.reset();
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

        return entities.stream().collect(Collectors.toMap(Function.identity(), e -> {
            Class<? extends Entity> entityCls = EntityList.getClass(e);
            return entityCls != null && IMob.class.isAssignableFrom(entityCls);
        }));
    }
}
