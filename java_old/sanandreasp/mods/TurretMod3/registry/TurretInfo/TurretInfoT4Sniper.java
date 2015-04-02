package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT4Sniper extends TurretInfo {

	public TurretInfoT4Sniper() {
		this.maxAmmo = 256;
		this.maxHealth = 40;
		this.damage = 25;
		this.maxEXP = 256;
		this.lowerRangeY = 20.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 64.5F;
		this.desc = "turret.desct4s";
		this.crafting = new Object[] {
				"SDL", " O ", "BBB",
				'S', new ItemStack(TM3ModRegistry.httm),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Blocks.nether_brick),
				'O', new ItemStack(Blocks.obsidian),
				'B', new ItemStack(Blocks.netherrack)
		};
		this.itemIcon = "TurretMod3:turret_07";

		int i = this.registerNewAmmoType("turret.amtp0t4s");
		 this.addAmmo(i, new ItemStack(Items.dye, 1, 4), 1);
		 this.addAmmo(i, new ItemStack(Blocks.lapis_block), 9);
//		 this.addAmmo(i, new ItemStack(TM3ModRegistry.bulletPack), 8);

		this.healItems.put(new ItemStack(Blocks.netherrack), 1);
		this.healItems.put(new ItemStack(Blocks.obsidian), 20);
		this.healItems.put(new ItemStack(Blocks.nether_brick), 10);
	}
}
