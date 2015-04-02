package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT2Revolver extends TurretInfo {

	public TurretInfoT2Revolver() {
		this.maxAmmo = 256;
		this.maxHealth = 40;
		this.damage = 4;
		this.maxEXP = 256;
//		this.item = new ItemStack(TM3ModRegistry.turretItem, 1, 3);
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 24.5F;
		this.desc = "turret.desct2r";
		this.crafting = new Object[] {
				"SDL", " I ", "BBB",
				'S', new ItemStack(Blocks.iron_block),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Items.dye, 1, 4),
				'I', new ItemStack(Blocks.stonebrick),
				'B', new ItemStack(Blocks.stone)
		};
		this.itemIcon = "TurretMod3:turret_04";

		int i = this.registerNewAmmoType("turret.amtp0t2r");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 3), 1);
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 4), 8);

		this.healItems.put(new ItemStack(Blocks.iron_block), 20);
		this.healItems.put(new ItemStack(Items.iron_ingot), 2);
		this.healItems.put(new ItemStack(Items.dye, 1, 4), 8);
		this.healItems.put(new ItemStack(Blocks.stonebrick, 1, OreDictionary.WILDCARD_VALUE), 2);
		this.healItems.put(new ItemStack(Blocks.stone, 1), 1);
	}
}
