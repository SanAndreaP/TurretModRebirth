package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.text.NumberFormat;
import java.util.Locale;

@SideOnly(Side.CLIENT)
public class ComponentTurretStatProcessor
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
                return getNumberFormat(1, true, langCode).format(turret.getHealth());
            }
            case "tier": {
                return getNumberFormat(0, false, langCode).format(turret.getTier());
            }
            case "range": {
                AxisAlignedBB aabb = this.turret.getRangeBB(null);
                NumberFormat nf = getNumberFormat(0, true, langCode);

                return String.format("min XYZ: %s / %s / %s$(br)max XYZ: %s / %s / %s",
                                     nf.format(aabb.minX), nf.format(aabb.minY), nf.format(aabb.minZ),
                                     nf.format(aabb.maxX), nf.format(aabb.maxY), nf.format(aabb.maxZ));
            }
        }

        return null;
    }

    public NumberFormat getNumberFormat(int numFract, boolean grouping, String langCode) {
        NumberFormat nf;

        if( numFract == 0 ) {
            nf = NumberFormat.getIntegerInstance(Locale.forLanguageTag(langCode));
            nf.setGroupingUsed(false);
        } else {
            nf = NumberFormat.getNumberInstance(Locale.forLanguageTag(langCode));
            nf.setMaximumFractionDigits(numFract);
            nf.setMinimumFractionDigits(numFract);
            nf.setGroupingUsed(grouping);
        }

        return nf;
    }
}
