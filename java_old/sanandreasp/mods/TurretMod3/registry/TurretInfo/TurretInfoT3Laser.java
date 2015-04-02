package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TurretInfoT3Laser extends TurretInfo {

	public TurretInfoT3Laser() {
		this.maxAmmo = 256;
		this.maxHealth = 60;
		this.damage = 6;
		this.maxEXP = 256;
//		this.item = new ItemStack(TM3ModRegistry.turretItem, 1, 4);
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 32.5F;
		this.desc = "turret.desct3l";
		this.crafting = new Object[] {
				"SDL", " O ", "BIB",
				'S', new ItemStack(Items.diamond),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Items.ender_pearl),
				'O', new ItemStack(Blocks.obsidian),
				'I', new ItemStack(Blocks.iron_block),
				'B', new ItemStack(Blocks.stone)
		};
		this.itemIcon = "TurretMod3:turret_05";

		int i = this.registerNewAmmoType("turret.amtp0t3l");
		 this.addAmmo(i, new ItemStack(Items.redstone), 1);
		 this.addAmmo(i, new ItemStack(Blocks.redstone_block), 9);

		this.healItems.put(new ItemStack(Blocks.iron_block), 15);
		this.healItems.put(new ItemStack(Items.iron_ingot), 1);
		this.healItems.put(new ItemStack(Blocks.obsidian), 30);
		this.healItems.put(new ItemStack(Blocks.stone, 1), 1);
	}
}
