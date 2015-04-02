package sanandreasp.mods.TurretMod3.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemArtilleryShells extends Item {

	@SideOnly(Side.CLIENT)
	private IIcon[] itemIcons;

	public ItemArtilleryShells() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return this.getUnlocalizedName() + getRealNumber(par1ItemStack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		int i = getRealNumber(par1);
		return i < 0 || i >= 5 ? this.itemIcons[0] : this.itemIcons[i];
	}

	public static int getRealNumber(int dmg) {
		return dmg > 4 ? dmg - 5 : dmg;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		switch(par1ItemStack.getItemDamage()) {
			case 1: case 6:
			case 3: case 8:
				par3List.add(StatCollector.translateToLocalFormatted("item.artilleryInfo.radius", 4));
				break;
			case 0: case 5:
			case 2: case 7:
				par3List.add(StatCollector.translateToLocalFormatted("item.artilleryInfo.radius", 2));
				break;
			case 4: case 9:
				par3List.add(StatCollector.translateToLocalFormatted("item.artilleryInfo.radius", 2));
				par3List.add(StatCollector.translateToLocal("item.artilleryInfo.shrapnel"));
				break;
		}
		switch(par1ItemStack.getItemDamage()) {
			case 2: case 3:
			case 7: case 8:
				par3List.add(StatCollector.translateToLocal("item.artilleryInfo.spread"));
				break;
		}
		if (par1ItemStack.getItemDamage() > 4) {
			par3List.add(StatCollector.translateToLocal("item.artilleryInfo.grief"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack) {
		return par1ItemStack.getItemDamage() > 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 10; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcons = new IIcon[5];
		this.itemIcons[0] = par1IconRegister.registerIcon("TurretMod3:artShell_small");
		this.itemIcons[1] = par1IconRegister.registerIcon("TurretMod3:artShell_big");
		this.itemIcons[2] = par1IconRegister.registerIcon("TurretMod3:artShell_smallFire");
		this.itemIcons[3] = par1IconRegister.registerIcon("TurretMod3:artShell_bigFire");
		this.itemIcons[4] = par1IconRegister.registerIcon("TurretMod3:artShell_frag");
	}

}
