/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.text.NumberFormat;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class TurretStatComponentProcessor
        implements IComponentProcessor
{
    ITurret turret;

    @Override
    public void setup(IVariableProvider provider) {
        this.turret = TurretRegistry.INSTANCE.get(new ResourceLocation(provider.get("turret").asString()));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public IVariable process(String s) {
        String langCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();

        switch( s ) {
            case "tier":
                return IVariable.wrap(MiscUtils.getNumberFormat(0, false, langCode).format(turret.getTier()));
            case "health":
                return IVariable.wrap(MiscUtils.getNumberFormat(1, true, langCode).format(turret.getHealth() / 2F));
            case "ammo":
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format(turret.getAmmoCapacity()));
            case "reload":
                return IVariable.wrap(MiscUtils.getTimeFromTicks(turret.getReloadTicks()));

            default:
                if( s.contains("range") ) {
                    AxisAlignedBB aabb = this.turret.getRangeBB(null);
                    NumberFormat nf = MiscUtils.getNumberFormat(0, true, langCode);
                    switch( s ) {
                        case "rangeLX": return IVariable.wrap(nf.format(aabb.minX * -1.0D));
                        case "rangeLY": return IVariable.wrap(nf.format(aabb.minY * -1.0D));
                        case "rangeLZ": return IVariable.wrap(nf.format(aabb.minZ * -1.0D));
                        case "rangeHX": return IVariable.wrap(nf.format(aabb.maxX));
                        case "rangeHY": return IVariable.wrap(nf.format(aabb.maxY));
                        case "rangeHZ": return IVariable.wrap(nf.format(aabb.maxZ));
                        default: // NO-OP
                    }
                }
        }

        return null;
    }
}
