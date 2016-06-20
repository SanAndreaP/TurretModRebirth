package sanandreasp.mods.TurretMod3.entity.turret;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvUpgrades;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;
import sanandreasp.mods.turretmod3.entity.EntityMobileBase;
import sanandreasp.mods.turretmod3.entity.IHealable;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.packet.PacketSendUpgrades;
import sanandreasp.mods.turretmod3.registry.AchievementPageTM;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntityTurret_Base extends EntityLiving implements IHealable {
    protected EntityLivingBase currentTarget;
    private ResourceLocation texture, glowTexture;
    public double wdtRange = 16.5F;
    public double hgtRangeU = 5.5F;
    public double hgtRangeD = 5.5F;
    public boolean isEconomied = false;
    public Map<String, Boolean> targets = Maps.newHashMap();
    public Map<Integer, ItemStack> upgrades = Maps.newHashMap();
//    public int xpCap = 256;

    public final TurretInfo tInfo = TurretInfo.getTurretInfo(this.getClass());

    protected boolean renderedInGui = false;

	public EntityTurret_Base(World par1World) {
		super(par1World);
		this.setSize(0.3F, 1.8F);
	}

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(20, (short) 0); // Ammo
        this.dataWatcher.addObject(21,  0.0F); // Health
        this.dataWatcher.addObject(22, (short) 0); // Ammo type
        this.dataWatcher.addObject(23, (short) 0); // Experience
        this.dataWatcher.addObject(24, ""); // Player name
        this.dataWatcher.addObject(25, "Turret"); // Turret name
        this.dataWatcher.addObject(26, ""); // Target name
        /**
         * &1 == uniqueTargets
         * &2 == isActive
         **/
        this.dataWatcher.addObject(27, (byte) (0 | 1 | 2));   // boolean stuff

        this.dataWatcher.addObject(28, 0); // shootTicks
        this.dataWatcher.addObject(29, (byte)0); // frequency
    }

	public void addExperience(int par1Xp) {
		if (par1Xp < 0) {
			FMLLog.warning("XP value cannot be less than 0! XP value was %s", par1Xp);
		}
		if (par1Xp > this.getExpCap()) {
			FMLLog.warning("XP value cannot be greater than the current XP cap! XP cap is %s, XP value was %s", this.getExpCap(), par1Xp);
		}
		if (this.getExperience()+par1Xp <= this.getExpCap())
			this.dataWatcher.updateObject(23, (short)(this.getExperience() + par1Xp));
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return !(par1DamageSource.isFireDamage() && hasFireImmunity()) && super.attackEntityFrom(par1DamageSource, par2);

    }

	public boolean hasFireImmunity() {
		return TurretUpgrades.hasUpgrade(TUpgFireImmunity.class, this.upgrades);
	}

	public void setInGui() {
		this.renderedInGui = true;
	}

	public boolean isInGui() {
		return this.renderedInGui;
	}

	public boolean isActive() {
		return (this.dataWatcher.getWatchableObjectByte(27) & 2) == 2;
	}

	public void setActiveState(boolean b) {
		byte currByte = this.dataWatcher.getWatchableObjectByte(27);
		this.dataWatcher.updateObject(27, (byte)(b ? currByte | 2 : currByte & ~2));
	}

    public void setTextures(String name){
        texture = new ResourceLocation(TM3ModRegistry.TEX_TURRETDIR + name +".png");
        glowTexture = new ResourceLocation(TM3ModRegistry.TEX_TURRETDIR + name +"G.png");
    }

    public ResourceLocation getTexture(){
        return texture;
    }

    public ResourceLocation getGlowTexture() {
        return glowTexture;
    }

	@Override
    protected boolean canDespawn() {
    	return false;
    }

	protected void checkForEnemies() {
		if (this.currentTarget == null && !this.worldObj.isRemote) {
			List<Entity> rangedEntities = getRangedEntities();
			for (Entity entity : rangedEntities) {
				if (entity != null && entity instanceof EntityLivingBase && isTargetValid((EntityLivingBase)entity)) {
					this.currentTarget = (EntityLivingBase) entity;
					break;
				}
			}
		}
		if (this.currentTarget != null && !this.worldObj.isRemote) {
			if (!isTargetValid(this.currentTarget))
				this.currentTarget = null;
		}
	}

	@Override
	protected void collideWithEntity(Entity par1Entity) {
		if (!(par1Entity instanceof EntityMobileBase)) super.collideWithEntity(par1Entity);
	}

	protected void decrAmmo() {
		this.isEconomied = !this.isEconomied && TurretUpgrades.hasUpgrade(TUpgEconomy.class, this.upgrades);
		if (!isEconomied) {
			if (!TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, this.upgrades))
				this.dataWatcher.updateObject(20, (short) Math.max(0, this.getAmmo()-1));
		}
	}

	protected void decrShootTicks() {
		this.dataWatcher.updateObject(28, this.getShootTicks()-1);
	}

	public int getFrequency() {
		return this.dataWatcher.getWatchableObjectByte(29) & 255;
	}

	public void setFrequency(int freq) {
		if (freq < 0 || freq > 255) return;
		this.dataWatcher.updateObject(29, (byte)freq);
	}

	public void dismantle() {
		EntityDismantleStorage stg = new EntityDismantleStorage(this.worldObj, this.tInfo.getInfoID());
		stg.setPosition(this.posX, this.posY, this.posZ);

		ItemStack turretItem = this.tInfo.getTurretItem().copy();
		turretItem.stackSize = 1;
		stg.inventory.addItemStackToInventory(turretItem);

		for (ItemStack is : this.upgrades.values()) {
			if (is != null) stg.inventory.addItemStackToInventory(is.copy());
		}

		ItemStack is = this.tInfo.getAmmoTypeItemWithLowestScore(this.getAmmoType());
		if (is != null) {
			ItemStack is1 = is.copy();
			is1.stackSize = MathHelper.floor_double((double)this.getAmmo() / (double)this.tInfo.getAmmoFromItem(is1));
			if (is1.getItemDamage() == OreDictionary.WILDCARD_VALUE) is1.setItemDamage(0);
			ItemStack[] splitIS = this.getGoodItemStacks(is1);
			for (ItemStack is2 : splitIS) {
				stg.inventory.addItemStackToInventory(is2);
			}
		}

		attackEntityFrom(DamageSource.magic, Math.max(this.getHealth(), this.getMaxHealth()));

		this.worldObj.spawnEntityInWorld(stg);
	}

    //TODO: find what this is supposed to do
    private ItemStack[] getGoodItemStacks(ItemStack is1) {
        return is1.stackSize>0 ? new ItemStack[]{is1} : new ItemStack[0];
    }

    public void faceEntity(Entity par1Entity, float par2, float par3)
    {
		if (par1Entity == null) return;
        double var4 = par1Entity.posX - this.posX;
        double var8 = par1Entity.posZ - this.posZ;
        double var6;

        if (par1Entity instanceof EntityLivingBase)
        {
            EntityLivingBase var10 = (EntityLivingBase)par1Entity;
            var6 = this.posY + (double)this.getEyeHeight() - (var10.posY + (double)var10.getEyeHeight());
        }
        else
        {
            var6 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double var14 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));
        this.rotationPitch = -this.updateRotation(this.rotationPitch, var13, par3, false);
        this.rotationYawHead = this.updateRotation(this.rotationYawHead, var12, par2, true);
    }

	protected TileEntityChest getAdjacendChest(TileEntityChest par1Chest) {
    	if (par1Chest == null) return null;
		return	par1Chest.adjacentChestXNeg != null ? par1Chest.adjacentChestXNeg :
				par1Chest.adjacentChestXPos != null ? par1Chest.adjacentChestXPos :
				par1Chest.adjacentChestZNeg != null ? par1Chest.adjacentChestZNeg :
				par1Chest.adjacentChestZPos;
    }

	protected IInventory[] getAdjacendContainer() {
    	List<IInventory> inventories = new ArrayList<IInventory>();
    	int x = MathHelper.floor_double(this.posX);
    	int y = MathHelper.floor_double(this.posY);
    	int z = MathHelper.floor_double(this.posZ);
    	TileEntity te = this.worldObj.getTileEntity(x, y, z);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	te = this.worldObj.getTileEntity(x + 1, y, z);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	te = this.worldObj.getTileEntity(x, y, z + 1);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	te = this.worldObj.getTileEntity(x - 1, y, z);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	te = this.worldObj.getTileEntity(x, y, z - 1);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	te = this.worldObj.getTileEntity(x, y - 1, z);
    	if (te != null && te instanceof IInventory) inventories.add((IInventory) te);
    	int prevInd = inventories.size();
    	for (int i = 0; i < prevInd; i++) {
    		IInventory inv = inventories.get(i);
    		if (inv instanceof TileEntityChest) {
    			TileEntityChest adjChest = getAdjacendChest((TileEntityChest)inv);
    			if (adjChest != null) {
    				inventories.add(adjChest);
    			}
    		}
    	}
    	return inventories.toArray(new IInventory[inventories.size()]);
    }

	public int getAmmo() {
		return this.dataWatcher.getWatchableObjectShort(20);
	}

	public int getAmmoType() {
		return this.dataWatcher.getWatchableObjectShort(22);
	}

	public EntityLivingBase getCurrentTarget() {
		return this.currentTarget;
	}

	public void resetCurrentTarget() {
		this.currentTarget = null;
	}

	public String getCurrentTargetStr() {
		return this.dataWatcher.getWatchableObjectString(26);
	}

	@Override
    protected String getDeathSound() {
    	return "turretmod3:hit.turretDeath";
    }

	public int getExpCap() {
		return this.tInfo.getMaxXP() * (TurretUpgrades.hasUpgrade(TUpgExpStorage.class, this.upgrades) ? 8 : 1);
	}

	public int getExperience() {
		return this.dataWatcher.getWatchableObjectShort(23);
	}

	@Override
	protected String getHurtSound() {
		return "turretmod3:hit.turrethit";
	}

    @Override
	protected String getLivingSound() {
    	return this.rand.nextInt(10) == 0 && this.isActive() ? "turretmod3:idle.turretidle" : null;
    }

	public int getMaxAmmo() {
		return 256;
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }

	public abstract int getMaxShootTicks();

	@Override
	public double getMountedYOffset() {
		return super.getMountedYOffset() - 1D;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(this.tInfo.getTurretItem().getItem(), 1, this.tInfo.getTurretItem().getItemDamage());
	}

	public String getPlayerName() {
		return this.dataWatcher.getWatchableObjectString(24);
	}

	public abstract TurretProjectile getProjectile();

	@SuppressWarnings("unchecked")
    protected List<Entity> getRangedEntities() {
		return this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(
				this.posX - this.wdtRange, this.posY - this.hgtRangeD, this.posZ - this.wdtRange, this.posX + this.wdtRange, this.posY + this.hgtRangeU, this.posZ + this.wdtRange));
	}

	@SuppressWarnings("unchecked")
    protected List<Entity> getRangedTurrets() {
		return this.worldObj.getEntitiesWithinAABB(this.getClass(), AxisAlignedBB.getBoundingBox(
				this.posX - this.wdtRange, this.posY - this.hgtRangeD, this.posZ - this.wdtRange, this.posX + this.wdtRange, this.posY + this.hgtRangeU, this.posZ + this.wdtRange));
	}

	public String getShootSound() {
		return "random.bow";
	}

	public int getShootTicks() {
		return this.dataWatcher.getWatchableObjectInt(28);
	}

	public float getSrvHealth() {
		return Math.max(this.dataWatcher.getWatchableObjectFloat(21), 0);
	}

	public String getTurretName() {
		return this.dataWatcher.getWatchableObjectString(25);
	}

	protected void grabContentFromChests() {
    	IInventory[] containers = getAdjacendContainer();

    	boolean playSound = false;

    	lblChest:
    	for (IInventory chest : containers) {
    		if (chest != null) {
    			for (int i = 0; i < chest.getSizeInventory() && (this.getAmmo() < this.getMaxAmmo() || this.getSrvHealth() < this.getMaxHealth()); i++) {
    				ItemStack cstSlot = chest.getStackInSlot(i);
    				if (cstSlot != null) {
    					if (tInfo.getHealthFromItem(cstSlot) > 0 && this.getHealth()+tInfo.getHealthFromItem(cstSlot) <= this.getMaxHealth()) {
    						this.heal(tInfo.getHealthFromItem(cstSlot));
    						chest.decrStackSize(i, 1);
    						playSound = true;
    						break lblChest;
    					} else if (tInfo.getAmmoFromItem(cstSlot) > 0 && this.getAmmo() < this.getMaxAmmo() && tInfo.getAmmoTypeFromItem(cstSlot) == this.getAmmoType()
    					  && !(this.getAmmo() > 0 && TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, this.upgrades))) {
    						int needed = this.getMaxAmmo() - this.getAmmo();
    						if (needed >= tInfo.getAmmoFromItem(cstSlot)) {
	    						if (cstSlot.stackSize * tInfo.getAmmoFromItem(cstSlot) > needed) {
	    							int decrStackSizeCnt = MathHelper.floor_double((double)needed / (double)tInfo.getAmmoFromItem(cstSlot));
	    							this.incrAmmo(tInfo.getAmmoFromItem(cstSlot) * decrStackSizeCnt);
	        						chest.decrStackSize(i, decrStackSizeCnt);
	        						playSound = true;
	    							break lblChest;
	    						} else {
	    							this.incrAmmo(cstSlot.stackSize * tInfo.getAmmoFromItem(cstSlot));
	        						chest.decrStackSize(i, cstSlot.stackSize);
	        						playSound = true;
	    							break lblChest;
	    						}
    						}
    					}
    				}
    			}
    		}
    	}

    	if (playSound) this.worldObj.playSoundAtEntity(this, "turretmod3:collect.chest", 1.0F, 1.0F);
    }

	public boolean hasPlayerAccess(EntityPlayer player) {
        return player != null && (this.getPlayerName().equals(player.getCommandSenderName()) || TM3ModRegistry.proxy.getPlayerTM3Data(player).getBoolean("isOP"));

    }

	public int incrAmmo(int count) {
		int remain = (this.getAmmo() + count) - this.getMaxAmmo();
		this.dataWatcher.updateObject(20, (short) Math.min(this.getMaxAmmo(), this.getAmmo() + count));
		return Math.max(0, remain);
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {
		ItemStack held = par1EntityPlayer.getHeldItem();
		if (held != null) {
			if (tInfo.getHealthFromItem(held) > 0 && this.getHealth()+tInfo.getHealthFromItem(held) <= this.getMaxHealth()) {
				this.heal(tInfo.getHealthFromItem(held));
				par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, 1);
				this.playSound("turretmod3:collect.ia_get", 1.0F, 1.0F);
				return true;
			} else if (tInfo.getAmmoFromItem(held) > 0 && this.getAmmo() < this.getMaxAmmo() && tInfo.getAmmoTypeFromItem(held) == this.getAmmoType()) {
				int needed = this.getMaxAmmo() - this.getAmmo();
				if (needed >= tInfo.getAmmoFromItem(held)) {
					if (held.stackSize * tInfo.getAmmoFromItem(held) > needed) {
						int decrStackSizeCnt = MathHelper.ceiling_double_int((double)needed / (double)tInfo.getAmmoFromItem(held));
						this.incrAmmo(needed);
						par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, decrStackSizeCnt);
					} else {
						this.incrAmmo(held.stackSize * tInfo.getAmmoFromItem(held));
						par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, held.stackSize);
					}
                    this.playSound("turretmod3:collect.ia_get", 1.0F, 1.0F);
                    return true;
				}
			} else if (tInfo.getAmmoFromItem(held) > 0 && tInfo.getAmmoTypeFromItem(held) != this.getAmmoType() && this.getPlayerName().equals(par1EntityPlayer.getCommandSenderName())) {
				ItemStack is = this.tInfo.getAmmoTypeItemWithLowestScore(this.getAmmoType()).copy();
				is.stackSize = MathHelper.floor_double((double)this.getAmmo() / (double)this.tInfo.getAmmoFromItem(is));
				if (is.getItemDamage() < 0) is.setItemDamage(0);
				ItemStack[] splitIS = this.getGoodItemStacks(is);
				boolean b = true;
				for (ItemStack is1 : splitIS) {
					b = b && par1EntityPlayer.inventory.addItemStackToInventory(is1);
				}
				if (b) {
					int ammo = this.tInfo.getAmmoFromItem(held);
					int needed = Math.min(this.getMaxAmmo() / ammo, held.stackSize);
					this.setAmmo(needed * ammo);
					this.setAmmoType(tInfo.getAmmoTypeFromItem(held));
					par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, needed);
					this.playSound("turretmod3:collect.ia_get", 1.0F, 1.0F);
					return true;
				}
			} else if (held.getItem() == TM3ModRegistry.tcu) {
				if (		(this.riddenByEntity != null
							&& this.riddenByEntity == par1EntityPlayer
							|| this.riddenByEntity == null && this.getPlayerName().equals(par1EntityPlayer.getCommandSenderName())
							&& par1EntityPlayer.isSneaking())
						&& TurretUpgrades.hasUpgrade(TUpgControl.class, this.upgrades)) {
					if (!worldObj.isRemote)
						par1EntityPlayer.mountEntity(this);
				} else {
					par1EntityPlayer.openGui(TM3ModRegistry.instance, 0, this.worldObj, this.getEntityId(), 0, 0);
				}
				return true;
			} else {
				int newUpgId = -1;
				upgCounter:
				for (int i = 0; i < TurretUpgrades.getUpgradeCount(); i++) {
					TurretUpgrades upg = TurretUpgrades.getUpgradeFromID(i);
					boolean isTurretInList = false;
					for (Class tCls : upg.getTurrets()) {
						if (tCls.isAssignableFrom(this.getClass())) {
							isTurretInList = true;
							break;
						}
					}
					if (!isTurretInList) {
						continue;
					} else if (!this.upgrades.containsKey(i) || this.upgrades.get(i) == null) {
						if (TM3ModRegistry.areStacksEqualWithWildcard(upg.getItem(), held)) {
							if (upg.getEnchantment() != null) {
								NBTTagList ench = held.getEnchantmentTagList();
						        for (int j = 0; ench != null && j < ench.tagCount(); ++j)
						        {
						        	NBTTagCompound var4 = ench.getCompoundTagAt(j);
						        	if (var4.getShort("id") == upg.getEnchantment().effectId) {
						        		newUpgId = i;
						        		break upgCounter;
						        	}
						        }
						        break;
							} else {
								newUpgId = i;
								break;
							}
						}
					} else if (this.upgrades.containsKey(i)
							&& TM3ModRegistry.areStacksEqualWithWildcard(this.upgrades.get(i), held)) {
						break;
					}
				}
				if (newUpgId >= 0 && TurretUpgrades.getUpgradeFromID(newUpgId).hasRequiredUpgrade(this.upgrades)) {
					ItemStack newUpgItem = held.copy();
					newUpgItem.stackSize = 1;
					this.upgrades.put(newUpgId, newUpgItem);
					par1EntityPlayer.inventory.decrStackSize(par1EntityPlayer.inventory.currentItem, 1);
					par1EntityPlayer.triggerAchievement(AchievementPageTM.upgrade);
					this.playSound("turretmod3:collect.ia_get", 1.0F, 1.0F);
					if (!this.worldObj.isRemote) PacketRecvUpgrades.send(this);
					return true;
				}
			}
		} else if (this.riddenByEntity != null && this.riddenByEntity == par1EntityPlayer) {
			par1EntityPlayer.mountEntity(this);
		}
		return false;
	}

	protected boolean isEntityTargeted(Entity entity) {
		if (!this.useUniqueTargets())
			return false;

        List<Entity> turrets = getRangedTurrets();
        boolean b = false;
        for (Entity turret : turrets) {
        	if (turret instanceof EntityTurret_Base && turret != this) {
        		b = ((EntityTurret_Base)turret).getCurrentTarget() == entity;
        		if (b) break;
        	}
        }
        return b;
	}

	public boolean isTargetValid(EntityLivingBase entity) {
		return this.isTargetValid(entity, this.wdtRange, this.hgtRangeU, this.hgtRangeD, false);
	}

	public boolean isTargetValid(EntityLivingBase entity, double wdtRng, double hgtURng, double hgtDRng, boolean seeThrough) {
        String entityStr = (String) EntityList.classToStringMapping.get(entity.getClass());
        boolean inList = this.targets.containsKey(entityStr) && this.targets.get(entityStr) && !isEntityTargeted(entity);

        return !(entity.isDead || entity.getHealth() <= 0
        		|| (!Double.isInfinite(wdtRng) && entity.getDistanceSqToEntity(this) > (wdtRng * wdtRng))
        		|| !(this.canEntityBeSeen(entity) || seeThrough)
        		|| (!Double.isInfinite(hgtDRng) && this.posY - entity.posY > hgtDRng)
        		|| (!Double.isInfinite(hgtURng) && entity.posY - this.posY > hgtURng)) && inList;
	}

	@Override
	public void knockBack(Entity par1Entity, float par2, double par3, double par5) { }

	@Override
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.riddenByEntity != null) {
        	this.riddenByEntity.mountEntity(this);
        }

        if (this.deathTime > 0)
        {
            int var1;

            if (!this.worldObj.isRemote) {
	            var1 = this.getExperience();

	            while (var1 > 0)
	            {
	                int var2 = EntityXPOrb.getXPSplit(var1);
	                var1 -= var2;
	                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
	            }
            }

            this.setDead();

            for (var1 = 0; var1 < 20 && this.worldObj.isRemote; ++var1)
            {
            	for (Object obj : this.tInfo.getCrafting()) {
            		if (obj != null && obj instanceof ItemStack) {
            			String s;
            			ItemStack is = ((ItemStack)obj);
            			if (is.getItem() instanceof ItemBlock) {
            				s = "tilecrack_"+Item.getIdFromItem(is.getItem())+"_"+is.getItemDamage();
            			} else {
                			s = "iconcrack_"+ Item.getIdFromItem(is.getItem());
            			}
            			this.worldObj.spawnParticle(s, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0D, 0D, 0D);
            		}
            	}
            	this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0D, 0D, 0D);
            }
        }
    }

	public boolean canCollectXP() {
		return TurretUpgrades.hasUpgrade(TUpgExperience.class, this.upgrades);
	}

	@Override
    public void onKillEntity(EntityLivingBase par1EntityLiving) {
    	super.onKillEntity(par1EntityLiving);
    	if (this.canCollectXP() && par1EntityLiving instanceof EntityLiving) {
    		this.addExperience((Integer)ObfuscationReflectionHelper.getPrivateValue(EntityLiving.class, (EntityLiving) par1EntityLiving, "experienceValue", "field_70728_aV"));
    	}
    	EntityPlayer player = this.worldObj.getPlayerEntityByName(this.getPlayerName());
    	if (player != null) {
    		if (this.riddenByEntity == null)
    			player.triggerAchievement(AchievementPageTM.firstStrike);
    		else if (this.riddenByEntity == player)
    			player.triggerAchievement(AchievementPageTM.camKill);
    	}
    }

	@Override
	public void onLivingUpdate() {
        if (this.newPosRotationIncrements > 0) {
            double var1 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double var3 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double var5 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }


        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isClientWorld())
        {
            this.worldObj.theProfiler.startSection("oldAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }

        this.worldObj.theProfiler.endSection();

        if (this.worldObj.isRemote && this.ticksExisted == 5) {
        	PacketSendUpgrades.send(this);
        }

        if (this.isActive() && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) && (this.getCurrentTargetStr() == null || this.getCurrentTargetStr().length() < 1))
        {
            this.rotationYawHead += 1F;
            this.rotationPitch = 0F;
        }
	}

	@Override
	public void onUpdate() {
		this.motionY -= 0.05;
		Block belowBlock = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY)-1, MathHelper.floor_double(this.posZ));
		boolean intercept = false;
		if (belowBlock != null) {
			AxisAlignedBB aabb = belowBlock.getCollisionBoundingBoxFromPool(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY)-1, MathHelper.floor_double(this.posZ));
			intercept = aabb != null && this.boundingBox.intersectsWith(aabb);
		}
		if (!intercept) {
			moveEntity(0F, this.motionY, 0F);
		} else {
			moveEntity(0F, 0F, 0F);
			this.motionY = 0F;
		}

		if (!this.isActive()) {
			if (this.riddenByEntity != null)
				this.riddenByEntity.mountEntity(null);
			if (this.rotationPitch < 25) this.rotationPitch += 0.5F;
			this.prevRotationYawHead = this.rotationYawHead;
			super.onUpdate();
			return;
		}

		if (this.getShootTicks() > 0) this.decrShootTicks();

		EntityPlayer riddenPlayer = (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) ? (EntityPlayer) this.riddenByEntity : null;
		if (riddenPlayer != null) {
			this.prevRotationPitch = this.rotationPitch = riddenPlayer.rotationPitch;
		}

		super.onUpdate();

		if (riddenPlayer != null) {
			this.prevRotationPitch = this.rotationPitch = riddenPlayer.rotationPitch;
			this.rotationYawHead = riddenPlayer.rotationYawHead;
			if (!this.worldObj.isRemote) this.dataWatcher.updateObject(26, "");
		} else {
			checkForEnemies();

			if (!this.worldObj.isRemote && this.currentTarget != null) {
				this.dataWatcher.updateObject(26, EntityList.getEntityString(this.currentTarget));
			} else if (!this.worldObj.isRemote) {
				this.dataWatcher.updateObject(26, "");
			}

			tryToShoot(false);

			rotationYaw = prevRotationYaw = renderYawOffset = prevRenderYawOffset = 0F;
		}

		if (!this.worldObj.isRemote) {
			if (this.ticksExisted % 5 == 0 && TurretUpgrades.hasUpgrade(TUpgChestGrabbing.class, this.upgrades))
				this.grabContentFromChests();
			this.dataWatcher.updateObject(21, this.getHealth());
		}

		if (this.ridingEntity == null) {
			if (this.posX > Math.floor(posX) + 0.501F || this.posX < Math.floor(posX) + 0.499F) {
				this.posX = Math.floor(posX) + 0.5;
				this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
			}
			if (this.posZ > Math.floor(posZ) + 0.501F || this.posZ < Math.floor(posZ) + 0.499F) {
				this.posZ = Math.floor(posZ) + 0.5;
				this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
			}
		}
	}

	@Override
    public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
    	super.readEntityFromNBT(par1nbtTagCompound);

        NBTTagList var1 = par1nbtTagCompound.getTagList("TurretTargets", Constants.NBT.TAG_COMPOUND);

        for (int var2 = 0; var2 < var1.tagCount(); ++var2)
        {
            NBTTagCompound var3 = var1.getCompoundTagAt(var2);
            String var4 = var3.getString("TgName");
            boolean var5 = var3.getBoolean("TgActive");
            this.targets.put(var4, var5);
        }

        NBTTagList var2 = par1nbtTagCompound.getTagList("TurretUpgrades", Constants.NBT.TAG_COMPOUND);

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
        	NBTTagCompound var4 = var2.getCompoundTagAt(var3);
        	int var5 = var4.getInteger("UgID");
        	ItemStack var6 = ItemStack.loadItemStackFromNBT(var4);
        	this.upgrades.put(var5, var6);
        }

    	this.setAmmo(par1nbtTagCompound.getInteger("ammoCount"));
    	this.setAmmoType(par1nbtTagCompound.getInteger("ammoType"));
    	this.dataWatcher.updateObject(24, par1nbtTagCompound.getString("userName"));
    	this.setTurretName(par1nbtTagCompound.getString("turretName"));
    	this.addExperience(par1nbtTagCompound.getInteger("turretExp"));
    	this.dataWatcher.updateObject(27, par1nbtTagCompound.getByte("booleanStuff"));
    	this.setShootTicks(par1nbtTagCompound.getInteger("shootTicks"));
    	this.isEconomied = par1nbtTagCompound.getBoolean("isEconomied");
    	this.setFrequency(par1nbtTagCompound.hasKey("frequency") ? par1nbtTagCompound.getInteger("frequency") : 0);

		if (!this.worldObj.isRemote) PacketRecvUpgrades.send(this);
    }

	public void remExperience() {
		this.dataWatcher.updateObject(23, (short) 0);
	}

	protected void setAmmo(int count) {
		this.dataWatcher.updateObject(20, (short) count);
	}

	protected void setAmmoType(int type) {
		this.dataWatcher.updateObject(22, (short) type);
	}

    public void setPlayerName(String par1Player) {
		for (Object obj : this.worldObj.playerEntities) {
			EntityPlayer player = (EntityPlayer)obj;
			if (player.getCommandSenderName().equals(par1Player)) {
				this.dataWatcher.updateObject(24, par1Player);
				return;
			}
		}
		FMLLog.warning("Player '%s' does not exist!", par1Player);
	}

    protected void setShootTicks(int st) {
		this.dataWatcher.updateObject(28, st);
	}

    public void setTurretName(String par1Name) {
    	if (par1Name.length() < 64)
    		this.dataWatcher.updateObject(25, par1Name);
	}

    public void setUniqueTargets(boolean b) {
		byte bt = this.dataWatcher.getWatchableObjectByte(27);
		bt = (byte) (b ? (bt | 1) : bt & ~1);
		this.dataWatcher.updateObject(27, bt);
	}

    public void shootProjectile(boolean isRidden) {
        TurretProjectile var2 = this.getProjectile();
        var2.isPickupable = !TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, this.upgrades) && !this.isEconomied;
        var2.ammoType = this.getAmmoType();
        if (isRidden) {
        	EntityPlayer player = (EntityPlayer) this.riddenByEntity;
        	var2.hasNoTarget = var2.isEndermanDamageable = true;
        	var2.setLocationAndAngles(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, player.rotationYaw, player.rotationPitch);
        	var2.posX -= (double)(MathHelper.cos(var2.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        	var2.posY -= 0.10000000149011612D;
        	var2.posZ -= (double)(MathHelper.sin(var2.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        	var2.setPosition(var2.posX, var2.posY, var2.posZ);
        	var2.yOffset = 0.0F;
        	var2.motionX = (double)(-MathHelper.sin(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI));
        	var2.motionZ = (double)(MathHelper.cos(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI));
        	var2.motionY = (double)(-MathHelper.sin(var2.rotationPitch / 180.0F * (float)Math.PI));
        	var2.setHeading(var2.motionX, var2.motionY, var2.motionZ, 1F * 1.5F, 1.0F);
        	var2.shootingEntity = this;
        } else {
        	var2.setTarget(this, this.currentTarget, 1.4F, 0.0F);
        }
        var2.isEndermanDamageable = TurretUpgrades.hasUpgrade(TUpgEnderHitting.class, this.upgrades);
        if(this.getShootSound()!=null)
            this.playSound(this.getShootSound(), this.getShootSoundRng(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(var2);
        var2.isMoving = true;
	}

    @Override
	public boolean shouldRiderFaceForward(EntityPlayer player) {
		return false;
	}

    public float getShootSoundRng() {
    	return 1.5F;
    }

    public void tryToShoot(boolean isRidden) {
		if ((this.currentTarget != null || isRidden) && !this.worldObj.isRemote && this.getHealth() > 0) {
			if (this.getShootTicks() <= 0) {
				if (this.getAmmo() > 0) {
					this.shootProjectile(isRidden);
			        this.decrAmmo();
				} else {
					this.playSound("random.click", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
				}
				this.setShootTicks(this.getMaxShootTicks());
			}
		}
	}

    @Override
    protected void updateEntityActionState()
    {
        ++this.entityAge;
        this.despawnEntity();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;

        if (!this.isActive())
        	return;

        if (this.currentTarget != null)
        {
            this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());
        }
        else if (this.worldObj.isRemote && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer))
        {
            this.rotationYawHead += 1F;
            this.rotationPitch = 0F;
        }
    }

    @Override
    public void updateRidden() {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
            }
        }

        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0F;
        this.fallDistance = 0.0F;
    }

    @Override
	public void updateRiderPosition() {
		double x = this.posX + 0.5 * (Math.sin(this.rotationYawHead / 180.0F * (float)Math.PI));
		double z = this.posZ + 0.5 * -(Math.cos(this.rotationYawHead / 180.0F * (float)Math.PI));

        this.riddenByEntity.setPosition(x, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), z);
        if (this.riddenByEntity.isEntityInsideOpaqueBlock())
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
	}

    protected float updateRotation(float par1, float par2, float par3, boolean par4)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
        return par1 + var4;
    }

    public boolean useUniqueTargets() {
		return (this.dataWatcher.getWatchableObjectByte(27) & 1) == 1;
	}

    @Override
    public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
    	super.writeEntityToNBT(par1nbtTagCompound);

        NBTTagList var1 = new NBTTagList();

        for (String targetName : this.targets.keySet())
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setString("TgName", targetName);
            var4.setBoolean("TgActive", this.targets.get(targetName));
            var1.appendTag(var4);
        }

        par1nbtTagCompound.setTag("TurretTargets", var1);

        NBTTagList var2 = new NBTTagList();

        for (int upgradeID : this.upgrades.keySet())
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setInteger("UgID", upgradeID);
            this.upgrades.get(upgradeID).writeToNBT(var4);
            var2.appendTag(var4);
        }

        par1nbtTagCompound.setTag("TurretUpgrades", var2);

    	par1nbtTagCompound.setInteger("ammoCount", this.getAmmo());
    	par1nbtTagCompound.setInteger("ammoType", this.getAmmoType());
    	par1nbtTagCompound.setString("userName", this.getPlayerName());
    	par1nbtTagCompound.setString("turretName", this.getTurretName());
    	par1nbtTagCompound.setInteger("turretExp", this.getExperience());
    	par1nbtTagCompound.setByte("booleanStuff", this.dataWatcher.getWatchableObjectByte(27));
    	par1nbtTagCompound.setInteger("shootTicks", this.getShootTicks());
    	par1nbtTagCompound.setInteger("frequency", this.getFrequency());
    	par1nbtTagCompound.setBoolean("isEconomied", this.isEconomied);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
    	return false;
    }
}
