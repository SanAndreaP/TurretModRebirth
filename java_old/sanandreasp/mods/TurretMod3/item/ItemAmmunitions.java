package sanandreasp.mods.TurretMod3.item;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;
import java.util.Map;

public class ItemAmmunitions extends Item {

	public static Map<Integer, String> dmgLangMapping = Maps.newHashMap();
	private static Map<Integer, IIcon> dmgIconMapping = Maps.newHashMap();

	public ItemAmmunitions() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return par1 < 0 || par1 >= dmgIconMapping.size() ? super.getIconFromDamage(par1) : dmgIconMapping.get(par1);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int dmg = par1ItemStack.getItemDamage();
		if (dmg >= dmgLangMapping.size() || dmg < 0)
			return super.getUnlocalizedName(par1ItemStack);
		else
			return "tm3." + dmgLangMapping.get(dmg);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int id : dmgLangMapping.keySet())
            if(id!=9)
			    par3List.add(new ItemStack(par1, 1, id));
	}

	public static void addAmmoItem(int dmg, String langStr) {
		dmgLangMapping.put(dmg, langStr);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		for (int i = 0; i < dmgLangMapping.size(); i++) {
			dmgIconMapping.put(i, par1IconRegister.registerIcon("TurretMod3:"+dmgLangMapping.get(i)));
		}
	}
}
