package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TurretInfoT2Minigun extends TurretInfo {

	public TurretInfoT2Minigun() {
		this.maxAmmo = 256;
		this.maxHealth = 40;
		this.damage = 1;
		this.maxEXP = 256;
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 24.5F;
		this.desc = "turret.desct2m";
		this.crafting = new Object[] {
				"GDL", " I ", "BBB",
				'G', new ItemStack(Items.gold_ingot),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Items.dye, 1, 4),
				'I', new ItemStack(Blocks.iron_block),
				'B', new ItemStack(Blocks.stonebrick)
		};
		this.itemIcon = "TurretMod3:turret_03";

		int i = this.registerNewAmmoType("turret.amtp0t2m");
		 this.addAmmo(i, new ItemStack(Items.wheat_seeds), 1);
		i = this.registerNewAmmoType("turret.amtp1t2m");
		 this.addAmmo(i, new ItemStack(Items.melon_seeds), 1);
		 this.addAmmo(i, new ItemStack(Items.melon), 1);
		 this.addAmmo(i, new ItemStack(Blocks.melon_block), 8);
		i = this.registerNewAmmoType("turret.amtp2t2m");
		 this.addAmmo(i, new ItemStack(Items.pumpkin_seeds), 1);
		 this.addAmmo(i, new ItemStack(Blocks.pumpkin), 4);

		this.healItems.put(new ItemStack(Blocks.iron_block), 20);
		this.healItems.put(new ItemStack(Items.iron_ingot), 2);
		this.healItems.put(new ItemStack(Items.gold_ingot), 8);
		this.healItems.put(new ItemStack(Items.dye, 1, 4), 8);
		this.healItems.put(new ItemStack(Blocks.stonebrick, 1, OreDictionary.WILDCARD_VALUE), 1);
	}
}
