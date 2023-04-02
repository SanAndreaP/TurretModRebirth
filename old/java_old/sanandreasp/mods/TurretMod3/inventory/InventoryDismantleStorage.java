package sanandreasp.mods.TurretMod3.inventory;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;

public class InventoryDismantleStorage extends InventoryBasic {

	private EntityDismantleStorage dismStg;

	public InventoryDismantleStorage(String par1Str, int par2, EntityDismantleStorage eds) {
		super(par1Str, false, par2);
		this.dismStg = eds;
	}

    public ItemStack[] getContent(){
        return ObfuscationReflectionHelper.getPrivateValue(InventoryBasic.class, this, "inventoryContents", "field_70482_c");
    }

	public boolean addItemStackToInventory(ItemStack par1ItemStack)
    {
        int var2;

        if (par1ItemStack.isItemDamaged())
        {
            var2 = this.getFirstEmptyStack();

            if (var2 >= 0)
            {
                this.setInventorySlotContents(var2, ItemStack.copyItemStack(par1ItemStack));
                this.getStackInSlot(var2).animationsToGo = 5;
                par1ItemStack.stackSize = 0;
                return true;
            }
            return false;
        }
        else
        {
            do
            {
                var2 = par1ItemStack.stackSize;
                par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
            }
            while (par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < var2);

            return par1ItemStack.stackSize < var2;
        }
    }

	@Override
	public String getInventoryName() {
		return "gui.invDismStorage";
	}

    public int getFirstEmptyStack()
    {
        for (int var1 = 0; var1 < this.getInventoryStackLimit(); ++var1)
        {
            if (this.getStackInSlot(var1) == null)
            {
                return var1;
            }
        }

        return -1;
    }

    private int storePartialItemStack(ItemStack par1ItemStack)
    {
        Item var2 = par1ItemStack.getItem();
        int var3 = par1ItemStack.stackSize;
        int var4;

        if (par1ItemStack.getMaxStackSize() == 1)
        {
            var4 = this.getFirstEmptyStack();

            if (var4 < 0)
            {
                return var3;
            }
            else
            {
                if (this.getStackInSlot(var4) == null)
                {
                    this.setInventorySlotContents(var4, ItemStack.copyItemStack(par1ItemStack));
                }

                return 0;
            }
        }
        else
        {
            var4 = this.storeItemStack(par1ItemStack);

            if (var4 < 0)
            {
                var4 = this.getFirstEmptyStack();
            }

            if (var4 < 0)
            {
                return var3;
            }
            else
            {
                if (this.getStackInSlot(var4) == null)
                {
                	this.setInventorySlotContents(var4, new ItemStack(var2, 0, par1ItemStack.getItemDamage()));

                    if (par1ItemStack.hasTagCompound())
                    {
                        this.getStackInSlot(var4).setTagCompound((NBTTagCompound)par1ItemStack.getTagCompound().copy());
                    }
                }

                int var5 = var3;

                if (var3 > this.getStackInSlot(var4).getMaxStackSize() - this.getStackInSlot(var4).stackSize)
                {
                    var5 = this.getStackInSlot(var4).getMaxStackSize() - this.getStackInSlot(var4).stackSize;
                }

                if (var5 > this.getInventoryStackLimit() - this.getStackInSlot(var4).stackSize)
                {
                    var5 = this.getInventoryStackLimit() - this.getStackInSlot(var4).stackSize;
                }

                if (var5 == 0)
                {
                    return var3;
                }
                else
                {
                    var3 -= var5;
                    this.getStackInSlot(var4).stackSize += var5;
                    this.getStackInSlot(var4).animationsToGo = 5;
                    return var3;
                }
            }
        }
    }

    private int storeItemStack(ItemStack par1ItemStack)
    {
        for (int var2 = 0; var2 < this.getSizeInventory(); ++var2)
        {
            if (this.getStackInSlot(var2) != null && this.getStackInSlot(var2).getItem() == par1ItemStack.getItem() && this.getStackInSlot(var2).isStackable() && this.getStackInSlot(var2).stackSize < this.getStackInSlot(var2).getMaxStackSize() && this.getStackInSlot(var2).stackSize < this.getInventoryStackLimit() && (!this.getStackInSlot(var2).getHasSubtypes() || this.getStackInSlot(var2).getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.getStackInSlot(var2), par1ItemStack))
            {
                return var2;
            }
        }

        return -1;
    }

    @Override
    public void openInventory() {
    	super.openInventory();
		if (this.dismStg.checkForDestroy)
			this.dismStg.toggleCheckForDestroy();
    }

    @Override
    public void closeInventory() {
    	super.closeInventory();
		this.dismStg.toggleCheckForDestroy();
    }
}
