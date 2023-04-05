/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate.leveling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.entity.turret.TurretRegistry;
import dev.sanandrea.mods.turretmod.init.TurretModRebirth;
import dev.sanandrea.mods.turretmod.network.SyncTurretStages;
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
    public static final StageLoader INSTANCE = new StageLoader();

    private static boolean syncEnabled = false;
    private static Long lastUpdate = null;

    private StageLoader() {
        super(JsonUtils.GSON, "turret_level_stages");
    }

    @SuppressWarnings("unused")
    public static void enableSync(FMLServerStartedEvent event) {
        syncEnabled = true;
    }

    @Override
    @SuppressWarnings("java:S2696")
    protected void apply(Map<ResourceLocation, JsonElement> files, @Nonnull IResourceManager resourceMgr, @Nonnull IProfiler profiler) {
        Map<ResourceLocation, Stage> stages = files.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> fromJson(v.getValue())));
        LevelData.applyStages(stages);

        if( syncEnabled ) {
            lastUpdate = System.currentTimeMillis();
            DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
                TurretModRebirth.NETWORK.sendToAll(new SyncTurretStages(stages));
                return null;
            });
        }
    }

    public static boolean needsUpdate(LevelData ls) {
        return lastUpdate != null && (ls.lastUpdate == null || lastUpdate > ls.lastUpdate);
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