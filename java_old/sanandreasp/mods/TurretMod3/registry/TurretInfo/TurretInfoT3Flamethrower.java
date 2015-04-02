package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT3Flamethrower extends TurretInfo {

	public TurretInfoT3Flamethrower() {
		this.maxAmmo = 512;
		this.maxHealth = 60;
		this.damage = 3;
		this.maxEXP = 256;
//		this.item = new ItemStack(TM3ModRegistry.turretItem, 1, 5);
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 8.5F;
		this.desc = "turret.desct3f";
		this.crafting = new Object[] {
				"SDL", " O ", "BIB",
				'S', new ItemStack(Items.bucket),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Items.ender_pearl),
				'O', new ItemStack(Blocks.obsidian),
				'I', new ItemStack(Blocks.iron_block),
				'B', new ItemStack(Items.iron_ingot)
		};
		this.itemIcon = "TurretMod3:turret_06";

		int i = this.registerNewAmmoType("turret.amtp0t3f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 5), 4);
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 6), 32);

		this.healItems.put(new ItemStack(Blocks.iron_block), 15);
		this.healItems.put(new ItemStack(Items.iron_ingot), 1);
		this.healItems.put(new ItemStack(Blocks.obsidian), 30);
	}
}
