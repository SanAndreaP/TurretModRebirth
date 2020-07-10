package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Range;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ComponentAmmoStatProcessor
        implements IComponentProcessor
{
    private IAmmunition ammo;
    private String subtype;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.ammo = AmmunitionRegistry.INSTANCE.getObject(new ResourceLocation(provider.get("ammo_type")));
        this.subtype = provider.has("ammo_subtype") ? provider.get("ammo_subtype") : null;
    }

    @Override
    public String process(String s) {
        String langCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Range<Float> ammoDmg = this.ammo.getDamageInfo();
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
                return TmrUtils.getNumberFormat(0, true, langCode).format(this.ammo.getAmmoCapacity());
            }
            case "item": {
                NonNullList<ItemStack> items = NonNullList.create();
                ResourceLocation eid = this.ammo.getBookEntryId();
                if( eid == null ) {
                    return null;
                }

                AmmunitionRegistry.INSTANCE.getObjects().stream().filter(a -> eid.equals(a.getBookEntryId())).forEach(a -> {
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
            case "turret_link": {
                ResourceLocation link = this.ammo.getTurret().getBookEntryId();
                if( link != null ) {
                    BookEntry entry = BookRegistry.INSTANCE.books.get(Resources.PATCHOULI.resource).contents.entries.get(link);
                    if( entry != null ) {
                        return "$(l:" + link.getPath() + ")" + entry.getName() + "$(/l)";
                    }
                }
            }
        }

        return null;
    }
}
