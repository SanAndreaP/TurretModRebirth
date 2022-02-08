package de.sanandrew.mods.turretmod.item.upgrades.leveling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.SyncTurretStages;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StageLoader
        extends JsonReloadListener
{
    private static boolean syncEnabled = false;

    public StageLoader() {
        super(JsonUtils.GSON, "turret_level_stages");
    }

    public static void enableSync(FMLServerStartedEvent event) {
        syncEnabled = true;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, @Nonnull IResourceManager resourceMgr, @Nonnull IProfiler profiler) {
        Map<ResourceLocation, Stage> stages = files.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> fromJson(v.getValue())));
        LevelStorage.applyStages(stages);

        if( syncEnabled ) {
            DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
                //TODO: fix this in SanLib: sendToAll does not need a player parameter!
                TurretModRebirth.NETWORK.sendToAll(new SyncTurretStages(stages), null);
                return null;
            });
        }
    }

    private static Stage fromJson(JsonElement je) {
        if( !je.isJsonObject() || je.getAsJsonObject().size() < 1 ) {
            return Stage.NULL_STAGE;
        }

        JsonObject           jo        = je.getAsJsonObject();
        int                  lvl       = JsonUtils.getIntVal(jo.get("level"));
        List<Stage.Modifier> modifiers = new ArrayList<>();

        jo.get("modifiers").getAsJsonArray().forEach(m -> {
            JsonObject       mod         = m.getAsJsonObject();
            String           modName     = JsonUtils.getStringVal(mod.get("name"));
            ResourceLocation attributeId = new ResourceLocation(JsonUtils.getStringVal(mod.get("attribute")));

            AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(mod.get("id").getAsString()),
                                                                        Stage.NAME_PREFIX + modName,
                                                                        mod.get("amount").getAsDouble(),
                                                                        AttributeModifier.Operation.fromValue(mod.get("mode").getAsInt()));
            String turretId = JsonUtils.getStringVal(mod.get("turret"), null);
            if( turretId != null ) {
                ResourceLocation turret = new ResourceLocation(turretId);
                if( !TurretRegistry.INSTANCE.get(turret).isValid() ) {
                    TmrConstants.LOG.log(Level.WARN, String.format("Skipping modifier %s for level %d, as it tries to target non-existing turret %s",
                                                                   modName, lvl, turretId));
                } else {
                    modifiers.add(new Stage.Modifier(ForgeRegistries.ATTRIBUTES.getValue(attributeId), attributeModifier, turret));
                }
            } else {
                modifiers.add(new Stage.Modifier(ForgeRegistries.ATTRIBUTES.getValue(attributeId), attributeModifier, null));
            }
        });

        return new Stage(lvl, modifiers.toArray(new Stage.Modifier[0]));
    }
}