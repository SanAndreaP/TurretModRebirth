package sanandreasp.mods.TurretMod3.registry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.CreativeTabTurrets;
import sanandreasp.mods.turretmod3.block.BlockLaptop;
import sanandreasp.mods.turretmod3.command.CommandTurretMod;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;
import sanandreasp.mods.turretmod3.entity.EntityMobileBase;
import sanandreasp.mods.turretmod3.entity.projectile.*;
import sanandreasp.mods.turretmod3.entity.turret.*;
import sanandreasp.mods.turretmod3.item.*;
import sanandreasp.mods.turretmod3.packet.PacketHandlerCommon;
import sanandreasp.mods.turretmod3.registry.TurretInfo.*;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.*;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

@Mod(modid=TM3ModRegistry.modID, name="Turret Mod 3", version="3.0.1")
public class TM3ModRegistry {

	/** modID field for the @Mod annotation and other references (for example the FML logging system) */
	public static final String modID = "TurretMod3";

	/** The @SidedProxy field for the Mod */
	@SidedProxy(clientSide="sanandreasp.mods.turretmod3.client.registry.ClientProxy", serverSide="sanandreasp.mods.turretmod3.registry.CommonProxy")
	public static CommonProxy proxy;

	/** The @Instance field for the Mod */
	@Instance(TM3ModRegistry.modID)
	public static TM3ModRegistry instance;

    public static SimpleNetworkWrapper networkWrapper;

	// Texture fields
	public static final String TEX_TURRETDIR	= "turretmod3:textures/entities/turrets/";
	public static final String TEX_GUITCUDIR	= "turretmod3:textures/guis/tcu_gui/";
	public static final String TEX_GUILAP		= "turretmod3:textures/guis/laptop/";
	public static final String TEX_GUIINFO		= "turretmod3:textures/guis/turretinfo_gui/";
        public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("textures/font/ascii.png");
	public static final ResourceLocation TEX_GUIBUTTONS	= new ResourceLocation("turretmod3:textures/guis/guis_buttons.png");
	public static final ResourceLocation TEX_TURRETCAM	= new ResourceLocation("turretmod3:textures/guis/turretCam.png");
	public static final ResourceLocation TEX_WHITELAP		= new ResourceLocation("turretmod3:textures/blocks/laptopWhite.png");
	public static final ResourceLocation TEX_BLACKLAP		= new ResourceLocation("turretmod3:textures/blocks/laptopBlack.png");
	public static final String TEX_LAPGLOW		= "turretmod3:textures/blocks/laptopGlow";

	// Block fields
	public static Block laptop;

	// Item fields
	public static Item turretItem;
	public static Item ammoItems;
	public static Item tcu;
	public static Item tInfoBook;
	public static Item mobileBase;
	public static Item httm;
	public static Item rocket;
	public static Item artilleryBall;
	public static Item turretRec1;

	// Creative tab field
	public static CreativeTabs tabTurret;

	public static float labelRenderRange = 8F;

	public static boolean canCollectorGetXP = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		// init Creative Tab
        tabTurret = new CreativeTabTurrets("turretTab");

		// create helper instance for the Config Manager (CFGMan)
        Configuration cfgman = new Configuration(evt.getSuggestedConfigurationFile());
		// load Config
        cfgman.load();
        labelRenderRange = cfgman.getFloat("Label Render Range", "Client side", labelRenderRange, 0, 500, "The range in blocks at which label is going to render");
        canCollectorGetXP = cfgman.getBoolean("Can Collector collect XP orbs", "Server side", canCollectorGetXP, "False to disable xp collection");
        cfgman.save();

		// register Forge Events and Handlers
        proxy.registerHandlers();

		// register Ammo-Item types
        ItemAmmunitions.addAmmoItem(0, "arrowPack");
        ItemAmmunitions.addAmmoItem(1, "pebbles");
        ItemAmmunitions.addAmmoItem(2, "pebble");
        ItemAmmunitions.addAmmoItem(3, "bullets");
        ItemAmmunitions.addAmmoItem(4, "bulletPack");
        ItemAmmunitions.addAmmoItem(5, "tank");
        ItemAmmunitions.addAmmoItem(6, "tankPack");
        ItemAmmunitions.addAmmoItem(7, "gMelonPack");
        ItemAmmunitions.addAmmoItem(8, "enderPearlPack");
        ItemAmmunitions.addAmmoItem(9, "ach_piercing");

		// initialize items
        initItems();

		// register Achievement Page and initialize Achievements
        AchievementPageTM.initAchievementPage();

