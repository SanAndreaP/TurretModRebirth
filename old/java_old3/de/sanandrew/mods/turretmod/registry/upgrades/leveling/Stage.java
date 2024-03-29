package de.sanandrew.mods.turretmod.registry.upgrades.leveling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Stage
{
    static final String NAME_PREFIX = TmrConstants.ID + ":leveling_";
    static final Stage  NULL_STAGE  = new Stage(-1);

    public final int        level;
    public final Modifier[] modifiers;

    private Stage(int level, Modifier... modifiers) {
        this.level = level;
        this.modifiers = modifiers;
    }

    static Stage[] load(String jsons) {
        boolean tried = false;
        do {
            try {
                List<Stage> stageList = new ArrayList<>();
                JsonArray json = JsonUtils.GSON.fromJson(jsons, JsonArray.class);
                for( JsonElement j : json ) {
                    JsonObject stage = j.getAsJsonObject();
                    int lvl = stage.get("level").getAsInt();
                    List<Modifier> modifiers = new ArrayList<>();
                    stage.get("modifiers").getAsJsonArray().forEach(m -> {
                        JsonObject mod = m.getAsJsonObject();
                        ResourceLocation turret = null;

                        AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(mod.get("id").getAsString()),
                                                                                    NAME_PREFIX + mod.get("name").getAsString(),
                                                                                    mod.get("amount").getAsDouble(),
                                                                                    mod.get("mode").getAsInt());
                        if( mod.has("turret") ) {
                            turret = new ResourceLocation(mod.get("turret").getAsString());
                            if( !TurretRegistry.INSTANCE.getObject(turret).isValid() ) {
                                TmrConstants.LOG.log(Level.WARN, String.format("Skipping modifier %s for level %d, as it tries to target non-existing turret %s",
                                                                               mod.get("name"), lvl, mod.get("turret")));
                                return;
                            }
                        }
                        modifiers.add(new Modifier(mod.get("attribute").getAsString(), attributeModifier, turret));
                    });

                    stageList.add(new Stage(lvl, modifiers.toArray(new Modifier[0])));
                }

                return stageList.toArray(new Stage[0]);
            } catch( JsonSyntaxException ex ) {
                TmrConstants.LOG.log(Level.WARN, "Cannot parse JSON from upgrades.leveling.stages config. Load default stages...", ex);
                jsons = String.join("\n", LevelStorage.getDefaultStages());
                if( tried ) {
                    return new Stage[0];
                }
                tried = true;
            }
        } while( true );
    }

    boolean check(int level, Stage currStage) {
        return level >= this.level && currStage.level < this.level;
    }

    void apply(ITurretInst turretInst, boolean playSound) {
        boolean hasMultiplier = false;
        for( Modifier m : this.modifiers ) {
            if( m.turret == null || m.turret.equals(turretInst.getTurret().getId()) ) {
                hasMultiplier |= EntityUtils.tryApplyModifier(turretInst.get(), m.attributeName, m.modifier);
            }
        }

        if( playSound && hasMultiplier ) {
            EntityLiving markus = turretInst.get();
            EnumEffect.LEVEL_UP.addEffect(markus);
        }
    }

    public static Map<String, ModifierInfo> fetchModifiers(Collection<Stage> stages, AbstractAttributeMap attrMap) {
        final Map<String, ModifierInfo>               currModifiers = new HashMap<>();
        final Map<String, Map<Integer, List<Double>>> modMap        = new HashMap<>();

        stages.forEach(s -> Arrays.stream(s.modifiers).forEach(m -> modMap.computeIfAbsent(m.attributeName, k -> new HashMap<>())
                                                                          .computeIfAbsent(m.modifier.getOperation(), k -> new ArrayList<>())
                                                                          .add(m.modifier.getAmount())));

        modMap.forEach((attrName, mods) -> {
            final double baseVal;
            if( attrMap != null ) {
                IAttributeInstance attrInst = attrMap.getAttributeInstanceByName(attrName);
                if( attrInst == null ) {
                    return;
                }

                baseVal = attrInst.getBaseValue();
            } else {
                baseVal = 100.0D;
            }

            final Stage.ModifierInfo modInfo = currModifiers.computeIfAbsent(attrName, a -> new Stage.ModifierInfo(baseVal));
            mods.getOrDefault(0, Collections.emptyList()).forEach(m -> modInfo.modValue += m);

            final double modValFn = modInfo.modValue;
            mods.getOrDefault(1, Collections.emptyList()).forEach(m -> modInfo.modValue += modValFn * m);
            mods.getOrDefault(2, Collections.emptyList()).forEach(m -> modInfo.modValue += modInfo.modValue * m);
        });

        return currModifiers;
    }

    public static class Modifier
    {
        public final String            attributeName;
        public final AttributeModifier modifier;
        public final ResourceLocation  turret;

        private Modifier(String attributeName, AttributeModifier modifier, ResourceLocation turret) {
            this.attributeName = attributeName;
            this.modifier = modifier;
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
