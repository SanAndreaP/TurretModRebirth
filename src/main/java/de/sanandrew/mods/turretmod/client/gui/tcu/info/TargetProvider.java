package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class TargetProvider
    extends TextProvider
{
    protected static final ITextComponent NO_TARGET = new TranslationTextComponent(Lang.TCU_TEXT.get("info.target.empty")).withStyle(TextFormatting.GRAY);

    @Nonnull
    @Override
    public String getName() {
        return "target";
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 104, 0 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.target.tooltip");
    }

    @Nonnull
    @Override
    public BiFunction<IGui, ITextComponent, ITextComponent> getTextFunction(IGui gui, ITurretEntity turret) {
        return (g, o) -> MiscUtils.apply(turret.getTargetProcessor(), p -> {
            if( p.hasTarget() ) {
                return p.getTargetName();
            } else {
                return NO_TARGET;
            }
        });
    }
}
