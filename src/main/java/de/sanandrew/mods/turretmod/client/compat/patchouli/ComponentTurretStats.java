package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.text.NumberFormat;
import java.util.Locale;

@SideOnly(Side.CLIENT)
public class ComponentTurretStats
        implements IComponentProcessor
{
    ITurret turret;

    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        this.turret = TurretRegistry.INSTANCE.getObject(new ResourceLocation(iVariableProvider.get("turret_type")));
    }

    @Override
    public String process(String s) {
        String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();

        switch( s ) {
            case "health": {
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag(langCode));
                nf.setMaximumFractionDigits(1);
                nf.setMinimumFractionDigits(1);
                nf.setGroupingUsed(true);

                return nf.format(turret.getHealth());
            }
            case "tier": {
                NumberFormat nf = NumberFormat.getIntegerInstance(Locale.forLanguageTag(langCode));
                nf.setGroupingUsed(false);

                return nf.format(turret.getTier());
            }
        }

        return null;
    }
}
