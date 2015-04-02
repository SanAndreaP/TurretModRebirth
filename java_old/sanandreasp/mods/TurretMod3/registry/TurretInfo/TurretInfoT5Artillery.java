package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.item.ItemArtilleryShells;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT5Artillery extends TurretInfo {

	public TurretInfoT5Artillery() {
		this.maxAmmo = 256;
		this.maxHealth = 100;
		this.damage = 15;
		this.maxEXP = 256;
		this.lowerRangeY = 20.5F;
		this.upperRangeY = 15.5F;
		this.rangeX = 50.5F;
		this.desc = "turret.desct5a";
		this.crafting = new Object[] {
				"SDL", "EO ", "BOB",
				'S', new ItemStack(TM3ModRegistry.httm),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Blocks.end_stone),
				'E', new ItemStack(Items.emerald),
				'O', new ItemStack(Blocks.obsidian),
				'B', new ItemStack(Blocks.stone)
		};
		this.itemIcon = "TurretMod3:turret_10";

		for (int j = 0; j < 10; j++) {
			int i = this.registerNewAmmoType("turret.amtp"+ItemArtilleryShells.getRealNumber(j)+"t5a");
			 this.addAmmo(i, new ItemStack(TM3ModRegistry.artilleryBall, 1, j), 1);
		}
//		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 8), 8);
//		 this.addAmmo(i, new ItemStack(Item.eyeOfEnder), 2);
//		 this.addAmmo(i, new ItemStack(TM3ModRegistry.bulletPack), 8);

		this.healItems.put(new ItemStack(Blocks.stone), 1);
		this.healItems.put(new ItemStack(Blocks.obsidian), 20);
		this.healItems.put(new ItemStack(Blocks.end_stone), 10);
	}
}
