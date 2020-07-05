package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Range;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ComponentAmmoStatProcessor
        implements IComponentProcessor
{
    IAmmunition ammo;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.ammo = AmmunitionRegistry.INSTANCE.getObject(new ResourceLocation(provider.get("ammo_type")));
    }

    @Override
    public String process(String s) {
        String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Range<Float> ammoDmg = ammo.getDamageInfo();
        float min = ammoDmg.getMinimum();
        float max = ammoDmg.getMaximum();

        switch( s ) {
            case "from": {
                return TmrUtils.getNumberFormat(1, true, langCode).format(min);
            }
            case "to": {
                if( min < max - 0.01F ) {
                    return TmrUtils.getNumberFormat(1, true, langCode).format(max);
                }
            }
        }

        return null;
    }

}
