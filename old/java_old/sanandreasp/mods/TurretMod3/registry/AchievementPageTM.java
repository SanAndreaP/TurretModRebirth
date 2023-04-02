package sanandreasp.mods.TurretMod3.registry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class AchievementPageTM {

	public static Achievement turretInfo;
	public static Achievement firstStrike;
	public static Achievement multiDeath;
	public static Achievement control;
	public static Achievement camKill;
	public static Achievement upgrade;
	public static Achievement piercing;

	public static void initAchievementPage() {
		turretInfo = new Achievement("Turret Info", "turretmod3.tinfo", 0, 0, new ItemStack(TM3ModRegistry.tInfoBook), null).registerStat();
		firstStrike = new Achievement("First Strike", "turretmod3.firstStrike", -2, 0, new ItemStack(Items.arrow), turretInfo).registerStat();
		multiDeath = new Achievement("Multi-Kill", "turretmod3.multiDeath", -2, -2, new ItemStack(TM3ModRegistry.turretItem, 1, 1), firstStrike).registerStat();
		control = new Achievement("Control", "turretmod3.control", 0, 2, new ItemStack(TM3ModRegistry.tcu), turretInfo).registerStat();
		camKill = new Achievement("Cam Kill", "turretmod3.camKill", -2, 2, new ItemStack(Items.saddle), control).registerStat();
		upgrade = new Achievement("Upgrade", "turretmod3.upgrade", 2, 2, new ItemStack(Items.golden_sword), control).registerStat();
		piercing = new Achievement("Piercing", "turretmod3.piercing", -4, 0, new ItemStack(TM3ModRegistry.ammoItems, 1, 9), firstStrike).registerStat();

		AchievementPage.registerAchievementPage(new AchievementPage("Turret Mod 3",
				turretInfo,
				firstStrike,
				multiDeath,
				control,
				camKill,
				upgrade,
				piercing
		));
	}
}
