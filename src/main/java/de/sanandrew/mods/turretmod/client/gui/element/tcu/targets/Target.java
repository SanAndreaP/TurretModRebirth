package de.sanandrew.mods.turretmod.client.gui.element.tcu.targets;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.config.Targets;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Range;

import java.util.UUID;

public class Target
        extends ElementParent<String>
{
    static final String TOGGLE_BUTTON_ON = "toggleButtonOn";
    static final String TOGGLE_BUTTON_OFF = "toggleButtonOff";
    static final String LABEL = "label";
    static final String ICON = "icon";

    private final ITurretEntity turret;
    private final int           w;
    private final int           h;

    final ResourceLocation creatureId;
    final UUID             playerId;
    final ITextComponent   text;

    private boolean isHovering;

    public Target(ITurretEntity turret, ResourceLocation creatureId, int w, int h) {
        this.turret = turret;
        this.w = w;
        this.h = h;
        this.creatureId = creatureId;
        this.playerId = null;
        this.text = Targets.getTargetName(creatureId);
    }

    public Target(ITurretEntity turret, UUID playerId, int w, int h) {
        this.turret = turret;
        this.w = w;
        this.h = h;
        this.creatureId = null;
        this.playerId = playerId;
        this.text = PlayerList.getPlayerName(playerId);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.get(LABEL).get(Text.class).setTextFunc((g, o) -> this.text);

        if( this.isCreature() ) {
            this.get(ICON).get(CreatureIcon.class).setEntityTypeSupplier(() -> Targets.getTargetType(this.creatureId));
        }

        super.setup(gui, inst);

        this.setTargetToggle();

        MiscUtils.accept(this.get(TOGGLE_BUTTON_ON).get(ButtonSL.class), b -> {
            b.setFunction(this::onToggleBtnPress);
            b.setHoverFunction((g, x, y, mx, my) -> this.isHovering);
        });
        MiscUtils.accept(this.get(TOGGLE_BUTTON_OFF).get(ButtonSL.class), b -> {
            b.setFunction(this::onToggleBtnPress);
            b.setHoverFunction((g, x, y, mx, my) -> this.isHovering);
        });
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        this.isHovering = IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.w, this.h);
        this.get(LABEL).get(Text.class).setColor(this.isHovering ? "hover" : null);

        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        super.tick(gui, inst);

        this.setTargetToggle();
    }

    private void setTargetToggle() {
        ITargetProcessor tp = this.turret.getTargetProcessor();

        boolean checkedCreature = this.isCreature() && tp.isEntityTargeted(this.creatureId);
        boolean checkedPlayer = this.isPlayer() && tp.isPlayerTargeted(this.playerId);
        boolean checked = checkedCreature || checkedPlayer;

        this.get(TOGGLE_BUTTON_OFF).get(ButtonSL.class).setVisible(!checked);
        this.get(TOGGLE_BUTTON_ON).get(ButtonSL.class).setVisible(checked);
    }

    private void onToggleBtnPress(Button btn) {
        boolean activate = this.get(TOGGLE_BUTTON_OFF).get(ButtonSL.class).isButton(btn);
        if( this.isCreature() ) {
            TurretPlayerActionPacket.setCreatureTarget(this.turret, activate, this.creatureId, null);
        } else if( this.isPlayer() ) {
            TurretPlayerActionPacket.setPlayerTarget(this.turret, activate, this.playerId);
        }
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }

    boolean isCreature() {
        return this.creatureId != null;
    }

    boolean isPlayer() {
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
        GuiElementInst label;
        GuiElementInst icon;

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

            t.put(LABEL, this.label);
            t.put(ICON, this.icon);

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
            this.btnToggleOff = new GuiElementInst(JsonUtils.getIntArray(tbOffData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 2, 2 }, Range.is(2)),
                                                   tbOffB.get(gui)).initialize(gui);

            JsonObject tbOnData = JsonUtils.deepCopy(btnData);
            JsonObject tbOnUV = MiscUtils.get(tbOnData.getAsJsonObject("uvOn"), JsonObject::new);
            JsonUtils.addJsonProperty(tbOnData, "uvEnabled", JsonUtils.getIntArray(tbOnUV.get("enabled"), new int[] { 244, 88 }));
            JsonUtils.fetchIntArray(tbOnUV.get("hover"), uv -> JsonUtils.addJsonProperty(tbOnData, "uvHover", uv));

            ButtonSL.Builder tbOnB = ButtonSL.Builder.buildFromJson(gui, tbOnData, null);
            this.btnToggleOn = new GuiElementInst(JsonUtils.getIntArray(tbOnData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 2, 2 }, Range.is(2)),
                                                  tbOnB.get(gui)).initialize(gui);
        }

        private void loadLabel(IGui gui, JsonObject lblData) {
            if( !lblData.has("color") ) {
                JsonObject color = new JsonObject();
                JsonUtils.addJsonProperty(color, "default", "0xFF000000");
                JsonUtils.addJsonProperty(color, "hover", "0xFF339900");
                lblData.add("color", color);
            }

            Text.Builder lblB = Text.Builder.buildFromJson(gui, lblData);
            this.label = new GuiElementInst(JsonUtils.getIntArray(lblData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 16, 2 }, Range.is(2)),
                                            lblB.get(gui)).initialize(gui);
        }

        private void loadCreatureIcon(IGui gui, JsonObject iconData, int w) {
            CreatureIcon.Builder iB = CreatureIcon.Builder.buildFromJson(gui, iconData);
            this.icon = new GuiElementInst(JsonUtils.getIntArray(iconData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { w - 10, 2 }, Range.is(2)),
                                           iB.get(gui)).initialize(gui);
        }

        private void loadPlayerIcon(IGui gui, JsonObject iconData, int w) {
            this.icon = new GuiElementInst(JsonUtils.getIntArray(iconData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { w - 10, 2 }, Range.is(2)),
                                           new PlayerIconTarget(this.playerId)).initialize(gui);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h) {
            Builder b = new Builder(turret, creatureId, playerId, w, h);

            b.loadToggleButtons(gui, MiscUtils.get(data.getAsJsonObject("toggleButton"), JsonObject::new));
            b.loadLabel(gui, MiscUtils.get(data.getAsJsonObject(LABEL), JsonObject::new));
            if( creatureId != null ) {
                b.loadCreatureIcon(gui, MiscUtils.get(data.getAsJsonObject(ICON + "Creature"), JsonObject::new), w);
            } else {
                b.loadPlayerIcon(gui, MiscUtils.get(data.getAsJsonObject(ICON + "Player"), JsonObject::new), w);
            }

            return b;
        }

        public static Target fromJson(IGui gui, JsonObject data, ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h) {
            return buildFromJson(gui, data, turret, creatureId, playerId, w, h).get(gui);
        }
    }
}
