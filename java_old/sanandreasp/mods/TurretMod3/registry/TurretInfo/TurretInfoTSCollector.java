package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TurretInfoTSCollector extends TurretInfo {

	public TurretInfoTSCollector() {
		this.maxAmmo = 0;
		this.maxHealth = 60;
		this.maxEXP = 1024;
		this.damage = 0;
		this.lowerRangeY = 16.5F;
		this.upperRangeY = 16.5F;
		this.rangeX = 16.5F;
		this.desc = "turret.desctsc";
		this.crafting = new Object[] {
				"P P", "OEO", "WWW",
				'P', new ItemStack(Items.ender_pearl),
				'E', new ItemStack(Blocks.emerald_block),
				'O', new ItemStack(Blocks.obsidian),
				'W', new ItemStack(Blocks.end_stone)
		};
		this.itemIcon = "TurretMod3:turret_12";

		this.healItems.put(new ItemStack(Blocks.end_stone), 30);
		this.healItems.put(new ItemStack(Blocks.obsidian), 20);
		this.healItems.put(new ItemStack(Items.emerald), 2);
		this.healItems.put(new ItemStack(Blocks.emerald_block), 18);
	}
}
