package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldTurret;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.TurretForcefield;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldPersonal;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public class InfoElement
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.info_progress");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");

    private static final String HEALTH          = "health";
    private static final String AMMO            = "ammo";
    private static final String PERSONAL_SHIELD = "personal_shield";
    private static final String FORCEFIELD      = "forcefield";
    private static final String TARGET          = "target";
    private static final String PLAYER          = "player";

    private int[]          size;
    private String         type;
    private GuiElementInst bar;
    private GuiElementInst text;
    private GuiElementInst icon;
    private boolean        visible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.size = JsonUtils.getIntArray(data.get("size"), new int[] { 164, 16 }, Range.is(2));

        this.type = JsonUtils.getStringVal(data.get("type"));

        if( data.has("bar") ) {
            this.bar = new GuiElementInst(new InfoBar(), data.getAsJsonObject("bar")).initialize(gui);
            this.bar.pos = JsonUtils.getIntArray(this.bar.data.get("offset"), new int[2], Range.is(2));
            this.bar.get().bakeData(gui, this.bar.data, this.bar);
        }

        if( data.has("text") ) {
            this.text = new GuiElementInst(new InfoText(), data.getAsJsonObject("text")).initialize(gui);
            this.text.pos = JsonUtils.getIntArray(this.text.data.get("offset"), new int[2], Range.is(2));
            this.text.alignment = JsonUtils.getStringArray(this.text.data.get("alignment"), new String[0], Range.between(1, 2));
            this.text.get().bakeData(gui, this.text.data, this.text);
        }

        if( data.has("icon") ) {
            this.icon = new GuiElementInst(new Texture(), data.getAsJsonObject("icon")).initialize(gui);
            this.icon.pos = JsonUtils.getIntArray(this.icon.data.get("offset"), new int[2], Range.is(2));
            this.icon.get().bakeData(gui, this.icon.data, this.icon);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        ITurretInst ti = ((IGuiTcuInst<?>) gui.get()).getTurretInst();

        this.visible = (!this.type.equals(FORCEFIELD) || (ti.getTurret() instanceof TurretForcefield)) &&
                       (!this.type.equals(PERSONAL_SHIELD) || ti.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PERSONAL));

        if( !this.visible ) {
            return;
        }

        Consumer<GuiElementInst> u = inst -> {
            if( inst != null ) {
                inst.get().update(gui, inst.data);
            }
        };
        u.accept(this.bar);
        u.accept(this.text);
        u.accept(this.icon);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        Consumer<GuiElementInst> r = inst -> {
            if( inst != null ) {
                GuiDefinition.renderElement(gui, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, partTicks, inst);
            }
        };
        r.accept(this.bar);
        r.accept(this.text);
        r.accept(this.icon);
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public class InfoBar
            extends Texture
    {
        private int[]  uvBg = null;

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            super.bakeData(gui, data, inst);

            if( this.uvBg == null ) {
                this.uvBg = JsonUtils.getIntArray(data.get("uvBackground"), null, Range.is(2));
            }
        }

        @Override
        protected void drawRect(IGui gui) {
            ITurretInst ti = ((IGuiTcuInst<?>) gui).getTurretInst();

            double perc = 0.0D;
            switch( InfoElement.this.type ) {
                case HEALTH:
                    perc = v(ti.get(),
                             e -> e.getHealth() / (double) e.getMaxHealth());
                    break;
                case AMMO:
                    perc = v(ti.getTargetProcessor(),
                             t -> t.getAmmoCount() / (double) t.getMaxAmmoCapacity());
                    break;
                case PERSONAL_SHIELD:
                    perc = v(ti.getUpgradeProcessor().<ShieldPersonal>getUpgradeInstance(Upgrades.SHIELD_PERSONAL.getId()),
                             u -> u != null ? u.getValue() / (double) ShieldPersonal.MAX_VALUE : 0.0D);
                    break;
                case FORCEFIELD:
                    perc = v(ti.getTurret() instanceof TurretForcefield ? ti.<ShieldTurret>getRAM(null) : null,
                             s -> s != null ? s.getValue() / (double) s.getMaxValue() : -1.0D);
            }

            if( perc > -0.1D ) {
                if( this.uvBg != null ) {
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uvBg[0], this.uvBg[1], this.size[0], this.size[1],
                                                            this.textureSize[0], this.textureSize[1]);
                }

                int barX = Math.max(0, Math.min(this.size[0], MathHelper.ceil(perc * this.size[0])));
                Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uv[0], this.uv[1], barX, this.size[1],
                                                        this.textureSize[0], this.textureSize[1]);
            }
        }

        private <T> double v(T e, Function<T, Double> func) {
            return func.apply(e);
        }
    }

    public final class InfoText
            extends Text
    {
        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFF000000");
            JsonUtils.addDefaultJsonProperty(data, "shadow", false);

            super.bakeData(gui, data, inst);
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public int getHeight() {
            return super.getHeight() - 2;
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            ITurretInst ti = ((IGuiTcuInst<?>) gui).getTurretInst();
            double[] vals;

            switch( InfoElement.this.type ) {
                case HEALTH:
                    vals = v(ti.get(),
                             e -> new double[] { e.getHealth(), e.getMaxHealth() });

                    return getRatioText(vals, "HP");
                case AMMO:
                    vals = v(ti.getTargetProcessor(),
                             t -> new double[] { t.getAmmoCount(), t.getMaxAmmoCapacity() });

                    return getRatioText(vals, "rounds");
                case PERSONAL_SHIELD:
                    Double spVal = v(ti.getUpgradeProcessor().<ShieldPersonal>getUpgradeInstance(Upgrades.SHIELD_PERSONAL.getId()),
                                     u -> u != null ? (double) u.getValue() : null);
                    if( spVal != null ) {
                        return String.format(" (+%s AP)", DECIMAL_FORMAT.format(spVal));
                    }
                case FORCEFIELD:
                    vals = v(ti.getTurret() instanceof TurretForcefield ? ti.<ShieldTurret>getRAM(null) : null,
                             s -> s != null ? new double[] { s.getValue(), s.getMaxValue() } : null);

                    return getRatioText(vals, "SP");
                case TARGET:
                    return MiscUtils.defIfNull(Strings.emptyToNull(ti.getTargetProcessor().getTargetName()), "N/A");
                case PLAYER:
                    return MiscUtils.defIfNull(Strings.emptyToNull(ti.getOwnerName()), "N/A");
            }

            return "";
        }

        private <T, U> U v(T e, Function<T, U> func) {
            return func.apply(e);
        }

        private String getRatioText(double[] vals, String unit) {
            return vals != null ? String.format("%s/%s %s", DECIMAL_FORMAT.format(vals[0]), DECIMAL_FORMAT.format(vals[1]), unit) : "";
        }
    }
}