		// register optional Main Menu, if MainMenuAPI is installed
        registerMainMenu();
	}

	/** registers the dungeon loot **/
	private void registerDungeonLoot() {
		ChestGenHooks dungeon = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
		ChestGenHooks pyramid = ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST);
		ChestGenHooks jtemple = ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST);

		WeightedRandomChestContent disc1WRCC = new WeightedRandomChestContent(new ItemStack(turretRec1), 1, 1, 5);
		WeightedRandomChestContent httmWRCC = new WeightedRandomChestContent(new ItemStack(httm), 1, 1, 5);

		dungeon.addItem(disc1WRCC);
		pyramid.addItem(disc1WRCC);
		pyramid.addItem(httmWRCC);
		jtemple.addItem(disc1WRCC);
		jtemple.addItem(httmWRCC);
	}

	/** registers the TurretMod Main Menu */
	private void registerMainMenu() {

	}

	/** initializes the items **/
	public void initItems() {
		laptop			= new BlockLaptop(Material.circuits)
							.setLightOpacity(0)
							.setHardness(1F)
							.setBlockName("tm3.laptop")
                            .setBlockTextureName("turretmod3:laptop")
							.setCreativeTab(tabTurret);

		turretItem		= new ItemTurret()
							.setTranslationKey("tm3.turretItem")
							.setCreativeTab(tabTurret);
		ammoItems		= new ItemAmmunitions()
							.setTranslationKey("tm3.arrowPack")
							.setCreativeTab(tabTurret);
		tcu				= new Item().setTextureName("TurretMod3:tcu")
							.setTranslationKey("tm3.turretControlU")
							.setCreativeTab(tabTurret);
		tInfoBook		= new ItemTurretInfo()
							.setTranslationKey("tm3.tInfoBook")
							.setCreativeTab(tabTurret);
		mobileBase		= new ItemMobileBase().setTextureName("TurretMod3:mobileBase")
							.setTranslationKey("tm3.mobileBase")
							.setCreativeTab(tabTurret);
		httm			= new Item().setTextureName("TurretMod3:httm")
							.setTranslationKey("tm3.htTurretMod")
							.setCreativeTab(tabTurret);
		rocket			= new ItemFLAKRockets()
							.setTranslationKey("tm3.flakRockets")
							.setCreativeTab(tabTurret);
		artilleryBall	= new ItemArtilleryShells()
							.setTranslationKey("tm3.artilleryBalls")
							.setCreativeTab(tabTurret);
		turretRec1		= new ItemTMDisc("tidalForce")
							.setTranslationKey("tm3.turretRec1")
							.setCreativeTab(tabTurret);

		this.registerItems(turretItem, ammoItems, tcu, tInfoBook, mobileBase, httm, rocket, artilleryBall, turretRec1);

		GameRegistry.registerBlock(laptop, ItemSimpleBlock.class, "tm3.laptopBLK");

		GameRegistry.registerTileEntity(TileEntityLaptop.class, "tm3.laptopTE");
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
	    // register Turret-Upgrades
		TurretUpgrades.addUpgrade(new TUpgControl());
		TurretUpgrades.addUpgrade(new TUpgChestGrabbing());
		TurretUpgrades.addUpgrade(new TUpgInfAmmo());
		TurretUpgrades.addUpgrade(new TUpgExperience());
		TurretUpgrades.addUpgrade(new TUpgExpStorage());
		TurretUpgrades.addUpgrade(new TUpgFireImmunity());
		TurretUpgrades.addUpgrade(new TUpgPiercing());
		TurretUpgrades.addUpgrade(new TUpgEconomy());
		TurretUpgrades.addUpgrade(new TUpgPurify());
		TurretUpgrades.addUpgrade(new TUpgEnderHitting());
		TurretUpgrades.addUpgrade(new TUpgPrecision());
		TurretUpgrades.addUpgrade(new TUpgSlowdownII());
		TurretUpgrades.addUpgrade(new TUpgStopMove());
		TurretUpgrades.addUpgrade(new TUpgExpStorageC());
		TurretUpgrades.addUpgrade(new TUpgTurretCollect());
		TurretUpgrades.addUpgrade(new TUpgItemCollect());
		TurretUpgrades.addUpgrade(new TUpgShieldRngI());
		TurretUpgrades.addUpgrade(new TUpgShieldRngII());
		TurretUpgrades.addUpgrade(new TUpgShieldPointsIncr());
		TurretUpgrades.addUpgrade(new TUpgShieldRepairIncr());
		TurretUpgrades.addUpgrade(new TUpgShieldMobPush());
		TurretUpgrades.addUpgrade(new TUpgRangeIncr());

	    // register mod entities
		int modEntityID = 0;

		EntityRegistry.registerModEntity(EntityTurret_T1Arrow.class, "Turret_T1A", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T1Shotgun.class, "Turret_T1S", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T2Minigun.class, "Turret_T2M", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T2Revolver.class, "Turret_T2R", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T3Laser.class, "Turret_T3L", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T3Flamethrower.class, "Turret_T3F", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T4Sniper.class, "Turret_T4S", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T4FLAK.class, "Turret_T4F", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T5Railgun.class, "Turret_T5R", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_T5Artillery.class, "Turret_T5A", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_TSSnowball.class, "Turret_T5S", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_TSCollector.class, "Turret_TSC", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_TSForcefield.class, "Turret_TSF", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTurret_TSHealer.class, "Turret_TSH", modEntityID++, this, 128, 1, true);

		EntityRegistry.registerModEntity(TurretProj_Arrow.class, "TurretProj_Arrow", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Pebble.class, "TurretProj_Pebble", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Seed.class, "TurretProj_Seed", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Bullet.class, "TurretProj_Bullet", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Laser.class, "TurretProj_Laser", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Flame.class, "TurretProj_Flame", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Plasma.class, "TurretProj_Plasma", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Rocket.class, "TurretProj_Rocket", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Shard.class, "TurretProj_Shard", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Explosive.class, "TurretProj_Explose", modEntityID++, this, 128, 1, true);
		EntityRegistry.registerModEntity(TurretProj_Snowball.class, "TurretProj_Snowball", modEntityID++, this, 128, 1, true);

		EntityRegistry.registerModEntity(EntityMobileBase.class, "MobileBase", modEntityID++, this, 128, 5, true);
		EntityRegistry.registerModEntity(EntityDismantleStorage.class, "DismStorage", modEntityID++, this, 128, 5, false);

        // register Turret-Informations
        TurretInfo.addTurretInfo(EntityTurret_T1Arrow.class, new TurretInfoT1Arrow());
        TurretInfo.addTurretInfo(EntityTurret_T1Shotgun.class, new TurretInfoT1Shotgun());
        TurretInfo.addTurretInfo(EntityTurret_T2Minigun.class, new TurretInfoT2Minigun());
        TurretInfo.addTurretInfo(EntityTurret_T2Revolver.class, new TurretInfoT2Revolver());
        TurretInfo.addTurretInfo(EntityTurret_T3Laser.class, new TurretInfoT3Laser());
        TurretInfo.addTurretInfo(EntityTurret_T3Flamethrower.class, new TurretInfoT3Flamethrower());
        TurretInfo.addTurretInfo(EntityTurret_T4Sniper.class, new TurretInfoT4Sniper());
        TurretInfo.addTurretInfo(EntityTurret_T4FLAK.class, new TurretInfoT4FLAK());
        TurretInfo.addTurretInfo(EntityTurret_T5Railgun.class, new TurretInfoT5Railgun());
        TurretInfo.addTurretInfo(EntityTurret_T5Artillery.class, new TurretInfoT5Artillery());
        TurretInfo.addTurretInfo(EntityTurret_TSSnowball.class, new TurretInfoTSSnowball());
        TurretInfo.addTurretInfo(EntityTurret_TSCollector.class, new TurretInfoTSCollector());
        TurretInfo.addTurretInfo(EntityTurret_TSForcefield.class, new TurretInfoTSForcefield());
        TurretInfo.addTurretInfo(EntityTurret_TSHealer.class, new TurretInfoTSHealer());

	    // register Handlers
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modID);
		new PacketHandlerCommon().registerOn(networkWrapper);

	    // initialize crafting recipes
		CraftingRegistry.initCraftings();

	    // register rendering informations
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		TurretTargetRegistry.initTargetReg();
	}

	@EventHandler
	public void startingServer(FMLServerStartingEvent evt) {
	    // register commands
		evt.registerServerCommand(new CommandTurretMod());
	}

	/** registers the Items **/
	private void registerItems(Item... items) {
		for (int i = 0; i < items.length; i++) GameRegistry.registerItem(items[i], "tm3.item_"+i);
	}

    public static boolean areStacksEqualWithWildcard(ItemStack stack1, ItemStack stack2){
        if(stack1==stack2)
            return true;
        return stack1!=null && stack2!=null && (stack1.isItemEqual(stack2)
                || (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE && stack1.getItem() == stack2.getItem()));
    }
}
