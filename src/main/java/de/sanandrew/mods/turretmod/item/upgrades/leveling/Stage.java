package de.sanandrew.mods.turretmod.item.upgrades.leveling;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Stage
{
    static final String NAME_PREFIX = TmrConstants.ID + ":leveling_";
    static final Stage  NULL_STAGE  = new Stage(-1);

    public final int        level;
    public final Modifier[] modifiers;

    public Stage(int level, Modifier... modifiers) {
        this.level = level;
        this.modifiers = modifiers;
    }

    boolean check(int level, Stage currStage) {
        return level >= this.level && currStage.level < this.level;
    }

    void apply(ITurretEntity turretInst, boolean playSound) {
        boolean hasMultiplier = false;
        for( Modifier m : this.modifiers ) {
            if( m.turret == null || m.turret.equals(turretInst.getDelegate().getId()) ) {
                hasMultiplier |= EntityUtils.tryApplyModifier(turretInst.get(), m.attribute, m.mod, true);
            }
        }

        if( playSound && hasMultiplier ) {
//            EnumEffect.LEVEL_UP.addEffect(turretInst.get()); //TODO: readd effects
        }
    }

    public static Map<Attribute, ModifierInfo> fetchModifiers(Collection<Stage> stages) {
        final Map<Attribute, ModifierInfo>                                   currModifiers = new HashMap<>();
        final Map<Attribute, Map<AttributeModifier.Operation, List<Double>>> modMap        = new HashMap<>();

        stages.stream().map(s -> s.modifiers).flatMap(Stream::of).forEach(m -> modMap.computeIfAbsent(m.attribute, k -> new EnumMap<>(AttributeModifier.Operation.class))
                                                                                     .computeIfAbsent(m.mod.getOperation(), k -> new ArrayList<>())
                                                                                     .add(m.mod.getAmount()));

        modMap.forEach((attr, mods) -> {
            final double baseVal = attr.getDefaultValue();

            final ModifierInfo modInfo = currModifiers.computeIfAbsent(attr, a -> new ModifierInfo(baseVal));
            mods.getOrDefault(AttributeModifier.Operation.ADDITION, Collections.emptyList()).forEach(m -> modInfo.modValue += m);

            final double modValFn = modInfo.modValue;
            mods.getOrDefault(AttributeModifier.Operation.MULTIPLY_BASE, Collections.emptyList()).forEach(m -> modInfo.modValue += modValFn * m);
            mods.getOrDefault(AttributeModifier.Operation.MULTIPLY_TOTAL, Collections.emptyList()).forEach(m -> modInfo.modValue += modInfo.modValue * m);
        });

        return currModifiers;
    }

    public static class Modifier
    {
        public final Attribute         attribute;
        public final AttributeModifier mod;
        public final ResourceLocation  turret;

        public Modifier(Attribute attribute, AttributeModifier modifier, ResourceLocation turret) {
            this.attribute = attribute;
            this.mod = modifier;
            this.turret = turret;
        }
    }

    public static final class ModifierInfo
    {
        public final double baseValue;
        private      double modValue;

        private ModifierInfo(double baseValue) {
            this.baseValue = baseValue;
            this.modValue = baseValue;
        }

        public double getModValue() {
            return this.modValue;
        }

        @Override
        @SuppressWarnings("NonFinalFieldReferenceInEquals")
        public boolean equals(Object o) {
            if( this == o ) {
                return true;
            }
            if( o == null || getClass() != o.getClass() ) {
                return false;
            }
            ModifierInfo that = (ModifierInfo) o;
            return Double.compare(that.baseValue, this.baseValue) == 0 &&
                   Double.compare(that.modValue, this.modValue) == 0;
        }

        @Override
        @SuppressWarnings("NonFinalFieldReferencedInHashCode")
        public int hashCode() {
            return Objects.hash(this.baseValue, this.modValue);
        }
    }
}
