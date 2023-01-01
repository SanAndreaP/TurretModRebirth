package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.JsonElement;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Range;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class AmmoStatComponentProcessor
        implements IComponentProcessor
{
    private IAmmunition[] ammo;

    @Override
    public void setup(IVariableProvider provider) {
        Spliterator<JsonElement> types = provider.get("ammo_types").unwrap().getAsJsonArray().spliterator();
        this.ammo = StreamSupport.stream(types, false).map(id -> AmmunitionRegistry.INSTANCE.get(new ResourceLocation(id.getAsString())))
                                 .toArray(IAmmunition[]::new);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public IVariable process(String s) {
        String langCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        Range<Float> ammoDmg = this.ammo[0].getDamageInfo();
        float min = ammoDmg.getMinimum();
        float max = ammoDmg.getMaximum();

        switch( s ) {
            case "from": {
                return IVariable.wrap(MiscUtils.getNumberFormat(1, true, langCode).format(min / 2F));
            }
            case "to": {
                if( min < max - 0.01F ) {
                    return IVariable.wrap(MiscUtils.getNumberFormat(1, true, langCode).format(max / 2F));
                }
                break;
            }
            case "rounds_provided": {
                return IVariable.wrap(MiscUtils.getNumberFormat(0, true, langCode).format(this.ammo[0].getCapacity()));
            }
            case "item": {
                NonNullList<ItemStack> items = NonNullList.create();

                Arrays.stream(this.ammo).forEach(a -> {
                    String[] aSubtypes = a.getSubtypes();
                    ResourceLocation aId = a.getId();
                    if( aSubtypes.length > 0 ) {
                        Arrays.stream(aSubtypes).forEach(as -> items.add(AmmunitionRegistry.INSTANCE.getItem(aId, as)));
                    } else {
                        items.add(AmmunitionRegistry.INSTANCE.getItem(aId));
                    }
                });

                return IVariable.wrap(PatchouliHelper.getItemStr(items));
            }
            default: // no-op
        }

        return null;
    }
}
