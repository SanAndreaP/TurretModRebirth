package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IIcon;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class HealthProvider
        implements ITcuInfoProvider
{
    private float health;
    private float maxHealth;

    @Nonnull
    @Override
    public String getName() {
        return "health";
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
