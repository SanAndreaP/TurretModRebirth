package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import de.sanandrew.mods.sanlib.lib.function.TriConsumer;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiFunction;

public enum TargetType
{
    PLAYER(
        (t, c, b) -> t.getTargetProcessor().updatePlayerTarget((UUID) c, b),
        (t, c) -> t.getTargetProcessor().isPlayerTargeted((UUID) c),
        (t, c) -> PlayerList.INSTANCE.getPlayerName((UUID) c),
        (t, c) -> EntityType.NEUTRAL),
    CREATURE(
        (t, c, b) -> t.getTargetProcessor().updateEntityTarget((ResourceLocation) c, b),
        (t, c) -> t.getTargetProcessor().isEntityTargeted((ResourceLocation) c),
        (t, c) -> LangUtils.translateEntityCls(MiscUtils.defIfNull(EntityList.getClass((ResourceLocation) c), Entity.class)),
        (t, c) -> {
            Class<?> cls = MiscUtils.defIfNull(EntityList.getClass((ResourceLocation) c), Entity.class);
            return IMob.class.isAssignableFrom(cls) ? EntityType.HOSTILE : IAnimals.class.isAssignableFrom(cls) ? EntityType.PEACEFUL : EntityType.NEUTRAL; });

    public static final TargetType[] VALUES = TargetType.values();

    private final TriConsumer<ITurretInst, Object, Boolean> updTargetFn;
    private final BiFunction<ITurretInst, Object, Boolean> getEnabledFn;
    private final BiFunction<ITurretInst, Object, String> getNameFn;
    private final BiFunction<ITurretInst, Object, EntityType> getTypeFn;

    TargetType(TriConsumer<ITurretInst, Object, Boolean> updTargetFn, BiFunction<ITurretInst, Object, Boolean> getEnabledFn, BiFunction<ITurretInst, Object, String> getNameFn,
               BiFunction<ITurretInst, Object, EntityType> getTypeFn)
    {
        this.updTargetFn = updTargetFn;
        this.getEnabledFn = getEnabledFn;
        this.getNameFn = getNameFn;
        this.getTypeFn = getTypeFn;
    }

    public void updateTarget(ITurretInst turretInst, Object id, boolean enabled) {
        this.updTargetFn.accept(turretInst, id, enabled);
    }

    public String getName(ITurretInst turretInst, Object id) {
        return this.getNameFn.apply(turretInst, id);
    }

    public EntityType getType(ITurretInst turretInst, Object id) {
        return this.getTypeFn.apply(turretInst, id);
    }

    public boolean isTargeted(ITurretInst turretInst, Object id) {
        return this.getEnabledFn.apply(turretInst, id);
    }

    public static TargetType fromString(String s) {
        return Arrays.stream(VALUES).filter(e -> e.name().equalsIgnoreCase(s)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(String.format("No enum constant %s.%s", TargetType.class.getCanonicalName(), s)));
    }

    public enum EntityType
    {
        HOSTILE,
        NEUTRAL,
        PEACEFUL
    }
}
