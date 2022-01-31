package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.UUID;

public class Target
        extends ElementParent<String>
{
    static final String TOGGLE_BUTTON_ON = "toggle_button_on";
    static final String TOGGLE_BUTTON_OFF = "toggle_button_off";

    private final ITurretEntity    turret;
    private final ResourceLocation creatureId;
    private final UUID             playerId;
    private final int              w;
    private final int              h;

    public Target(ITurretEntity turret, ResourceLocation creatureId, int w, int h) {
        this.turret = turret;
        this.creatureId = creatureId;
        this.playerId = null;
        this.w = w;
        this.h = h;
    }

    public Target(ITurretEntity turret, UUID playerId, int w, int h) {
        this.turret = turret;
        this.w = w;
        this.h = h;
        this.creatureId = null;
        this.playerId = playerId;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.setTargetToggle();
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        super.tick(gui, inst);

        this.setTargetToggle();
    }

    private void setTargetToggle() {
        boolean checkedCreature = this.isCreature() && this.turret.getTargetProcessor().isEntityTargeted(this.creatureId);
        boolean checkedPlayer = this.isPlayer() && this.turret.getTargetProcessor().isPlayerTargeted(this.playerId);
        boolean checked = checkedCreature || checkedPlayer;

        this.get(TOGGLE_BUTTON_OFF).get(ButtonSL.class).setVisible(!checked);
        this.get(TOGGLE_BUTTON_ON).get(ButtonSL.class).setVisible(checked);
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }

    private boolean isCreature() {
        return this.creatureId != null;
    }

    private boolean isPlayer() {
        return this.playerId != null;
    }

    public static class Builder
            implements IBuilder<Target>
    {
        final ITurretEntity    turret;
        final ResourceLocation creatureId;
        final UUID             playerId;
        final int              w;
        final int              h;

        GuiElementInst btnToggleOn;
        GuiElementInst btnToggleOff;

        public Builder(ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h) {
            this.turret = turret;
            this.creatureId = creatureId;
            this.playerId = playerId;
            this.w = w;
            this.h = h;
        }

        @Override
        public void sanitize(IGui gui) { /* no-op */ }

        @Override
        public Target get(IGui gui) {
            this.sanitize(gui);

            Target t;
            if( this.creatureId != null ) {
                t = new Target(this.turret, this.creatureId, this.w, this.h);
            } else {
                t = new Target(this.turret, this.playerId, this.w, this.h);
            }

            t.put(TOGGLE_BUTTON_OFF, this.btnToggleOff);
            t.put(TOGGLE_BUTTON_ON, this.btnToggleOn);

            return t;
        }

        private void loadToggleButtons(IGui gui, JsonObject btnData) {
            JsonUtils.addDefaultJsonProperty(btnData, "size", new int[] { 12, 8 });
            JsonUtils.addDefaultJsonProperty(btnData, "useVanillaTexture", false);
            JsonUtils.addDefaultJsonProperty(btnData, "uvSize", new int[] { 12, 8 });

            JsonObject tbOffData = JsonUtils.deepCopy(btnData);
            JsonObject tbOffUV = MiscUtils.get(tbOffData.getAsJsonObject("uvOff"), JsonObject::new);
            JsonUtils.addJsonProperty(tbOffData, "uvEnabled", JsonUtils.getIntArray(tbOffUV.get("enabled"), new int[] { 244, 72 }));
            JsonUtils.fetchIntArray(tbOffUV.get("hover"), uv -> JsonUtils.addJsonProperty(tbOffData, "uvHover", uv));

            ButtonSL.Builder tbOffB = ButtonSL.Builder.buildFromJson(gui, tbOffData, null);
            this.btnToggleOff = new GuiElementInst(JsonUtils.getIntArray(tbOffData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)),
                                                  tbOffB.get(gui)).initialize(gui);

            JsonObject tbOnData = JsonUtils.deepCopy(btnData);
            JsonObject tbOnUV = MiscUtils.get(tbOnData.getAsJsonObject("uvOn"), JsonObject::new);
            JsonUtils.addJsonProperty(tbOnData, "uvEnabled", JsonUtils.getIntArray(tbOnUV.get("enabled"), new int[] { 244, 88 }));
            JsonUtils.fetchIntArray(tbOnUV.get("hover"), uv -> JsonUtils.addJsonProperty(tbOnData, "uvHover", uv));

            ButtonSL.Builder tbOnB = ButtonSL.Builder.buildFromJson(gui, tbOnData, null);
            this.btnToggleOn = new GuiElementInst(JsonUtils.getIntArray(tbOnData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)),
                                                  tbOnB.get(gui)).initialize(gui);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h) {
            Builder b = new Builder(turret, creatureId, playerId, w, h);

            b.loadToggleButtons(gui, MiscUtils.get(data.getAsJsonObject("toggleButton"), JsonObject::new));

            return b;
        }

        public static Target fromJson(IGui gui, JsonObject data, ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h) {
            return buildFromJson(gui, data, turret, creatureId, playerId, w, h).get(gui);
        }
    }
}
