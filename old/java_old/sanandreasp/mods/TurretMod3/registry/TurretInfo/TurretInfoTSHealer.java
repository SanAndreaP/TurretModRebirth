package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoTSHealer extends TurretInfo {

	public TurretInfoTSHealer() {
		this.maxAmmo = 256;
		this.maxHealth = 30;
		this.maxEXP = 256;
		this.damage = 0;
//		this.item = new ItemStack(TM3ModRegistry.turretItem, 1, 7);
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 16.5F;
		this.desc = "turret.desctsh";
		this.crafting = new Object[] {
				" DG", " W ", "CGC",
				'D', new ItemStack(Blocks.dispenser),
				'G', new ItemStack(Blocks.gold_block),
				'W', new ItemStack(Blocks.planks, 0, OreDictionary.WILDCARD_VALUE),
				'C', new ItemStack(Blocks.cobblestone)
		};
		this.itemIcon = "TurretMod3:turret_14";

		int i = this.registerNewAmmoType("turret.amtp0tsh");
		 this.addAmmo(i, new ItemStack(Items.speckled_melon), 2);
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 7), 18);
		i = this.registerNewAmmoType("turret.amtp1tsh");
		 this.addAmmo(i, new ItemStack(Items.golden_carrot), 4);
		i = this.registerNewAmmoType("turret.amtp2tsh");
		 this.addAmmo(i, new ItemStack(Items.gold_nugget), 1);


		this.healItems.put(new ItemStack(Blocks.cobblestone), 5);
		this.healItems.put(new ItemStack(Blocks.gold_block), 15);
		this.healItems.put(new ItemStack(Items.gold_ingot), 1);
	}
}
