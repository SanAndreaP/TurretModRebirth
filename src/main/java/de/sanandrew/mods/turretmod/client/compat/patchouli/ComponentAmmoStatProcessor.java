package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.Arrays;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ComponentAmmoStatProcessor
        implements IComponentProcessor
{
    private IAmmunition[] ammo;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.ammo = Arrays.stream(provider.get("ammo_type").split(";")).map(id -> AmmunitionRegistry.INSTANCE.getObject(new ResourceLocation(id)))
                          .toArray(IAmmunition[]::new);
    }

    @Override
    public String process(String s) {
        String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Range<Float> ammoDmg = this.ammo[0].getDamageInfo();
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
                break;
            }
            case "rounds_provided": {
                return TmrUtils.getNumberFormat(0, true, langCode).format(this.ammo[0].getAmmoCapacity());
            }
            case "item": {
                NonNullList<ItemStack> items = NonNullList.create();

                Arrays.stream(this.ammo).forEach(a -> {
                    String[] aSubtypes = a.getSubtypes();
                    ResourceLocation aId = a.getId();
                    if( aSubtypes != null && aSubtypes.length > 0 ) {
                        Arrays.stream(aSubtypes).forEach(as -> items.add(AmmunitionRegistry.INSTANCE.getItem(aId, as)));
                    } else {
                        items.add(AmmunitionRegistry.INSTANCE.getItem(aId));
                    }
                });

                return ItemStackUtil.serializeIngredient(Ingredient.fromStacks(items.toArray(new ItemStack[0])));
            }
        }

        return null;
    }
}
