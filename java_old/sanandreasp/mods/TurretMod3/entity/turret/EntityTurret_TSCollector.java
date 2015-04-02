package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.*;

import java.util.List;

public class EntityTurret_TSCollector extends EntityTurret_Base {

	private int xpCooldown = 0;
	private int itemCooldown = 0;

	public EntityTurret_TSCollector(World par1World) {
		super(par1World);
	    wdtRange = 	16.5F;
	    hgtRangeU = 16.5F;
	    hgtRangeD = 16.5F;
        setTextures("tsCollector");
	}

	@Override
	protected String getLivingSound() {
		return this.isActive() ? "turretmod3:idle.turretexp" : "";
	}

	@Override
	public void addExperience(int par1Xp) {
		if (this.xpCooldown == 0) super.addExperience(par1Xp);
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
    }

	@Override
	public int getMaxAmmo() {
		return 0;
	}

	@Override
	public int getExpCap() {
		return TurretUpgrades.hasUpgrade(TUpgExpStorageC.class, this.upgrades) ? 4096 : 512;
	}

	@Override
	protected void checkForEnemies() {
		;
	}

	@Override
	public TurretProjectile getProjectile() {
		return null;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		;
	}

	@Override
	public void tryToShoot(boolean isRidden) {
		;
	}

	@Override
	public boolean isTargetValid(EntityLivingBase entity) {
		return false;
	}

	@Override
	public boolean canCollectXP() {
		return true;
	}

	@Override
	public int getMaxShootTicks() {
		return 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.wdtRange < 64D && TurretUpgrades.hasUpgrade(TUpgRangeIncr.class, this.upgrades)) {
			this.wdtRange =
					this.hgtRangeD =
					this.hgtRangeU = 64.5D;
		}

		if (this.xpCooldown > 0) --this.xpCooldown;
		if (!this.worldObj.isRemote && this.isActive() && TM3ModRegistry.canCollectorGetXP && this.xpCooldown == 0) {
			List<EntityXPOrb> orbs = this.worldObj.getEntitiesWithinAABB(EntityXPOrb.class, AxisAlignedBB.getBoundingBox(
					this.posX - this.wdtRange, this.posY - this.wdtRange, this.posZ - this.wdtRange, this.posX + this.wdtRange, this.posY + this.wdtRange, this.posZ + this.wdtRange
				));

			for (EntityXPOrb orb : orbs) {
				if (orb != null && orb.getXpValue() > 0 && this.getExpCap() >= this.getExperience() + orb.getXpValue()) {
					this.addExperience(orb.getXpValue());
					this.xpCooldown = 2;
	                this.playSound("random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
					orb.setDead();
				}
				if (this.xpCooldown > 0) break;
			}
		}

		if (!this.worldObj.isRemote && this.isActive() && TurretUpgrades.hasUpgrade(TUpgTurretCollect.class, this.upgrades) && this.xpCooldown == 0) {
			List<EntityTurret_Base> turrets =  this.worldObj.getEntitiesWithinAABB(EntityTurret_Base.class, AxisAlignedBB.getBoundingBox(
					this.posX - this.wdtRange, this.posY - this.wdtRange, this.posZ - this.wdtRange, this.posX + this.wdtRange, this.posY + this.wdtRange, this.posZ + this.wdtRange
				));

			for (EntityTurret_Base turret : turrets) {
				if (turret.canCollectXP()
						&& !EntityTurret_TSCollector.class.isAssignableFrom(turret.getClass())
						&& turret.getExperience() > 0
						&& turret.getPlayerName().equals(this.getPlayerName())
						&& this.getExpCap() >= this.getExperience() + turret.getExperience()) {
					this.addExperience(turret.getExperience());
					this.xpCooldown = 2;
	                this.playSound("random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
					turret.remExperience();
				}
				if (this.xpCooldown > 0) break;
			}
		}

		if (this.itemCooldown > 0) --this.itemCooldown;
		if (!this.worldObj.isRemote && this.isActive() && itemCooldown == 0 && TurretUpgrades.hasUpgrade(TUpgItemCollect.class, this.upgrades)) {
			List<EntityItem> items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(
					this.posX - this.wdtRange, this.posY - this.wdtRange, this.posZ - this.wdtRange, this.posX + this.wdtRange, this.posY + this.wdtRange, this.posZ + this.wdtRange
				));
			IInventory[] chests = this.getAdjacendContainer();

			lblContainers:
			for (IInventory chest : chests) {
				for (EntityItem item : items) {
					if (item != null && item.getEntityData().hasKey("TM3_PlayerName") && item.getEntityData().getString("TM3_PlayerName").equals(this.getPlayerName())) {
						ItemStack is = item.getEntityItem();
						if (is != null && is.stackSize > 0) {
							ItemStack ret = this.addItemToContainer(chest, is);
							if (ret == null || ret.stackSize < 1) {
								this.itemCooldown = 2;
				                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				                item.setDead();
				                break lblContainers;
							} else if (ret.stackSize < is.stackSize) {
								this.itemCooldown = 2;
				                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				                item.setEntityItemStack(ret);
							}
						}
					}
				}
			}
		}
	}

	private ItemStack addItemToContainer(IInventory inv, ItemStack is) {
		if (inv == null || inv.getInventoryStackLimit() < 1) {
			return is;
		}
		for (int i = 0; i < inv.getSizeInventory() && is != null && is.stackSize > 0; i++) {
			ItemStack slot = inv.getStackInSlot(i);
			if (slot == null || slot.stackSize < 1) {
				if (inv.getInventoryStackLimit() >= is.stackSize) {
					inv.setInventorySlotContents(i, is.copy());
					return null;
				} else {
					slot = is.copy();
					is.stackSize -= slot.stackSize = inv.getInventoryStackLimit();
					inv.setInventorySlotContents(i, slot);
				}
			} else if (slot.isItemEqual(is)) {
				if (slot.stackSize + is.stackSize <= inv.getInventoryStackLimit() && slot.stackSize + is.stackSize <= is.getMaxStackSize()) {
					slot.stackSize += is.stackSize;
					inv.setInventorySlotContents(i, slot);
					return null;
				} else if (slot.stackSize < inv.getInventoryStackLimit() && slot.stackSize < slot.getMaxStackSize()) {
					is.stackSize -= Math.min(slot.getMaxStackSize(), inv.getInventoryStackLimit()) - slot.stackSize;
					slot.stackSize = Math.min(slot.getMaxStackSize(), inv.getInventoryStackLimit());
					inv.setInventorySlotContents(i, slot);
				}
			}
		}
		return is;
	}
}
