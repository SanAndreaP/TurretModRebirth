package sanandreasp.mods.TurretMod3.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.item.ItemTurret;

import java.util.Map;
import java.util.Random;

public class TileEntityLaptop extends TileEntity implements IInventory {

	public float screenAngle;
	public float prevScreenAngle;
    public boolean isOpen;
    public boolean isUsedByPlayer;
    public byte randomLightmap;
    public boolean lightUpdated = false;
    private ItemStack[] inventory;

	public TileEntityLaptop() {
		this.randomLightmap = (byte) (new Random()).nextInt(5);
		this.inventory = new ItemStack[16];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		this.prevScreenAngle = this.screenAngle;
        float f = 0.1F;
        double d0;

        if (this.isOpen && this.screenAngle == 0.0F)
        {
            double d1 = (double)this.xCoord + 0.5D;
            d0 = (double)this.zCoord + 0.5D;

            this.worldObj.playSoundEffect(d1, (double)this.yCoord + 0.5D, d0, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (!this.isOpen && this.screenAngle > 0.0F || this.isOpen && this.screenAngle < 1.0F)
        {
            float f1 = this.screenAngle;

            if (this.isOpen)
            {
                this.screenAngle += f;
            }
            else
            {
            	this.lightUpdated = false;
                this.screenAngle -= f;
            }

            if (this.screenAngle > 1.0F)
            {
                this.screenAngle = 1.0F;
            }

            float f2 = 0.5F;

            if (this.screenAngle < f2 && f1 >= f2)
            {
                d0 = (double)this.xCoord + 0.5D;
                double d2 = (double)this.zCoord + 0.5D;

                this.worldObj.playSoundEffect(d0, (double)this.yCoord + 0.5D, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.screenAngle >= 0.999F) {
            	this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            	this.worldObj.updateLightByType(EnumSkyBlock.Block, this.xCoord, this.yCoord, this.zCoord);
            	this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            	this.lightUpdated = true;
            }

            if (this.screenAngle < 0.0F)
            {
                this.screenAngle = 0.0F;
            }
        }
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeData(nbt);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readData(pkt.func_148857_g());
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);

        NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.inventory.length; ++var3)
        {
            if (this.inventory[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.inventory[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
		par1nbtTagCompound.setTag("Items", var2);

		this.writeData(par1nbtTagCompound);
	}

	private void writeData(NBTTagCompound par1nbtTagCompound) {
		par1nbtTagCompound.setBoolean("isOpen", this.isOpen);
		par1nbtTagCompound.setByte("randomLightmap", this.randomLightmap);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);

        NBTTagList var2 = par1nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);

            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.inventory.length)
            {
                this.inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

		this.readData(par1nbtTagCompound);
	}

	private void readData(NBTTagCompound par1nbtTagCompound) {
		this.isOpen = par1nbtTagCompound.getBoolean("isOpen");
		if (par1nbtTagCompound.hasKey("randomLightmap"))
			this.randomLightmap = par1nbtTagCompound.getByte("randomLightmap");
	}

	public void programItemsTargets(Map<String, Boolean> list) {
		for (ItemStack is : this.inventory) {
			if (is != null && is.stackSize > 0) {
				ItemTurret.setTargets(is, list);
				this.markDirty();
			}
		}
	}

	public void programItemsNameAndFreq(String name, int freq) {
		for (ItemStack is : this.inventory) {
			if (is != null && is.stackSize > 0) {
				ItemTurret.addCustmNameAndFreq(is, name, freq);
				this.markDirty();
			}
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 16;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
        if (this.inventory[par1] != null)
        {
            ItemStack itemstack;

            if (this.inventory[par1].stackSize <= par2)
            {
                itemstack = this.inventory[par1];
                this.inventory[par1] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.inventory[par1].splitStack(par2);

                if (this.inventory[par1].stackSize <= 0)
                {
                    this.inventory[par1] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.inventory[par1] != null)
        {
            ItemStack itemstack = this.inventory[par1];
            this.inventory[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.inventory[i] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.isOpen;
	}

	@Override
    public boolean receiveClientEvent(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.isUsedByPlayer = par2 == 1;
            return true;
        }
        else if (par1 == 2)
        {
        	this.isOpen = par2 == 1;
        	return true;
        }
        else
        {
            return super.receiveClientEvent(par1, par2);
        }
    }

	@Override
	public void openInventory() {
		this.isUsedByPlayer = true;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, 1);
	}

	@Override
	public void closeInventory() {
		this.isUsedByPlayer = false;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, 0);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

}
