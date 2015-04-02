package sanandreasp.mods.TurretMod3.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.inventory.ContainerLaptopUpgrades;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sanandreasp.mods.turretmod3.registry.TurretTargetRegistry.trTargets;

public class ItemTurret extends Item {

	@SideOnly(Side.CLIENT)
	private IIcon[] turretIcons;

	public ItemTurret() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            Block var11 = par3World.getBlock(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            double var12 = 0.0D;

            if (par7 == 1 && var11.getRenderType() == 11)
            {
                var12 = 0.5D;
            }

            if (spawnTurret(par3World, par1ItemStack, (double)par4 + 0.5D, (double)par5 + var12, (double)par6 + 0.5D, par2EntityPlayer) != null && !par2EntityPlayer.capabilities.isCreativeMode)
            {
                --par1ItemStack.stackSize;
            }

            return true;
        }
    }

    public static Entity spawnTurret(World par0World, ItemStack par1, double par2, double par4, double par6, EntityPlayer par7Player)
    {
        Entity var8 = getTurretByDamage(par0World, par1.getItemDamage());
        if (var8 != null && var8 instanceof EntityTurret_Base)
        {
        	EntityTurret_Base var10 = (EntityTurret_Base)var8;
            var8.setLocationAndAngles(par2, par4, par6, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
            var10.rotationYawHead = var10.rotationYaw;
            var10.renderYawOffset = var10.rotationYaw;
            var10.setPlayerName(par7Player.getCommandSenderName());
            var10.targets = getTargets(par1);

            Map<Integer, ItemStack> upgMap = Maps.newHashMap();
            List<ItemStack> upgList = getUpgItems(par1);
	    	Class<? extends EntityTurret_Base> turretCls = TurretInfo.getTurretClass(par1.getItemDamage());
            for (ItemStack is : upgList) {
            	upgMap.put(TurretUpgrades.getUpgradeFromItem(is, turretCls).getUpgradeID(), is);
            }
            var10.upgrades = upgMap;

            String s = getCustomName(par1);
            if (s != null && s.length() > 0)
            	var10.setTurretName(s);
            else if (par1.hasTagCompound() && par1.getTagCompound().hasKey("display") && par1.getTagCompound().getCompoundTag("display").hasKey("Name"))
            	var10.setTurretName(par1.getTagCompound().getCompoundTag("display").getString("Name"));

            int freq = getFrequency(par1);
            var10.setFrequency(freq);

            par0World.spawnEntityInWorld(var10);
            var10.playLivingSound();

        }
        return var8;
    }

    public static Entity getTurretByDamage(World world, int dmg) {
    	Class<? extends EntityTurret_Base> cls = TurretInfo.getTurretClass(dmg);
    	try {
			return cls.getConstructor(World.class).newInstance(world);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    	for (int i = 0; i < TurretInfo.getTurretCount(); i++) {
			TurretInfo tinf = TurretInfo.getTurretInfo(TurretInfo.getTurretClass(i));
    		par3List.add(tinf.getTurretItem().copy());
    	}
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
    	if (this.hasEffect(par1ItemStack))
    		return "\247d" + getUnlocalizedName(par1ItemStack) + "\247r";
    	return getUnlocalizedName(par1ItemStack);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
    	if (getProgramTag(par1ItemStack).hasKey("progName"))
    		par3List.add("\247o" + String.format(StatCollector.translateToLocal("item.turretInfo.name"), getCustomName(par1ItemStack)) + "\247r");
    	if (getProgramTag(par1ItemStack).hasKey("progFreq"))
    		par3List.add("\247o" + String.format(StatCollector.translateToLocal("item.turretInfo.freq"), getFrequency(par1ItemStack)) + "\247r");
    	if (getProgramTag(par1ItemStack).hasKey("progTargets"))
    		par3List.add("\247o" + StatCollector.translateToLocal("item.turretInfo.target") + "\247r");
    	if (getProgramTag(par1ItemStack).hasKey("progUpgrades")) {
    		par3List.add("\247o" + StatCollector.translateToLocal("item.turretInfo.upd") + "\247r");
    		if (par2EntityPlayer.openContainer instanceof ContainerLaptopUpgrades) {
    	    	Class<? extends EntityTurret_Base> turretCls = TurretInfo.getTurretClass(par1ItemStack.getItemDamage());
				for (ItemStack is : getUpgItems(par1ItemStack)) {
					TurretUpgrades upgCls = TurretUpgrades.getUpgradeFromItem(is, turretCls);
					if (upgCls != null)
						par3List.add(" - " + upgCls.getName());
				}
    		}
    	}
    }

    private static NBTTagCompound getProgramTag(ItemStack is) {
    	NBTTagCompound isTag = is.hasTagCompound() ? is.getTagCompound() : new NBTTagCompound();
    	if (isTag.hasKey("tm3_program")) {
    		return (NBTTagCompound) isTag.getTag("tm3_program");
    	} else {
    		return new NBTTagCompound();
    	}
    }

    private static void setProgramTag(ItemStack is, NBTTagCompound nbt) {
    	if (!nbt.hasKey("progTargets") && !nbt.hasKey("progName") && !nbt.hasKey("progFreq") && !nbt.hasKey("progUpgrades") && is.getTagCompound() != null)
    		is.getTagCompound().removeTag("tm3_program");
    	else
    		is.setTagInfo("tm3_program", nbt);
    }

    public static void setTargets(ItemStack is, Map<String, Boolean> targets) {
    	NBTTagCompound nbt = getProgramTag(is);
        NBTTagList nbtList = new NBTTagList();

        for (String targetName : targets.keySet())
        {
            NBTTagCompound innerNBT = new NBTTagCompound();
            innerNBT.setString("TgName", targetName);
            innerNBT.setBoolean("TgActive", targets.get(targetName));
            nbtList.appendTag(innerNBT);
        }

        nbt.setTag("progTargets", nbtList);

        setProgramTag(is, nbt);
    }

    public static Map<String, Boolean> getTargets(ItemStack is) {
    	NBTTagCompound nbt = getProgramTag(is);
    	Map<String, Boolean> returningList = Maps.newHashMap();
    	if (nbt.hasKey("progTargets")) {
    		NBTTagList var1 = nbt.getTagList("progTargets", Constants.NBT.TAG_COMPOUND);

            for (int var2 = 0; var2 < var1.tagCount(); ++var2)
            {
                NBTTagCompound var3 = var1.getCompoundTagAt(var2);
                String var4 = var3.getString("TgName");
                boolean var5 = var3.getBoolean("TgActive");
                returningList.put(var4, var5);
            }
    	}

    	if (returningList.size() < 1) {
			List<String> entities = new ArrayList<String>(EntityList.classToStringMapping.values());
			List<String> stdTargets = trTargets.getTargetStrings();
			for (String entityName : entities) {
				returningList.put(entityName, stdTargets.contains(entityName));
			}
    	}

    	return returningList;
    }

    public static boolean isUpgradeValid(ItemStack turretItm, ItemStack upgItm, List<ItemStack> upgList) {
    	if (turretItm == null || upgItm == null)
    		return false;
    	if (turretItm.stackSize <= 0 || upgItm.stackSize <= 0)
    		return false;
    	Class<? extends EntityTurret_Base> turretCls = TurretInfo.getTurretClass(turretItm.getItemDamage());

		TurretUpgrades upg = TurretUpgrades.getUpgradeFromItem(upgItm, turretCls);
        return upg != null && upg.hasRequiredUpgrade(upgList) && !TurretUpgrades.hasUpgrade(upg.getClass(), upgList);

    }

    public static void addUpgItem(ItemStack turretItm, ItemStack upgItm) {
    	NBTTagCompound nbt = getProgramTag(turretItm);
    	NBTTagList nbtList = new NBTTagList();

    	List<ItemStack> preUpgList = getUpgItems(turretItm.copy());
    	List<ItemStack> newUpgList = new ArrayList<ItemStack>(preUpgList);

    	if (isUpgradeValid(turretItm, upgItm, preUpgList)) newUpgList.add(upgItm.copy());

    	for (ItemStack item : newUpgList) {
			NBTTagCompound innerNBT = new NBTTagCompound();
			item.writeToNBT(innerNBT);
			nbtList.appendTag(innerNBT);
    	}

    	nbt.setTag("progUpgrades", nbtList);

    	setProgramTag(turretItm, nbt);
    }

    public static void addCustmNameAndFreq(ItemStack turretItm, String name, int freq) {
    	NBTTagCompound nbt = getProgramTag(turretItm);

    	if (name.length() < 256 && name.length() > 0)
    		nbt.setString("progName", name);
    	else if (nbt.hasKey("progName"))
    		nbt.removeTag("progName");
    	if (freq < 256 && freq > 0)
    		nbt.setShort("progFreq", (short)freq);
    	else if (freq == 0 && nbt.hasKey("progFreq"))
    		nbt.removeTag("progFreq");

    	setProgramTag(turretItm, nbt);
    }

    public static String getCustomName(ItemStack turretItm) {
    	NBTTagCompound nbt = getProgramTag(turretItm);
    	if (nbt.hasKey("progName"))
    		return nbt.getString("progName");

    	return null;
    }

    public static int getFrequency(ItemStack turretItm) {
    	NBTTagCompound nbt = getProgramTag(turretItm);
    	if (nbt.hasKey("progFreq"))
    		return nbt.getShort("progFreq");

    	return 0;
    }

    public static List<ItemStack> getUpgItems(ItemStack turretItm) {
    	List<ItemStack> retList = new ArrayList<ItemStack>();

    	if (turretItm == null) return retList;

        NBTTagList upgList = getProgramTag(turretItm).getTagList("progUpgrades", Constants.NBT.TAG_COMPOUND);

        for (int ind = 0; ind < upgList.tagCount(); ++ind)
        {
            NBTTagCompound innerNBT = upgList.getCompoundTagAt(ind);
            retList.add(ItemStack.loadItemStackFromNBT(innerNBT));
        }

    	return retList;
    }

    @Override
    public int getItemStackLimit(ItemStack itemStack) {
    	return 4;
    }

    @Override
    public boolean getShareTag() {
    	return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
    	return par1ItemStack.getTagCompound() != null && par1ItemStack.getTagCompound().hasKey("tm3_program");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
    	this.turretIcons = new IIcon[TurretInfo.getTurretCount()];
    	for (int i = 0; i < TurretInfo.getTurretCount(); i++) {
    		TurretInfo tinf = TurretInfo.getTurretInfo(TurretInfo.getTurretClass(i));
    		this.turretIcons[i] = par1IconRegister.registerIcon(tinf != null ? tinf.getIconFile() : "TurretMod3:turret_01");
    	}
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return this.turretIcons != null ? this.turretIcons[par1] : null;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return TurretInfo.getTurretInfo(TurretInfo.getTurretClass(par1ItemStack.getItemDamage())).getTurretName();
	}
}
