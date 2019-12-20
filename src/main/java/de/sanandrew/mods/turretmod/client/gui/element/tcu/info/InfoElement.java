package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
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
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info_progress");

    private static final String HEALTH          = "health";
    private static final String AMMO            = "ammo";
    private static final String PERSONAL_SHIELD = "personal_shield";
    private static final String FORCEFIELD      = "forcefield";
    private static final String TARGET          = "target";
    private static final String PLAYER          = "player";

    private int[]          size;
    private String         type;
    private GuiElementInst bar  = null;
    private GuiElementInst text = null;
    private GuiElementInst icon = null;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.size = JsonUtils.getIntArray(data.get("size"), new int[] { 164, 16 }, Range.is(2));

        this.type = JsonUtils.getStringVal(data.get("type"));

        if( data.has("bar") ) {
            this.bar = JsonUtils.GSON.fromJson(data.get("bar"), GuiElementInst.class);
            this.bar.element = new InfoBar();
            gui.getDefinition().initElement(this.bar);
            this.bar.data.addProperty("type", this.type);
            this.bar.get().bakeData(gui, this.bar.data);
        }

        if( data.has("text") ) {
            this.text = JsonUtils.GSON.fromJson(data.get("text"), GuiElementInst.class);
            this.text.element = new InfoText();
            gui.getDefinition().initElement(this.text);
            this.text.data.addProperty("type", this.type);
            this.text.get().bakeData(gui, this.text.data);
        }

        if( data.has("icon") ) {
            this.icon = JsonUtils.GSON.fromJson(data.get("icon"), GuiElementInst.class);
            gui.getDefinition().initElement(this.icon);
            this.icon.data.addProperty("type", this.type);
            this.icon.get().bakeData(gui, this.icon.data);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        if( this.type.equals(FORCEFIELD) && !(((IGuiTcuInst<?>) gui.get()).getTurretInst().getTurret() instanceof TurretForcefield) ) {
            return;
        }

        Consumer<GuiElementInst> u = inst -> { if( inst != null ) inst.get().update(gui, inst.data); };
        u.accept(this.bar);
        u.accept(this.text);
        u.accept(this.icon);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        ITurretInst ti = ((IGuiTcuInst<?>) gui.get()).getTurretInst();
        if( this.type.equals(FORCEFIELD) && !(ti.getTurret() instanceof TurretForcefield) ) {
            return;
        }
        if( this.type.equals(PERSONAL_SHIELD) && !ti.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PERSONAL) ) {
            return;
        }

        Consumer<GuiElementInst> r = inst -> { if( inst != null ) inst.get().render(gui, partTicks, x + inst.pos[0], y + inst.pos[1], mouseX, mouseY, inst.data); };
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

    public static class InfoBar
            extends Texture
    {
        private String type = null;
        private int[]  uvBg = null;

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            super.bakeData(gui, data);

            if( this.type == null ) {
                this.type = JsonUtils.getStringVal(data.get("type"));
            }
            if( this.uvBg == null ) {
                this.uvBg = JsonUtils.getIntArray(data.get("uvBackground"), null, Range.is(2));
            }
        }

        @Override
        protected void drawRect(IGui gui) {
            ITurretInst ti = ((IGuiTcuInst<?>) gui).getTurretInst();

            double perc = 0.0D;
            switch( this.type ) {
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
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uvBg[0], this.uvBg[1], this.data.size[0], this.data.size[1],
                                                            this.data.textureSize[0], this.data.textureSize[1]);
                }

                int barX = Math.max(0, Math.min(this.data.size[0], MathHelper.ceil(perc * this.data.size[0])));
                Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], barX, this.data.size[1],
                                                        this.data.textureSize[0], this.data.textureSize[1]);
            }
        }

        private static <T> double v(T e, Function<T, Double> func) {
            return func.apply(e);
        }
    }

    public static final class InfoText
            extends Text
    {
        private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");

        private String type = null;

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFF000000");
            JsonUtils.addDefaultJsonProperty(data, "shadow", false);

            super.bakeData(gui, data);

            if( this.type == null ) {
                this.type = JsonUtils.getStringVal(data.get("type"));
            }
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

            switch( this.type ) {
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

        private static <T, U> U v(T e, Function<T, U> func) {
            return func.apply(e);
        }

        private static String getRatioText(double[] vals, String unit) {
            return vals != null ? String.format("%s/%s %s", DECIMAL_FORMAT.format(vals[0]), DECIMAL_FORMAT.format(vals[1]), unit) : "";
        }
    }
}
