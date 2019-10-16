package de.sanandrew.mods.turretmod.registry.upgrades.leveling;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
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

    boolean check(int level, Stage currStage) {
        return level >= this.level && currStage.level < this.level;
    }

    void apply(ITurretInst turretInst) {
        for( Modifier m : this.modifiers ) {
            TmrUtils.tryApplyModifier(turretInst.get(), m.attributeName, m.modifier);
        }
    }

    static Stage[] load(String[] jsons) {
        List<Stage> stageList = new ArrayList<>();
        for( String j : jsons ) {
            JsonObject stage = JsonUtils.GSON.fromJson(j, JsonObject.class);
            int lvl = stage.get("level").getAsInt();
            List<Modifier> modifiers = new ArrayList<>();
            stage.get("modifiers").getAsJsonArray().forEach(m -> {
                JsonObject mod = m.getAsJsonObject();

                AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(mod.get("id").getAsString()),
                                                                            NAME_PREFIX + mod.get("name").getAsString(),
                                                                            mod.get("amount").getAsDouble(),
                                                                            mod.get("mode").getAsInt());
                modifiers.add(new Modifier(mod.get("attribute").getAsString(), attributeModifier));
            });

            stageList.add(new Stage(lvl, modifiers.toArray(new Modifier[0])));
        }

        return stageList.toArray(new Stage[0]);
    }

    public static class Modifier
    {
        public final String            attributeName;
        public final AttributeModifier modifier;

        private Modifier(String attributeName, AttributeModifier modifier) {
            this.attributeName = attributeName;
            this.modifier = modifier;
        }
    }
}
