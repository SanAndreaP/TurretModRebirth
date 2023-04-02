package sanandreasp.mods.TurretMod3.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.inventory.InventoryDismantleStorage;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityDismantleStorage extends EntityLivingBase {

	public InventoryDismantleStorage inventory;
	public Class<? extends EntityTurret_Base> tbClass = null;
	public Object tbRender = null;
	public boolean checkForDestroy = false;

	public EntityDismantleStorage(World par1World) {
		super(par1World);
		this.setSize(0.65F, 0.8F);
		this.dataWatcher.addObject(20, 0);
		this.inventory = new InventoryDismantleStorage("gui.invDismStorage", 27, this);
	}

	public EntityDismantleStorage(World par1World, int i) {
		this(par1World);
		this.dataWatcher.updateObject(20, i);
	}

	public void toggleCheckForDestroy() {
		this.checkForDestroy = !this.checkForDestroy;
	}

	protected boolean isMovementBlocked() {
		return true;
	};

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.tbClass == null) {
			this.tbClass = TurretInfo.getTurretClass(this.dataWatcher.getWatchableObjectInt(20));
		}

		if (this.checkForDestroy) {
			List<Boolean> b = new ArrayList<Boolean>();
			for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
				ItemStack is = this.inventory.getStackInSlot(i);
				b.add(is != null && is.stackSize > 0);
			}
			if (!b.contains(true)) {
				this.attackEntityFrom(DamageSource.magic, Math.max(this.getHealth(), this.getMaxHealth()));
			}
		}
	}

	@Override
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime > 5)
        {
            int var1;

            this.setDead();

            for (var1 = 0; var1 < 20 && this.worldObj.isRemote; ++var1)
            {
            	TurretInfo tInfo = TurretInfo.getTurretInfo(this.tbClass);
            	for (Object obj : tInfo.getCrafting()) {
            		if (obj != null && obj instanceof ItemStack) {
            			String s = "";
            			ItemStack is = ((ItemStack)obj);
            			if (is.getItem() instanceof ItemBlock) {
            				s = "tilecrack_"+ Item.getIdFromItem(is.getItem())+"_"+is.getItemDamage();
            			} else {
                			s = "iconcrack_"+Item.getIdFromItem(is.getItem());
            			}
            			this.worldObj.spawnParticle(s, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0D, 0D, 0D);
            		}
            	}
            	this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0D, 0D, 0D);
            }

            for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            	ItemStack is = this.inventory.getStackInSlot(i);
            	if (is != null) {
            		this.entityDropItem(is, 0.0F).setVelocity(0F, 0.2F, 0F);
            	}
            }
        }
    }

	@Override
	protected String getDeathSound() {
		return "dig.stone";
	}

	@Override
	public boolean interactFirst(EntityPlayer par1EntityPlayer) {
		if (!worldObj.isRemote)
            par1EntityPlayer.openGui(TM3ModRegistry.instance, 2, this.worldObj, this.getEntityId(), 0, 0);
		return true;
	}

	@Override
	public void moveEntity(double par1, double par3, double par5) {
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1) {

		this.dataWatcher.updateObject(20, par1.getInteger("turretCls"));

        NBTTagList var1 = par1.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

        for (int var2 = 0; var2 < var1.tagCount(); ++var2)
        {
            NBTTagCompound var3 = var1.getCompoundTagAt(var2);
            int slot = var3.getInteger("slot");
            ItemStack is = ItemStack.loadItemStackFromNBT(var3);
            this.inventory.setInventorySlotContents(slot, is);
        }
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1) {

		par1.setInteger("turretCls", this.dataWatcher.getWatchableObjectInt(20));

        NBTTagList var1 = new NBTTagList();

        for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
        	NBTTagCompound var4 = new NBTTagCompound();
        	ItemStack is = this.inventory.getStackInSlot(i);
        	if (is != null) {
        		var4.setInteger("slot", i);
        		is.writeToNBT(var4);
        		var1.appendTag(var4);
        	}
        }

        par1.setTag("Inventory", var1);
	}

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1.0D);
    }

    @Override
    public ItemStack getHeldItem()
    {
        return this.inventory.getStackInSlot(0);
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack itemStack) {
        this.inventory.setInventorySlotContents(slot, itemStack);
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot)
    {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack[] getLastActiveItems()
    {
        return this.inventory.getContent();
    }
}
