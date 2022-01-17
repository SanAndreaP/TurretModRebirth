package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IIcon;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class HealthProvider
        implements ITcuInfoProvider
{
    private float health;
    private float maxHealth;

    GuiElementInst icon;

    @Nonnull
    @Override
    public String getName() {
        return "health";
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(txtData, "size", new int[] {16, 16});
        JsonUtils.addDefaultJsonProperty(txtData, "uv", new int[] {88, 16});

        Texture.Builder tb = Texture.Builder.buildFromJson(gui, txtData);
        this.icon = new GuiElementInst(JsonUtils.getIntArray(txtData.get("offset"), new int[] {0, 0}, Range.is(2)), tb.get(gui)).initialize(gui);

//        Texture.Builder txtBuilder = new Texture.Builder(JsonUtils.getIntArray(txtData, new int[] {16, 16}, Range.is(3)));
//        txtBuilder.
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.icon.element.setup(gui, this.icon);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);
    }

    //    @Nonnull
//    @Override
//    public ITextComponent getLabel() {
//        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.tooltip"));
//    }
//
//    @Override
//    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
//        // TODO: render personal shield
//    }
//
//    @Override
//    public void tick(IGui gui, ITurretEntity turret) {
//        MiscUtils.accept(turret.get(), e -> {
//            this.health = e.getHealth();
//            this.maxHealth = e.getMaxHealth();
//        });
//    }
//
//    @Nonnull
//    @Override
//    public IIcon getIcon() {
//        return IIcon.get((mw, mh) -> new int[] { 86, 16 });
//    }

//    @Nullable
//    @Override
//    public ITextComponent getValueStr() {
//        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.health.suffix"), this.health / 2.0F)
//                   .withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFFA03030)));
//    }
//
//    @Override
//    public float getCurrValue() {
//        return this.health;
//    }
//
//    @Override
//    public float getMaxValue() {
//        return this.maxHealth;
//    }
//
//    @Nonnull
//    @Override
//    public ITexture buildIcon() {
//        return ITexture.icon((mw, mh) -> new int[] { 86, 16 });
//    }
//
//    @Nullable
//    @Override
//    public ITexture buildProgressBar() {
//        return ITexture.progressBar((mw, mh) -> new int[] { 0, 149 },
//                                    (mw, mh) -> new int[] { 0, 146 });
//    }
}
